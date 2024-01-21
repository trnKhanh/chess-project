package com.chessproject.ui.chessboard;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.chessproject.R;
import com.chessproject.Utils;
import com.chessproject.chess.ui.BoardView;
import com.chessproject.detection.ChessPositionDetector;
import com.chessproject.ui.camera.CameraViewModel;

public class ChessBoardFragment extends Fragment {

    private ChessPositionDetector detector = new ChessPositionDetector();
    private CameraViewModel cameraViewModel;
    private String fen;
    private BoardView chessBoard;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraViewModel = new ViewModelProvider(requireActivity()).get(CameraViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chess_board, container, false);
        initializeUI(root);

        return root;
    }

    private void initializeUI(View root){
        chessBoard = root.findViewById(R.id.detectedChessBoard);
        cameraViewModel.getCapturedImage().observe(requireActivity(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap capturedImage) {
                byte[] image = Utils.imageToByteArray(capturedImage);
                fen = detector.detectPosition(image);
                chessBoard = new BoardView(requireContext(), fen);
            }
        });
    }
}
