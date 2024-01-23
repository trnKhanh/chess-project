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

import java.util.ArrayList;

public class HelpEvaluationGameFragment extends HelpFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help_evaluation_game, container, false);
        return root;
    }
}
