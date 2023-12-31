package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

public class CellView extends androidx.appcompat.widget.AppCompatImageView {
    final static int HIGHLIGHTED = 1;
    final static int LEGAL_MOVE = 1 << 1;
    final static int LEGAL_MOVE_PIECE = 1 << 2;

    final static String TAG = "CellView";
    Context mContext;
    int mPosition;
    int mCellState = 0;
    Paint mDotPaint = new Paint();
    Paint mRingPaint = new Paint();

    Paint mHighlightedPaint = new Paint();
    BoardController mBoardController;
    public CellView(Context context, int position, BoardController boardController) {
        super(context);
        mDotPaint.setARGB(150, 150, 150, 150);
        mDotPaint.setStyle(Paint.Style.FILL);

        mRingPaint.setARGB(150, 150, 150, 150);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(4);

        mHighlightedPaint.setARGB(100, 255, 255, 0);

        mContext = context;
        mPosition = position;

        mBoardController = boardController;

//        setZ(1);
        int colId = mPosition % 8;
        int rowId = mPosition / 8;
        if ((colId + rowId) % 2 == 0) {
            setBackgroundColor(Color.WHITE);
        } else {
            setBackgroundColor(Color.BLACK);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Set position in board
        int colId = mPosition % 8;
        int rowId = mPosition / 8;
        setX(colId * getWidth());
        setY(rowId * getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, String.valueOf(mCellState));
        if ((mCellState & (LEGAL_MOVE)) != 0) {
            // Draw a dot if not a piece
            canvas.drawCircle((float)getWidth() / 2, (float)getHeight() / 2, (float)getWidth() / 10f, mDotPaint);
        } else if ((mCellState & (LEGAL_MOVE_PIECE)) != 0){
            // Draw a ring if a piece
            canvas.drawCircle((float)getWidth() / 2, (float)getHeight() / 2, (float)getWidth() / 2f - 4, mRingPaint);
        }

        if ((mCellState & (HIGHLIGHTED)) != 0) {
            canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), mHighlightedPaint);
//            canvas.drawARGB(75, 255, 255, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "TOUCHED");
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "TOUCHED");
                if (event.getX() >= 0 && event.getX() <= getWidth() && event.getY() >= 0 && event.getY() <= getHeight())
                    mBoardController.placeSelectedPiece(mPosition);
                break;
        }

        return true;
    }

    public void setCellState(int cellState) {
        mCellState = cellState;
        invalidate();
    }

    public void toggleHighlighted() {
        mCellState ^= HIGHLIGHTED;
        invalidate();
    }
    public void toggleLegalMove(boolean isCapture) {
        clearLegalMove();
        if (isCapture) {
            mCellState ^= LEGAL_MOVE_PIECE;
        } else {
            mCellState ^= LEGAL_MOVE;
        }
        invalidate();
    }
    public void clearLegalMove() {
        mCellState &= ~(LEGAL_MOVE | LEGAL_MOVE_PIECE);
        invalidate();
    }
    public void clearState() {
        mCellState = 0;
        invalidate();
    }
}
