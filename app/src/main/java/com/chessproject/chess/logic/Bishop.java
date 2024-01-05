package com.chessproject.chess.logic;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getImageResource() {
        String fileName = isWhite() ? "white_bishop" : "black_bishop";
        return fileName;
    }
    @Override
    public List<Point> getLegalMoves(Point P, Board board) {
        List<Point> bishopMoves = new ArrayList<>();
        List<Point> direction = Arrays.asList(
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
                    bishopMoves.add(new Point(x, y));
                else {
                    if (board.getPiece(x, y).isWhite() != this.isWhite())
                        bishopMoves.add(new Point(x, y));
                    break;
                }

            }
        }
        return bishopMoves;
    }

}

