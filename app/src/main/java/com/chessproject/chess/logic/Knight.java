package com.chessproject.chess.logic;

import com.chessproject.R;

import java.util.ArrayList;

public class Knight extends Piece{
    final static String TAG = "Knight";

    public Knight(boolean white, int position, Board board) {
        super(white, position, board);
    }

    @Override
    public Piece copy() {
        Piece clone = new Knight(mWhite, mPosition, mBoard);
        return clone;
    }

    @Override
    public int getImageResource(boolean whitePerspective) {
        if (mWhite)
            return whitePerspective ? R.drawable.white_knight : R.drawable.white_knight_reverse;
        else
            return whitePerspective ? R.drawable.black_knight : R.drawable.black_knight_reverse;
    }
    public String getSymbol(){
        return "n";
    }

    @Override
    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> legalMoves = new ArrayList<>();
        if (mBoard.isWhiteTurn() != isWhite()) return legalMoves;
        int colId = mPosition % 8;
        int rowId = mPosition / 8;

        for (int offsetCol = 1; offsetCol <= 2; ++offsetCol) {
            int offsetRow = 3 - offsetCol;
            for (int i = -1; i <= 1; i += 2) {
                for (int j = -1; j <= 1; j += 2) {
                    int x = colId + offsetCol * i;
                    int y = rowId + offsetRow * j;
                    Piece piece = mBoard.getPiece(x + y * 8);
                    if (piece != null && mBoard.getPiece(y * 8 + x).isWhite() == isWhite())
                        continue;

                    if (x >= 0 && y >= 0 && x < 8 && y < 8) {
                        if (mBoard.canMove(mPosition, x + 8 * y, isWhite()))
                            legalMoves.add(y * 8 + x);
                    }
                }
            }
        }
        return legalMoves;
    }
}
