package com.chessproject.detection.data;

import androidx.annotation.NonNull;

public class BoundingBox {
    public float x;
    public float y;
    public float width;
    public float height;
    public float confidence;
    public String cls;

    public BoundingBox(float x, float y, float width, float height, float confidence, String cls)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.confidence = confidence;
        this.cls = cls;
    }

    @NonNull
    @Override
    public String toString() {
        String res = "";
        res += "\"x\": " + String.valueOf(x) + ", ";
        res += "\"y\": " + String.valueOf(y) + ", ";
        res += "\"width\": " + String.valueOf(width) + ", ";
        res += "\"height\": " + String.valueOf(height) + ", ";
        res += "\"confidence\": " + String.valueOf(confidence) + ", ";
        res += "\"cls\": " + cls;
        return "{ " + res + " }";
    }
}
