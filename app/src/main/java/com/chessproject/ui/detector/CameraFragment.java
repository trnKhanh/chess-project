package com.chessproject.ui.detector;

import static com.chessproject.utils.ImageUtils.imageToBitmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.LifecycleCameraController;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.chessproject.R;
import com.chessproject.databinding.FragmentCameraBinding;

public class CameraFragment extends Fragment {
    final static String TAG = "CameraFragment";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private DetectorViewModel detectorViewModel;
    private LifecycleCameraController cameraController;
    FragmentCameraBinding binding;
    NavController navController;
    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean o) {
                    if (o) {
                        setupCamera();
                        binding.previewView.setController(cameraController);
                    } else {
                        navController.navigateUp();
                    }
                }
            });
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detectorViewModel = new ViewModelProvider(requireActivity()).get(DetectorViewModel.class);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            setupCamera();
            binding.previewView.setController(cameraController);
        }
        setupListener();
        return root;
    }


    private void setupListener() {
        binding.captureButton.setOnClickListener(view -> {
            captureImage();
        });
    }

    private void setupCamera() {
        cameraController = new LifecycleCameraController(requireContext());
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
    }

    private void captureImage() {
        cameraController.takePicture(ContextCompat.getMainExecutor(requireContext()),
            new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                    super.onCaptureSuccess(imageProxy);
                    Bitmap bitmap = imageToBitmap(imageProxy);
                    detectorViewModel.setCapturedImage(bitmap);
                    imageProxy.close();
                    replaceWithPrepareImageFragment();
                }
                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                    exception.printStackTrace();
                }
            });
    }

    private void replaceWithPrepareImageFragment() {
        navController.navigate(R.id.navigation_prepare_image);
    }

    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }
}
