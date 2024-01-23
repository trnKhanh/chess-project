package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.chessproject.R;

public class CellView extends androidx.appcompat.widget.AppCompatImageView {
    final static int HIGHLIGHTED = 1;
    final static int LEGAL_MOVE = 1 << 1;
    final static int LEGAL_MOVE_PIECE = 1 << 2;
    final static int SELECTED = 1 << 3;
    final static String TAG = "CellView";
    Context mContext;
    int mPosition;
    int mCellState = 0;
    Paint mDotPaint = new Paint();
    Paint mRingPaint = new Paint();
    Paint mHighlightedPaint = new Paint();
    Paint mSelectedPaint = new Paint();
    BoardController mBoardController;
    public CellView(Context context, int position, BoardController boardController) {
        super(context);
        mContext = context;
        mPosition = position;
        mBoardController = boardController;

        // Setup paint for cell
        mDotPaint.setARGB(150, 175, 175, 175);
        mDotPaint.setStyle(Paint.Style.FILL);

        mRingPaint.setARGB(150, 175, 175, 175);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(4);

        mHighlightedPaint.setARGB(100, 255, 255, 0);

        mSelectedPaint.setARGB(100, 200, 200, 200);
        mSelectedPaint.setStyle(Paint.Style.FILL);

        // Set color of cell
        int colId = mPosition % 8;
        int rowId = mPosition / 8;
        if ((colId + rowId) % 2 == 0) {
            setBackgroundColor(0xffeeeed2);
        } else {
            setBackgroundColor(0xff769656);
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
        Log.d(TAG, "DRAW");
        if ((mCellState & (LEGAL_MOVE)) != 0) {
            // Draw a dot if not a piece
            canvas.drawCircle((float) getWidth() / 2, (float) getHeight() / 2, (float) getWidth() / 10f, mDotPaint);
        } else if ((mCellState & (LEGAL_MOVE_PIECE)) != 0) {
            // Draw a ring if a piece
            canvas.drawCircle((float) getWidth() / 2, (float) getHeight() / 2, (float) getWidth() / 2f - 4f, mRingPaint);
        }

        if ((mCellState & (HIGHLIGHTED)) != 0) {
            // Highlight the cell
            canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), mHighlightedPaint);
        }
        if ((mCellState & (SELECTED)) != 0) {
            // Highlight the cell
            canvas.drawCircle((float) getWidth() / 2, (float) getHeight() / 2, (float) getWidth() * 1.2f - 4f, mSelectedPaint);
        }
    }
    public void setCellState(int cellState) {
        mCellState = cellState;
        invalidate();
    }
    public void toggleHighlighted() {
        mCellState ^= HIGHLIGHTED;
        invalidate();
    }
    public void toggleSelected() {
        mCellState ^= SELECTED;
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
