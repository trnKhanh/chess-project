package com.chessproject.dataset;

import android.util.Pair;

import java.util.ArrayList;

public class EvaluationGameDataset {
    // 1 record is a pair of 2 values: fen and answer/which side is winning
    ArrayList<Pair<String, String>> mFens = new ArrayList<>();
    int mCurrentFenId = 0;
    EvaluationGameDataset() {
        // TODO: Read from file assets
    }
    static EvaluationGameDataset mInstance = null;
    public static EvaluationGameDataset getInstance() {
        if (mInstance == null)
            mInstance = new EvaluationGameDataset();
        return mInstance;
    }
    public Pair<String, String> nextRecord() {
        if (mFens.size() == 0)
            return null;
        Pair<String, String> fen = mFens.get(mCurrentFenId);
        mCurrentFenId++;
        mCurrentFenId %= mFens.size();

        return fen;
    }
}
