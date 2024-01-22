package com.chessproject.chess.logic;

import com.chessproject.R;

import java.util.ArrayList;

public class Queen extends Piece{
    final static String TAG = "Queen";

    public Queen(boolean white, int position, Board board) {
        super(white, position, board);
    }

    @Override
    public Piece copy() {
        Piece clone = new Queen(mWhite, mPosition, mBoard);
        return clone;
    }

    @Override
    public int getImageResource() {
        return mWhite ? R.drawable.white_queen : R.drawable.black_queen;
    }
    public String getSymbol(){
        if (isWhite()) return "Q";
        return "q";
    }

    @Override
    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> legalMoves = new ArrayList<>();
        if (mBoard.isWhiteTurn() != isWhite()) return legalMoves;
        int colId = mPosition % 8;
        int rowId = mPosition / 8;

        int[][] directions = new int[][]{
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1},
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
                    if (mBoard.getPiece(y * 8 + x).isWhite() != isWhite()) legalMoves.add(x + 8 * y);
                    break;
                }
                legalMoves.add(x + 8 * y);
            }
        }
        return legalMoves;
    }
}
