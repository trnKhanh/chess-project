package com.chessproject.detection.services;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SegmentationService {
    @POST("chessboard-segmentation/1?api_key=" + ServiceConfig.ROBOFLOW_API_KEY)
    Call<ResponseBody> detectChessboard(@Body RequestBody body);
}
