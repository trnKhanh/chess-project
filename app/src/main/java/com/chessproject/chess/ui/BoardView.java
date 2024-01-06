package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chessproject.chess.logic.Board;
import com.chessproject.chess.logic.Knight;
import com.chessproject.chess.logic.Piece;

import java.lang.reflect.Array;
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
    ArrayList<PieceView> mExtraPieceViews = new ArrayList<>();
    boolean mIsSetupBoard = true;
    public void toggleSetupBoard() {
        mIsSetupBoard = !mIsSetupBoard;
        mBoard.clearHistory();
        requestLayout();
    }

    private void initBoard(String fen) {
        setClipChildren(false);
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

        Piece whiteKnight = new Knight(true, 9 * 8, mBoard);
        PieceView whiteKnightView = new PieceView(mContext, whiteKnight, this);
        mExtraPieceViews.add(whiteKnightView);
        this.addView(whiteKnightView);

        Piece blackKnight = new Knight(false, 10 * 8, mBoard);
        PieceView blackKnightView = new PieceView(mContext, blackKnight, this);
        mExtraPieceViews.add(blackKnightView);
        this.addView(blackKnightView);
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

        int defaultWidth = DEFAULT_WIDTH;
        int defaultHeight = DEFAULT_HEIGHT;

        if (mIsSetupBoard) {
            defaultHeight += DEFAULT_WIDTH * 3 / 8;
        }

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(defaultWidth, widthSize);
        } else {
            width = defaultWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(defaultHeight, heightSize);
        } else {
            height = defaultHeight;
        }

        setMeasuredDimension(width, height);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width / 8, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(width / 8, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        mBoard.printBoard();
        for (int i = 0; i < 64; ++i) {
            mCellViews[i].clearState();
        }
        if (mIsSetupBoard) {
            for (PieceView pieceView: mExtraPieceViews) {
                pieceView.setVisibility(VISIBLE);
            }
            return;
        } else {
            for (PieceView pieceView: mExtraPieceViews) {
                pieceView.setVisibility(GONE);
            }
        }
        // Highlighted last move
        Pair<Integer, Integer> lastMove = mBoard.getLastMove();
        if (lastMove != null) {
            mCellViews[lastMove.first].toggleHighlighted();
            mCellViews[lastMove.second].toggleHighlighted();
        }
        if (mSelectedPieceView != null) {
            ArrayList<Integer> legalMoves = mSelectedPieceView.getPiece().getLegalMoves();
            for (int position: legalMoves) {
                if (mBoard.getPiece(position) == null) {
                    mCellViews[position].toggleLegalMove(false);
                } else {
                    mCellViews[position].toggleLegalMove(true);
                }
            }
        }
    }

    @Override
    public boolean setSelectedPiece(PieceView pieceView) {
        if (mSelectedPieceView == null) {
            mSelectedPieceView = pieceView;
        }

        if (mIsSetupBoard && pieceView.getPiece().getPosition() >= 64) {
            // if it is a setup board and selected piece is the "extra" piece, then just select it.
            if (mSelectedPieceView != null) {
                mSelectedPieceView = pieceView;
            }
            // If it is a "extra" piece, then create a new copy
            mExtraPieceViews.remove(mSelectedPieceView);
            Piece piece = mSelectedPieceView.getPiece().copy();
            PieceView newPieceView = new PieceView(mContext, piece, this);
            Log.d(TAG, "CREATED");
            this.addView(newPieceView);
            mExtraPieceViews.add(newPieceView);
        }
        mSelectedPieceView.setZ(2);
        invalidate();
        return mSelectedPieceView == pieceView;
    }

    @Override
    public void placeSelectedPiece(int position) {
        if (mSelectedPieceView != null) {
            int oldPosition = mSelectedPieceView.getPiece().getPosition();
            // Try moving piece to new position
            boolean success = mSelectedPieceView.getPiece().moveTo(position, !mIsSetupBoard);

            mSelectedPieceView.animateMove();
            mSelectedPieceView.setZ(1);
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

                } else if (mIsSetupBoard) {
                    // If it is a setup board then remove a piece when it go out side the board/failed move.
                    mPieceViewMap.remove(oldPosition);
                    this.removeView(mSelectedPieceView);
                }

                mSelectedPieceView = null;
            }
        }
        invalidate();
    }
}
