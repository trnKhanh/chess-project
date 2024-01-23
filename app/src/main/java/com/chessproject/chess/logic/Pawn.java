package com.chessproject.chess.logic;

import com.chessproject.R;

import java.util.ArrayList;

public class Pawn extends Piece{
    final static String TAG = "Pawn";

    public Pawn(boolean white, int position, Board board) {
        super(white, position, board);
    }


    @Override
    public Piece copy() {
        Piece clone = new Pawn(mWhite, mPosition, mBoard);
        return clone;
    }

    @Override
    public int getImageResource(boolean whitePerspective) {
        if (mWhite)
            return whitePerspective ? R.drawable.white_pawn : R.drawable.white_pawn_reverse;
        else
            return whitePerspective ? R.drawable.black_pawn : R.drawable.black_pawn_reverse;
    }
    public String getSymbol(){
        return "p";
    }

    @Override
    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> legalMoves = new ArrayList<>();
        if (mBoard.isWhiteTurn() != isWhite()) return legalMoves;
        int colId = mPosition % 8;
        int rowId = mPosition / 8;
        int direction;
        if (isWhite() == true) direction = -1;
        else direction = 1;

        for (int i = 1; i <= 2; i++) {
            if (isWhite() == true){
                if (i == 2 && rowId != 6 ) break;
            }
            if (isWhite() == false){
                if (i == 2 && rowId != 1 ) break;
            }


            int x = colId;
            int y = rowId + i * direction;
            Piece piece = mBoard.getPiece(x + y * 8);
            if (piece != null) break;
            if (mBoard.canMove(mPosition, x + 8 * y, isWhite()))
                legalMoves.add(x + 8 * y);
        }
        int x = colId;
        int y = rowId + direction;
        if (x - 1 >= 0)
        {
            Piece piece = mBoard.getPiece(x - 1 + y * 8);
            if (piece != null && mBoard.getPiece(x - 1 + y * 8).isWhite() != isWhite())
                if (mBoard.canMove(mPosition, x - 1 + 8 * y, isWhite()))
                    legalMoves.add(x - 1 + 8 * y);
        }
        if (x + 1 >= 0)
        {
            Piece piece = mBoard.getPiece(x + 1 + y * 8);
            if (piece != null && mBoard.getPiece(x + 1 + y * 8).isWhite() != isWhite())
                if (mBoard.canMove(mPosition, x + 1 + 8 * y, isWhite()))
                    legalMoves.add(x + 1 + 8 * y);
        }

        return legalMoves;
    }
}
