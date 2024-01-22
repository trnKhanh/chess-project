package com.chessproject.ui.games;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chessproject.databinding.FragmentEvaluationGameBinding;
import com.chessproject.dataset.EvaluationGameDataset;

public class EvaluationGameFragment extends Fragment {
    private FragmentEvaluationGameBinding binding;
    Pair<String, String> mCurrentRecord = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEvaluationGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.whiteButton.setTag("w");
        binding.blackButton.setTag("b");
        binding.drawButton.setTag("d");
        binding.chessboard.toggleDisabled();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String choice = (String) v.getTag();
                if (mCurrentRecord != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setCancelable(true)
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mCurrentRecord = EvaluationGameDataset.getInstance().nextRecord();
                                    if (mCurrentRecord != null)
                                        binding.chessboard.setFen(mCurrentRecord.first);
                                    dialog.cancel();
                                }
                            });
                    if (mCurrentRecord.second == choice) {
                        builder.setTitle("Correct").show();
                    } else {
                        builder.setTitle("Wrong").show();
                    }
                }
            }
        };
        binding.blackButton.setOnClickListener(listener);
        binding.whiteButton.setOnClickListener(listener);
        binding.drawButton.setOnClickListener(listener);
        return root;
    }
}
