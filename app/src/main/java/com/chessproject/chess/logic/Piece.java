package com.chessproject.chess.logic;

import android.util.Log;

import java.util.ArrayList;

public abstract class Piece implements Cloneable{
    final static String TAG = "Piece";
    boolean mWhite;
    int mPosition;
    Board mBoard;
    boolean mIsPromoting = false;
    public Piece(boolean white, int position, Board board) {
        mWhite = white;
        mPosition = position;
        mBoard = board;
    }

    public static Piece createPiece(int indexSquare, char c, Board board) {
        switch (c){
            case 'n':
                return new Knight(false, indexSquare, board);
            case 'k':
                return new King(false, indexSquare, board);
            case 'q':
                return new Queen(false, indexSquare, board);
            case 'p':
                return new Pawn(false, indexSquare, board);
            case 'b':
                return new Bishop(false, indexSquare, board);
            case 'r':
                return new Rook(false, indexSquare, board);
            case 'N':
                return new Knight(true, indexSquare, board);
            case 'K':
                return new King(true, indexSquare, board);
            case 'Q':
                return new Queen(true, indexSquare, board);
            case 'P':
                return new Pawn(true, indexSquare, board);
            case 'B':
                return new Bishop(true, indexSquare, board);
            case 'R':
                return new Rook(true, indexSquare, board);
            default:
                return null;
        }
    }

    public abstract Piece copy();
    public abstract int getImageResource(boolean whitePerspective);
    public abstract ArrayList<Integer> getLegalMoves();
    public boolean canMove(){
        return isWhite() == mBoard.isWhiteTurn();
    }
    public int getPosition() {
        return mPosition;
    }
    public boolean moveTo(int newPosition) {
        Log.d(TAG, "Move from " + String.valueOf(mPosition) + " to " + String.valueOf(newPosition));
        ArrayList<Integer> legalMoves = getLegalMoves();
        // Check if the move is legal before moving.
        if (legalMoves.contains(newPosition)) {
            // Try moving piece in board.
            if (mBoard.movePiece(mPosition, newPosition)) {
                // TODO: update mIsPromoting here, the following only for testing
                if (this.getClass() == Pawn.class) {
                    if ((isWhite() && newPosition / 8 == 0) || (!isWhite() && newPosition / 8 == 7) ){
                        mBoard.setNewPromote();
                        mIsPromoting = true;
                    }
                }
                // If the move is successful then update position.
                mPosition = newPosition;
                mBoard.setEnPassantPosition(-1);
                return true;
            }
        }
        return false;
    }
    public Piece promote(String pieceType) {
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

    public abstract String getSymbol();
}
