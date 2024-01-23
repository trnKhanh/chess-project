package com.chessproject.chess.logic;

import android.util.Log;

import com.chessproject.R;

import java.util.ArrayList;

public class King extends Piece{
    final static String TAG = "King";

    public King(boolean white, int position, Board board) {
        super(white, position, board);
    }

    @Override
    public Piece copy() {
        Piece clone = new King(mWhite, mPosition, mBoard);
        return clone;
    }

    @Override
    public int getImageResource(boolean whitePerspective) {
        if (mWhite)
            return whitePerspective ? R.drawable.white_king : R.drawable.white_king_reverse;
        else
            return whitePerspective ? R.drawable.black_king : R.drawable.black_king_reverse;
    }
    public String getSymbol(){
        return "k";
    }

    @Override
    public ArrayList<Integer> getLegalMoves() {

        ArrayList<Integer> legalMoves = new ArrayList<>();
        if (mBoard.isWhiteTurn() != isWhite()) return legalMoves;
        int colId = mPosition % 8;
        int rowId = mPosition / 8;

        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0) continue;
                int x = colId + i;
                int y = rowId + j;
                if (x < 0 || y < 0 || x >= 8 || y >= 8) continue;
                Piece piece = mBoard.getPiece(x + y * 8);
                if (piece != null && mBoard.getPiece(y * 8 + x).isWhite() == isWhite())
                    continue;
                if (mBoard.canMove(mPosition, x + 8 * y, isWhite()))
                    legalMoves.add(x + 8 * y);
            }
        return legalMoves;
    }
}
