package com.example.huangming.swipelayout;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 左滑删除控件，用法见同级目录下的readme文件
 *
 * @author Huangming
 * @date 2016/7/6
 * @modified 增加一个关闭的方法，增加一个拖出完成监听器  Huangming 2016/10/31
 * @modified 支持正常显示部分可点击  Huangming 2016/12/2
 */
public class SwipeLayout extends LinearLayout {

    private ViewDragHelper viewDragHelper;
    private View contentView;
    private View actionView;
    private int dragDistance;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int draggedX;

    private boolean isOpened = false;

	private int mLastXIntercept;
 
    private OnSwipeLayoutOpenedListener listener;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }

    @Override
    protected void onFinishInflate() {
        contentView = getChildAt(0);
        actionView = getChildAt(1);
        actionView.setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        dragDistance = actionView.getMeasuredWidth();
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int i) {
            return view == contentView || view == actionView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            draggedX = left;
            if (changedView == contentView) {
                actionView.offsetLeftAndRight(dx);
            } else {
                contentView.offsetLeftAndRight(dx);
            }
            if (actionView.getVisibility() == View.GONE) {
                actionView.setVisibility(View.VISIBLE);
            }
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                final int leftBound = getPaddingLeft();
                final int minLeftBound = -leftBound - dragDistance;
                final int newLeft = Math.min(Math.max(minLeftBound, left), 0);
                return newLeft;
            } else {
                final int minLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() - dragDistance;
                final int maxLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() + getPaddingRight();
                final int newLeft = Math.min(Math.max(left, minLeftBound), maxLeftBound);
                return newLeft;
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragDistance;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			boolean settleToOpen;
			if (xvel > AUTO_OPEN_SPEED_LIMIT) {
				settleToOpen = false;
			} else if (xvel < -AUTO_OPEN_SPEED_LIMIT) {
				settleToOpen = true;
			} else if (isOpened) {
				if (draggedX > -dragDistance * 0.9) {
					settleToOpen = false;
				} else {
					settleToOpen = true;
				}
			} else {
				if (draggedX <= -dragDistance * 0.1) {
					settleToOpen = true;
				} else {
					settleToOpen = false;
				}
			}
			isOpened = settleToOpen;
			final int settleDestX = settleToOpen ? -dragDistance : 0;
			viewDragHelper.smoothSlideViewTo(contentView, settleDestX, 0);
			ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
			if (settleToOpen && listener != null) {
				listener.onSwipeLayoutOpened();
			}
        }
    }

   
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        viewDragHelper.shouldInterceptTouchEvent(ev);
        
        boolean intercepted = false;
        int action = MotionEventCompat.getActionMasked(ev);
        int x = (int) ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastXIntercept = x;
            onTouchEvent(ev);
			intercepted = false;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = x - mLastXIntercept;
			if (Math.abs(deltaX) > 10) {
				intercepted = true;
			} else {
				intercepted = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			intercepted = false;
			break;
		default:
			break;
		}
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 关闭拖出的内容
     *
     * @param
     * @return
     *
     * @author Huangming
     * @date 2016/10/31
     * @modified [describe][editor][date]
     */
    public void close(){
        viewDragHelper.smoothSlideViewTo(contentView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        isOpened = false;
    }

    /**
     * 设置监听器
     *
     * @param
     * @return
     *
     * @author Huangming
     * @date 2016/10/31
     * @modified [describe][editor][date]
     */
    public void setOnSwipelayoutOpenedListener(OnSwipeLayoutOpenedListener listener){
        this.listener = listener;
    }

    /**
     * 拖出完成监听器
     *
     * @author Huangming
     * @date 2016/10/31
     * @modified [describe][editor][date]
     */
    public interface OnSwipeLayoutOpenedListener {
        void onSwipeLayoutOpened();
    }
}
