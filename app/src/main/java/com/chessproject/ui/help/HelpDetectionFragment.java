package com.chessproject.ui.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chessproject.R;
import com.chessproject.adapter.TutorialPagerAdapter;
import com.chessproject.entity.TutorialInfo;

import java.util.ArrayList;

public class HelpDetectionFragment extends HelpFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help_detection, container, false);
        initializeUI(root);
        return root;
    }

    @Override
    protected void initializeUI(View root) {
        super.initializeUI(root);
        tutorialContainer = root.findViewById(R.id.image_tutorial_container_detection);
        initData();
        adapter = new TutorialPagerAdapter(requireContext(), tutorialSlide);
        tutorialContainer.setAdapter(adapter);
        tutorialContainer.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void initData() {
        super.initData();
        tutorialSlide = new ArrayList<>();
        tutorialSlide.add(new TutorialInfo("STEP 1/2: ", R.drawable.chess_vision_image));
        tutorialSlide.add(new TutorialInfo("STEP 2/2: ", R.drawable.black_bishop));
    }
}
