package com.chessproject.ui.camera;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> capturedImage = new MutableLiveData<>();

    public void setCapturedImage(Bitmap bitmap) {
        capturedImage.setValue(bitmap);
    }

    public LiveData<Bitmap> getCapturedImage() {
        return capturedImage;
    }
}