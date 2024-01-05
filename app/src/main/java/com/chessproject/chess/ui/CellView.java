package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    BoardController mBoardController;
    public CellView(Context context, int position, BoardController boardController) {
        super(context);
        mDotPaint.setColor(Color.GRAY);
        mDotPaint.setStyle(Paint.Style.FILL);

        mRingPaint.setColor(Color.GRAY);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(4);

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
            canvas.drawCircle((float)getWidth() / 2, (float)getHeight() / 2, (float)getWidth() * 4.5f / 10f, mRingPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "TOUCHED");
                if (event.getX() >= 0 && event.getX() <= getWidth() && event.getY() >= 0 && event.getY() <= getHeight())
                    mBoardController.finishMove(mPosition);
                break;
        }

        return true;
    }

    public void setCellState(int cellState) {
        mCellState = cellState;
        invalidate();
    }
}
