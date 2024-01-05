package com.chessproject.chess.logic;

import android.util.Log;

import com.chessproject.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Knight extends Piece{
    final static String TAG = "Knight";

    Knight(boolean white, int position, Board board) {
        super(white, position, board);
    }

    @Override
    public int getImageResource() {
        Log.d(TAG, String.valueOf(mWhite));
        return mWhite ? R.drawable.white_knight : R.drawable.black_knight;
    }

    @Override
    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> legalMoves = new ArrayList<>();
        int colId = mPosition % 8;
        int rowId = mPosition / 8;

        for (int offsetCol = 1; offsetCol <= 2; ++offsetCol) {
            int offsetRow = 3 - offsetCol;
            for (int i = -1; i <= 1; i += 2) {
                for (int j = -1; j <= 1; j += 2) {
                    int x = colId + offsetCol * i;
                    int y = rowId + offsetRow * j;
                    if (x >= 0 && y >= 0 && x < 8 && y < 8) {
                        legalMoves.add(y * 8 + x);
                    }
                }
            }
        }
        return legalMoves;
    }
}
