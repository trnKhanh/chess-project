package com.chessproject.chess.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

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
        setZ(1);
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
                centerX = getX() + event.getX();
                centerY = getY() + event.getY();
                // Adjust position
                centerX = centerX < 0 ? 0 : centerX;
                centerX = centerX > getWidth() * 8 ? getWidth() * 8 : centerX;
                centerY = centerY < 0 ? 0 : centerY;
                centerY = centerY > getHeight() * 8 ? getHeight() * 8 : centerY;

                setX(centerX - (float)getWidth() / 2);
                setY(centerY - (float)getHeight() / 2);

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // Calculate the new position
                float x = getX() + (float) getWidth() / 2;
                float y = getY() + (float) getHeight() / 2;
                int colId = (int) x / getWidth();
                int rowId = (int) y / getHeight();
                // Clear board when choose other cells
                mBoardController.finishMove(rowId * 8 + colId);

                break;
        };

        return true;
    }
}
