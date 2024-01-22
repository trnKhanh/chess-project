package com.chessproject.ui.camera;

import static com.chessproject.utils.ImageUtils.imageToBitmap;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.chessproject.R;
import com.chessproject.databinding.FragmentCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CameraViewModel cameraViewModel;
    ImageView captureButton;
    private PreviewView previewView;
    private LifecycleCameraController cameraController;
    FragmentCameraBinding viewBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraViewModel = new ViewModelProvider(requireActivity()).get(CameraViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = viewBinding.getRoot();
        return root;
    }

    private void setupListener() {
        captureButton.setOnClickListener(view -> {
            captureImage();
        });
    }

    private void setupCamera() {
        cameraController = new LifecycleCameraController(requireContext());
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        captureButton = viewBinding.captureButton;
        previewView = viewBinding.previewView;
        setupCamera();
        setupListener();
    }

    private void captureImage() {
        if (cameraController != null) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraController.takePicture(ContextCompat.getMainExecutor(requireContext()),
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                                Bitmap bitmap = imageToBitmap(imageProxy);
                                cameraViewModel.setCapturedImage(bitmap);
                                replaceWithPrepareImageFragment();
                                imageProxy.close();
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                exception.printStackTrace();
                            }
                        });
            } else {
                requestCameraPermission();
            }
        }
    }

    private void replaceWithPrepareImageFragment() {
        NavController controller = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
        controller.navigate(R.id.navigation_prepare_image);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, capture the image again
                captureImage();
            } else {
                Toast.makeText(requireContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
