package com.chessproject.ui.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chessproject.MyApplication;
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
                startPuzzle();
            }
        });
        return binding.getRoot();
    }
    void startPuzzle() {
        binding.retryButton.setVisibility(View.GONE);
        binding.chessboard.setDisabled(false);

        mCurPuzzle = PuzzleDataset.getInstance(getContext()).nextPuzzle();
        binding.chessboard.setFen(mCurPuzzle.getFen());
        binding.chessboard.setLastMoveEvaluation(0,-1);
        binding.chessboard.setPerspective(mCurPuzzle.isWhiteToMove());
        if (mCurPuzzle.isWhiteToMove()) {
            binding.sideToMove.setText(R.string.you_are_white);
        } else {
            binding.sideToMove.setText(R.string.you_are_black);
        }
        Handler mainHandler = ((MyApplication)(getActivity().getApplication())).getMainHandler();
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Move current move and go to next move
                Board.Move move = mCurPuzzle.getCurrentMove();
                mCurPuzzle.nextMove();
                Log.d(TAG, String.valueOf(move.getOldPosition()));
                Log.d(TAG, String.valueOf(move.getNewPosition()));
                binding.chessboard.movePiece(move);
            }
        }, 100);

    }
}
