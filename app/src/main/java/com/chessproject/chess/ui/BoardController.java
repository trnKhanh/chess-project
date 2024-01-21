package com.chessproject.chess.ui;

public interface BoardController {
    boolean setSelectedPiece(int position, boolean preserved);
    void placeSelectedPiece(int position);
    void setSelectedCell(int pos);
    void promoteSelectedPiece(String pieceType);
    void rollbackLastMove();
}
