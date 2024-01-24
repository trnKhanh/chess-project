package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Mesh;
import android.graphics.MeshSpecification;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chessproject.MyApplication;
import com.chessproject.R;
import com.chessproject.chess.logic.Board;
import com.chessproject.chess.logic.King;
import com.chessproject.chess.logic.Knight;
import com.chessproject.chess.logic.Piece;
import com.chessproject.evaluation.ChessPositionEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoardView extends FrameLayout implements BoardController {
    final static String TAG = "BoardView";
    final static int DEFAULT_WIDTH = 800;
    final static int DEFAULT_HEIGHT = 800;
    public final static int CORRECT_MOVE = 1;
    public final static int WRONG_MOVE = 2;
    Context mContext;
    PieceView mPromotedPieceView = null;
    Board mBoard;
    CellView[] mCellViews;
    HashMap<Integer, PieceView> mPieceViewMap;
    ArrayList<PieceView> mExtraPieceViews;
    boolean mIsSetupBoard = false;
    int mSelectedPosition = -1;
    CellView mSelectedCellView = null;
    ImageView mLastMoveEvalView = null;
    PromotionView mWhitePromotionSelections, mBlackPromotionSelections;
    ArrayList<Pair<Integer, Integer>> mArrows;
    Pair<Integer, Integer> mBestMove = null;
    Paint mArrowPaint = new Paint();
    boolean mIsHidden = false;
    boolean mDisabled = false;
    boolean mIsEvaluated = false;
    boolean mIsWhitePerspective = true;
    public interface FinishedMoveListener {
        void onFinishMove(Board.Move move);
    }
    FinishedMoveListener mFinishedMoveListener = null;
    private void initBoard(String fen) {
        removeAllViews();
        mArrows = new ArrayList<>();
        mExtraPieceViews = new ArrayList<>();
        mPieceViewMap = new HashMap<>();
        mCellViews = new CellView[64];
        mSelectedCellView = null;
        mSelectedPosition = -1;
        mPromotedPieceView = null;
        // Set clip children to false so that piece can be view even if it is outside of board.
        setClipChildren(false);
        // Set will not draw to false so that invalidate will trigger onDraw.
        setWillNotDraw(false);
        // Set arrow paint
        mArrowPaint.setARGB(200, 0, 255, 0);
        mArrowPaint.setStrokeWidth(2);
        // Create board based on fen.
        mBoard = new Board(fen);
        // Create PieceView for each pieces in board.
        for (Piece piece: mBoard.getPieces()) {
            PieceView pieceView = new PieceView(mContext, piece, this);
            mPieceViewMap.put(piece.getPosition(), pieceView);
            pieceView.setVisibility(INVISIBLE);
            this.addView(pieceView);
        }
        Log.d(TAG, String.valueOf(mPieceViewMap.size()));
        // Create 8x8 CellViews
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                CellView cellView = new CellView(mContext, i * 8 + j, this);
                mCellViews[i * 8 + j] = cellView;
                this.addView(cellView);
            }
        }
        // Add last move evaluation icon
        mLastMoveEvalView = new ImageView(mContext);
        this.addView(mLastMoveEvalView);

        // Promotion views
        mWhitePromotionSelections = new PromotionView(mContext, true, this);
        mWhitePromotionSelections.setOrientation(LinearLayout.VERTICAL);
        mWhitePromotionSelections.setZ(4);
        mWhitePromotionSelections.setVisibility(GONE);
        this.addView(mWhitePromotionSelections);

        mBlackPromotionSelections = new PromotionView(mContext, false, this);
        mBlackPromotionSelections.setOrientation(LinearLayout.VERTICAL);
        mBlackPromotionSelections.setZ(4);
        mBlackPromotionSelections.setVisibility(GONE);
        this.addView(mBlackPromotionSelections);
        requestLayout();
        updateEvaluation();
    }
    public BoardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        // Need to implement this so that it can be declared in XML.
        super(context, attrs);

        mContext = context;
        initBoard(null);
    }
    public BoardView(@NonNull Context context, String fen) {
        // Need to implement this so that it can be created in Java code.
        super(context);

        mContext = context;
        initBoard(fen);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int boardSize = Math.min(width, height);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(boardSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(boardSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        // Measure all the children: CellViews and PieceViews
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width / 8, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(width / 8, MeasureSpec.EXACTLY);

        for (int i = 0; i < 64; ++i) {
            mCellViews[i].measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        for (PieceView pieceView: mPieceViewMap.values()) {
            pieceView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        for (PieceView pieceView: mExtraPieceViews) {
            pieceView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        // Measure last move evaluation icon view
        int iconWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width / 16, MeasureSpec.EXACTLY);
        int iconHeightMeasureSpec = MeasureSpec.makeMeasureSpec(width / 16, MeasureSpec.EXACTLY);
        mLastMoveEvalView.measure(iconWidthMeasureSpec, iconHeightMeasureSpec);
        // Measure promotion selection boxes
        int promotionSelectionWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width / 8, MeasureSpec.EXACTLY);
        int promotionSelectionHeightMeasureSpec = MeasureSpec.makeMeasureSpec(width / 8 * 4, MeasureSpec.EXACTLY);
        mBlackPromotionSelections.measure(promotionSelectionWidthMeasureSpec, promotionSelectionHeightMeasureSpec);
        mWhitePromotionSelections.measure(promotionSelectionWidthMeasureSpec, promotionSelectionHeightMeasureSpec);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDisabled)
            return true;
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDisabled)
            return true;
        Log.d(TAG, "TOUCH DRAW");
        // Touch event handled to correctly show the current selected cell.
        // Calculate position of touched.
        int cellWidth = getWidth() / 8;
        int cellHeight = getHeight() / 8;
        int rowId = (int)event.getY() / cellHeight;
        int colId = (int)event.getX() / cellWidth;
        if (rowId < 0 || rowId >= 8 || colId < 0 || colId >= 8) {
            rowId = -1;
            colId = -1;
        }
        int position = rowId * 8 + colId;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // Clear promotion whenever there is click on board
                finishPromotion();
            case MotionEvent.ACTION_MOVE:
                setSelectedCell(position);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getX() >= 0 && event.getX() <= getWidth() && event.getY() >= 0 && event.getY() <= getHeight()) {
                    if (mIsHidden){
                        if (mSelectedPosition == -1)
                            setSelectedPiece(position, false);
                        else {
                            if (position == mSelectedPosition)
                                setSelectedPiece(-1, false);
                            else
                                placeSelectedPiece(position);
                        }
                    }
                    else {
                        placeSelectedPiece(position);
                    }
                }
                setSelectedCell(-1);
                break;
        }
        // Board always consume motion event.
        return true;
    }
    @Override
    public void onDrawForeground(@NonNull Canvas canvas) {
        super.onDrawForeground(canvas);

        // If best move is not null then draw it only
        if (mBestMove != null) {
            mArrows.clear();
            mArrows.add(mBestMove);
        }
        if (mIsHidden) {
            // If in hidden mode, then draw arrow as last move
            Board.Move move = mBoard.getLastMove();
            if (move != null)
                drawArrow(new Pair<>(move.getOldPosition(), move.getNewPosition()), canvas, mArrowPaint);
        }
        // Draw all arrows
        for (Pair<Integer, Integer> arrow: mArrows) {
            drawArrow(arrow, canvas, mArrowPaint);
        }
    }
    void drawArrow(Pair<Integer, Integer> arrow, Canvas canvas, Paint paint) {
        // arrow head width is 3/4 of cell width
        float arrowHeadWidth = (getWidth() / 8f) * 3f / 4f;
        // arrow head length is 3/4 of cell width
        float arrowHeadLength = (getWidth() / 8f) * 3f / 4f;
        // arrow stem width is 1/2 of arrow head width
        float arrowStemWidth = arrowHeadWidth / 2;
        int startPosition = arrow.first;
        int endPosition = arrow.second;
        // Root of arrow is the center of the start cell
        float startX = startPosition % 8 * (getWidth() / 8f) + getWidth() / 16f;
        float startY = startPosition / 8 * (getHeight() / 8f) + getHeight() / 16f;
        // Head of arrow is the center of the end cell
        float endX = endPosition % 8 * (getWidth() / 8f) + getWidth() / 16f;
        float endY = endPosition / 8 * (getHeight() / 8f) + getHeight() / 16f;
        // Calculate arrow length
        float arrowLength = (float) Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));
        // Calculate sin and cos for rotation
        float sin = (endX - startX) / arrowLength;
        float cos = -(endY - startY) / arrowLength;

        Path path = new Path();

        path.moveTo(startX, startY);
        // Draw vertical arrow rooted at start position
        path.lineTo(startX - arrowStemWidth / 2, startY);
        path.lineTo(startX - arrowStemWidth / 2, startY - arrowLength + arrowHeadLength);
        path.lineTo(startX - arrowHeadWidth / 2, startY - arrowLength + arrowHeadLength);
        path.lineTo(startX, startY - arrowLength);
        path.lineTo(startX + arrowHeadWidth / 2, startY - arrowLength + arrowHeadLength);
        path.lineTo(startX + arrowStemWidth / 2, startY - arrowLength + arrowHeadLength);
        path.lineTo(startX + arrowStemWidth / 2, startY);
        path.lineTo(startX, startY);
        // Rotate arrow to correct destination
        Matrix matrix = new Matrix();
        matrix.setSinCos(sin, cos, startX, startY);
