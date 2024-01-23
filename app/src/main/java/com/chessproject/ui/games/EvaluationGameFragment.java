package com.chessproject.ui.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chessproject.R;
import com.chessproject.databinding.FragmentEvaluationGameBinding;
import com.chessproject.dataset.EvaluationGameDataset;

public class EvaluationGameFragment extends Fragment {
    final static String TAG = "EvaluationGameFragment";
    private FragmentEvaluationGameBinding binding;
    Pair<String, String> mCurrentRecord = null;
    TextView resultTextView;
    AlertDialog alertDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEvaluationGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.whiteButton.setTag("White");
        binding.blackButton.setTag("Black");
        binding.drawButton.setTag("Draw");
        binding.chessboard.toggleDisabled();
        resultTextView = new TextView(requireContext());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        startGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = alertDialogBuilder.setView(resultTextView).create();

        resultTextView.setText("Correct");
        resultTextView.setTextSize(30);
        resultTextView.setBackgroundColor(requireContext().getColor(R.color.success_500));
        resultTextView.setPadding(20, 20, 20, 20);
        resultTextView.setTextColor(Color.WHITE);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String choice = (String) v.getTag();
                if (mCurrentRecord != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setCancelable(true)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    startGame();
                                }
                            });
                    if (mCurrentRecord.second.compareTo(choice) == 0) {
                        resultTextView.setText("Correct");
                        resultTextView.setBackgroundColor(requireContext().getColor(R.color.success_500));
                    } else {
                        resultTextView.setText("Wrong");
                        resultTextView.setBackgroundColor(requireContext().getColor(R.color.error_500));
                    }
                    alertDialog.show();
                }
            }
        };
        binding.blackButton.setOnClickListener(listener);
        binding.whiteButton.setOnClickListener(listener);
        binding.drawButton.setOnClickListener(listener);
        startGame();
        return root;
    }

    void startGame() {
        mCurrentRecord = EvaluationGameDataset.getInstance(requireContext()).nextRecord();
        binding.chessboard.setFen(mCurrentRecord.first);

        if (binding.chessboard.getBoard().isWhiteTurn()) {
            binding.sideToMove.setText("White to move");
            binding.sideToMoveIcon.setBackground(requireContext().getDrawable(R.drawable.white_side));
        } else {
            binding.sideToMove.setText("Black to move");
            binding.sideToMoveIcon.setBackground(requireContext().getDrawable(R.drawable.black_side));
        }
        Log.d(TAG, mCurrentRecord.second);
    }
}
