package com.chessproject.chess.logic;

import android.util.Log;

import java.util.ArrayList;

public class Board {
    public static class Move {
        private final int oldPosition;
        private final int newPosition;
        private String promotionFrom = null;
        private String promotionTo = null;
        private Piece capturedPiece = null;
        private int enPassantPosition = -1;
        private boolean[] canCastle;
        private int castle = -1;
        public void setCastle(int c) {
            castle = c;
        }
        public int getCastle() {
            return castle;
        }
        public void setCanCastle(boolean[] castle) {
            canCastle = new boolean[4];
            System.arraycopy(castle, 0, canCastle, 0, 4);
        }
        public boolean[] getCanCastle() {
            return canCastle;
        }
        public void setEnPassantPosition(int position){
            enPassantPosition = position;
        }
        public int getEnPassantPosition() {
            return enPassantPosition;
        }
        public int getOldPosition() {
            return oldPosition;
        }
        public int getNewPosition() {
            return newPosition;
        }
        public String getPromotionFrom() {
            return promotionFrom;
        }
        public String getPromotionTo() {
            return promotionTo;
        }
        public void setPromotion(String from, String to) {
            promotionFrom = from;
            promotionTo = to;
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
        public Move(int oldPosition, int newPosition, String promotionFrom, String promotionTo, Piece capturedPiece) {
            this.oldPosition = oldPosition;
            this.newPosition = newPosition;
            this.promotionFrom = promotionFrom;
            this.promotionTo = promotionTo;
            this.capturedPiece = capturedPiece;
        }
        public boolean equal(Move move) {
            return this.oldPosition == move.oldPosition &&
                    this.newPosition == move.newPosition &&
                    (this.promotionFrom == null || this.promotionFrom.compareTo(move.promotionFrom) == 0) &&
                    (this.promotionTo == null || this.promotionTo.compareTo(move.promotionTo) == 0);
        }
    }
    // TODO: find ways to record history
    final static String TAG = "Board";
    final static int WHITE_CASTLE_KING = 0;
    final static int WHITE_CASTLE_QUEEN = 1;
    final static int BLACK_CASTLE_KING = 2;
    final static int BLACK_CASTLE_QUEEN = 3;
    boolean[] canCastle = {true, true, true, true};
    public void setCanCastle(int code, boolean value) {
        canCastle[code] = value;
    }
    public boolean getCanCastle(int code)
    {
        return canCastle[code];
    }
    ArrayList<Move> mMovesHistory;
    Piece[] mPieces = new Piece[64];
    int mPromotionCount = 0;
    private boolean mIsWhiteTurn = true;
    int enPassantPosition = -1;
    public void setEnPassantPosition(int position) {
        enPassantPosition = position;
    }
    public int getEnPassantPosition() {
        return enPassantPosition;
    }
    public Board(String fen) {
        if (fen == null) {
            fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        }
        mMovesHistory = new ArrayList<>();
        // TODO: remove this when finishing all logics
        String[] fields = fen.split(" ");
        String piecePlaces = fields[0];
        int squareIndex = 0;
        // piecePlaces
        int indexSquare = 0;
        for (char c : piecePlaces.toCharArray()) {
            if (c == '/') continue;
            if (Character.isDigit(c)){
                int emptySquares = Character.getNumericValue(c);
                indexSquare += emptySquares;
            }
            else {
                mPieces[indexSquare] = Piece.createPiece(indexSquare, c, this);
                indexSquare++;
            }
        }
        // white or black turn
        String turnSide = fields[1];
        mIsWhiteTurn = turnSide.equals("w");
        Log.d(TAG, "LAN1" + fen);
        Log.d(TAG, "LAN2" + getFen());
        // TODO: En Passant and castle
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
    public Piece getPiece(int col, int row){
        if (col < 0 || row < 0 || col >= 8 || row >= 8) return null;
        return mPieces[col + row * 8];
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
        Log.d(TAG, "En passant: " + String.valueOf(enPassantPosition));
        move.setEnPassantPosition(enPassantPosition);
        move.setCanCastle(canCastle);
        int rowId = position / 8;
        int colId = position % 8;
        int newRowId = newPosition / 8;
        int newColId = newPosition % 8;
        Piece piece = mPieces[position];
        // Remove piece at position.
        mPieces[position] = null;
        // If the new position contains the piece, then remove it.

        if (piece.getClass() == Pawn.class && enPassantPosition != -1) {
            int enPassantRowId = enPassantPosition / 8;
            int enPassantColId = enPassantPosition % 8;
            if (newColId == enPassantColId && Math.abs(enPassantRowId - newRowId) == 1 && colId != newColId) {
                Log.d(TAG, "En passant " + String.valueOf(newColId) + " " + String.valueOf(newRowId));
                // If the condition is satisfied then this is an en passant move.
                // Then remove piece at en passant position
                move.setCapturedPiece(mPieces[enPassantPosition]);
                mPieces[enPassantPosition] = null;
            }
        }
        if (piece.getClass() == King.class) {
            if (piece.isWhite()) {
                if (rowId == 7 &&
                    rowId == newRowId &&
                    newColId == 6 &&
                    getCanCastle(WHITE_CASTLE_KING) &&
                    mPieces[63] != null) {
                    mPieces[61] = mPieces[63];
                    mPieces[61].setPosition(61);
                    mPieces[63] = null;
                    move.setCastle(WHITE_CASTLE_KING);
                }
                if (rowId == 7 &&
                    rowId == newRowId &&
                    newColId == 2 &&
                    getCanCastle(WHITE_CASTLE_QUEEN) &&
                    mPieces[56] != null) {
                    mPieces[59] = mPieces[56];
                    mPieces[59].setPosition(59);
                    mPieces[56] = null;
                    move.setCastle(WHITE_CASTLE_QUEEN);
                }
            } else {
                if (rowId == 0 &&
                    rowId == newRowId &&
                    newColId == 6 &&
                    getCanCastle(BLACK_CASTLE_KING) &&
                    mPieces[7] != null) {
                    mPieces[5] = mPieces[7];
                    mPieces[5].setPosition(5);
                    mPieces[7] = null;
                    move.setCastle(BLACK_CASTLE_KING);
                }
                if (rowId == 0 &&
                    rowId == newRowId &&
                    newColId == 2 &&
                    getCanCastle(BLACK_CASTLE_QUEEN) &&
                    mPieces[0] != null) {
                    mPieces[3] = mPieces[0];
                    mPieces[3].setPosition(3);
                    mPieces[0] = null;
                    move.setCastle(BLACK_CASTLE_QUEEN);
                }
            }
        }
        if (mPieces[newPosition] != null) {
            move.setCapturedPiece(mPieces[newPosition]);
            mPieces[newPosition] = null;
        }
        // Add piece to new position
        mPieces[newPosition] = piece;
        // Add move to moves history.
        mMovesHistory.add(move);
        mIsWhiteTurn = !mIsWhiteTurn;
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
        return mIsWhiteTurn;
    }

    public Piece promote(int position, String pieceType) {
        Piece oldPiece = mPieces[position];
        if (oldPiece == null) {
            return null;
        }
        String from = mPieces[position].getSymbol();
        // TODO: set promotion to correct pieces
        switch (pieceType) {
            case "q":
                mPieces[position] = new Queen(oldPiece.isWhite(), position, this);
                break;
            case "r":
                mPieces[position] = new Rook(oldPiece.isWhite(), position, this);
                break;
            case "b":
                mPieces[position] = new Bishop(oldPiece.isWhite(), position, this);
                break;
            case "n":
                mPieces[position] = new Knight(oldPiece.isWhite(), position, this);
                break;
        }
        getLastMove().setPromotion(from, pieceType);
        Log.d(TAG, from);
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
        enPassantPosition = move.getEnPassantPosition();
        canCastle = move.getCanCastle();
        switch (move.getCastle()) {
            case WHITE_CASTLE_KING:
                mPieces[63] = mPieces[61];
                mPieces[63].setPosition(63);
                mPieces[61] = null;
                break;
            case WHITE_CASTLE_QUEEN:
                mPieces[56] = mPieces[59];
                mPieces[56].setPosition(56);
                mPieces[59] = null;
                break;
            case BLACK_CASTLE_KING:
                mPieces[7] = mPieces[5];
                mPieces[7].setPosition(7);
                mPieces[5] = null;
                break;
            case BLACK_CASTLE_QUEEN:
                mPieces[0] = mPieces[3];
                mPieces[0].setPosition(0);
                mPieces[3] = null;
                break;
        }
        // Update pieces
        // If there is promotion then reverse it
        if (move.getPromotionFrom() != null) {
            Log.d(TAG, String.valueOf(move.getPromotionFrom()));
            switch (move.getPromotionFrom()) {
                case "p":
                    mPieces[move.getNewPosition()] = new Pawn(
                            mPieces[move.getNewPosition()].isWhite(),
                            move.getNewPosition(),
                            this);
                    break;
                case "q":
                    mPieces[move.getNewPosition()] = new Queen(
                            mPieces[move.getNewPosition()].isWhite(),
                            move.getNewPosition(),
                            this);
                    break;
                case "r":
                    mPieces[move.getNewPosition()] = new Rook(
                            mPieces[move.getNewPosition()].isWhite(),
                            move.getNewPosition(),
                            this);
                    break;
                case "b":
                    mPieces[move.getNewPosition()] = new Bishop(
                            mPieces[move.getNewPosition()].isWhite(),
                            move.getNewPosition(),
                            this);
                    break;
                case "n":
                    mPieces[move.getNewPosition()] = new Knight(
                            mPieces[move.getNewPosition()].isWhite(),
                            move.getNewPosition(),
                            this);
                    break;
            }
            Log.d(TAG, String.valueOf(mPieces[move.getNewPosition()].getClass()));
        }

        // Reverse the move
        mPieces[move.getOldPosition()] = mPieces[move.getNewPosition()];
        // If the move captured a piece, then bring it back
        mPieces[move.getNewPosition()] = null;
        if (move.getCapturedPiece() != null)
            mPieces[move.getCapturedPiece().getPosition()] = move.getCapturedPiece();
        // Update position of piece
        mPieces[move.getOldPosition()].setPosition(move.getOldPosition());
        Log.d(TAG, String.valueOf(move.getPromotionFrom()));
        Log.d(TAG, String.valueOf(move.getPromotionTo()));
        mIsWhiteTurn = !mIsWhiteTurn;
        return move;
    }
    public String getFen() {
        String fen = "";
        int blankCell = 0;
        for (int indexSquare = 0; indexSquare < 64; indexSquare++){
            if (indexSquare != 0 && indexSquare % 8 == 0){
                if (blankCell > 0) fen += Integer.toString(blankCell);
                blankCell = 0;
                fen += '/';
            }
            Piece piece = getPiece(indexSquare);
            if (piece == null) {
                blankCell++;
            }
            else {
                if (blankCell > 0) fen += Integer.toString(blankCell);
                blankCell = 0;
                if (piece instanceof Pawn) fen += (piece.isWhite() ? "P" : "p");
                if (piece instanceof Queen) fen += (piece.isWhite() ? "Q" : "q");
                if (piece instanceof Rook) fen += (piece.isWhite() ? "R" : "r");
                if (piece instanceof King) fen += (piece.isWhite() ? "K" : "k");
                if (piece instanceof Bishop) fen += (piece.isWhite() ? "B" : "b");
                if (piece instanceof Knight) fen += (piece.isWhite() ? "N" : "n");
            }
        }
        if (blankCell > 0) fen += Integer.toString(blankCell);
        if (isWhiteTurn()) fen += " w";
        else fen += " b";
        fen += " - - 0 0";
        return fen;
    }
    public boolean canMove(int oldPosition, int newPosition, boolean isWhite) {
        boolean isValidMove = movePiece(oldPosition, newPosition);
        if (!isValidMove) return false;
        if (isCheck(isWhite)) {
            rollbackLastMove();
            return false;
        }
        else {
            rollbackLastMove();
            return true;
        }
    }

    public boolean isCheck(boolean isWhite) {
        // find King Position
        int kingPosition = 0;
        for (int i = 0; i < 64; i++) {
            Piece piece = getPiece(i);
            // if the King with isWhite
            if (piece != null && piece.getClass() == King.class && piece.isWhite() == isWhite) {
                kingPosition = i;
                break;
            }
        }
        Log.d(TAG, "KING POSITION IS " + String.valueOf(kingPosition));
        int colId = kingPosition % 8;
        int rowId = kingPosition / 8;
        // if king is checked by a knight
        for (int offsetCol = 1; offsetCol <= 2; ++offsetCol) {
            int offsetRow = 3 - offsetCol;
            for (int i = -1; i <= 1; i += 2) {
                for (int j = -1; j <= 1; j += 2) {
                    int x = colId + offsetCol * i;
                    int y = rowId + offsetRow * j;
                    Log.d(TAG, String.valueOf(x) + ' ' + String.valueOf(y));
                    Piece piece = getPiece(x, y);
                    if (piece == null) continue;
                    if (piece.getClass() == Knight.class && piece.isWhite() != isWhite)
                        return true;
                }
            }
        }
        //if king is check by a rook or queen or bishop

        // bishop direct
        int[][] Directions = new int[][]{
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1},
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0},
        };
        for (int[] d : Directions) {
            int x = colId;
            int y = rowId;
            while (true) {
                x += d[0];
                y += d[1];
                if (x < 0 || y < 0 || x >= 8 || y >= 8) break;
                Piece piece = getPiece(x, y);
                if (piece == null ) continue;
                if (piece.isWhite() == isWhite) break;
                // if is checked by a queen
                if (piece.getClass() == Queen.class)
                    return true;
                // if is checked by a rook
                if (piece.getClass() == Rook.class && (d[0] * d[1] == 0)) return true;
                // if is checked by a bishop
                if (piece.getClass() == Bishop.class && (d[0] * d[1] != 0)) return true;
                break;
            }
        }
        // is check by a King
        for (int i = -1 ; i <= 1; i++)
            for (int j = -1; j <= 1; j++){
                int x = colId + i;
                int y = rowId + i;
                Piece piece = getPiece(x, y);
                if (piece == null) continue;
                if (piece.isWhite() != isWhite && piece.getClass() == King.class) return true;
            }
        // is check by a Pawn
        int offset = (isWhite) ? -1 : 1;
        for (int i = -1 ; i <= 1; i+= 2){
            int x = colId + offset;
            int y = rowId + i;
            Piece piece = getPiece(x, y);
            if (piece == null) continue;
            if (piece.isWhite() != isWhite && piece.getClass() == Pawn.class) return true;
        }
        return false;
    }
}
