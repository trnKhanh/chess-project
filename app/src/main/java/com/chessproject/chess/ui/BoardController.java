package com.chessproject.chess.ui;

public interface BoardController {
    boolean setSelectedPiece(PieceView pieceview);
    void finishMove(int position);
}
