package com.chessproject.ui.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.chessproject.R;
import com.chessproject.databinding.FragmentGamesBinding;

public class GamesFragment extends Fragment {
    private FragmentGamesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GamesViewModel notificationsViewModel =
                new ViewModelProvider(this).get(GamesViewModel.class);

        binding = FragmentGamesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        NavController navController = NavHostFragment.findNavController(this);
        binding.evaluationGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navigation_evaluation_game);
            }
        });
        binding.puzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navigation_puzzle);
            }
        });
        binding.blindPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navigation_blind_puzzle);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}