package com.chessproject.chess.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chessproject.R;

public class PromotionView extends LinearLayout {
    final static String TAG = "PromotionView";
    Context mContext;
    BoardController mBoardController;

    public PromotionView(Context context, boolean white, BoardController boardController) {
        super(context);

        mContext = context;
        mBoardController = boardController;
        // Add ImageViews of pieces
        // Set tag to each view
        ImageView queen = new ImageView(mContext);
        queen.setTag("q");
        ImageView rook = new ImageView(mContext);
        rook.setTag("r");
        ImageView bishop = new ImageView(mContext);
        bishop.setTag("b");
        ImageView knight = new ImageView(mContext);
        knight.setTag("n");
        knight.setBackgroundColor(Color.RED);
        // Add view to layout
        this.addView(queen);
        this.addView(rook);
        this.addView(bishop);
        this.addView(knight);
        // Set image resource
        // TODO: Reorder when add all pieces assets: QUEEN -> ROOK -> BISHOP -> KNIGHT
        if (white) {
            queen.setImageResource(R.drawable.white_queen);
            rook.setImageResource(R.drawable.white_rook);
            bishop.setImageResource(R.drawable.white_bishop);
            knight.setImageResource(R.drawable.white_knight);
        } else {
            queen.setImageResource(R.drawable.black_queen);
            rook.setImageResource(R.drawable.black_rook);
            bishop.setImageResource(R.drawable.black_bishop);
            knight.setImageResource(R.drawable.black_knight);
        }
        queen.setBackgroundColor(Color.WHITE);
        rook.setBackgroundColor(Color.WHITE);
        bishop.setBackgroundColor(Color.WHITE);
        knight.setBackgroundColor(Color.WHITE);

        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBoardController.promoteSelectedPiece((String) v.getTag());
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int cellWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

        int measureSpec = MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).measure(measureSpec, measureSpec);
        }
    }
}
