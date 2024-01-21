package com.chessproject.dataset;

import android.content.Context;

import com.chessproject.chess.logic.Board;
import com.chessproject.chess.logic.Knight;
import com.chessproject.chess.logic.Piece;
import com.chessproject.utils.ChessUtils;
import com.chessproject.utils.FileUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PuzzleDataset {
    public static class Puzzle {
        String mFen;
        int mMoveId = 0;
        ArrayList<Board.Move> mMoves;
        boolean mWhiteToMove;
        public Puzzle(String fen, ArrayList<Board.Move> moves, boolean whiteToMove) {
            mFen = fen;
            mMoves = moves;
            mWhiteToMove = whiteToMove;
        }
        public String getFen() {
            return mFen;
        }
        public ArrayList<Board.Move> getMoves() {
            return mMoves;
        }
        public Board.Move nextMove() {
            if (mMoveId + 1 >= mMoves.size())
                return null;
            mMoveId++;
            return mMoves.get(mMoveId);
        }
        public Board.Move getCurrentMove() {
            if (mMoveId >= mMoves.size())
                return null;
            return mMoves.get(mMoveId);
        }
        public boolean isWhiteToMove() {
            return mWhiteToMove;
        }
    }
    ArrayList<Puzzle> mPuzzles;
    int curPuzzleId = 0;
    static PuzzleDataset mInstance;
    PuzzleDataset(Context context) {
        // TODO: Read dataset here
        ArrayList<HashMap<String, String>> puzzleRecords = FileUtils.readCSV(context, "lichess_db_puzzle.csv");

        mPuzzles = new ArrayList<>();
        for (HashMap<String, String> puzzleRecord: puzzleRecords) {
            String fen = puzzleRecord.get("FEN");
            Board board = new Board(fen);

            String[] moveStrings = puzzleRecord.get("Moves").split(",");
            boolean whiteTurn = board.isWhiteTurn();

            ArrayList<Board.Move> moves = new ArrayList<>();
            for (String moveString: moveStrings) {
                int oldPosition = ChessUtils.getPosition(moveString.substring(0, 2));
                int newPosition = ChessUtils.getPosition(moveString.substring(2, 4));
                Board.Move move = new Board.Move(oldPosition, newPosition);
                if(moveString.length() > 4) {
                    String promotionTo = moveString.substring(4, 5);
                    move.setPromotion("p", promotionTo);
                }
                moves.add(move);
            }
            mPuzzles.add(new Puzzle(fen, moves, whiteTurn));
        }
    }
    public static PuzzleDataset getInstance(Context context) {
        if (mInstance == null)
            mInstance = new PuzzleDataset(context);
        return mInstance;
    }
    public Puzzle nextPuzzle() {
        if (mPuzzles.isEmpty())
            return null;
        int id = curPuzzleId;
        curPuzzleId++;
        curPuzzleId %= mPuzzles.size();
        return mPuzzles.get(id);
    }
}
