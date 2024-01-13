package com.chessproject.chess.ui;

public interface BoardController {
    PieceView mSelectedPieceView = null;
    boolean setSelectedPiece(PieceView pieceview, boolean preserved);
    void placeSelectedPiece(int position);
    void setSelectedCell(int pos);
    void promoteSelectedPiece(String pieceType);
}
