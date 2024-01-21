package com.chessproject.ui.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chessproject.R;
import com.chessproject.chess.logic.Board;
import com.chessproject.chess.ui.BoardView;
import com.chessproject.databinding.FragmentPuzzleBinding;
import com.chessproject.dataset.PuzzleDataset;


public class PuzzleFragment extends Fragment {
    final static String TAG = "PuzzleFragment";
    FragmentPuzzleBinding binding;
    PuzzleDataset.Puzzle mCurPuzzle;
    AlertDialog mFinishDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPuzzleBinding.inflate(inflater, container, false);
        mFinishDialog = (new AlertDialog.Builder(getContext()))
                .setTitle(getContext().getResources().getString(R.string.congratulation))
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        binding.chessboard.setFinishedMoveListener(new BoardView.FinishedMoveListener() {
            @Override
            public void onFinishMove(Board.Move move) {
                if (move == null)
                    return;
                Board.Move correctMove = mCurPuzzle.getCurrentMove();
                Log.d(TAG, String.valueOf(move));
                if (correctMove.equal(move)) {
                    binding.chessboard.setLastMoveEvaluation(BoardView.CORRECT_MOVE);
                    Board.Move nextMove = mCurPuzzle.nextMove();
                    if (nextMove == null) {
                        mFinishDialog.show();
                    } else {
                        binding.chessboard.movePiece(mCurPuzzle.nextMove());
                    }
                } else {
                    binding.chessboard.setLastMoveEvaluation(BoardView.WRONG_MOVE);
                    binding.chessboard.toggleDisabled();
                    binding.retryButton.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.retryButton.setVisibility(View.GONE);
        binding.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chessboard.rollbackLastMove();
                binding.chessboard.toggleDisabled();
                binding.retryButton.setVisibility(View.GONE);
            }
        });
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPuzzle();
            }
        });
        return binding.getRoot();
    }
    void startPuzzle() {
        mCurPuzzle = PuzzleDataset.getInstance(getContext()).nextPuzzle();
        Log.d(TAG, mCurPuzzle.getFen());
        Log.d(TAG, String.valueOf(mCurPuzzle.getCurrentMove().getNewPosition()));
        binding.chessboard.setFen(mCurPuzzle.getFen());
        Board.Move move = mCurPuzzle.getCurrentMove();
        mCurPuzzle.nextMove();
        binding.chessboard.movePiece(move);
    }
}
