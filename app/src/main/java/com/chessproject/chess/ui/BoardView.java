package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chessproject.R;
import com.chessproject.chess.logic.Board;
import com.chessproject.chess.logic.Knight;
import com.chessproject.chess.logic.Piece;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardView extends FrameLayout implements BoardController {
    final static String TAG = "BoardView";
    final static int DEFAULT_WIDTH = 800;
    final static int DEFAULT_HEIGHT = 800;
    Context mContext;
    PieceView mSelectedPieceView = null;
    PieceView mPromotedPieceView = null;
    Board mBoard;
    CellView[] mCellViews = new CellView[64];
    HashMap<Integer, PieceView> mPieceViewMap = new HashMap<>();
    ArrayList<PieceView> mExtraPieceViews = new ArrayList<>();
    boolean mIsSetupBoard = false;
    CellView mSelectedCellView = null;
    ImageView mLastMoveEvalView = null;
    PromotionView mWhitePromotionSelections, mBlackPromotionSelections;
    ArrayList<Pair<Integer, Integer>> mArrows = new ArrayList<>();
    Paint mArrowPaint = new Paint();
    public void toggleSetupBoard() {
        mIsSetupBoard = !mIsSetupBoard;
        mBoard.clearHistory();
        requestLayout();
    }

    private void initBoard(String fen) {
        // Set clip children to false so that piece can be view even if it is outside of board.
        setClipChildren(false);
        // Set will not draw to false so that invalidate will trigger onDraw.
        setWillNotDraw(false);
        // Set arrow paint
        mArrowPaint.setARGB(200, 0, 255, 0);
        mArrowPaint.setStrokeWidth(2);
        // TODO: Dummy arrows only, remove after testing
        mArrows.add(new Pair<>(20, 2));
        mArrows.add(new Pair<>(8, 23));
        mArrows.add(new Pair<>(7, 24));
        mArrows.add(new Pair<>(56, 47));
        // Create board based on fen.
        mBoard = new Board(fen);
        // Create PieceView for each pieces in board.
        for (Piece piece: mBoard.getPieces()) {
            PieceView pieceView = new PieceView(mContext, piece, this);
            mPieceViewMap.put(piece.getPosition(), pieceView);
            this.addView(pieceView);
        }
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

        // TODO: Implement Setup Board separately
        Piece whiteKnight = new Knight(true, 9 * 8, mBoard);
        PieceView whiteKnightView = new PieceView(mContext, whiteKnight, this);
//        mExtraPieceViews.add(whiteKnightView);
//        this.addView(whiteKnightView);

        Piece blackKnight = new Knight(false, 10 * 8, mBoard);
        PieceView blackKnightView = new PieceView(mContext, blackKnight, this);
//        mExtraPieceViews.add(blackKnightView);
//        this.addView(blackKnightView);

        // Promotion views
        mWhitePromotionSelections = new PromotionView(mContext, true, this);
        mWhitePromotionSelections.setOrientation(LinearLayout.VERTICAL);
        mWhitePromotionSelections.setZ(3);
        mWhitePromotionSelections.setVisibility(GONE);
        this.addView(mWhitePromotionSelections);

        mBlackPromotionSelections = new PromotionView(mContext, false, this);
        mBlackPromotionSelections.setOrientation(LinearLayout.VERTICAL);
        mBlackPromotionSelections.setZ(3);
        mBlackPromotionSelections.setVisibility(GONE);
        this.addView(mBlackPromotionSelections);
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
    public boolean onTouchEvent(MotionEvent event) {
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
                if (event.getX() >= 0 && event.getX() <= getWidth() && event.getY() >= 0 && event.getY() <= getHeight())
                    placeSelectedPiece(position);
                setSelectedCell(-1);
                break;
        }
        // Board always consume motion event.
        return true;
    }
    @Override
    public void onDrawForeground(@NonNull Canvas canvas) {
        super.onDrawForeground(canvas);
        // arrow head width is 3/4 of cell width
        float arrowHeadWidth = (getWidth() / 8f) * 3f / 4f;
        // arrow head length is 3/4 of cell width
        float arrowHeadLength = (getWidth() / 8f) * 3f / 4f;
        // arrow stem width is 1/2 of arrow head width
        float arrowStemWidth = arrowHeadWidth / 2;
        // Draw all arrows
        for (Pair<Integer, Integer> arrow: mArrows) {
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

            canvas.drawPath(path, mArrowPaint);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // Clear state of all cells.
        for (int i = 0; i < 64; ++i) {
            mCellViews[i].clearState();
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
        // If there is selected piece then presenting it legal moves.
        if (mSelectedPieceView != null) {
            ArrayList<Integer> legalMoves = mSelectedPieceView.getPiece().getLegalMoves();
            for (int position: legalMoves) {
                mCellViews[position].toggleLegalMove(mBoard.getPiece(position) != null);
            }
        }
        // Animate move of all PieceViews
        for (PieceView pieceView: mPieceViewMap.values()) {
            if (pieceView != mSelectedPieceView)
                pieceView.animateMove();
        }
    }
    // Below is the implementation of BoardController.
    @Override
    public boolean setSelectedPiece(PieceView pieceView, boolean preserved) {
        // Clear promotion when there is new piece selection
        finishPromotion();
        if (mSelectedPieceView == null) {
            // If there is no currently selected piece then just select it
            mSelectedPieceView = pieceView;
        } else if (!preserved) {
            // If not need to preserve current selected piece
            // then set the Z of old selection to normal and select new piece.
            mSelectedPieceView.setZ(1);
            mSelectedPieceView = pieceView;
        }
        // TODO: Implement Setup Board separately
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
        // Set Z to 2 so that it is above all other pieces.
        mSelectedPieceView.setZ(2);
        invalidate();
        // Return if the select action is successful.
        return mSelectedPieceView == pieceView;
    }

    @Override
    public void placeSelectedPiece(int position) {
        // TODO: Consider doing BoardView update based on Board object in separate function (e.g onDraw).
        // TODO: Implement Setup Board separately
        // If there is a selected piece then carry out action.
        if (mSelectedPieceView != null) {
            // Save the old position.
            int oldPosition = mSelectedPieceView.getPiece().getPosition();
            // Try moving piece to new position.
            boolean success = mSelectedPieceView.getPiece().moveTo(position);
            // Animate move the new position.
//            mSelectedPieceView.animateMove();
            // Set Z back to 1 so that it is on the same level of other pieces.
            mSelectedPieceView.setZ(1);
            // If the new position is different from old position, i.e user clicks the other other cell.
            if (oldPosition != position) {
                // If the move is successful/legal
                if (success) {
                    // Check if move is promotion
                    boolean isPromotion = mSelectedPieceView.getPiece().isPromoting();
                    if (isPromotion) {
                        mPromotedPieceView = mSelectedPieceView;
                    }
                    // If clicked cell contains a piece, then remove it, i.e capturing piece.
                    if (mPieceViewMap.containsKey(position)) {
                        PieceView pieceView = mPieceViewMap.get(position);
                        mPieceViewMap.remove(position);
                        this.removeView(pieceView);
                    }
                    // Update piece's view position
                    mPieceViewMap.remove(oldPosition);
                    mPieceViewMap.put(position, mSelectedPieceView);
                    // Update last move evaluation
                    int iconWidth = mLastMoveEvalView.getWidth();
                    int iconHeight = mLastMoveEvalView.getHeight();
                    float iconX = mSelectedCellView.getX() + mSelectedCellView.getWidth() - (float)iconWidth / 2;
                    float iconY = mSelectedCellView.getY() - (float)iconHeight / 2;
                    mLastMoveEvalView.setX(iconX);
                    mLastMoveEvalView.setY(iconY);
                    // TODO: add functionality to evaluation view
                    if (!isPromotion) {
                        switch (mBoard.getLastMoveEvaluation()) {
                            case 0:
                                mLastMoveEvalView.setImageResource(0);
                                break;
                            case 1:
                                mLastMoveEvalView.setImageResource(R.drawable.missed_icon);
                                break;
                            case 2:
                                mLastMoveEvalView.setImageResource(R.drawable.correct_icon);
                                break;
                        }
                    } else {
                        mLastMoveEvalView.setImageResource(0);
                    }
                } else if (mIsSetupBoard) {
                    // If it is a setup board then remove a piece when it go out side the board/failed move.
                    mPieceViewMap.remove(oldPosition);
                    this.removeView(mSelectedPieceView);
                }
                // Deselect the piece.
                mSelectedPieceView = null;
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
    public void showPromotionBox() {
        int promotionBoxX = (int)mPromotedPieceView.getPiece().getPosition() % 8 * (getWidth() / 8);
        int promotionBoxY = (int)mPromotedPieceView.getPiece().getPosition() / 8 * (getHeight() / 8);
        promotionBoxY = Math.min(promotionBoxY, getHeight() / 2);
        if (mBoard.isWhiteTurn()) {
            mWhitePromotionSelections.setVisibility(VISIBLE);
            mWhitePromotionSelections.setX(promotionBoxX);
            mWhitePromotionSelections.setY(promotionBoxY);
        } else {
            mBlackPromotionSelections.setVisibility(VISIBLE);
            mBlackPromotionSelections.setX(promotionBoxX);
            mBlackPromotionSelections.setY(promotionBoxY);
        }
    }
    public void finishPromotion() {
        if (mBoard.isPromoting()) {
            rollbackLastMove();
        }
        mPromotedPieceView = null;
        invalidate();
    }
    public void rollbackLastMove() {
        // Reset all selection
        mPromotedPieceView = null;
        mSelectedPieceView = null;
        mSelectedCellView = null;
        // Try roll back last move
        Board.Move move = mBoard.rollbackLastMove();
        // If succeed
        if (move != null) {
            // If the move is promotion, then revert it.
            if (move.getPromotionFrom() != null) {
                if (mPieceViewMap.containsKey(move.getNewPosition())) {
                    mPieceViewMap.get(move.getNewPosition()).setPiece(move.getPromotionFrom());
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
                mPieceViewMap.put(move.getNewPosition(), pieceView);
            }
            invalidate();
        }
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
