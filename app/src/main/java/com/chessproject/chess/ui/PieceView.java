package com.chessproject.chess.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.chessproject.chess.logic.Piece;

public class PieceView extends androidx.appcompat.widget.AppCompatImageView {
    final static String TAG = "PieceView";
    Context mContext;
    Piece mPiece;
    BoardController mBoardController;
    public Piece getPiece() {
        return mPiece;
    }

    public PieceView(Context context, Piece piece, BoardController boardController) {
        super(context);
        mContext = context;
        mPiece = piece;
        mBoardController = boardController;

        setZ(1);
        // Set image resource to correct piece
        setImageResource(mPiece.getImageResource());
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Set position in board
        int colId = mPiece.getPosition() % 8;
        int rowId = mPiece.getPosition() / 8;
        Log.d(TAG, String.valueOf(mPiece.getPosition()));
        setX(colId * getWidth());
        setY(rowId * getHeight());
    }

    public void animateMove() {
        int colId = mPiece.getPosition() % 8;
        int rowId = mPiece.getPosition() / 8;
        float oldX = getX();
        float oldY = getY();

        float newX = colId * getWidth();
        float newY = rowId * getHeight();

        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setX(newX + (oldX - newX) * value);
                setY(newY + (oldY - newY) * value);
            }
        });
        animator.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float centerX, centerY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // Set selected piece to this
                if (!mBoardController.setSelectedPiece(this))
                    return false;
            case MotionEvent.ACTION_MOVE:
                // Set position so that the center is under the touch position
                setScaleX(1.5f);
                setScaleY(1.5f);
                centerX = getX() + event.getX();
                centerY = getY() + event.getY();

                setX(centerX - (float)getWidth() / 2);
                setY(centerY - (float)getHeight() / 2);

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                setScaleX(1f);
                setScaleY(1f);
                // Calculate the new position
                float x = getX() + (float) getWidth() / 2;
                float y = getY() + (float) getHeight() / 2;
                Log.d(TAG, "x " + String.valueOf(x));
                Log.d(TAG, "y " + String.valueOf(y));
                int colId = (int)Math.floor((double) x / (double)getWidth());
                int rowId = (int)Math.floor((double) y / (double)getHeight());
                if (colId >= 8 || colId < 0 || rowId >= 8 || rowId < 0) {
                    rowId = -1;
                    colId = -1;
                }
                // Clear board when choose other cells
                mBoardController.placeSelectedPiece(rowId * 8 + colId);

                break;
        };

        return true;
    }
}
