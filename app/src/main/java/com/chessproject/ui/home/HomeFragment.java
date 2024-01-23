package com.chessproject.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.chessproject.R;
import com.chessproject.databinding.FragmentCameraBinding;
import com.chessproject.databinding.FragmentHomeBinding;
import com.chessproject.ui.detector.CameraFragment;
import com.chessproject.ui.games.GamesFragment;

public class HomeFragment extends Fragment{
    final static String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    NavController navController;

    Button buttonStartDetection;
    LinearLayout exploreGameLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        buttonStartDetection = root.findViewById(R.id.button_start_detection);
        exploreGameLayout = root.findViewById(R.id.container_game);
        navController = NavHostFragment.findNavController(this);
        setupListener();
        return root;
    }

    private void setupListener() {
        buttonStartDetection.setOnClickListener(view -> {
            navController.navigate(R.id.navigation_camera);
        });
        exploreGameLayout.setOnClickListener(view -> {
            navController.navigate(R.id.navigation_games);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}