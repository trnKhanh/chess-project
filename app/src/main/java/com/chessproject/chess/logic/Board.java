package com.chessproject.chess.logic;

import android.graphics.Point;

public class Board {
    private Piece[][] pieces = new Piece[8][8];

    public Piece getPiece(int x, int y){
        return this.pieces[x][y];
    }

}
