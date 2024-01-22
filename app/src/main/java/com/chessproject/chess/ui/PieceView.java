package com.chessproject.chess.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.chessproject.R;
import com.chessproject.chess.logic.Piece;

public class PieceView extends androidx.appcompat.widget.AppCompatImageView {
    final static String TAG = "PieceView";
    Context mContext;
    Piece mPiece;
    BoardController mBoardController;
    boolean mWhitePerspective = true;
    public Piece getPiece() {
        return mPiece;
    }
    public void setPiece(Piece piece) {
        mPiece = piece;
        setImageResource(piece.getImageResource());
    }
    public PieceView(Context context, Piece piece, BoardController boardController) {
        super(context);
        mContext = context;
        setPiece(piece);
        mBoardController = boardController;
        // Set Z so that piece is above cell
        setZ(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Set position in board
        int colId = mPiece.getPosition() % 8;
        int rowId = mPiece.getPosition() / 8;
        setX(colId * getWidth());
        setY(rowId * getHeight());
    }

    public void animateMove() {
        // Calculate position of destination
        int colId = mPiece.getPosition() % 8;
        int rowId = mPiece.getPosition() / 8;
        float newX = colId * getWidth();
        float newY = rowId * getHeight();

        // Get current position
        float oldX = getX();
        float oldY = getY();
        // Set up animation moving piece from current position to destination
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

        // Calculate the new position
        float centerX = getX() + event.getX();
        float centerY = getY() + event.getY();

        int colId = (int)Math.floor((double) centerX / (double)getWidth());
        int rowId = (int)Math.floor((double) centerY / (double)getHeight());
        // If the new position is outside of the board then set row id and col id to outside/negative.
        if (colId >= 8 || colId < 0 || rowId >= 8 || rowId < 0) {
            rowId = -1;
            colId = -1;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // Try set selected piece to this if there is no current selected piece.
                return mBoardController.setSelectedPiece(mPiece.getPosition(), true);
            case MotionEvent.ACTION_MOVE:
                // Scale up the piece
                setScaleX(1.5f);
                setScaleY(1.5f);

                setX(centerX - (float)getWidth() / 2);
                setY(centerY - (float)getHeight() / 2);

                mBoardController.setSelectedCell(rowId * 8 + colId);

                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // Scale the piece back to normal
                setScaleX(1f);
                setScaleY(1f);

                // Try placing the selected piece.
                mBoardController.placeSelectedPiece(rowId * 8 + colId);

                return true;
        };

        return false;
    }

    public void setPerspective(boolean white) {
        if (!white) {
            setRotation(180);
        } else {
            setRotation(0);
        }
    }
}
