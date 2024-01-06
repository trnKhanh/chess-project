package com.chessproject.chess.ui;

public interface BoardController {
    PieceView mSelectedPieceView = null;
    boolean setSelectedPiece(PieceView pieceview);
    void placeSelectedPiece(int position);
}
