package com.chessproject.chess.logic;

import android.util.Pair;

import java.util.ArrayList;

public class Board {
    ArrayList<Piece> mPieces;
    ArrayList<Pair<Integer, Integer>> mMovesHistory;
    Piece[] mBoard = new Piece[64];
    public Board(String fen) {
        mPieces = new ArrayList<>();
        mPieces.add(new Knight(true, 15, this));
        mPieces.add(new Knight(false, 30, this));

        mMovesHistory = new ArrayList<>();
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
        if (newPosition >= 0 && newPosition < 64) {
            if (mBoard[newPosition] != null) {
                mPieces.remove(mBoard[newPosition]);
            }
            if (position >= 0 && position < 64) {
                mBoard[newPosition] = mBoard[position];
                mMovesHistory.add(new Pair<>(position, newPosition));
            }
        }
        if (position >= 0 && position < 64) {
            mBoard[position] = null;
        }
    }

    public Pair<Integer, Integer> getLastMove() {
        if (mMovesHistory.size() == 0) {
            return null;
        }
        return mMovesHistory.get(mMovesHistory.size() - 1);
    }
    public void setPiece(int position, Piece piece) {
        if (position < 0 || position >= 64)
            return;
        if (mBoard[position] != null)
            mPieces.remove(mBoard[position]);
        mBoard[position] = piece;
        if (mBoard[position] != null)
            mPieces.add(mBoard[position]);
    }
    public void clearHistory() {
        mMovesHistory.clear();
    }


}
