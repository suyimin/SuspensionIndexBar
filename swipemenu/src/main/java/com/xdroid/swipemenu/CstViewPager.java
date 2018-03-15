package com.xdroid.swipemenu;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Intro: 解决 滑动冲突的 ViewPager
 * <p>
 * 因为ViewPager 和 SwipMenuLayout都是水平方向滑动的控件。
 * 所以在一起使用时会有冲突，
 * 使用本控件(CstViewPager)，可以在ViewPager的第一页使用左滑。在ViewPager的最后一页使用右滑菜单。
 */
public class CstViewPager extends ViewPager {

    private int mLastX, mLastY;

    public CstViewPager(Context context) {
        super(context);
    }

    public CstViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isHorizontalScroll(x, y)) {
                    //除了在 第一页的手指向右滑 ， 最后一页的左滑，其他时刻都是父控件需要拦截事件
                    if (isReactFirstPage() && isScrollRight(x)) {
                        intercept = false;
                    } else if (isReachLastPage() && isScrollLeft(x)) {
                        intercept = false;
                    } else {
                        intercept = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        mLastX = x;
        mLastY = y;

        boolean onInterceptTouchEvent = super.onInterceptTouchEvent(ev);
        return intercept || onInterceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    //是否在水平滑动
    private boolean isHorizontalScroll(int x, int y) {
        return Math.abs(y - mLastY) < Math.abs(x - mLastX);
    }

    //是否未到达最后一页
    private boolean isReachLastPage() {
        PagerAdapter adapter = getAdapter();
        if (null != adapter && adapter.getCount() - 1 == getCurrentItem()) {
            return true;
        } else {
            return false;
        }
    }

    //是否在第一页
    private boolean isReactFirstPage() {
        if (getCurrentItem() == 0) {
            return true;
        } else {
            return false;
        }
    }

    //是否左滑
    private boolean isScrollLeft(int x) {
        return x - mLastX < 0;
    }

    private boolean isScrollRight(int x) {
        return x - mLastX > 0;
    }
}
