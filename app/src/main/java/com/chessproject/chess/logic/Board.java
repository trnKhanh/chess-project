package com.chessproject.chess.logic;

import java.util.ArrayList;

public class Board {
    ArrayList<Piece> mPieces;
    Piece[] mBoard = new Piece[64];
    public Board(String fen) {
        mPieces = new ArrayList<>();
        mPieces.add(new Knight(true, 15, this));
        mPieces.add(new Knight(false, 30, this));
        mBoard[15] = mPieces.get(0);
        mBoard[30] = mPieces.get(1);
    }

    public ArrayList<Piece> getPieces() {
        return mPieces;
    }
    public Piece getPiece(int position) {
        return mBoard[position];
    }
    public void movePiece(int position, int newPosition) {
        if (mBoard[newPosition] != null) {
            mPieces.remove(mBoard[newPosition]);
        }
        mBoard[newPosition] = mBoard[position];
        mBoard[position] = null;
    }
}
