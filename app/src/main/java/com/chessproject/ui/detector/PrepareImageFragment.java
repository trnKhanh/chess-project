package com.chessproject.ui.detector;// ... (existing imports)

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.chessproject.MyApplication;
import com.chessproject.R;
import com.chessproject.detection.ChessPositionDetector;
import com.chessproject.utils.ImageUtils;

import java.util.concurrent.ExecutorService;

public class PrepareImageFragment extends Fragment implements View.OnClickListener {

    private DetectorViewModel detectorViewModel;
    ImageView capturedImageView;
    Button continueButton;
    TextView blackTurnButton;
    TextView whiteTurnButton;
    TextView whiteViewButton;
    TextView blackViewButton;

    TextView nextTurnTextView;
    TextView nextViewTextView;
    private boolean isWhiteTurn = true;
    private boolean isWhiteView = true;
    private ProgressDialog progressDialog;
    private NavController navController;
    ExecutorService executorService;
    Handler mainHandler;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = ((MyApplication) getActivity().getApplication()).getExecutorService();
        mainHandler = ((MyApplication) getActivity().getApplication()).getMainHandler();
        detectorViewModel = new ViewModelProvider(requireActivity()).get(DetectorViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prepare_image, container, false);
        initializeUI(root);

        blackViewButton.setOnClickListener(this);
        whiteViewButton.setOnClickListener(this);
        blackTurnButton.setOnClickListener(this);
        whiteTurnButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Detecting...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return root;
    }

    private void initializeUI(View root) {
        capturedImageView = root.findViewById(R.id.imagePreView);
        continueButton = root.findViewById(R.id.continueButton);
        blackTurnButton = root.findViewById(R.id.nextTurnIsBlack);
        blackViewButton = root.findViewById(R.id.viewIsBlack);
        whiteTurnButton = root.findViewById(R.id.nextTurnIsWhite);
        whiteViewButton = root.findViewById(R.id.viewIsWhite);
        nextTurnTextView = root.findViewById(R.id.nextTurnTextView);
        nextViewTextView = root.findViewById(R.id.nextViewTextView);
        capturedImageView.setImageBitmap(detectorViewModel.getCapturedImage().getValue());
        fixToggleButton();
    }

    private void fixToggleButton() {
        updateToggleButtonState("Choose next turn: ", whiteTurnButton, blackTurnButton, nextTurnTextView, isWhiteTurn);
        updateToggleButtonState("Choose view: ", whiteViewButton, blackViewButton, nextViewTextView, isWhiteView);
    }

    private void updateToggleButtonState(String pattern, TextView activeButton, TextView inactiveButton, TextView textView, boolean isWhite) {
        if (isWhite) {
            activeButton.setBackgroundResource(R.drawable.rounded_background_border_true);
            activeButton.setTypeface(null, Typeface.BOLD);
            activeButton.setTextColor(Color.BLACK);
            inactiveButton.setBackgroundResource(R.drawable.rounded_background_border_false);
            inactiveButton.setTypeface(null, Typeface.NORMAL);
            inactiveButton.setTextColor(Color.parseColor("#808080"));
            textView.setText(pattern + "White");
        } else {
            inactiveButton.setBackgroundResource(R.drawable.rounded_background_border_true);
            inactiveButton.setTypeface(null, Typeface.BOLD);
            inactiveButton.setTextColor(Color.BLACK);
            activeButton.setBackgroundResource(R.drawable.rounded_background_border_false);
            activeButton.setTypeface(null, Typeface.NORMAL);
            activeButton.setTextColor(Color.parseColor("#808080"));
            textView.setText(pattern + "Black");
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.viewIsWhite) {
            isWhiteView = true;
        } else if (id == R.id.viewIsBlack) {
            isWhiteView = false;
        } else if (id == R.id.nextTurnIsWhite) {
            isWhiteTurn = true;
        } else if (id == R.id.nextTurnIsBlack) {
            isWhiteTurn = false;
        } else if (id == R.id.continueButton){
            goToCheckBoardFragment();
        }
        fixToggleButton();
    }

    private void goToCheckBoardFragment() {
        showProgressDialog();
        Bitmap bitmap = detectorViewModel.getCapturedImage().getValue();
        byte[] bytes = ImageUtils.getBytesFromBitmap(bitmap);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String fen = (new ChessPositionDetector()).detectPosition(bytes);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        detectorViewModel.setFen(fen);
                        hideProgressDialog();
                        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                        navController.navigate(R.id.navigation_result);
                    }
                });
            }
        });
    }
}
