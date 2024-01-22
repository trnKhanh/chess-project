package com.chessproject.chess.logic;

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
    public int getImageResource() {
        return mWhite ? R.drawable.white_king : R.drawable.black_king;
    }
    public String getSymbol(){
        if (isWhite()) return "K";
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
//                mBoard.movePiece(mPosition, x + 8 * y);
//                boolean kt = mBoard.isCheck();
//                mBoard.rollbackLastMove();
                legalMoves.add(x + 8 * y);
            }
        return legalMoves;
    }
}
