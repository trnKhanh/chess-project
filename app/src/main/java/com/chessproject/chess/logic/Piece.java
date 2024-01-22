package com.chessproject.chess.logic;

import java.util.ArrayList;

public abstract class Piece implements Cloneable{
    boolean mWhite;
    int mPosition;
    Board mBoard;
    boolean mIsPromoting = false;
    public Piece(boolean white, int position, Board board) {
        mWhite = white;
        mPosition = position;
        mBoard = board;
    }
    public abstract String getSymbol();
    public abstract Piece copy();
    public abstract int getImageResource();
    public abstract ArrayList<Integer> getLegalMoves();
    public int getPosition() {
        return mPosition;
    }
    public boolean moveTo(int newPosition) {
        ArrayList<Integer> legalMoves = getLegalMoves();
        // Check if the move is legal before moving.
        if (legalMoves.contains(newPosition)) {
            // Try moving piece in board.
            if (mBoard.movePiece(mPosition, newPosition)) {
                // TODO: update mIsPromoting here, the following only for testing
                if (newPosition < 8) {
                    mBoard.setNewPromote();
                    mIsPromoting = true;
                }
                // If the move is successful then update position.
                mPosition = newPosition;
                return true;
            }
        }
        return false;
    }
    public Piece promote(String pieceType) {
        if (pieceType == null)
            return null;
        mIsPromoting = false;
        mBoard.finishPromote();
        return mBoard.promote(mPosition, pieceType);
    }
    public void setPosition(int position) {
        mPosition = position;
    }
    public boolean isPromoting() {
        return mIsPromoting;
    }
    public boolean isWhite() {
        return mWhite;
    }
}
