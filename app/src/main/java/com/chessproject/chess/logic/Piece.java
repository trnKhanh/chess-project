package com.chessproject.chess.logic;

import java.util.ArrayList;

public abstract class Piece implements Cloneable{
    boolean mWhite;
    int mPosition;
    Board mBoard;
    public Piece(boolean white, int position, Board board) {
        mWhite = white;
        mPosition = position;
        mBoard = board;
    }
    public abstract Piece copy();
    public abstract int getImageResource();
    public abstract ArrayList<Integer> getLegalMoves();
    public int getPosition() {
        return mPosition;
    }

    public boolean moveTo(int newPosition, boolean checkLegal) {
        ArrayList<Integer> legalMoves = getLegalMoves();
        if (!checkLegal || legalMoves.contains(newPosition)) {
            mBoard.movePiece(mPosition, newPosition);
            mPosition = newPosition;
            return newPosition >= 0 && newPosition < 64;
        }

        return false;
    }
}
