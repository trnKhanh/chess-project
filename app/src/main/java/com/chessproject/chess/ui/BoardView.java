package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chessproject.chess.logic.Board;
import com.chessproject.chess.logic.Piece;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardView extends FrameLayout implements BoardController {
    final static String TAG = "BoardView";
    final static int DEFAULT_WIDTH = 800;
    final static int DEFAULT_HEIGHT = 800;
    Context mContext;
    PieceView mSelectedPieceView = null;
    Board mBoard;
    LayoutParams mPieceParams;
    CellView[] mCellViews = new CellView[64];
    HashMap<Integer, PieceView> mPieceViewMap = new HashMap<>();

    private void initBoard(String fen) {
        setWillNotDraw(false);
        mBoard = new Board(fen);
        mPieceParams = new LayoutParams(DEFAULT_WIDTH / 8, DEFAULT_HEIGHT / 8);

        for (Piece piece: mBoard.getPieces()) {
            PieceView pieceView = new PieceView(mContext, piece, this);
            mPieceViewMap.put(piece.getPosition(), pieceView);
            this.addView(pieceView);
        }
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                CellView cellView = new CellView(mContext, i * 8 + j, this);
                this.addView(cellView);
                mCellViews[i * 8 + j] = cellView;
            }
        }
    }
    public BoardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        initBoard(null);
    }
    public BoardView(@NonNull Context context, String fen) {
        super(context);

        mContext = context;
        initBoard(fen);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(DEFAULT_WIDTH, widthSize);
        } else {
            width = DEFAULT_WIDTH;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(DEFAULT_HEIGHT, heightSize);
        } else {
            height = DEFAULT_HEIGHT;
        }

        setMeasuredDimension(width, height);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width / 8, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height / 8, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 64; ++i) {
            mCellViews[i].setCellState(0);
        }
        if (mSelectedPieceView != null) {
            ArrayList<Integer> legalMoves = mSelectedPieceView.getPiece().getLegalMoves();
            Log.d(TAG, String.valueOf(legalMoves.size()));
            for (int position: legalMoves) {
                Log.d(TAG, String.valueOf(mBoard.getPiece(position)));
                if (mBoard.getPiece(position) == null) {
                    mCellViews[position].setCellState(CellView.LEGAL_MOVE);
                } else {
                    mCellViews[position].setCellState(CellView.LEGAL_MOVE_PIECE);
                }
            }
        }
    }

    @Override
    public boolean setSelectedPiece(PieceView pieceView) {
        if (mSelectedPieceView == null) {
            mSelectedPieceView = pieceView;
            mSelectedPieceView.setZ(2);
        }
        invalidate();
        return mSelectedPieceView == pieceView;
    }

    @Override
    public void finishMove(int position) {
        if (mSelectedPieceView != null) {
            // Try moving piece to new position
            int oldPosition = mSelectedPieceView.getPiece().getPosition();
            boolean success = mSelectedPieceView.getPiece().moveTo(position);
            mSelectedPieceView.animateMove();
            // If user clicks another cell
            if (oldPosition != position) {
                // If the move is successful/legal
                if (success) {
                    // If clicked cell contains a piece, then remove it
                    if (mPieceViewMap.containsKey(position)) {
                        PieceView pieceView = mPieceViewMap.get(position);
                        mPieceViewMap.remove(position);
                        this.removeView(pieceView);
                    }
                    // Update piece's view position
                    mPieceViewMap.remove(oldPosition);
                    mPieceViewMap.put(position, mSelectedPieceView);
                }
                mSelectedPieceView = null;
            }
        }
        invalidate();
    }
}
