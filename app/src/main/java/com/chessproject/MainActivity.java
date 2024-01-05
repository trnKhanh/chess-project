package com.chessproject;

import static com.chessproject.Utils.getBytesFromBitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chessproject.detection.ChessPositionDetector;

import java.io.ByteArrayOutputStream;

import javax.net.ssl.HandshakeCompletedEvent;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "Main activity";
    HandlerThread handlerThread;
    Handler handler;
    ChessPositionDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        detector = new ChessPositionDetector(new ChessPositionDetector.OnResultListener() {
//            @Override
//            public void onResult(String fen) {
//                Log.d(TAG, fen);
//            }
//        });
//        Button btn = (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (handler != null) {
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chessboard);
//                            detector.detectPosition(getBytesFromBitmap(bitmap));
//                        }
//                    });
//                }
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        handlerThread = new HandlerThread("detector");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    protected void onPause() {
        handlerThread.quitSafely();

        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (InterruptedException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        super.onPause();
    }


}