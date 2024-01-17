package com.chessproject;

import static com.chessproject.Utils.getBytesFromBitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chessproject.chess.ui.BoardView;
import com.chessproject.detection.ChessPositionDetector;
import com.chessproject.evaluation.ChessPositionEvaluator;

import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "Main activity";
//    HandlerThread handlerThread;
//    Handler handler;
    ChessPositionDetector detector;
    ExecutorService mExecutorService;
    Handler mMainHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set ExecutorService
        mExecutorService = ((MyApplication) getApplication()).getExecutorService();
        // Set main handler
        mMainHandler = ((MyApplication) getApplication()).getMainHandler();

        BoardView boardView = (BoardView) findViewById(R.id.chessboard);

//        Button btn = (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                boardView.rollbackLastMove();
//                boardView.movePiece(15, 5);
//                boardView.invalidate();
//            }
//        });


        detector = new ChessPositionDetector();
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chessboard);
                        String fen = detector.detectPosition(getBytesFromBitmap(bitmap));
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                btn.setText(fen);
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

//        handlerThread = new HandlerThread("detector");
//        handlerThread.start();
//        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    protected void onPause() {
//        handlerThread.quitSafely();
//
//        try {
////            handlerThread.join();
////            handlerThread = null;
////            handler = null;
//        } catch (InterruptedException e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }

        super.onPause();
    }


}