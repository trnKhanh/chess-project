package com.chessproject.ui.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chessproject.databinding.FragmentChessboardBinding;

public class ChessboardFragment extends Fragment {
    FragmentChessboardBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChessboardBinding.inflate(inflater, container, false);

        binding.rollback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chessboard.rollbackLastMove();
            }
        });

        return binding.getRoot();
    }
}
