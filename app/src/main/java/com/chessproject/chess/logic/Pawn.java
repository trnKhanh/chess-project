
package com.chessproject.chess.logic;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getImageResource() {
        // Triển khai logic để trả về tên tệp hình ảnh cho pnguân cờ King
        String fileName = isWhite() ? "white_pawn" : "black_pawn";
        return fileName;
    }
    @Override
    public List<Point> getLegalMoves(Point P, Board board) {
        List<Point> pawnMoves = new ArrayList<>();
        if (isWhite()) {
            if (P.x == 6 && board.getPiece(P.x - 2, P.y) == null)
                pawnMoves.add(new Point(P.x - 2, P.y));
            if (board.getPiece(P.x - 1, P.y) == null)
                pawnMoves.add(new Point(P.x - 1, P.y));
        }
        else {
            if (P.x == 1 && board.getPiece(P.x + 2, P.y) == null)
                pawnMoves.add(new Point(P.x + 2, P.y));
            if (board.getPiece(P.x + 1, P.y) == null)
                pawnMoves.add(new Point(P.x + 1, P.y));
        }

        // ăn quân
        if (isWhite()) {
            if (P.y > 0 && board.getPiece(P.x - 1, P.y - 1).isWhite() != this.isWhite())
                pawnMoves.add(new Point(P.x - 1, P.y - 1));
            if (P.y < 7 && board.getPiece(P.x - 1, P.y + 1).isWhite() != this.isWhite())
                pawnMoves.add(new Point(P.x - 1, P.y + 1));
        }
        if (isWhite()) {
            if (P.y > 0 && board.getPiece(P.x + 1, P.y - 1).isWhite() != this.isWhite())
                pawnMoves.add(new Point(P.x + 1, P.y - 1));
            if (P.y < 7 && board.getPiece(P.x + 1, P.y + 1).isWhite() != this.isWhite())
                pawnMoves.add(new Point(P.x + 1, P.y + 1));
        }
        return pawnMoves;
    }

}


