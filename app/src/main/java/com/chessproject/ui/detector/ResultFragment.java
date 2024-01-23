package com.chessproject.ui.detector;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.chessproject.R;
import com.chessproject.chess.ui.BoardView;
import com.chessproject.detection.ChessPositionDetector;

public class ResultFragment extends Fragment {
    final static String TAG = "ResultFragment";
    private DetectorViewModel detectorViewModel;
    private String fen;
    private BoardView chessBoard;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detectorViewModel = new ViewModelProvider(requireActivity()).get(DetectorViewModel.class);
        fen = detectorViewModel.getFen().getValue();
        Log.d(TAG, String.valueOf(fen));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_result, container, false);
        initializeUI(root);

        return root;
    }

    private void initializeUI(View root){
        chessBoard = root.findViewById(R.id.detectedChessBoard);
        chessBoard.setFen(fen);
    }
}
