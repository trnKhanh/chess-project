package com.example.chessvision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChessBoardActivity extends AppCompatActivity implements View.OnClickListener {
    Button backButton;
    ImageView imageView;
    private static byte[] sImageData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chessboard);

        backButton = findViewById(R.id.backButtonFromChessBoard);
        backButton.setOnClickListener(this);
        imageView = findViewById(R.id.imageBoardGenerated);

        ImageView imageView = findViewById(R.id.imageBoardGenerated);
        if (sImageData != null) {
            Bitmap bitmap = rotateBitmap(BitmapFactory.decodeByteArray(sImageData, 0, sImageData.length), 90);
            imageView.setImageBitmap(bitmap);
        }
    }

    public static void setImageData(byte[] imageData) {
        sImageData = imageData;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.backButtonFromChessBoard){
            onBackPressed();
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
