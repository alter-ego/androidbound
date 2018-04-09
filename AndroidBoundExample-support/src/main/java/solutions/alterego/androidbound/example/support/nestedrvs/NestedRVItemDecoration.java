package solutions.alterego.androidbound.example.support.nestedrvs;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class NestedRVItemDecoration extends RecyclerView.ItemDecoration {

    int categoryMargin = 0;

    public NestedRVItemDecoration(Resources resources) {
        categoryMargin = (int) (resources.getDisplayMetrics().density * 16);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);

        if (pos > 0) {

            outRect.top = categoryMargin;

            if (pos % 2 != 0) {
                outRect.left = categoryMargin;
                outRect.right = outRect.left / 2;
            } else {
                outRect.right = categoryMargin;
                outRect.left = outRect.right / 2;
            }

            outRect.bottom = categoryMargin;
        } else {
            outRect.top = categoryMargin / 2;
            outRect.left = categoryMargin / 2;
            outRect.right = categoryMargin / 2;
        }
    }

}
