package com.chessproject.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.chessproject.detection.data.BoundingBox;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.CastOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChessPieceDetector {
    private final static String TAG = "ChessPieceDetector";
    private static ChessPieceDetector mInstance = null;
    public static ChessPieceDetector getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ChessPieceDetector(context);
        return mInstance;
    }
    private final static int INPUT_WIDTH = 640;
    private final static int INPUT_HEIGHT = 640;
    private final static float INPUT_MEAN = 0f;
    private final static float INPUT_STD = 255f;
    private final static DataType INPUT_TYPE = DataType.FLOAT32;
    private final static DataType OUTPUT_TYPE = DataType.FLOAT32;

    private final static int NUM_ELEMENT = 8400;
    private final static float CONF_THRESHOLD = 0.5f;
    private final static float IOU_THRESHOLD = 75f;
    private String[] className = {"0", "1", "10", "11", "12", "2", "3", "4", "5", "6", "7", "8", "9"};
    Interpreter interpreter = null;
    ImageProcessor imageProcessor = (new ImageProcessor.Builder())
            .add(new NormalizeOp(INPUT_MEAN, INPUT_STD))
            .add(new CastOp(INPUT_TYPE))
            .build();
    ChessPieceDetector(Context context) {
        try {
            MappedByteBuffer model = FileUtil.loadMappedFile(context, "chess_piece_detection_float32.tflite");

            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(4);
            interpreter = new Interpreter(model, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BoundingBox> detect(Bitmap bitmap) {
        // Preprocess Step
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, false);
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(resizedBitmap);
        tensorImage = imageProcessor.process(tensorImage);

        TensorBuffer output = TensorBuffer.createFixedSize(
                new int[]{1, 17, 8400},
                OUTPUT_TYPE
        );
        Object[] inputs = {tensorImage.getBuffer()};
        Map<Integer, Object> outputs = new HashMap<>();
        outputs.put(0, output.getBuffer());
        // Run model
        interpreter.runForMultipleInputsOutputs(inputs, outputs);

        // Process raw output to get bounding boxes
        ArrayList<BoundingBox> boxes = processOutput(output.getFloatArray());
        // Running NMS algorithm to remove redundant boxes
        ArrayList<BoundingBox> processedBoxes = applyNMS(boxes);
        for (BoundingBox box: processedBoxes) {
            box.x *= bitmap.getWidth();
            box.y *= bitmap.getHeight();
            box.width *= bitmap.getWidth();
            box.height *= bitmap.getHeight();
            Log.d(TAG, String.valueOf(box.confidence) + " " + String.valueOf(box.cls));
        }
        return processedBoxes;
    }
    ArrayList<BoundingBox> processOutput(float[] output) {
        ArrayList<BoundingBox> boxes = new ArrayList<>();
        for (int i = 0; i < NUM_ELEMENT; ++i) {
            float xc = output[i];
            float yc = output[i + NUM_ELEMENT];
            float w = output[i + NUM_ELEMENT * 2];
            float h = output[i + NUM_ELEMENT * 3];
            float x = xc - w / 2f;
            float y = yc - h / 2f;
            float conf = 0;
            int classId = 0;
            for (int j = 4; j < 17; ++j) {
                if (output[i + NUM_ELEMENT * j] > conf) {
                    conf = output[i + NUM_ELEMENT * j];
                    classId = j - 4;
                }
            }

            BoundingBox box = new BoundingBox(x, y, w, h, conf, className[classId]);
            boxes.add(box);
        }
        return boxes;
    }

    ArrayList<BoundingBox> applyNMS(List<BoundingBox> boxes) {
        boxes.sort(new Comparator<BoundingBox>() {
            @Override
            public int compare(BoundingBox o1, BoundingBox o2) {
                return -Float.compare(o1.confidence, o2.confidence);
            }
        });
        Set<Integer> removedId = new HashSet<>();
        Set<Integer> maskId = new HashSet<>();
        ArrayList<BoundingBox> boundingBoxes = new ArrayList<>();
        for (int i = 0; i < boxes.size(); ++i) {
            if (removedId.contains(i))
                continue;
            if (boxes.get(i).confidence < CONF_THRESHOLD)
                break;
            maskId.add(i);
            for (int j = i + 1; j < boxes.size(); ++j) {
                if (!boxes.get(i).cls.equals(boxes.get(j).cls))
                    continue;
                float iou = getIOU(boxes.get(i), boxes.get(j));
                if (iou > IOU_THRESHOLD) {
                    removedId.add(j);
                }
            }
        }
        for (int id: maskId) {
            boundingBoxes.add(boxes.get(id));
        }
        return boundingBoxes;
    }
    float getIOU(BoundingBox b1, BoundingBox b2) {
        float x1 = Math.max(b1.x, b2.x);
        float y1 = Math.max(b1.y, b2.y);
        float x2 = Math.min(b1.x + b1.width, b2.x + b2.width);
        float y2 = Math.min(b1.y + b1.height, b2.y + b2.height);

        float intersectArea = (x2 - x1) * (y2 - y1);
        float area1 = b1.width * b1.height;
        float area2 = b2.width * b2.height;

        return intersectArea / (area1 + area2 - intersectArea);
    }

}
