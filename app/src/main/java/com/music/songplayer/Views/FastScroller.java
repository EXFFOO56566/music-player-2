package com.music.songplayer.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.songplayer.Common;
import com.music.songplayer.R;
import com.music.songplayer.Utils.BubbleTextGetter;
import com.music.songplayer.Utils.TypefaceHelper;

import static androidx.recyclerview.widget.RecyclerView.OnScrollListener;

public class FastScroller extends LinearLayout {
    private static final int BUBBLE_ANIMATION_DURATION = 1000;
    private static final int TRACK_SNAP_RANGE = 5;
    private final ScrollListener scrollListener = new ScrollListener();
    private TextView bubble;
    private View handle;
    private View mLineView;
    private RecyclerView recyclerView;
    private int height;

    private ObjectAnimator currentAnimator = null;

    public FastScroller(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    public FastScroller(final Context context) {
        super(context);
        initialise(context);
    }

    public FastScroller(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    private void initialise(Context context) {

        setOrientation(HORIZONTAL);
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.recyclerview_fastscroller, this, true);
        bubble = (TextView) findViewById(R.id.fastscroller_bubble);
        bubble.setTypeface(TypefaceHelper.getTypeface(Common.getInstance(), TypefaceHelper.FUTURA_BOLD));
        handle = findViewById(R.id.fastscroller_handle);
        mLineView = findViewById(R.id.view_line);
        hideViews();
        bubble.setVisibility(INVISIBLE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < handle.getX())
                    return false;
                if (currentAnimator != null)
                    currentAnimator.cancel();
                if (bubble.getVisibility() == INVISIBLE)
                    showBubble();
                handle.setSelected(true);
            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                setBubbleAndHandlePosition(y);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handle.setSelected(false);
                hideBubble();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;
            if (handle.getY() == 0)
                proportion = 0f;
            else if (handle.getY() + handle.getHeight() >= height - TRACK_SNAP_RANGE)
                proportion = 1f;
            else
                proportion = y / (float) height;
            int targetPos = getValueInRange(0, itemCount - 1, (int) (proportion * (float) itemCount));

            if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            }
            String bubbleText = ((BubbleTextGetter) recyclerView.getAdapter()).getTextToShowInBubble(targetPos);
            bubble.setText(bubbleText);
        }
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void setBubbleAndHandlePosition(float y) {
        int bubbleHeight = bubble.getHeight();
        int handleHeight = handle.getHeight();
        handle.setY(getValueInRange(0, height - handleHeight, (int) (y - handleHeight / 2)));
        bubble.setY(getValueInRange(0, height - bubbleHeight - handleHeight / 2, (int) (y - bubbleHeight)));
    }

    private void showBubble() {
        bubble.setVisibility(VISIBLE);
        showViews();
        if (currentAnimator != null)
            currentAnimator.cancel();
        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 0f, 1f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.start();
    }

    private void hideBubble() {
        if (currentAnimator != null)
            currentAnimator.cancel();

        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bubble.setVisibility(INVISIBLE);
                hideViews();
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                bubble.setVisibility(INVISIBLE);
                hideViews();
                currentAnimator = null;
            }
        });
        currentAnimator.start();
    }

    private class ScrollListener extends OnScrollListener {
        @Override
        public void onScrolled(RecyclerView rv, int dx, int dy) {
            if (handle.isSelected()) {
                return;
            }
            View firstVisibleView = recyclerView.getChildAt(0);
            int firstVisiblePosition = recyclerView.getChildLayoutPosition(firstVisibleView);
            int visibleRange = recyclerView.getChildCount();
            int lastVisiblePosition = firstVisiblePosition + visibleRange;
            int itemCount = recyclerView.getAdapter().getItemCount();
            int position;
            if (firstVisiblePosition == 0)
                position = 0;
            else if (lastVisiblePosition == itemCount)
                position = itemCount;
            else
                position = (int) (((float) firstVisiblePosition / (((float) itemCount - (float) visibleRange))) * (float) itemCount);
            float proportion = (float) position / (float) itemCount;
            setBubbleAndHandlePosition(height * proportion);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                hideViews();
            } else {
                showViews();
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    }

    private void hideViews() {
        new Handler().postDelayed(() -> {
            (handle)
                    .animate()
                    .translationX(handle.getWidth()+50)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
            (mLineView)
                    .animate()
                    .translationX(handle.getWidth()+50)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
        }, 1000);
    }

    private void showViews() {
        (handle)
                .animate()
                .translationX(0)
                .setInterpolator(new DecelerateInterpolator(2));
        (mLineView)
                .animate()
                .translationX(0)
                .setInterpolator(new DecelerateInterpolator(2));
    }
}