//            matrix.setRotate(angle, startX, startY);
        path.transform(matrix);

        canvas.drawPath(path, paint);
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        Log.d(TAG, "DRAW");
        super.onDraw(canvas);
        // Clear state of all cells.
        for (int i = 0; i < 64; ++i) {
            mCellViews[i].clearState();
        }
        for (PieceView pieceView: mPieceViewMap.values()) {
            if (mIsHidden)
                pieceView.setVisibility(GONE);
            else {
                pieceView.setPerspective(mIsWhitePerspective);
                pieceView.setVisibility(VISIBLE);
            }
        }
        // TODO: Implement Setup Board separately
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
        if (mBoard.isCheck(true)) {
            for (PieceView pieceView: mPieceViewMap.values()) {
                if (pieceView.getPiece().getClass() == King.class && pieceView.getPiece().isWhite()) {
                    mCellViews[pieceView.getPiece().getPosition()].toggleChecked();
                }
            }
        }
        if (mBoard.isCheck(false)) {
            for (PieceView pieceView: mPieceViewMap.values()) {
                if (pieceView.getPiece().getClass() == King.class && !pieceView.getPiece().isWhite()) {
                    mCellViews[pieceView.getPiece().getPosition()].toggleChecked();
                }
            }
        }
        // If there is a promotion
        if (mPromotedPieceView != null) {
            showPromotionBox();
        } else {
            mWhitePromotionSelections.setVisibility(GONE);
            mBlackPromotionSelections.setVisibility(GONE);
        }
        // If there is a selected cell
        if (mSelectedCellView != null)
            mSelectedCellView.toggleSelected();
        // Highlighted last move
        Board.Move lastMove = mBoard.getLastMove();
        if (lastMove != null) {
            mCellViews[lastMove.getOldPosition()].toggleHighlighted();
            mCellViews[lastMove.getNewPosition()].toggleHighlighted();
        }
        // In hidden mode, highlight CellView at selected position
        if (mIsHidden && mSelectedPosition != -1) {
            mCellViews[mSelectedPosition].toggleHighlighted();
        }
        // If there is selected piece then presenting it legal moves.
        if (mPieceViewMap.containsKey(mSelectedPosition) && !mIsHidden) {
            mCellViews[mSelectedPosition].toggleHighlighted();
            ArrayList<Integer> legalMoves = mPieceViewMap.get(mSelectedPosition).getPiece().getLegalMoves();
            for (int position: legalMoves) {
                mCellViews[position].toggleLegalMove(mBoard.getPiece(position) != null);
            }
        }
        // Animate move of all PieceViews
        if (!mIsHidden) {
            for (PieceView pieceView : mPieceViewMap.values()) {
                if (pieceView.getPiece().getPosition() != mSelectedPosition)
                    pieceView.animateMove();
            }
        }
    }
    public void setFen(String fen) {
        initBoard(fen);
    }
    public void setPerspective(boolean white) {
        mIsWhitePerspective = white;
        for (PieceView pieceView: mPieceViewMap.values()) {
            pieceView.setPerspective(white);
        }
        if (white) {
            setRotation(0);
        } else {
            setRotation(180);
        }

    }
    public void setFinishedMoveListener(FinishedMoveListener finishedMoveListener) {
        mFinishedMoveListener = finishedMoveListener;
    }
    void finishMove() {
        updateEvaluation();
        if (mFinishedMoveListener != null) {
            mFinishedMoveListener.onFinishMove(mBoard.getLastMove());
        }
        invalidate();
    }
    void updateEvaluation() {
        if (!mIsEvaluated)
            return;

        ExecutorService executorService = ((MyApplication)mContext.getApplicationContext()).getExecutorService();
        Handler mainHandler = ((MyApplication)mContext.getApplicationContext()).getMainHandler();
        String fen = mBoard.getFen();
        // Set best move to null and clear all arrows
        mBestMove = null;
        mArrows.clear();
        invalidate();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Pair<Integer, Integer> bestMove = new ChessPositionEvaluator(fen).getBestMove();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Only update if fen is the newest
                        Log.d(TAG, "FEN = " + String.valueOf(fen));
                        Log.d(TAG, "FEN = " + String.valueOf(mBoard.getFen()));
                        if (fen.compareTo(mBoard.getFen()) == 0) {
                            mBestMove = bestMove;
                            invalidate();
                        }
                    }
                });
            }
        });
    }
    void showPromotionBox() {
        int promotionBoxX = (int)mPromotedPieceView.getPiece().getPosition() % 8 * (getWidth() / 8);
        int promotionBoxY = (int)mPromotedPieceView.getPiece().getPosition() / 8 * (getHeight() / 8);
        promotionBoxY = Math.min(promotionBoxY, getHeight() / 2);
        if (mPromotedPieceView.getPiece().isWhite()) {
            mWhitePromotionSelections.setVisibility(VISIBLE);
            mWhitePromotionSelections.setX(promotionBoxX);
            mWhitePromotionSelections.setY(promotionBoxY);
        } else {
            mBlackPromotionSelections.setVisibility(VISIBLE);
            mBlackPromotionSelections.setX(promotionBoxX);
            mBlackPromotionSelections.setY(promotionBoxY);
        }
    }
    void finishPromotion() {
        if (mPromotedPieceView != null) {
            if (mBoard.isPromoting()) {
                rollbackLastMove();
            } else {
                // Update evaluation when not rolling back
                finishMove();
            }
        }
        mPromotedPieceView = null;
        invalidate();
    }
    public void movePiece(Board.Move move) {
        Log.d(TAG, "MOVE FROM " + String.valueOf(move.getOldPosition()));
        if (mPieceViewMap.containsKey(move.getOldPosition())) {
            setSelectedPiece(move.getOldPosition(), false);
            placeSelectedPiece(move.getNewPosition());
            if (mBoard.isPromoting()) {
                promoteSelectedPiece(move.getPromotionTo());
            }
        }
    }
    public void setLastMoveEvaluation(int position, int state) {
        // Update last move evaluation
        int iconWidth = mLastMoveEvalView.getWidth();
        int iconHeight = mLastMoveEvalView.getHeight();
        float iconX = position % 8 * mCellViews[0].getWidth() + mCellViews[0].getWidth() - (float)iconWidth / 2;
        float iconY = position / 8 * mCellViews[0].getHeight() - (float)iconHeight / 2;
        if (getRotation() == 180) {
            iconX -= mCellViews[0].getWidth();
            iconY += mCellViews[0].getHeight();
        }
        mLastMoveEvalView.setX(iconX);
        mLastMoveEvalView.setY(iconY);
        switch (state) {
            case CORRECT_MOVE:
                mLastMoveEvalView.setImageResource(R.drawable.correct_icon);
                break;
            case WRONG_MOVE:
                mLastMoveEvalView.setImageResource(R.drawable.missed_icon);
                break;
            default:
                mLastMoveEvalView.setImageResource(0);
        }
    }
    @Override
    public void rollbackLastMove() {
        // Reset all selection
        mPromotedPieceView = null;
        mSelectedPosition = -1;
        mSelectedCellView = null;
        // Try roll back last move
        Board.Move move = mBoard.rollbackLastMove();
        // If succeed
        if (move != null) {
            // If the move is promotion, then revert it.
            if (move.getPromotionFrom() != null) {
                if (mPieceViewMap.containsKey(move.getNewPosition())) {
                    mPieceViewMap.get(move.getNewPosition()).setPiece(mBoard.getPiece(move.getOldPosition()));
                }
            }
            // Update piece view map
            if (mPieceViewMap.containsKey(move.getNewPosition())) {
                // If there is a PieceView at old position, then remove it.
                if (mPieceViewMap.containsKey(move.getOldPosition())) {
                    this.removeView(mPieceViewMap.get(move.getOldPosition()));
                }
                // If there is a PieceView at new position, then move it to old position.
                // Then trigger moving animation.
                if (mPieceViewMap.containsKey(move.getNewPosition())) {
                    mPieceViewMap.put(move.getOldPosition(), mPieceViewMap.get(move.getNewPosition()));
//                    mPieceViewMap.get(move.getOldPosition()).animateMove();
                    mPieceViewMap.remove(move.getNewPosition());
                }
            }
            // If the move captured a piece then put it back
            if (move.getCapturedPiece() != null) {
                PieceView pieceView = new PieceView(mContext, move.getCapturedPiece(), this);
                this.addView(pieceView);
                mPieceViewMap.put(move.getCapturedPiece().getPosition(), pieceView);
            }
            // Update evaluation
            updateEvaluation();
            setLastMoveEvaluation(0,0);

            invalidate();
        }
    }
    public Board getBoard() {
        return mBoard;
    }
    public void toggleDisabled() {
        mDisabled = !mDisabled;
        invalidate();
    }
    public void setDisabled(boolean disabled) {
        mDisabled = disabled;
    }
    public void toggleHidden() {
        mIsHidden = !mIsHidden;
        invalidate();
    }
    public void setHidden(boolean hidden) {
        mIsHidden = hidden;
    }
    public void toggleEvaluation() {
        mIsEvaluated = !mIsEvaluated;
        updateEvaluation();
    }
    public void setEvaluation(boolean evaluation) {
        mIsEvaluated = evaluation;
        updateEvaluation();
    }
    // Below is the implementation of BoardController.
    @Override
    public boolean setSelectedPiece(int position, boolean preserved) {
        PieceView pieceView = mPieceViewMap.get(position);
        // Clear promotion when there is new piece selection
        finishPromotion();
        if (mSelectedPosition == -1) {
            // If there is no currently selected piece then just select it
            mSelectedPosition = position;
        } else if (!preserved) {
            // If not need to preserve current selected piece
            // then set the Z of old selection to normal and select new piece.
            if (mPieceViewMap.containsKey(mSelectedPosition))
                mPieceViewMap.get(mSelectedPosition).setZ(2);
            mSelectedPosition = position;
        }
        if (mPieceViewMap.containsKey(mSelectedPosition)) {
            // Set Z to 3 so that it is above all other pieces.
            mPieceViewMap.get(mSelectedPosition).setZ(3);
        }
        invalidate();
        // Return if the select action is successful.
        return mSelectedPosition == position;
    }
    @Override
    public void placeSelectedPiece(int position) {
        // TODO: Consider doing BoardView update based on Board object in separate function (e.g onDraw).
        // TODO: Implement Setup Board separately
        // If there is a selected piece then carry out action.
        if (mSelectedPosition != -1) {
            // Save the old position.
            int oldPosition = mSelectedPosition;
            // Try moving piece to new position.
            boolean success = false;
            if (mPieceViewMap.containsKey(mSelectedPosition)) {
                // Set Z back to 2 so that it is on the same level of other pieces.
                mPieceViewMap.get(mSelectedPosition).setZ(2);
                success = mPieceViewMap.get(mSelectedPosition).getPiece().moveTo(position);
            }
            // If the new position is different from old position, i.e user clicks the other other cell.
            if (oldPosition != position) {
                // If the move is successful/legal
                if (success) {
                    PieceView selectedPieceView = mPieceViewMap.get(mSelectedPosition);
                    // Check if move is promotion
                    boolean isPromotion = selectedPieceView.getPiece().isPromoting();
                    if (isPromotion) {
                        mPromotedPieceView = selectedPieceView;
                    }
                    Board.Move move = mBoard.getLastMove();
                    // If clicked cell contains a piece, then remove it, i.e capturing piece.
                    if (move.getCapturedPiece() != null) {
                        Log.d(TAG, "Captured piece");
                        PieceView pieceView = mPieceViewMap.get(move.getCapturedPiece().getPosition());
                        mPieceViewMap.remove(position);
                        this.removeView(pieceView);
                    }
                    // Update piece's view position
                    mPieceViewMap.remove(oldPosition);
                    mPieceViewMap.put(position, selectedPieceView);

                    // TODO: add functionality to evaluation view
                    if (!isPromotion) {
                        // Update evaluation
                        finishMove();
                    } else {
                        mLastMoveEvalView.setImageResource(0);
                    }
                } else {
                    // Return null if the move is not successful
                    if (mFinishedMoveListener != null)
                        mFinishedMoveListener.onFinishMove(null);
                }
                // Deselect the piece.
                mSelectedPosition = -1;
            } else {
                if (mPieceViewMap.containsKey(mSelectedPosition)) {
                    // Move selected piece back to center of its cell
                    mPieceViewMap.get(mSelectedPosition).animateMove();
                }

            }
        }
        // Deselect the cell
        setSelectedCell(-1);
        invalidate();
    }
    @Override
    public void setSelectedCell(int pos) {
        if (mSelectedCellView != null) {
            mSelectedCellView.setZ(0);
        }
        if (pos >= 0 && pos < 64) {
            mSelectedCellView = mCellViews[pos];
            mSelectedCellView.setZ(1);
        } else {
            mSelectedCellView = null;
        }
        invalidate();
    }
    @Override
    public void promoteSelectedPiece(String pieceType) {
        if (mPromotedPieceView != null) {
            Piece piece = mPromotedPieceView.getPiece().promote(pieceType);
            if (piece != null) {
                mPromotedPieceView.setPiece(piece);
            }
        }
        finishPromotion();
    }
}
