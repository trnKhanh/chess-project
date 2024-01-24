package com.chessproject.chess.logic;

import com.chessproject.R;

import java.util.ArrayList;

public class Rook extends Piece{
    final static String TAG = "Rook";

    public Rook(boolean white, int position, Board board) {
        super(white, position, board);
    }

    @Override
    public Piece copy() {
        Piece clone = new Rook(mWhite, mPosition, mBoard);
        return clone;
    }

    @Override
    public int getImageResource(boolean whitePerspective) {
        if (mWhite)
            return whitePerspective ? R.drawable.white_rook : R.drawable.white_rook_reverse;
        else
            return whitePerspective ? R.drawable.black_rook : R.drawable.black_rook_reverse;
    }
    public String getSymbol(){
        return "r";
    }

    @Override
    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> legalMoves = new ArrayList<>();
        if (mBoard.isWhiteTurn() != isWhite()) return legalMoves;
        int colId = mPosition % 8;
        int rowId = mPosition / 8;

        int[][] directions = new int[][]{
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0},
        };
        for (int[] d: directions){
            int x = colId;
            int y = rowId;
            while (true){
                x += d[0];
                y += d[1];
                if (x < 0 || y < 0 || x >= 8 || y >= 8) break;
                Piece piece = mBoard.getPiece(x + y * 8);
                if (piece != null){
                    if (mBoard.getPiece(y * 8 + x).isWhite() != isWhite())
                        if (mBoard.canMove(mPosition, x + 8 * y, isWhite()))
                            legalMoves.add(x + 8 * y);
                    break;
                }
                if (mBoard.canMove(mPosition, x + 8 * y, isWhite()))
                    legalMoves.add(x + 8 * y);
            }
        }
        return legalMoves;
    }

    @Override
    public boolean moveTo(int newPosition) {
        int oldPosition = mPosition;
        if (!super.moveTo(newPosition))
            return false;
        if (isWhite()) {
            if (oldPosition == 56) {
                mBoard.setCanCastle(Board.WHITE_CASTLE_QUEEN, false);
            }
            if (oldPosition == 63) {
                mBoard.setCanCastle(Board.WHITE_CASTLE_KING, false);
            }
        } else {
            if (oldPosition == 0) {
                mBoard.setCanCastle(Board.BLACK_CASTLE_QUEEN, false);
            }
            if (oldPosition == 7) {
                mBoard.setCanCastle(Board.BLACK_CASTLE_KING, false);
            }
        }
        return true;
    }
}
