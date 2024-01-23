package com.chessproject.ui.help;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.chessproject.adapter.TutorialPagerAdapter;
import com.chessproject.entity.TutorialInfo;

import java.util.ArrayList;

public class HelpFragment extends Fragment {
    protected ArrayList <TutorialInfo> tutorialSlide;
    protected RecyclerView tutorialContainer;
    protected TutorialPagerAdapter adapter;

    protected void initData(){}
    protected void initializeUI(View root){}
}
