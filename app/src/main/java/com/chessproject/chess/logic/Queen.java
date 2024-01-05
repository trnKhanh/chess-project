package com.chessproject.chess.logic;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Queen extends Piece {
    public Queen(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getImageResource() {
        // Triển khai logic để trả về tên tệp hình ảnh cho pnguân cờ King
        String fileName = isWhite() ? "white_queen" : "black_queen";
        return fileName;
    }
    @Override
    public List<Point> getLegalMoves(Point P, Board board) {
        List<Point> queenMoves = new ArrayList<>();
        List<Point> direction = Arrays.asList(
                new Point(1, 0),
                new Point(-1, 0),
                new Point(0, 1),
                new Point(0, -1),
                new Point(1, 1),
                new Point(1, -1),
                new Point(-1, 1),
                new Point(-1, -1)
        );
        for (Point dir : direction) {
            int x = P.x;
            int y = P.y;
            while (true) {
                x += dir.x;
                y += dir.y;
                if (x < 0 || x >= 8 || y < 0 || y >= 8) break;
                if (board.getPiece(x, y) == null)
                    queenMoves.add(new Point(x, y));
                else {
                    if (board.getPiece(x, y).isWhite() != this.isWhite())
                        queenMoves.add(new Point(x, y));
                    break;
                }
            }
        }
        return queenMoves;
    }
}

