package com.chessproject.detection;

import android.graphics.Matrix;
import android.health.connect.datatypes.HeightRecord;
import android.health.connect.datatypes.units.Pressure;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.chessproject.detection.data.BoundingBox;
import com.chessproject.detection.data.Point;
import com.chessproject.detection.services.DetectionService;
import com.chessproject.detection.services.SegmentationService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public class ChessPositionDetector {
    final static String API_KEY = "7k9RzlTrav0kMFIzfIgu";
    final static String TAG = "Chess position detector";
    final static double AREA_ERROR_THRESHOLD = 0.1;
    final static double TRANSFORMED_WIDTH = 640;
    final static double TRANSFORMED_HEIGHT = 640;
    final static double TRANSFORMED_OFFSET = 64;
    OnResultListener onResultListener;
    public interface OnResultListener {
        void onResult(String fen);
    }
    public ChessPositionDetector(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }
    public void detectPosition(byte[] image) {
        ArrayList<Point> pts = detectChessboard(image);
        ArrayList<BoundingBox> boxes = detectChessPiece(image);
        Log.d(TAG, String.valueOf(pts.size()));
        Log.d(TAG, String.valueOf(boxes.size()));


        String fen = getFen(pts, boxes);
        onResultListener.onResult(fen);
    }
    public String getFen(ArrayList<Point> pts, ArrayList<BoundingBox> boxes) {
        long n = pts.size();
        Point F = pts.get(0);
        Point A = F;

        for (int i = 1; i < n; ++i)
        {
            if (F.getDistance(A) < F.getDistance(pts.get(i))) {
                A = pts.get(i);
            }
        }

        Point B = A;
        for (int i = 1; i < n; ++i)
        {
            if (A.getDistance(B) < A.getDistance(pts.get(i))) {
                B = pts.get(i);
            }
        }

        Point C = A;
        Point D = A;
        for (int i = 1; i < n; ++i) {
            if (C.getDistance(A, B) < pts.get(i).getDistance(A, B)) {
                C = pts.get(i);
            }
            if (D.getDistance(A, B) > pts.get(i).getDistance(A, B)) {
                D = pts.get(i);
            }
        }

        double polygonArea = Point.getArea(pts);
        ArrayList<Point> quad = new ArrayList<>();
        quad.add(A);
        quad.add(C);
        quad.add(B);
        quad.add(D);
        double quadArea = Point.getArea(quad);

        double errorRate = Math.abs(1 - quadArea/polygonArea);
        Log.d(TAG, "Quad area: " + String.valueOf(quadArea));
        Log.d(TAG, "Poly area: " + String.valueOf(polygonArea));
        if (errorRate > AREA_ERROR_THRESHOLD) {
            C = pts.get(0);
            D = pts.get(1);
            quad.set(0, A);
            quad.set(1, B);
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < i; ++j) {
                    double curArea;

                    quad.set(2, pts.get(i));
                    quad.set(3, pts.get(j));
                    curArea = Point.getArea(quad);
                    if (quadArea < curArea) {
                        quadArea = curArea;
                        C = pts.get(i);
                        D = pts.get(j);
                    }

                    quad.set(2, pts.get(j));
                    quad.set(3, pts.get(i));
                    curArea = Point.getArea(quad);
                    if (quadArea < curArea) {
                        quadArea = curArea;
                        C = pts.get(j);
                        D = pts.get(i);
                    }
                }
            }
        } else {
            B = quad.get(1);
            C = quad.get(2);
        }
        Log.d(TAG, "Quad area: " + String.valueOf(quadArea));

        quad.set(0, A);
        quad.set(1, B);
        quad.set(2, C);
        quad.set(3, D);

        Collections.sort(quad, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                if (o1.y - o2.y < 0)
                    return -1;
                else
                    return 1;
            }
        });

        if (quad.get(0).x < quad.get(1).x) {
            A = quad.get(0);
            B = quad.get(1);
        } else {
            B = quad.get(0);
            A = quad.get(1);
        }

        if (quad.get(2).x < quad.get(3).x) {
            D = quad.get(2);
            C = quad.get(3);
        } else {
            C = quad.get(2);
            D = quad.get(3);
        }
        Log.d(TAG, "A: " + String.valueOf(A));
        Log.d(TAG, "B: " + String.valueOf(B));
        Log.d(TAG, "C: " + String.valueOf(C));
        Log.d(TAG, "D: " + String.valueOf(D));
        float[] src = new float[] {
                (float) A.x,
                (float) A.y,
                (float) B.x,
                (float) B.y,
                (float) C.x,
                (float) C.y,
                (float) D.x,
                (float) D.y,
        };

        float[] dest = new float[] {
                (float) TRANSFORMED_OFFSET,
                (float) TRANSFORMED_OFFSET,
                (float) (TRANSFORMED_WIDTH - TRANSFORMED_OFFSET),
                (float) TRANSFORMED_OFFSET,
                (float) (TRANSFORMED_WIDTH - TRANSFORMED_OFFSET),
                (float) (TRANSFORMED_HEIGHT - TRANSFORMED_OFFSET),
                (float) TRANSFORMED_OFFSET,
                (float) (TRANSFORMED_HEIGHT - TRANSFORMED_OFFSET),
        };
        Matrix matrix = new Matrix();
        matrix.setPolyToPoly(src, 0, dest, 0, 4);
        Matrix inverseMatrix = new Matrix();
        matrix.invert(inverseMatrix);
        Log.d(TAG, String.valueOf(src[0]));
        inverseMatrix.mapPoints(dest);
        Log.d(TAG, String.valueOf(src[0]));
        matrix.mapPoints(dest);
        Log.d(TAG, String.valueOf(dest[0]));
        float[] board = new float[] {
                (float) TRANSFORMED_OFFSET,
                (float) TRANSFORMED_OFFSET,
                (float) (TRANSFORMED_WIDTH - TRANSFORMED_OFFSET),
                (float) (TRANSFORMED_HEIGHT - TRANSFORMED_OFFSET),
        };
        Pair<String, Double>[][] predictedBoard = new Pair[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                predictedBoard[i][j] = new Pair<>("", 0.0);
            }
        }

        for (BoundingBox box: boxes) {
            double x = box.x;
            double y = box.y;
            double width = box.width;
            double height = box.height;
            double conf = box.confidence;
            Log.d(TAG, String.valueOf(x));
            String cls = box.cls;
            float[] rect = new float[] {
                    (float) x,
                    (float) y,
                    (float) (x + width),
                    (float) (y + height),
            };
            int posX = -1, posY = -1;
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    if (isInRect(i, j, board, rect, inverseMatrix)) {
                        if (posX == -1 || posY < j)
                        {
                            posX = i;
                            posY = j;
                        }
                    }
                }
            }
            if (posX != -1 && predictedBoard[posX][posY].second < conf) {
                predictedBoard[posX][posY] = new Pair<>(cls, conf);
            }
        }
        HashMap<String, String> pieceMap = ChessConstants.getPieceMap();
        StringBuilder fen = new StringBuilder();

        for (int j = 0; j < 8; ++j) {
            int cnt = 0;
            for (int i = 0; i < 8; ++i) {
                if (predictedBoard[i][j].first.compareTo("") == 0) {
                    cnt++;
                    continue;
                }

                if (cnt != 0) {
                    fen.append(String.valueOf(cnt));
                    cnt = 0;
                }
                fen.append(pieceMap.get(predictedBoard[i][j].first));
            }
            if (cnt != 0) {
                fen.append(String.valueOf(cnt));
            }
            if (j + 1 < 8) {
                fen.append("/");
            }
        }

        return fen.toString();
    };
    private boolean isInRect(Point pt, float[] rect) {
        return (pt.x >= rect[0]) && (pt.y >= rect[1]) && (pt.x <= rect[2]) && (pt.y <= rect[3]);
    }
    private boolean isInRect(int i, int j, float[] board, float[] rect, Matrix inverseMatrix) {
        return isInRect(getCenter(i, j, board, inverseMatrix), rect);
    }
    private Point getCenter(int i, int j, float[] board, Matrix inverseMatrix) {
        float[] o = new float[] {
                (float)(board[0] + (board[2] - board[0]) / 16.0),
                (float)(board[1] + (board[3] - board[1]) / 16.0),
        };

        float[] pt = new float[] {
                (float)(o[0] + (board[2] - board[0]) * (float)i / 8.0),
                (float)(o[1] + (board[3] - board[1]) * (float)j / 8.0),
        };
        inverseMatrix.mapPoints(pt);

        Point res = new Point((double)pt[0], (double)pt[1]);

        return res;
    };

    public ArrayList<Point> detectChessboard(byte[] image) {
        // Initiate retrofit and service
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://outline.roboflow.com/").build();
        SegmentationService service = retrofit.create(SegmentationService.class);

        // Construct body
        String encodedString = Base64.getEncoder().encodeToString(image);
        RequestBody body = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), encodedString);

        ArrayList<Point> pts = null;
        // Send request
        try {
            Response<ResponseBody> response = service.detectChessboard(body).execute();
            ResponseBody data = response.body();
            JSONObject jsonData = new JSONObject(data.string());
            JSONArray predictions = jsonData.getJSONArray("predictions");
            JSONArray points = predictions.getJSONObject(0).getJSONArray("points");

            pts = new ArrayList<>();
            for (int i = 0; i < points.length(); ++i)
            {
                JSONObject point = points.getJSONObject(i);

                double x = point.getDouble("x");
                double y = point.getDouble("y");
                pts.add(new Point(x, y));
            }
        } catch (IOException e) {
            Log.e(TAG, "Chessboard segmentation failed: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Chessboard segmentation failed: " + e.getMessage());
        }

        return pts;
    }

    public ArrayList<BoundingBox> detectChessPiece(byte[] image) {
        // Initiate retrofit and service
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://detect.roboflow.com/").build();
        DetectionService service = retrofit.create(DetectionService.class);

        // Construct body
        String encodedString = Base64.getEncoder().encodeToString(image);
        RequestBody body = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), encodedString);

        ArrayList<BoundingBox> boundingBoxes = null;
        // Send request
        try {
            Response<ResponseBody> response = service.detectChessPiece(body).execute();
            ResponseBody data = response.body();
            JSONObject jsonData = new JSONObject(data.string());
            JSONArray predictions = jsonData.getJSONArray("predictions");

            boundingBoxes = new ArrayList<>();
            for (int i = 0; i < predictions.length(); ++i)
            {
                JSONObject box = predictions.getJSONObject(i);
                double x = box.getDouble("x");
                double y = box.getDouble("y");
                double w = box.getDouble("width");
                double h = box.getDouble("height");
                double conf = box.getDouble("confidence");
                String cls = box.getString("class");

                boundingBoxes.add(new BoundingBox(x, y, w, h, conf, cls));
            }
        } catch (IOException e) {
            Log.e(TAG, "Chess pieces detection failed: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Chess pieces detection failed: " + e.getMessage());
        }

        return boundingBoxes;
    }
}
