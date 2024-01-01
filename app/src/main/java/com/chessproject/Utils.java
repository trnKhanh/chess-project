package com.chessproject;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class Utils {
    static public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}
