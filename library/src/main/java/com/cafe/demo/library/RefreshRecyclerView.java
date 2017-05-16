package com.cafe.demo.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.cafe.demo.library.refresh.IRecyclerView;
import com.cafe.demo.library.refresh.OnRefreshListener;


/**
 * Created by cafe on 2017/5/3.
 */

public class RefreshRecyclerView extends ViewGroup implements NestedScrollingParent,
        NestedScrollingChild, IRecyclerView {

    private static final int INVALID_POINTER = -1;
    private static final String LOG_TAG = "refresh";
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final int ALPHA_ANIMATION_DURATION = 300;
    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private int mActivePointerId = INVALID_POINTER;
    //用来判断滑动控件是否在执行滑动回顶点的动画
    private boolean mIsBeingDragged;
    private int pointerIndex;
    private CircleImageView mCircleView;
    private int mTouchSlop;
    private int mMediumAnimationDuration;
    private DecelerateInterpolator mDecelerateInterpolator;
    private int mCircleDiameter;
    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private MaterialProgressDrawable mProgress;
    private int mSpinnerOffsetEnd;

    private int mTotalDragDistance = -1;    //256
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private int mCurrentTargetOffsetTop;
    private int mOriginalOffsetTop;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };
    private int mFrom;
    private RecyclerView mRecyclerView;
    private int mCircleViewIndex = -1;
    private boolean mReturningToStart;
    private boolean mRefreshing = false;
    private boolean mNestedScrollInProgress;
    private float mInitialDownY;
    private float mInitialMotionY;

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);
    private static final float DRAG_RATE = .5f;
    // Whether the client has set a custom starting position;
    boolean mUsingCustomStart;
    // Whether this item is scaled up rather than clipped
    boolean mScale = false;
    private Animation mAlphaStartAnimation;
    private RecyclerView.Adapter adapter;

    private Animation mAlphaMaxAnimation;
    private boolean mNotify;
    private Animation mScaleAnimation;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = mSpinnerOffsetEnd - Math.abs(mOriginalOffsetTop);
            } else {
                endTarget = mSpinnerOffsetEnd;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mCircleView.getTop();
            setTargetOffsetTopAndBottom(offset  /* requires update */);
            mProgress.setArrowScale(1 - interpolatedTime);
        }
    };
    private Animation mScaleDownAnimation;
    private static final int SCALE_DOWN_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;
    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };
    private float mStartingScale;
    private Animation mScaleDownToStartAnimation;
    OnRefreshListener mListener;
    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @SuppressLint("NewApi")
        @Override
        public void onAnimationEnd(Animation animation) {
            Log.i("refresh111", " onAnimationEnd");

            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress.setAlpha(MAX_ALPHA);
                mProgress.start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
                mCurrentTargetOffsetTop = mCircleView.getTop();
            } else {
                reset();
            }
        }
    };
    private boolean isAutoRefresh = true;
    private boolean isRefreshEnable = true;


    void reset() {
        mCircleView.clearAnimation();
        mProgress.stop();
        mCircleView.setVisibility(View.GONE);
        setColorViewAlpha(MAX_ALPHA);
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0 /* animation complete and view is hidden */);
        } else {
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop
                    /* requires update */);
        }
        mCurrentTargetOffsetTop = mCircleView.getTop();
    }

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        //ViewConfigurationi:包含了方法和标准的常量用来设置UI的超时、大小和距离
        //mTouchSlop:是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
