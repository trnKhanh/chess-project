package com.chessproject.ui.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chessproject.R;
import com.chessproject.chess.logic.Board;
import com.chessproject.chess.ui.BoardView;
import com.chessproject.databinding.FragmentBlindPuzzleBinding;
import com.chessproject.dataset.PuzzleDataset;


public class BlindPuzzleFragment extends Fragment {
    final static String TAG = "HiddenPuzzleFragment";
    FragmentBlindPuzzleBinding binding;
    PuzzleDataset.Puzzle mCurPuzzle;
    AlertDialog mFinishDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBlindPuzzleBinding.inflate(inflater, container, false);
        mFinishDialog = (new AlertDialog.Builder(getContext()))
                .setTitle(getContext().getResources().getString(R.string.congratulation))
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.chessboard.setHidden(false);
                        binding.chessboard.setDisabled(true);
                        binding.readyButton.setVisibility(View.VISIBLE);
                        startPuzzle();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        startPuzzle();
        binding.chessboard.setDisabled(true);
        binding.chessboard.setFinishedMoveListener(new BoardView.FinishedMoveListener() {
            @Override
            public void onFinishMove(Board.Move move) {
                if (move == null || binding.chessboard.getBoard().isWhiteTurn() == mCurPuzzle.isWhiteToMove())
                    return;
                Board.Move correctMove = mCurPuzzle.getCurrentMove();
                if (correctMove.equal(move)) {
                    // If user is correct
                    // then set last move evaluation to correct
                    binding.chessboard.setLastMoveEvaluation(move.getNewPosition(), BoardView.CORRECT_MOVE);
                    Board.Move nextMove = mCurPuzzle.nextMove();
                    if (nextMove == null) {
                        mFinishDialog.show();
                    } else {
                        binding.chessboard.movePiece(nextMove);
                        mCurPuzzle.nextMove();
                    }
                } else {
                    binding.chessboard.setLastMoveEvaluation(move.getNewPosition(), BoardView.WRONG_MOVE);
                    binding.chessboard.setDisabled(true);
                    binding.retryButton.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.readyButton.setVisibility(View.GONE);
                binding.chessboard.setHidden(true);
                binding.chessboard.setDisabled(false);
                startMove();
            }
        });
        binding.retryButton.setVisibility(View.GONE);
        binding.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chessboard.rollbackLastMove();
                binding.chessboard.setDisabled(false);
                binding.retryButton.setVisibility(View.GONE);
            }
        });
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chessboard.setHidden(false);
                binding.chessboard.setDisabled(true);
                binding.readyButton.setVisibility(View.VISIBLE);
                startPuzzle();
            }
        });
        return binding.getRoot();
    }
    void startPuzzle() {
        mCurPuzzle = PuzzleDataset.getInstance(getContext()).nextPuzzle();

        binding.chessboard.setFen(mCurPuzzle.getFen());
        binding.chessboard.setLastMoveEvaluation(0,-1);
    }
    void startMove() {
        // Move current move and go to next move
        Board.Move move = mCurPuzzle.getCurrentMove();
        mCurPuzzle.nextMove();
        binding.chessboard.movePiece(move);
    }
}
