package com.chessproject.detection;

import java.util.HashMap;

public class ChessConstants {

    static HashMap<String, String> getPieceMap() {
        HashMap<String, String> pieceMap = new HashMap<>();
        pieceMap.put("1", "b");
        pieceMap.put("2", "k");
        pieceMap.put("3", "n");
        pieceMap.put("4", "p");
        pieceMap.put("5", "q");
        pieceMap.put("6", "r");
        pieceMap.put("7", "B");
        pieceMap.put("8", "K");
        pieceMap.put("9", "N");
        pieceMap.put("10", "P");
        pieceMap.put("11", "Q");
        pieceMap.put("12", "R");

        return pieceMap;
    }
}
