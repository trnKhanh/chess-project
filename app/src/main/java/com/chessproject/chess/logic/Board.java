package com.chessproject.chess.logic;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

public class Board {
    public static class Move {
        private final int oldPosition;
        private final int newPosition;
        private Piece promotionFrom;
        private Piece capturedPiece;
        public int getOldPosition() {
            return oldPosition;
        }

        public int getNewPosition() {
            return newPosition;
        }

        public Piece getPromotionFrom() {
            return promotionFrom;
        }
        public void setPromotionFrom(Piece piece) {
            promotionFrom = piece;
        }

        public Piece getCapturedPiece() {
            return capturedPiece;
        }
        public void setCapturedPiece(Piece piece) {
            capturedPiece = piece;
        }
        public Move(int oldPosition, int newPosition) {
            this.oldPosition = oldPosition;
            this.newPosition = newPosition;
        }
        public Move(int oldPosition, int newPosition, Piece promotionFrom, Piece capturedPiece) {
            this.oldPosition = oldPosition;
            this.newPosition = newPosition;
            this.promotionFrom = promotionFrom;
            this.capturedPiece = capturedPiece;
        }
    }
    // TODO: find ways to record history
    final static String TAG = "Board";
    ArrayList<Move> mMovesHistory;
    Piece[] mPieces = new Piece[64];
    int mPromotionCount = 0;
    public Board(String fen) {
        mMovesHistory = new ArrayList<>();
        // TODO: remove this when finishing all logics
        mPieces[15] = new Knight(true, 15, this);
        mPieces[30] = new Knight(false, 30, this);
    }

    public ArrayList<Piece> getPieces() {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 64; ++i) {
            if (mPieces[i] != null)
                pieces.add(mPieces[i]);
        }
        return pieces;
    }
    public Piece getPiece(int position) {
        // If position is invalid then return null.
        if (position < 0 || position >= 64)
            return null;
        return mPieces[position];
    }
    public void setPiece(int position, Piece piece) {
        // If position is invalid then return.
        if (position < 0 || position >= 64)
            return;
        // Set position to new piece.
        mPieces[position] = piece;
    }
    public boolean isPromoting() {
        return mPromotionCount > 0;
    }
    public void setNewPromote() {
        mPromotionCount++;
    }
    public void finishPromote() {
        mPromotionCount--;
    }
    public boolean movePiece(int position, int newPosition) {
        // If there is promotion at the momment, then fail.
        if (isPromoting()) {
            return false;
        }
        Move move = new Move(position, newPosition);
        // If position is invalid then return.
        if (position < 0 || position >= 64 || newPosition < 0 || newPosition >= 64) {
            return false;
        }
        // If there is no piece at position then return.
        if (mPieces[position] == null) {
            return false;
        }
        Piece piece = mPieces[position];
        // Remove piece at position.
        mPieces[position] = null;
        // If the new position contains the piece, then remove it.
        if (mPieces[newPosition] != null) {
            move.setCapturedPiece(mPieces[newPosition]);
            mPieces[newPosition] = null;
        }
        // Add piece to new position
        mPieces[newPosition] = piece;
        // Add move to moves history.
        mMovesHistory.add(move);

        return true;
    }
    public void printBoard() {
        // Mainly use to debug
        Log.d(TAG, "len " + String.valueOf(getPieces().size()));
        for (int i = 0; i < 8; ++i) {
            for (int j= 0; j < 8; ++j) {
                if (mPieces[i * 8 + j] != null) {
                    Log.d(TAG, String.valueOf(i) + " " + String.valueOf(j));
                }
            }
        }
    }

    public Move getLastMove() {
        if (mMovesHistory.size() == 0) {
            return null;
        }
        return mMovesHistory.get(mMovesHistory.size() - 1);
    }

    public void clearHistory() {
        mMovesHistory.clear();
    }
    public int getLastMoveEvaluation() {
        return (int)(Math.random() * 3);
    }
    public boolean isWhiteTurn() {
        return true;
    }

    public Piece promote(int position, String pieceType) {
        Piece oldPiece = mPieces[position];
        if (oldPiece == null) {
            return null;
        }
        getLastMove().setPromotionFrom(mPieces[position]);
        // TODO: set promotion to correct pieces
        switch (pieceType) {
            case "q":
                mPieces[position] = new Knight(!oldPiece.isWhite(), position, this);
                break;
            case "r":
                break;
            case "b":
                break;
            case "n":
                mPieces[position] = new Knight(oldPiece.isWhite(), position, this);
                break;
        }
        Log.d(TAG, String.valueOf(mPieces[position].isWhite()));
        return mPieces[position];
    }
    public Move rollbackLastMove() {
        if (mMovesHistory.size() == 0) {
            return null;
        }
        Move move = mMovesHistory.get(mMovesHistory.size() - 1);
        mMovesHistory.remove(mMovesHistory.size() - 1);
        // If there is promoting then set it back to 0
        mPromotionCount = 0;
        // If there is no piece at the destination then return null
        if (mPieces[move.getNewPosition()] == null) {
            return null;
        }
        // Update pieces
        // If there is promotion then reverse it
        if (move.getPromotionFrom() != null)
            mPieces[move.getNewPosition()] = move.getPromotionFrom();
        // Reverse the move
        mPieces[move.getOldPosition()] = mPieces[move.getNewPosition()];
        // If the move captured a piece, then bring it back
        mPieces[move.getNewPosition()] = move.getCapturedPiece();
        // Update position of piece
        mPieces[move.getOldPosition()].setPosition(move.getOldPosition());
        return move;
    }
    public String getFen() {
        return "r2q1rk1/ppp2ppp/3bbn2/3p4/8/1B1P4/PPPPPPPP/RNB1QRK1 w - - 5 11";
    }
}
