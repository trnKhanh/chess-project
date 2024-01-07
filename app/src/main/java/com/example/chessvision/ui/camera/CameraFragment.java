package com.example.chessvision.ui.camera;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chessvision.R;
import com.example.chessvision.databinding.FragmentCameraBinding;

import java.io.IOException;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener {
    private FragmentCameraBinding binding;
    private static byte[] sImageData;
    ImageView imageCapture;

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Button mCaptureButton;
    private Button mGenerate;
    private Button mChooseImageButton;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1001;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1002;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        CameraViewModel cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        imageCapture = root.findViewById(R.id.imageCapture);
        mCaptureButton = root.findViewById(R.id.btnCapture);
        mSurfaceView = root.findViewById(R.id.surfaceView);
        mGenerate = root.findViewById(R.id.btnGenerate);
        mChooseImageButton = root.findViewById(R.id.btnChooseImage);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mCaptureButton.setOnClickListener(this);
        mGenerate.setOnClickListener(this);
        mChooseImageButton.setOnClickListener(this);
        return root;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void captureImage() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data != null) {
                    Bitmap bitmap = rotateBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), 90);
                    imageCapture.setImageBitmap(bitmap);
                    // Turn to image state
                    imageCapture.setVisibility(View.VISIBLE);
                    mCaptureButton.setText("CAPTURE AGAIN");
                    mGenerate.setVisibility(View.VISIBLE);
                    sImageData = data; // Store the image data
                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        setCameraDisplayOrientation(requireActivity(), Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate for the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void restartCameraPreview() {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                imageCapture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int Id = v.getId();
        if (Id == R.id.btnCapture) {
            String text = mCaptureButton.getText().toString();
            if(text.equals("CAPTURE"))
                captureImage();
            else {
                restartCameraPreview();
                turnToCameraState();
            }
        }
        else if (Id == R.id.btnGenerate) {
            showNextTurnOptionsDialog();
        } else if (Id == R.id.btnChooseImage) {
            openGalleryForImage();
        }
    }

    private void turnToCameraState() {
        imageCapture.setVisibility(View.INVISIBLE);
        mCaptureButton.setText("CAPTURE");
        mGenerate.setVisibility(View.INVISIBLE);
    }

    private void turnToImageState(){
        imageCapture.setVisibility(View.VISIBLE);
        mCaptureButton.setText("CAPTURE AGAIN");
        mGenerate.setVisibility(View.VISIBLE);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            openGalleryForImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryForImage();
            } else {
                Toast.makeText(requireContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
