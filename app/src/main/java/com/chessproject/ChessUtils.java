package com.chessproject;

public class ChessUtils {
    public static int getPosition(String positionString) {
        if (positionString.length() != 2)
            return -1;
        int colId = (int)(positionString.charAt(0)) - (int) 'a';
        int rowId = 8 - Integer.parseInt(positionString.substring(1));
        return rowId * 8 + colId;
    }

    public static String getPositionString(int position) {
        if (position < 0 || position >= 64)
            return null;
        int colId = position % 8;
        int rowId = 8 - position / 8;

        char col =(char) (colId + (int)'a');
        return col + String.valueOf(rowId);
    }
}
