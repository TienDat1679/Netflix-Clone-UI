package com.netflixcloneui.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class ViewPager2ViewHeightAnimator {

    private ViewPager2 viewPager2;

    public void setViewPager2(ViewPager2 viewPager2) {
        if (this.viewPager2 != viewPager2) {
            if (this.viewPager2 != null) {
                this.viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback);
            }
            this.viewPager2 = viewPager2;
            if (this.viewPager2 != null) {
                this.viewPager2.registerOnPageChangeCallback(onPageChangeCallback);
            }
        }
    }

    private LinearLayoutManager getLayoutManager() {
        if (viewPager2 == null || viewPager2.getChildCount() == 0) return null;
        View child = viewPager2.getChildAt(0);
        if (child instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) child;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                return (LinearLayoutManager) recyclerView.getLayoutManager();
            }
        }
        return null;
    }

    private final ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            recalculate(position, positionOffset);
        }
    };

    public void recalculate(int position, float positionOffset) {
        LinearLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return;

        View leftView = layoutManager.findViewByPosition(position);
        if (leftView == null) return;

        View rightView = layoutManager.findViewByPosition(position + 1);

        if (viewPager2 != null) {
            int leftHeight = getMeasuredViewHeightFor(leftView);
            int height;

            if (rightView != null) {
                int rightHeight = getMeasuredViewHeightFor(rightView);
                height = leftHeight + (int) ((rightHeight - leftHeight) * positionOffset);
            } else {
                height = leftHeight;
            }

            ViewGroup.LayoutParams layoutParams = viewPager2.getLayoutParams();
            layoutParams.height = height;
            viewPager2.setLayoutParams(layoutParams);
            viewPager2.invalidate();
        }
    }

    private int getMeasuredViewHeightFor(@NonNull View view) {
        int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
        int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(wMeasureSpec, hMeasureSpec);
        return view.getMeasuredHeight();
    }
}

