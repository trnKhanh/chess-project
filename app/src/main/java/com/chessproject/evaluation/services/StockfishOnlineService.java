package com.chessproject.evaluation.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockfishOnlineService {
    @GET("api/stockfish.php?mode=eval")
    Call<ResponseBody> getEvaluation(@Query("fen") String fen, @Query("depth") int depth);

    @GET("api/stockfish.php?mode=bestmove")
    Call<ResponseBody> getBestMove(@Query("fen") String fen, @Query("depth") int depth);

    @GET("api/stockfish.php?mode=lines")
    Call<ResponseBody> getTopLines(@Query("fen") String fen, @Query("depth") int depth);
}
