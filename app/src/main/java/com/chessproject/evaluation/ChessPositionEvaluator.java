package com.chessproject.evaluation;

import android.util.Log;
import android.util.Pair;

import com.chessproject.utils.ChessUtils;
import com.chessproject.evaluation.services.StockfishOnlineService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChessPositionEvaluator {
    final static String TAG = "ChessPositionEvaluator";
    private String mFen;
    private StockfishOnlineService service;
    public ChessPositionEvaluator(String fen) {
        mFen = fen;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://stockfish.online/")
                .build();
        service = retrofit.create(StockfishOnlineService.class);
    }

    public float getEvaluation() {
        try {
            Response<ResponseBody> response = service.getEvaluation(mFen, 13).execute();
            ResponseBody body = response.body();
            JSONObject data = null;
            if (body != null) {
                data = new JSONObject(body.string());
                String evaluationString = data.getString("data");

                Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
                Matcher matcher = pattern.matcher(evaluationString);
                if (matcher.find()) {
                    float eval = Float.parseFloat(matcher.group(0));
                    return eval;
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Chess position evaluation failed: " + e.getMessage());
        }
        return 0;
    }
    public Pair<Integer, Integer> getBestMove() {
        try {
            Response<ResponseBody> response = service.getBestMove(mFen, 13).execute();
            ResponseBody body = response.body();
            JSONObject data = null;
            if (body != null) {
                data = new JSONObject(body.string());
                String bestMoveString = data.getString("data");

                String bestMove = bestMoveString.substring(9, 13);
                int fromPosition = ChessUtils.getPosition(bestMove.substring(0, 2));
                int toPosition = ChessUtils.getPosition(bestMove.substring(2, 4));
                return new Pair<>(fromPosition, toPosition);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Chess position get best move failed: " + e.getMessage());
        }
        return null;
    }
}
