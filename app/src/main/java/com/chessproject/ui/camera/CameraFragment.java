package com.chessproject.ui.camera;

import static com.chessproject.utils.ImageUtils.imageToBitmap;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.chessproject.R;
import com.chessproject.databinding.FragmentCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {
    private FragmentCameraBinding binding;

    private static final int REQUEST_CODE_SELECT_IMAGE = 1001;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int FILE_STORAGE_PERMISSION_REQUEST_CODE = 101;

    ImageView capturedImageView;
    Button backButton;
    ImageView captureButton;
    ImageView openGalleryButton;
    Button generateButton;
    private ImageCapture imageCapture;
    PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        CameraViewModel cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        capturedImageView = root.findViewById(R.id.capturedImageView);
        backButton = root.findViewById(R.id.reCapture);
        captureButton = root.findViewById(R.id.captureButton);
        previewView = root.findViewById(R.id.previewView);
        openGalleryButton = root.findViewById(R.id.btnOpenGallery);
        generateButton = root.findViewById(R.id.btnGenerate);

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                CameraSelector cameraSelector1 = CameraSelector.DEFAULT_BACK_CAMERA;

                androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle(
                        this, // Use the fragment as the LifecycleOwner
                        cameraSelector,
                        preview,
                        imageCapture);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));

        captureButton.setOnClickListener(view -> {
            captureImage();
        });

        backButton.setOnClickListener(view -> {
            resetToCameraPreview();
        });

        openGalleryButton.setOnClickListener(view -> {
            openGalleryForImage();
        });

        generateButton.setOnClickListener(view -> {
            goToGenerateChessBoard();
        });
        return root;
    }

    private void goToGenerateChessBoard() {
        showNextTurnOptionsDialog();
    }

    private void resetToCameraPreview() {
        capturedImageView.setVisibility(View.GONE);
        captureButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);
        generateButton.setVisibility(View.GONE);
        openGalleryButton.setVisibility(View.VISIBLE);
    }

    public void captureImage() {
        if (imageCapture != null) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                imageCapture.takePicture(ContextCompat.getMainExecutor(getContext()), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        displayCapturedImage(image);
                        image.close();
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
    private void displayCapturedImage(ImageProxy image) {
        Bitmap bitmap = imageToBitmap(image);

        capturedImageView.setImageBitmap(bitmap);
        capturedImageView.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.GONE);
        generateButton.setVisibility(View.VISIBLE);
        openGalleryButton.setVisibility(View.GONE);
    }

    private void showNextTurnOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose next turn")
                .setItems(new CharSequence[]{"Black", "White"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Black Move

                                break;
                            case 1: // White Move

                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void openGalleryForImage() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
//        } else {
//            requestFileStoragePermission();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                capturedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void requestFileStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                FILE_STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(requireContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == FILE_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // File storage permission granted, proceed with file-related operations
                openGalleryForImage();
            } else {
                Toast.makeText(requireContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
