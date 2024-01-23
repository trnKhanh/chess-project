package com.chessproject.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.chessproject.detection.data.Point;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.CastOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.lang.reflect.Array;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChessboardSegmentation {
    private final static String TAG = "ChessboardSegmentation";
    private static ChessboardSegmentation mInstance = null;
    public static ChessboardSegmentation getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ChessboardSegmentation(context);
        return mInstance;
    }
    private final static int INPUT_WIDTH = 640;
    private final static int INPUT_HEIGHT = 640;
    private final static float INPUT_MEAN = 0f;
    private final static float INPUT_STD = 255f;
    private final static DataType INPUT_TYPE = DataType.FLOAT32;
    private final static DataType OUTPUT_TYPE = DataType.FLOAT32;

    private final static int NUM_ELEMENT = 8400;
    private final static float CONF_THRESHOLD = 0.75f;
    private final static float IOU_THRESHOLD = 0.5f;
    Interpreter interpreter = null;
    ImageProcessor imageProcessor = (new ImageProcessor.Builder())
            .add(new NormalizeOp(INPUT_MEAN, INPUT_STD))
            .add(new CastOp(INPUT_TYPE))
            .build();
    ChessboardSegmentation(Context context) {
        try {
            MappedByteBuffer model = FileUtil.loadMappedFile(context, "chessboard_seg_float16.tflite");

            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(4);
            interpreter = new Interpreter(model, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Point> detect(Bitmap bitmap) {
        // Preprocess step
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, false);
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(resizedBitmap);
        tensorImage = imageProcessor.process(tensorImage);

        TensorBuffer output0 = TensorBuffer.createFixedSize(
                new int[]{1, 38, 8400},
                OUTPUT_TYPE
        );
        TensorBuffer output1 = TensorBuffer.createFixedSize(
                new int[]{1, 160, 160, 32},
                OUTPUT_TYPE
        );
        Object[] inputs = {tensorImage.getBuffer()};
        Map<Integer, Object> outputs = new HashMap<>();
        outputs.put(0, output0.getBuffer());
        outputs.put(1, output1.getBuffer());
        // Run model
        interpreter.runForMultipleInputsOutputs(inputs, outputs);

        ArrayList<float[]> result = processOutput(output0.getFloatArray(), output1.getFloatArray());
        // get masks of images
        int[] colors = new int[160 * 160];
        for (float[] px: result) {
            for (int i = 0; i < 160; ++i) {
                for (int j = 0; j < 160; ++j) {
                    if (px[i * 160 + j] < 0)
                        colors[i * 160 + j] = Color.BLACK;
                    else
                        colors[i * 160 + j] = Color.WHITE;
                }
            }
        }
        // Generate bitmap from int array
        Bitmap answer = Bitmap.createBitmap(colors, 160, 160, Bitmap.Config.ARGB_8888);
        // Convert bitmap to Mat
        Mat mat = new Mat(160, 160, CvType.CV_8UC3);
        Utils.bitmapToMat(answer, mat);
        // Convert Mat to Gray Mat
        Mat grey = new Mat();
        Imgproc.cvtColor(mat, grey, Imgproc.COLOR_RGB2GRAY);
        // Find contours of the Mat
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(grey, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        // Find largest contour
        int maxId = 0;
        double maxVal = 0;
        for (int i = 0; i < contours.size(); ++i) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxVal) {
                maxId = i;
                maxVal = area;
            }
        }
        org.opencv.core.Point[] points = contours.get(maxId).toArray();
        ArrayList<Point> myPoints = new ArrayList<>();
        for (int i = 0; i < points.length; ++i)
        {
            double x = points[i].x / 160f * bitmap.getWidth();
            double y = points[i].y / 160f * bitmap.getHeight();
            myPoints.add(new Point(x, y));
        }
        return myPoints;
    }
    ArrayList<float[]> processOutput(float[] output0, float[] output1) {
        ArrayList<Mask> masks = new ArrayList<>();
        for (int i = 0; i < NUM_ELEMENT; ++i) {
            float xc = output0[i];
            float yc = output0[i + NUM_ELEMENT];
            float w = output0[i + NUM_ELEMENT * 2];
            float h = output0[i + NUM_ELEMENT * 3];
            float x = xc - w / 2f;
            float y = yc - h / 2f;
            float conf = output0[i + NUM_ELEMENT * 4];
            float[] maskWeights = new float[32];
            for (int j = 6; j < 38; ++j) {
                maskWeights[j - 6] = output0[i + NUM_ELEMENT * j];
            }
            BoundingBox box = new BoundingBox(x, y, w, h);
            masks.add(new Mask(box, maskWeights, conf));
        }

        ArrayList<Mask> processedMasks = applyNMS(masks);
        ArrayList<float[]> resultMasks = new ArrayList<>();

        for (Mask mask: processedMasks) {
            float[] resultMask = new float[160 * 160];
            for (int i = 0; i < 160; ++i) {
                for (int j = 0; j < 160; ++j) {
                    resultMask[i * 160 + j] = 0;
                    for (int k = 0; k < 32; ++k) {
                        resultMask[i * 160 + j] += mask.maskWeights[k] * output1[i * 160 * 32 + j * 32 + k];
                    }
                }
            }
            resultMasks.add(resultMask);
        }

        return resultMasks;
    }

    ArrayList<Mask> applyNMS(List<Mask> masks) {
        masks.sort(new Comparator<Mask>() {
            @Override
            public int compare(Mask o1, Mask o2) {
                return -Float.compare(o1.conf, o2.conf);
            }
        });
        Set<Integer> removedId = new HashSet<>();
        Set<Integer> maskId = new HashSet<>();
        ArrayList<Mask> processedMasks = new ArrayList<>();
        for (int i = 0; i < masks.size(); ++i) {
            if (removedId.contains(i))
                continue;
            if (masks.get(i).conf < CONF_THRESHOLD)
                break;
            maskId.add(i);
            for (int j = i + 1; j < masks.size(); ++j) {
                float iou = getIOU(masks.get(i).box, masks.get(j).box);
                if (iou > IOU_THRESHOLD) {
                    removedId.add(j);
                }
            }
        }
        for (int id: maskId) {
            processedMasks.add(masks.get(id));
        }
        return processedMasks;
    }
    float getIOU(BoundingBox b1, BoundingBox b2) {
        float x1 = Math.max(b1.x, b2.x);
        float y1 = Math.max(b1.y, b2.y);
        float x2 = Math.min(b1.x + b1.w, b2.x + b2.w);
        float y2 = Math.min(b1.y + b1.h, b2.y + b2.h);

        float intersectArea = (x2 - x1) * (y2 - y1);
        float area1 = b1.w * b1.h;
        float area2 = b2.w * b2.h;

        return intersectArea / (area1 + area2 - intersectArea);
    }

    static class BoundingBox {
        public float x, y, w, h;
        public BoundingBox(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
    static class Mask {
        public float[] maskWeights = new float[32];
        public BoundingBox box;
        public float conf;
        public Mask(BoundingBox box, float[] maskWeights, float conf) {
            this.box = box;
            this.maskWeights = maskWeights;
            this.conf = conf;
        }
    }
}
