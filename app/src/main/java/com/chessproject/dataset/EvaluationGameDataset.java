package com.chessproject.dataset;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class EvaluationGameDataset {
    // 1 record is a pair of 2 values: fen and answer/which side is winning
    ArrayList<Pair<String, String>> mFens = new ArrayList<>();
    Random random = new Random();
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
        int id = random.nextInt();
        id %= mFens.size();
        if (id < 0)
            id += mFens.size();
        return mFens.get(id);
    }
}
