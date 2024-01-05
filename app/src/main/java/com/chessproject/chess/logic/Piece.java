package com.chessproject.chess.logic;

import java.util.ArrayList;

public abstract class Piece {
    boolean mWhite;
    int mPosition;
    Board mBoard;
    Piece(boolean white, int position, Board board) {
        mWhite = white;
        mPosition = position;
        mBoard = board;
    }
    public abstract int getImageResource();
    public abstract ArrayList<Integer> getLegalMoves();
    public int getPosition() {
        return mPosition;
    }

    public boolean moveTo(int newPosition) {
        ArrayList<Integer> legalMoves = getLegalMoves();
        if (legalMoves.contains(newPosition)) {
            mBoard.movePiece(mPosition, newPosition);
            mPosition = newPosition;
            return true;
        }

        return false;
    }
}
