package com.alibaba.android.bindingx.plugin.weex;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

public class RecycleUtil {
    public static int recyclerVerticalScrollOffset(@NonNull RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int position = manager.findFirstVisibleItemPosition();
            View firstItemView = manager.findViewByPosition(position);
            return firstItemView.getTop();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] firstVisibleItems = manager.findFirstVisibleItemPositions(null);
            if (firstVisibleItems.length > 0) {
                View firstItemView = manager.findViewByPosition(firstVisibleItems[0]);
                return firstItemView.getTop();
            }
        }
        return recyclerView.computeVerticalScrollOffset();
    }
}
