package com.chessproject.ui.detector;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetectorViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> capturedImage = new MutableLiveData<>();
    private final MutableLiveData<String> mFen = new MutableLiveData<>();

    public void setCapturedImage(Bitmap bitmap) {
        capturedImage.setValue(bitmap);
    }
    public void setFen(String fen) {
        mFen.setValue(fen);
    }

    public LiveData<Bitmap> getCapturedImage() {
        return capturedImage;
    }
    public LiveData<String> getFen() {
        return mFen;
    }
}