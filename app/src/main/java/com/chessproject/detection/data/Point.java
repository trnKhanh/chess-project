package com.chessproject.detection.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getDistance(Point b) {
        return Math.sqrt((this.x - b.x) * (this.x - b.x) + (this.y - b.y) * (this.y - b.y));
    }
    public double getCross(Point b) {
        return (this.x * b.y - this.y * b.x);
    }
    public double getDistance(Point a, Point b) {
        double distance = a.getDistance(b);

        Point d1 = new Point(b.x - a.x, b.y - a.y);
        Point d2 = new Point(this.x - a.x, this.y - a.y);
        double cross = d1.getCross(d2);

        return cross / distance;
    }
    public static double getArea(ArrayList<Point> pts) {
        double area = 0;
        int n = pts.size();
        for (int i = 0; i < pts.size(); ++i) {
            area += (pts.get((i+1) % n).x - pts.get(i).x) * (pts.get((i+1) % n).y + pts.get(i).y);
        }
        return Math.abs(area / 2);
    }

    @NonNull
    @Override
    public String toString() {
        String res = "";
        res += "\"x\": " + String.valueOf(x) + ", ";
        res += "\"y\": " + String.valueOf(y);
        return "{ " + res + " }";
    }
}
