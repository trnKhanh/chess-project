package com.chessproject.chess.logic;

import android.graphics.Point;

import java.util.List;

public abstract class Piece {
    private boolean isWhite;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public abstract String getImageResource();

    public abstract List<Point> getLegalMoves(Point P, Board Board);
    public boolean isAttacked(Point P, Board Board){
        return true;
    }
}