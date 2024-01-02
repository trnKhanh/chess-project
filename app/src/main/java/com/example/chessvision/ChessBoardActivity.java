package com.example.chessvision;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChessBoardActivity extends AppCompatActivity implements View.OnClickListener {
    Button backButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chessboard);

        backButton = findViewById(R.id.backButtonFromChessBoard);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.backButtonFromChessBoard){
            onBackPressed();
        }
    }
}