//        ViewGroup默认情况下，出于性能考虑，会被设置成WILL_NOT_DROW，这样，ondraw就不会被执行了。
//        如果我们想重写一个viewgroup的ondraw方法，有两种方法：
//        1，构造函数中，给viewgroup设置一个颜色。
//        2，构造函数中，调用setWillNotDraw（false），去掉其WILL_NOT_DRAW flag。
//        在viewgroup初始化的时候，它调用了一个私有方法：initViewGroup，
//        它里面会有一句setFlags（WILLL_NOT_DRAW,DRAW_MASK）;相当于调用了
//        setWillNotDraw（true），所以说，对于ViewGroup，他就认为是透明的了，如果我们想要重写
//        onDraw，就要调用setWillNotDraw（false）。
        setWillNotDraw(false);

        //set the speed of animator
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        //set how big the progressView should be
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * metrics.density);//40dp

        //创建进度view
        createProgressView();

        //设置子控件是否由getChildDrawingOrder 方法定义的顺序来绘制
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerOffsetEnd = (int) (DEFAULT_CIRCLE_TARGET * metrics.density);


        mTotalDragDistance = mSpinnerOffsetEnd;

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        setNestedScrollingEnabled(true);

        //原始的偏移量为 -40dp 当前目标的顶部偏移量
        mOriginalOffsetTop = mCurrentTargetOffsetTop = -mCircleDiameter;

        //移动 progress 到顶部
        moveToStart(1.0f);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        mRecyclerView = new RecyclerView(context, attrs, defStyleAttr);
        mRecyclerView.setId(R.id.id_recyclerView);
        addView(mRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState = )
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if ()
            }

        });
    }


    private void createProgressView() {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT);
        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }

    void moveToStart(float interpolatedTime) {
        int targetTop = 0;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mCircleView.getTop();
        Log.i("offset", "offset  " + offset);
        Log.i("offset", "mCircleView.getTop()  " + mCircleView.getTop());
        setTargetOffsetTopAndBottom(offset);
    }

    /**
     * 使 progressView回到顶部
     *
     * @param offset 加载view 的偏移量
     */
    void setTargetOffsetTopAndBottom(int offset) {
        Log.i("offset", "setTargetOffsetTopAndBottom   ");
        //使 progressView置顶

        mCircleView.bringToFront();
        // Offset this view's vertical location by the specified number of pixels.
        ViewCompat.offsetTopAndBottom(mCircleView, offset);
        mCurrentTargetOffsetTop = mCircleView.getTop();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mRecyclerView == null) {
            return;
        }
        //mTarget的尺寸为match_parent，除去内边距
        mRecyclerView.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        //设置mCircleView的尺寸
        mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY));
        mCircleViewIndex = -1;

        // Get the index of the circleview.获取circleview的索引值，主要是为了后面重载getChildDrawingOrder时要用

        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index;
                break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();

        //将mTarget放在覆盖parent的位置（除去内边距）
        mRecyclerView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        //将mCircleView放在mTarget的平面位置上面居中，初始化时是完全隐藏在屏幕外的
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();
        mCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop,
                (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isRefreshEnable) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;


        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }


        /**
         *  如果 不可用 / 正在返回起点 / recycler可以向上滚动 / 正在刷新 /
         *  则不拦截
         *  即让recyclerView自己处理滚动事件
         */
        if (!isEnabled() || mReturningToStart || mRecyclerView.canScrollVertically(-1)
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            Log.i("intercept", "return false  ");
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());

                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);

                if (pointerIndex < 0) {
                    return false;
                }
                //the initial down location Y
                mInitialDownY = ev.getY(pointerIndex);
                Log.i("touch", "mInitialDownY  " + mInitialDownY);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                //1、判断滑动距离是否足够
                //2、设置 boolean 变量（是否拖拽）
                //3、progress 设置透明
                startDragging(y);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        Log.i("intercept", "mIsBeingDragged  " + mIsBeingDragged);
        Log.i("intercept", "action  " + (action == MotionEvent.ACTION_DOWN ? "ACTION_DOWN" : action == MotionEvent.ACTION_MOVE ? "ACTION_MOVE" : action == MotionEvent.ACTION_UP ? "ACTION_UP" : " "));

        //如果正在拖拽则拦截 即自己的touch处理该事件
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex = -1;
        Log.i("onTouchEvent", "onTouchEvent  begin");
        Log.i("onTouchEvent", "mReturningToStart   " + mReturningToStart);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        Log.i("onTouchEvent", "if  " + (!isEnabled() || mReturningToStart || mRecyclerView.canScrollVertically(-1)
                || mRefreshing || mNestedScrollInProgress));

        if (!isEnabled() || mReturningToStart || mRecyclerView.canScrollVertically(-1)
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("onTouchEvent", "onTouchEvent  ACTION_DOWN");
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                break;
            case MotionEvent.ACTION_MOVE: {
                Log.i("onTouchEvent", "onTouchEvent  ACTION_MOVE");
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                //1、判断滑动距离是否足够
                //2、设置 boolean 变量（是否拖拽）
                //3、progress 设置透明
                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) {
                        moveSpinner(overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;

    }

    @SuppressLint("NewApi")
    private void moveSpinner(float overscrollTop) {
        mProgress.showArrow(true);

        float originalDragPercent = overscrollTop / mTotalDragDistance;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
        float slingshotDist = mUsingCustomStart ? mSpinnerOffsetEnd - mOriginalOffsetTop
                : mSpinnerOffsetEnd;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        Log.i("mCircleView", "getVisibility" + (getVisibility()));

        // where 1.0f is a full circle
        if (mCircleView.getVisibility() != View.VISIBLE) {

            mCircleView.setVisibility(View.VISIBLE);
        }

        if (!mScale) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        }

        if (mScale) {
            setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
        }
        //该参数 由 0 变到1

        Log.i("moveSpinner", "overscrollTop < mTotalDragDistance ------------------------ " + (overscrollTop < mTotalDragDistance));
        Log.i("moveSpinner", "overscrollTop   " + (overscrollTop));
        Log.i("moveSpinner", "Math.min(1f, overscrollTop / mTotalDragDistance)   " + (Math.min(1f, overscrollTop / mTotalDragDistance)));


        /**
         * 这两个都是执行 透明度动画效果 一个是由深变浅
         * 另一个由浅变深
         */
        if (overscrollTop < mTotalDragDistance) {
            //若progress 透明度大于76
            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation();
            }
        } else {
            //若progress 透明度小于 255
            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation();
            }
        }

        //上面的计算 以及当前计算都是为了算出progress 的箭头跟随手指滑动的效果
        float strokeStart = adjustedPercent * .8f;

        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress.setArrowScale(Math.min(1f, adjustedPercent));

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress.setProgressRotation(rotation);


        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop  /* requires update */);

        Log.i("moveSpinner", "strokeStart   " + (strokeStart));
        Log.i("moveSpinner", "adjustedPercent   " + (adjustedPercent));
        Log.i("moveSpinner", "tensionPercent   " + (tensionPercent));
        Log.i("moveSpinner", "targetY   " + (targetY));
        Log.i("moveSpinner", "rotation   " + (rotation));

    }

    @SuppressLint("NewApi")
    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    @SuppressLint("NewApi")
    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
    }

    @SuppressLint("NewApi")
    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        // Pre API 11, alpha is used in place of scale. Don't also use it to
        // show the trigger point.
        if (mScale && isAlphaUsedForScale()) {
            return null;
        }
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress.setAlpha(
                        (int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        mCircleView.setAnimationListener(null);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(alpha);
        return alpha;
    }

    /**
     * 动画不为空，同时已经开始，且没有结束
     *
     * @param animation
     * @return
     */
    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    /**
     * mScale = false, this does an alpha animation.
     * mScale = true, this does a scale animation.
     *
     * @param progress
     */
    void setAnimationProgress(float progress) {

        ViewCompat.setScaleX(mCircleView, progress);
        ViewCompat.setScaleY(mCircleView, progress);

//            setColorViewAlpha((int) (progress * MAX_ALPHA));
//            ViewCompat.setScaleX(mCircleView, 1f);
//            ViewCompat.setScaleY(mCircleView, 1f);


    }

    /**
     * Pre API 11, alpha is used to make the progress circle appear instead of scale.
     */
    private boolean isAlphaUsedForScale() {
//        return android.os.Build.VERSION.SDK_INT < 11;
        return false;
    }

    @SuppressLint("NewApi")
    private void setColorViewAlpha(int targetAlpha) {
//        mCircleView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > mTotalDragDistance) {
            Log.i("finishSpinner", "finishSpinner  " + (overscrollTop > mTotalDragDistance));
            setRefreshing(true, true /* notify */);
        } else {
            // cancel refresh
            Log.i("finishSpinner", "cancel refresh  ");
            mRefreshing = false;
            mProgress.setStartEndTrim(0f, 0f);
            Animation.AnimationListener listener = null;
            if (!mScale) {
                listener = new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!mScale) {
                            startScaleDownAnimation(null);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                };
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
            mProgress.showArrow(false);
        }
    }

    void startScaleDownAnimation(Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownAnimation);
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    /**
     * 刷新未执行，返回起点  位移 透明度动画
     *
     * @param from
     * @param listener
     */
    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener);
        } else {
            mFrom = from;
            mAnimateToStartPosition.reset();
            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
                mCircleView.setAnimationListener(listener);
            }
            mCircleView.clearAnimation();
            mCircleView.startAnimation(mAnimateToStartPosition);
        }
    }

    /**
     * 刷新 未执行 ，返回起点  scale 动画
     *
     * @param from
     * @param listener
     */
    @SuppressLint("NewApi")
    private void startScaleDownReturnToStartAnimation(int from,
                                                      Animation.AnimationListener listener) {
        mFrom = from;
        mStartingScale = ViewCompat.getScaleX(mCircleView);
        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownToStartAnimation);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        Log.i("setRefreshing", mRefreshing + "  pre  mRefreshing");
        Log.i("setRefreshing", refreshing + "  pre  refreshing");

        if (mRefreshing != refreshing) {
            mNotify = notify;
            mRefreshing = refreshing;

            /*
             * 如果 mRefreshing = true 即正在刷新，则跳转animateOffsetToCorrectPosition()
             * 表示，使加载view回到原位，
             *
             *
             * mRefreshing = false，刷新完毕，则跳转startScaleDownAnimation()
             * 缩小 并使其消失加载view
             *
             * 最后都跳转到listener  在动画结束后，为true 则运行加载动画，否则reset 重置加载view
             *
             */
            if (mRefreshing) {
                Log.i("setRefreshing", "animateOffsetToCorrectPosition");

                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                Log.i("setRefreshing", "startScaleDownAnimation");
                startScaleDownAnimation(mRefreshListener);
            }
        }

        Log.i("setRefreshing", mRefreshing + "  aft  mRefreshing");
        Log.i("setRefreshing", refreshing + "  aft  refreshing");
        Log.i("setRefreshing", "  ---------------------------------");
    }

    /**
     * 刷新完成
     */
    public void completeRefresh() {
        setRefreshing(false);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = mSpinnerOffsetEnd + mOriginalOffsetTop;
            } else {
                endTarget = mSpinnerOffsetEnd;
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }


    @SuppressLint("NewApi")
    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mCircleView.setVisibility(View.VISIBLE);
        mProgress.setAlpha(MAX_ALPHA);
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleAnimation);
    }

    @SuppressLint("NewApi")
    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
            if (mProgress.getAlpha() != STARTING_PROGRESS_ALPHA)
                mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        }
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        this.adapter = adapter;
    }

    /**
     * 设置自动刷新
     * 1、移动到correct位置
     * 2、调用放大动画
     * 3、调用加载动画
     * 4、调用setRefresh(true)方法
     * 5、完成刷新
     */
    private void autoRefresh() {


        if (android.os.Build.VERSION.SDK_INT > 15) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.i("setAutoRefresh", "onGlobalLayout    " + isAutoRefresh);

                    if (!isAutoRefresh) {
                        if (android.os.Build.VERSION.SDK_INT > 15)
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        return;
                    }
                    setRefreshing(true);
                    mNotify = true;
                    if (android.os.Build.VERSION.SDK_INT > 15)
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);

                }
            });
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        autoRefresh();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    @Override
    public void init(RecyclerView.Adapter adapter, RecyclerView.LayoutManager manager) {
        setLayoutManager(manager);
        setAdapter(adapter);
    }

    @Override
    public void setOnRefreshListener(com.cafe.demo.library.refresh.OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 设置是否自动刷新，默认为true
     *
     * @param isAutoRefresh
     */
    @Override
    public void setAutoRefresh(boolean isAutoRefresh) {
        Log.i("setAutoRefresh", "setAutoRefresh    " + isAutoRefresh);
        this.isAutoRefresh = isAutoRefresh;
    }

    @Override
    public void setRefreshEnable(boolean isRefreshEnable) {
        this.isRefreshEnable = isRefreshEnable;
    }


}
