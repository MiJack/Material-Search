package cn.mijack.persistentsearchdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Mr.Yuan on 2017/8/10.
 */
public class ItemDivider extends RecyclerView.ItemDecoration {
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int childCount = layoutManager.getChildCount();
        int left = 0;
        int right = parent.getWidth();
        int top = 0;
        int height = 1;
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FF737373"));
        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            if (i != 0) {
                top += height;
                c.drawRect(left, top, right, top + height, paint);
            }
            top += child.getHeight();
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int position = layoutManager.getPosition(view);
        if (position != 0) {
            outRect.top = 1;
        }
    }
}
