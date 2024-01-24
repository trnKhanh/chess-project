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
        if (!mBoard.isCheck(isWhite())) {
            if (isWhite()) {
                if (mBoard.getCanCastle(Board.WHITE_CASTLE_KING)) {
                    if (mBoard.getPiece(61) == null &&
                            mBoard.getPiece(62) == null &&
                            legalMoves.contains(61) &&
                            mBoard.canMove(mPosition, 62, isWhite())) {
                        legalMoves.add(62);
                    }
                }
                if (mBoard.getCanCastle(Board.WHITE_CASTLE_QUEEN)) {
                    if (mBoard.getPiece(59) == null &&
                            mBoard.getPiece(58) == null &&
                            legalMoves.contains(59) && mBoard.canMove(mPosition, 58, isWhite())) {
                        legalMoves.add(58);
                    }
                }
            } else {
                if (mBoard.getCanCastle(Board.BLACK_CASTLE_KING)) {
                    if (mBoard.getPiece(5) == null &&
                            mBoard.getPiece(6) == null &&
                            legalMoves.contains(5) && mBoard.canMove(mPosition, 6, isWhite())) {
                        legalMoves.add(6);
                    }
                }
                if (mBoard.getCanCastle(Board.BLACK_CASTLE_QUEEN)) {
                    if (mBoard.getPiece(2) == null &&
                            mBoard.getPiece(3) == null &&
                            legalMoves.contains(3) && mBoard.canMove(mPosition, 2, isWhite())) {
                        legalMoves.add(2);
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public boolean moveTo(int newPosition) {
        if (!super.moveTo(newPosition))
            return false;
        if (isWhite()) {
            mBoard.setCanCastle(Board.WHITE_CASTLE_KING, false);
            mBoard.setCanCastle(Board.WHITE_CASTLE_QUEEN, false);
        } else {
            mBoard.setCanCastle(Board.BLACK_CASTLE_QUEEN, false);
            mBoard.setCanCastle(Board.BLACK_CASTLE_KING, false);
        }
        return true;
    }
}
