package com.chessproject.dataset;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.chessproject.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EvaluationGameDataset {
    // 1 record is a pair of 2 values: fen and answer/which side is winning
    static final String TAG = "EvaluationGameDataset";
    ArrayList<Pair<String, String>> mFens = new ArrayList<>();
    Random random = new Random();
    EvaluationGameDataset(Context context) {
        ArrayList<HashMap<String, String>> evalGameRecords = FileUtils.readCSV(context, "FenDatasetEvaBar.csv");
        for (HashMap<String, String> record: evalGameRecords) {
            mFens.add(new Pair<>(record.get("fen"), record.get("isWining")));
        }
        Log.d(TAG, String.valueOf(mFens.size()));
    }
    static EvaluationGameDataset mInstance = null;
    public static EvaluationGameDataset getInstance(Context context) {
        if (mInstance == null)
            mInstance = new EvaluationGameDataset(context);
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
