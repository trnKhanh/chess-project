
package com.chessproject.chess.logic;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Knight extends Piece {
    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getImageResource() {
        // Triển khai logic để trả về tên tệp hình ảnh cho pnguân cờ King
        String fileName = isWhite() ? "white_knight" : "black_knight";
        return fileName;
    }
    @Override
    public List<Point> getLegalMoves(Point P, Board board) {
        List<Point> knightMoves = new ArrayList<>();
        for (int i = -2; i <= 2; i++){
            if (i == 0) continue;
            int cx = P.x + i;
            if (cx < 0 || cx > 8) continue;
            int j = 3 - Math.abs(i);
            int cy = P.y + j;
            if (0 <= cy && cy < 8 && (board.getPiece(cx, cy) == null || board.getPiece(cx, cy).isWhite() != isWhite()))
                knightMoves.add(new Point(cx, cy));
            cy = P.y - j;
            if (0 <= cy && cy < 8 && (board.getPiece(cx, cy) == null || board.getPiece(cx, cy).isWhite() != isWhite()))
                knightMoves.add(new Point(cx, cy));
        }

        return knightMoves;
    }

}


