package com.chessproject.ui.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

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
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(tutorialContainer);
        tutorialContainer.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void initData() {
        super.initData();
        tutorialSlide = new ArrayList<>();
        tutorialSlide.add(new TutorialInfo("STEP 1/3: Capture the image of a chessboard.", R.drawable.detection_tutorial_step_1));
        tutorialSlide.add(new TutorialInfo("STEP 2/3: Choose which side is going next and perspective of the chessboard (from white or from black side) and click Continue.", R.drawable.detection_tutorial_step_2));
        tutorialSlide.add(new TutorialInfo("STEP 3/3: Get the result.", R.drawable.detection_tutorial_step_3));
    }
}
