package com.chessproject.detection.data;

import androidx.annotation.NonNull;

public class BoundingBox {
    public double x;
    public double y;
    public double width;
    public double height;
    public double confidence;
    public String cls;

    public BoundingBox(double x, double y, double width, double height, double confidence, String cls)
    {
        this.x = x - width / 2;
        this.y = y - height / 2;
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
