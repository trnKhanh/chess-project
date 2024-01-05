package com.chessproject.chess.logic;


import android.graphics.Point;

import java.util.ArrayList;

import java.util.List;

public class King extends Piece {
    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getImageResource() {
        // Triển khai logic để trả về tên tệp hình ảnh cho pnguân cờ King
        String fileName = isWhite() ? "white_king" : "black_king";
        return fileName;
    }
    @Override
    public List<Point> getLegalMoves(Point P, Board board) {
        List<Point> kingMoves = new ArrayList<>();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0) continue;
                int cx = P.x + i, cy = P.y + j;
                if (0 <= cx && cx < 8 && 0 <= cy && cy < 8 && (board.getPiece(cx, cy) == null || board.getPiece(cx, cy).isWhite() != isWhite()))
                    kingMoves.add(new Point(cx, cy));
            }
        return kingMoves;
    }

}

