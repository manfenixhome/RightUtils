package com.rightutils.rightutils.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by Anton Maniskevich on 5/13/15.
 */
public class RightSwipeRefreshLayour extends ViewGroup {
	// Maps to ProgressBar.Large style
	public static final int LARGE = MaterialProgressDrawable.LARGE;
	// Maps to ProgressBar default style
	public static final int DEFAULT = MaterialProgressDrawable.DEFAULT;

	private static final String TAG = RightSwipeRefreshLayour.class.getSimpleName();

	private static final int MAX_ALPHA = 255;
	private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

	private static final int CIRCLE_DIAMETER = 40;
	private static final int CIRCLE_DIAMETER_LARGE = 56;

	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
	private static final int INVALID_POINTER = -1;
	private static final float DRAG_RATE = .5f;

	// Max amount of circle that can be filled by progress during swipe gesture,
	// where 1.0 is a full circle
	private static final float MAX_PROGRESS_ANGLE = .8f;

	private static final int SCALE_DOWN_DURATION = 150;

	private static final int ALPHA_ANIMATION_DURATION = 300;

	private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

	private static final int ANIMATE_TO_START_DURATION = 200;

	// Default background for the progress spinner
	private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
	// Default offset in dips from the top of the view to where the progress spinner should stop
	private static final int DEFAULT_CIRCLE_TARGET = 64;

	private View mTarget; // the target of the gesture
	private OnRefreshListener mListener;
	private boolean mRefreshing = false;
	private int mTouchSlop;
	private float mTotalDragDistance = -1;
	private int mMediumAnimationDuration;
	private int mCurrentTargetOffsetTop;
	private int mCurrentTargetOffsetBottom;
	// Whether or not the starting offset has been determined.
	private boolean mOriginalOffsetCalculated = false;

	private float mInitialMotionY;
	private boolean mIsTopBeingDragged;
	private boolean mIsBottomBeingDragged;
	private int mActivePointerId = INVALID_POINTER;
	// Whether this item is scaled up rather than clipped
	private boolean mScale;

	// Target is returning to its start offset because it was cancelled or a
	// refresh was triggered.
	private boolean mReturningToStart;
	private final DecelerateInterpolator mDecelerateInterpolator;
	private static final int[] LAYOUT_ATTRS = new int[] {
			android.R.attr.enabled
	};

	private CircleImageView topCircleView;

	private CircleImageView bottomCircleView;

	private int topCircleViewIndex = -1;

	private int bottomCircleViewIndex = -1;

	protected int mFrom;

	private float mStartingScale;

	protected int mOriginalOffsetTop;
	protected int mOriginalOffsetBottom;

	private MaterialProgressDrawable topProgress;

	private MaterialProgressDrawable bottomProgress;

	private Animation mTopAlphaStartAnimation;

	private Animation mBottomAlphaStartAnimation;

	private Animation mTopAlphaMaxAnimation;

	private Animation mBottomAlphaMaxAnimation;

	private Animation mScaleDownToStartAnimation;

	private float mSpinnerFinalOffset;

	private boolean mNotify;

	private int mCircleWidth;

	private int mCircleHeight;
	private boolean needAddBottomPading;

	// Whether the client has set a custom starting position;
	private boolean mUsingCustomStart;

	private Animation.AnimationListener topRefreshListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (mRefreshing) {
				// Make sure the progress view is fully visible
				topProgress.setAlpha(MAX_ALPHA);
				topProgress.start();
				if (mNotify) {
					if (mListener != null) {
						mListener.onRefresh(TOP_REFRESH);
					}
				}
			} else {
				topProgress.stop();
				topCircleView.setVisibility(View.GONE);
				setTopColorViewAlpha(MAX_ALPHA);
				// Return the circle to its start position
				if (mScale) {
					setTopAnimationProgress(0 /* animation complete and view is hidden */);
				} else {
					setTopTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true /* requires update */);
				}
			}
			mCurrentTargetOffsetTop = topCircleView.getTop();
		}
	};

	private Animation.AnimationListener bottomRefreshListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (mRefreshing) {
				// Make sure the progress view is fully visible
				bottomProgress.setAlpha(MAX_ALPHA);
				bottomProgress.start();
				if (mNotify) {
					if (mListener != null) {
						mListener.onRefresh(BOTTOM_REFRESH);
					}
				}
			} else {
				bottomProgress.stop();
				bottomCircleView.setVisibility(View.GONE);
				setBottomColorViewAlpha(MAX_ALPHA);
				// Return the circle to its start position
				if (mScale) {
					setBottomAnimationProgress(0 /* animation complete and view is hidden */);
				} else {
					setBottomTargetOffsetTopAndBottom(mOriginalOffsetBottom - mCurrentTargetOffsetBottom, true /* requires update */);
				}
			}
			mCurrentTargetOffsetBottom = bottomCircleView.getTop();
		}
	};

	private void setTopColorViewAlpha(int targetAlpha) {
		topCircleView.getBackground().setAlpha(targetAlpha);
		topProgress.setAlpha(targetAlpha);
	}

	private void setBottomColorViewAlpha(int targetAlpha) {
		bottomCircleView.getBackground().setAlpha(targetAlpha);
		bottomProgress.setAlpha(targetAlpha);
	}

	public void setProgressViewOffset(boolean scale, int start, int end) {
		mScale = scale;
		topCircleView.setVisibility(View.GONE);
		mOriginalOffsetTop = mCurrentTargetOffsetTop = start;
		mSpinnerFinalOffset = end;
		mUsingCustomStart = true;
		topCircleView.invalidate();
	}

	public void setProgressViewEndTarget(boolean scale, int end) {
		mSpinnerFinalOffset = end;
		mScale = scale;
		topCircleView.invalidate();
	}

	public void setSize(int size) {
		if (size != MaterialProgressDrawable.LARGE && size != MaterialProgressDrawable.DEFAULT) {
			return;
		}
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		if (size == MaterialProgressDrawable.LARGE) {
			mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
		} else {
			mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
		}
		// force the bounds of the progress circle inside the circle view to
		// update by setting it to null before updating its size and then
		// re-setting it
		topCircleView.setImageDrawable(null);
		topProgress.updateSizes(size);
		topCircleView.setImageDrawable(topProgress);

		bottomCircleView.setImageDrawable(null);
		bottomProgress.updateSizes(size);
		bottomCircleView.setImageDrawable(bottomProgress);
	}

	public RightSwipeRefreshLayour(Context context) {
		this(context, null);
	}

	public RightSwipeRefreshLayour(Context context, AttributeSet attrs) {
		super(context, attrs);
		FrameLayout frameLayout = (FrameLayout) ((Activity)getContext()).getWindow().getDecorView();
		needAddBottomPading = false;
		int navigationBarHeight = getNavigationBarHeight();
		Log.i(TAG, "Need height="+navigationBarHeight);
		if (navigationBarHeight > 0) {
			for (int i = 0; i < frameLayout.getChildCount(); i++) {
				frameLayout.getChildAt(i).measure(0, 0);
				Log.i(TAG, "height="+frameLayout.getChildAt(i).getMeasuredHeight());
				if (frameLayout.getChildAt(i).getMeasuredHeight() == navigationBarHeight) {
					needAddBottomPading = true;
					Log.i(TAG, "found");
					break;
				}
			}
		}

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

		setWillNotDraw(false);
		mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

		final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
		setEnabled(a.getBoolean(0, true));
		a.recycle();

		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
		mCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);

		createProgressView();
		ViewCompat.setChildrenDrawingOrderEnabled(this, true);
		// the absolute offset has to take into account that the circle starts at an offset
		mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
		mTotalDragDistance = mSpinnerFinalOffset;
	}

	protected int getChildDrawingOrder(int childCount, int i) {
		if (topCircleViewIndex < 0) {
			return i;
		} else if (i == childCount - 1) {
			// Draw the selected child last
			return topCircleViewIndex;
		} else if (i >= topCircleViewIndex) {
			// Move the children after the selected child earlier one
			return i + 1;
		} else {
			// Keep the children before the selected child the same
			return i;
		}
	}

	private void createProgressView() {
		topCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2);
		topProgress = new MaterialProgressDrawable(getContext(), this);
		topProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
		topCircleView.setImageDrawable(topProgress);
		topCircleView.setVisibility(View.GONE);
		addView(topCircleView);

		bottomCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2);
		bottomProgress = new MaterialProgressDrawable(getContext(), this);
		bottomProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
		bottomCircleView.setImageDrawable(bottomProgress);
		bottomCircleView.setVisibility(GONE);
		addView(bottomCircleView);
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	private boolean isAlphaUsedForScale() {
		return android.os.Build.VERSION.SDK_INT < 11;
	}

	public void setRefreshing(boolean refreshing, @RightSwipeRefreshLayour.RefreshType int refreshType) {
		if (refreshType == TOP_REFRESH) {
			if (refreshing && mRefreshing != refreshing) {
				// scale and show
				mRefreshing = refreshing;
				int endTarget = 0;
				if (!mUsingCustomStart) {
					endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
				} else {
					endTarget = (int) mSpinnerFinalOffset;
				}
				setTopTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true /* requires update */);
				mNotify = false;
				startTopScaleUpAnimation(topRefreshListener);
			} else {
				setTopRefreshing(refreshing, false /* notify */);
			}
		} else if (refreshType == BOTTOM_REFRESH) {
			if (refreshing && mRefreshing != refreshing) {
				// scale and show
				mRefreshing = refreshing;
				int endTarget = 0;
				if (!mUsingCustomStart) {
					endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetBottom);
				} else {
					endTarget = (int) mSpinnerFinalOffset;
				}
				setBottomTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetBottom, true /* requires update */);
				mNotify = false;
				startBottomScaleUpAnimation(bottomRefreshListener);
			} else {
				setBottomRefreshing(refreshing, false /* notify */);
			}
		}
	}

	private void startTopScaleUpAnimation(Animation.AnimationListener listener) {
		topCircleView.setVisibility(View.VISIBLE);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			topProgress.setAlpha(MAX_ALPHA);
		}
		Animation topScaleAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				setTopAnimationProgress(interpolatedTime);
			}
		};
		topScaleAnimation.setDuration(mMediumAnimationDuration);
		if (listener != null) {
			topCircleView.setAnimationListener(listener);
		}
		topCircleView.clearAnimation();
		topCircleView.startAnimation(topScaleAnimation);
	}

	private void startBottomScaleUpAnimation(Animation.AnimationListener listener) {
		bottomCircleView.setVisibility(View.VISIBLE);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			bottomProgress.setAlpha(MAX_ALPHA);
		}
		Animation bottomScaleAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				setTopAnimationProgress(interpolatedTime);
			}
		};
		bottomScaleAnimation.setDuration(mMediumAnimationDuration);
		if (listener != null) {
			bottomCircleView.setAnimationListener(listener);
		}
		bottomCircleView.clearAnimation();
		bottomCircleView.startAnimation(bottomScaleAnimation);
	}

	private void setTopAnimationProgress(float progress) {
		if (isAlphaUsedForScale()) {
			setTopColorViewAlpha((int) (progress * MAX_ALPHA));
		} else {
			ViewCompat.setScaleX(topCircleView, progress);
			ViewCompat.setScaleY(topCircleView, progress);
		}
	}

	private void setBottomAnimationProgress(float progress) {
		if (isAlphaUsedForScale()) {
			setBottomColorViewAlpha((int) (progress * MAX_ALPHA));
		} else {
			ViewCompat.setScaleX(bottomCircleView, progress);
			ViewCompat.setScaleY(bottomCircleView, progress);
		}
	}

	private void setTopRefreshing(boolean refreshing, final boolean notify) {
		if (mRefreshing != refreshing) {
			mNotify = notify;
			ensureTarget();
			mRefreshing = refreshing;
			if (mRefreshing) {
				animateTopOffsetToCorrectPosition(mCurrentTargetOffsetTop, topRefreshListener);
			} else {
				startTopScaleDownAnimation(topRefreshListener);
			}
		}
	}

	private void setBottomRefreshing(boolean refreshing, final boolean notify) {
		if (mRefreshing != refreshing) {
			mNotify = notify;
			ensureTarget();
			mRefreshing = refreshing;
			if (mRefreshing) {
				animateBottomOffsetToCorrectPosition(mCurrentTargetOffsetBottom, bottomRefreshListener);
			} else {
				startBottomScaleDownAnimation(bottomRefreshListener);
			}
		}
	}

	private void startTopScaleDownAnimation(Animation.AnimationListener listener) {
		Animation topScaleDownAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				setTopAnimationProgress(1 - interpolatedTime);
			}
		};
		topScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
		topCircleView.setAnimationListener(listener);
		topCircleView.clearAnimation();
		topCircleView.startAnimation(topScaleDownAnimation);
	}

	private void startBottomScaleDownAnimation(Animation.AnimationListener listener) {
		Animation bottomScaleDownAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				setBottomAnimationProgress(1 - interpolatedTime);
			}
		};
		bottomScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
		bottomCircleView.setAnimationListener(listener);
		bottomCircleView.clearAnimation();
		bottomCircleView.startAnimation(bottomScaleDownAnimation);
	}

	private void startTopProgressAlphaStartAnimation() {
		mTopAlphaStartAnimation = startTopAlphaAnimation(topProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
	}

	private void startBottomProgressAlphaStartAnimation() {
		mBottomAlphaStartAnimation = startBottomAlphaAnimation(bottomProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
	}

	private void startTopProgressAlphaMaxAnimation() {
		mTopAlphaMaxAnimation = startTopAlphaAnimation(topProgress.getAlpha(), MAX_ALPHA);
	}

	private void startBottomProgressAlphaMaxAnimation() {
		mBottomAlphaMaxAnimation = startBottomAlphaAnimation(bottomProgress.getAlpha(), MAX_ALPHA);
	}

	private Animation startTopAlphaAnimation(final int startingAlpha, final int endingAlpha) {
		// Pre API 11, alpha is used in place of scale. Don't also use it to
		// show the trigger point.
		if (mScale && isAlphaUsedForScale()) {
			return null;
		}
		Animation alpha = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				topProgress
						.setAlpha((int) (startingAlpha + ((endingAlpha - startingAlpha)
								* interpolatedTime)));
			}
		};
		alpha.setDuration(ALPHA_ANIMATION_DURATION);
		// Clear out the previous animation listeners.
		topCircleView.setAnimationListener(null);
		topCircleView.clearAnimation();
		topCircleView.startAnimation(alpha);
		return alpha;
	}

	private Animation startBottomAlphaAnimation(final int startingAlpha, final int endingAlpha) {
		// Pre API 11, alpha is used in place of scale. Don't also use it to
		// show the trigger point.
		if (mScale && isAlphaUsedForScale()) {
			return null;
		}
		Animation alpha = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				bottomProgress
						.setAlpha((int) (startingAlpha + ((endingAlpha - startingAlpha)
								* interpolatedTime)));
			}
		};
		alpha.setDuration(ALPHA_ANIMATION_DURATION);
		// Clear out the previous animation listeners.
		bottomCircleView.setAnimationListener(null);
		bottomCircleView.clearAnimation();
		bottomCircleView.startAnimation(alpha);
		return alpha;
	}

	public void setProgressBackgroundColor(int colorRes) {
		topCircleView.setBackgroundColor(colorRes);
		topProgress.setBackgroundColor(getResources().getColor(colorRes));
		bottomCircleView.setBackgroundColor(colorRes);
		bottomProgress.setBackgroundColor(getResources().getColor(colorRes));
	}

	public void setColorSchemeResources(int... colorResIds) {
		final Resources res = getResources();
		int[] colorRes = new int[colorResIds.length];
		for (int i = 0; i < colorResIds.length; i++) {
			colorRes[i] = res.getColor(colorResIds[i]);
		}
		setColorSchemeColors(colorRes);
	}

	public void setColorSchemeColors(int... colors) {
		ensureTarget();
		topProgress.setColorSchemeColors(colors);
		bottomProgress.setColorSchemeColors(colors);
	}

	public boolean isRefreshing() {
		return mRefreshing;
	}

	private void ensureTarget() {
		// Don't bother getting the parent height if the parent hasn't been laid
		// out yet.
		if (mTarget == null) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (!child.equals(topCircleView) && !child.equals(bottomCircleView)) {
					mTarget = child;
					break;
				}
			}
		}
	}

	public void setDistanceToTriggerSync(int distance) {
		mTotalDragDistance = distance;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		if (getChildCount() == 0) {
			return;
		}
		if (mTarget == null) {
			ensureTarget();
		}
		if (mTarget == null) {
			return;
		}
		final View child = mTarget;
		final int childLeft = getPaddingLeft();
		final int childTop = getPaddingTop();
		final int childWidth = width - getPaddingLeft() - getPaddingRight();
		final int childHeight = height - getPaddingTop() - getPaddingBottom();
		child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
		int circleWidth = topCircleView.getMeasuredWidth();
		int circleHeight = topCircleView.getMeasuredHeight();
		topCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop, (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
		bottomCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetBottom, (width / 2 + circleWidth / 2), mCurrentTargetOffsetBottom + circleHeight);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mTarget == null) {
			ensureTarget();
		}
		if (mTarget == null) {
			return;
		}
		mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
		topCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mCircleHeight, MeasureSpec.EXACTLY));
		bottomCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mCircleHeight, MeasureSpec.EXACTLY));
		if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
			mOriginalOffsetCalculated = true;
			mCurrentTargetOffsetTop = mOriginalOffsetTop = -topCircleView.getMeasuredHeight();
			//https://code.google.com/p/android/issues/detail?id=88256 getNavigationBarHeight()
			mCurrentTargetOffsetBottom = mOriginalOffsetBottom = mTarget.getMeasuredHeight() - (needAddBottomPading || android.os.Build.VERSION.SDK_INT <21 ? getNavigationBarHeight():0) + bottomCircleView.getMeasuredHeight();
		}
		topCircleViewIndex = -1;
		bottomCircleViewIndex = -1;
		// Get the index of the circleview.
		for (int index = 0; index < getChildCount(); index++) {
			if (getChildAt(index) == topCircleView) {
				topCircleViewIndex = index;
				break;
			}
			if (getChildAt(index) == bottomCircleView) {
				bottomCircleViewIndex = index;
				break;
			}
		}
	}

	private int getNavigationBarHeight() {
		Resources resources = getContext().getResources();
		int orientation = resources.getConfiguration().orientation;
		int id = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape","dimen", "android");
		if (id > 0) {
			return resources.getDimensionPixelSize(id);
		}
		return 0;
	}

	/**
	 * @return Whether it is possible for the child view of this layout to
	 *         scroll up. Override this if the child view is a custom view.
	 */
	public boolean canChildScrollUp() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
			} else {
				return mTarget.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, -1);
		}
	}

	/**
	 * @return Whether it is possible for the child view of this layout to
	 *         scroll down. Override this if the child view is a custom view.
	 */
	public boolean canChildScrollDown() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			//TODO must be corrected
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
			} else {
				return mTarget.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, 1);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		ensureTarget();

		final int action = MotionEventCompat.getActionMasked(ev);

		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}

		if (!isEnabled() || mReturningToStart || (canChildScrollUp() && canChildScrollDown()) || mRefreshing) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				setTopTargetOffsetTopAndBottom(mOriginalOffsetTop - topCircleView.getTop(), true);
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mIsTopBeingDragged = false;
				mIsBottomBeingDragged = false;
				final float initialMotionY = getMotionEventY(ev, mActivePointerId);
				if (initialMotionY == -1) {
					return false;
				}
				mInitialMotionY = initialMotionY;

			case MotionEvent.ACTION_MOVE:
				if (mActivePointerId == INVALID_POINTER) {
					Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
					return false;
				}

				final float y = getMotionEventY(ev, mActivePointerId);
				if (y == -1) {
					return false;
				}
				final float yDiff = y - mInitialMotionY;
				if (yDiff > mTouchSlop && !mIsTopBeingDragged) {
					if (canChildScrollUp()) {
						return false;
					}
					mIsTopBeingDragged = true;
					topProgress.setAlpha(STARTING_PROGRESS_ALPHA);
				} else if (Math.abs(yDiff) > mTouchSlop && !mIsBottomBeingDragged) {
					if (canChildScrollDown()) {
						return false;
					}
					mIsBottomBeingDragged = true;
					bottomProgress.setAlpha(STARTING_PROGRESS_ALPHA);
				}
				break;

			case MotionEventCompat.ACTION_POINTER_UP:
				onSecondaryPointerUp(ev);
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mIsTopBeingDragged = false;
				mIsBottomBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				break;
		}

		return mIsTopBeingDragged || mIsBottomBeingDragged;
	}

	private float getMotionEventY(MotionEvent ev, int activePointerId) {
		final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
		if (index < 0) {
			return -1;
		}
		return MotionEventCompat.getY(ev, index);
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean b) {
		// Nope.
	}

	private boolean isAnimationRunning(Animation animation) {
		return animation != null && animation.hasStarted() && !animation.hasEnded();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);

		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}

		if (!isEnabled() || mReturningToStart || (canChildScrollUp() && canChildScrollDown())) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mIsTopBeingDragged = false;
				mIsBottomBeingDragged = false;
				break;

			case MotionEvent.ACTION_MOVE: {
				final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				if (pointerIndex < 0) {
					Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
					return false;
				}

				final float y = MotionEventCompat.getY(ev, pointerIndex);
				if (mIsTopBeingDragged) {
					final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
					topProgress.showArrow(true);
					float originalDragPercent = overscrollTop / mTotalDragDistance;
					if (originalDragPercent < 0) {
						return false;
					}
					float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
					float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
					float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
					float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetTop : mSpinnerFinalOffset;
					float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
					float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
					float extraMove = (slingshotDist) * tensionPercent * 2;

					int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
					// where 1.0f is a full circle
					if (topCircleView.getVisibility() != View.VISIBLE) {
						topCircleView.setVisibility(View.VISIBLE);
					}
					if (!mScale) {
						ViewCompat.setScaleX(topCircleView, 1f);
						ViewCompat.setScaleY(topCircleView, 1f);
					}
					if (overscrollTop < mTotalDragDistance) {
						if (mScale) {
							setTopAnimationProgress(overscrollTop / mTotalDragDistance);
						}
						if (topProgress.getAlpha() > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mTopAlphaStartAnimation)) {
							// Animate the alpha
							startTopProgressAlphaStartAnimation();
						}
						float strokeStart = (float) (adjustedPercent * .8f);
						topProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
						topProgress.setArrowScale(Math.min(1f, adjustedPercent));
					} else {
						if (topProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mTopAlphaMaxAnimation)) {
							// Animate the alpha
							startTopProgressAlphaMaxAnimation();
						}
					}
					float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
					topProgress.setProgressRotation(rotation);
					setTopTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
				}
				if (mIsBottomBeingDragged) {
					final float overscrollBottom = (y - mInitialMotionY) * DRAG_RATE;
					bottomProgress.showArrow(true);
					float originalDragPercent = overscrollBottom / mTotalDragDistance;
					if (originalDragPercent > 0) {
						return false;
					}
					float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
					float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
					float extraOS = Math.abs(overscrollBottom) - mTotalDragDistance;
					float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetBottom : mSpinnerFinalOffset;
					float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
					float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
					float extraMove = (slingshotDist) * tensionPercent * 2;

					int targetY = mOriginalOffsetBottom - (int) ((slingshotDist * dragPercent) + extraMove);
					// where 1.0f is a full circle
					if (bottomCircleView.getVisibility() != View.VISIBLE) {
						bottomCircleView.setVisibility(View.VISIBLE);
					}
					if (!mScale) {
						ViewCompat.setScaleX(bottomCircleView, 1f);
						ViewCompat.setScaleY(bottomCircleView, 1f);
					}
					if (Math.abs(overscrollBottom) < mTotalDragDistance) {
						if (mScale) {
							setBottomAnimationProgress(overscrollBottom / mTotalDragDistance);
						}
						if (bottomProgress.getAlpha() > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mBottomAlphaStartAnimation)) {
							// Animate the alpha
							startBottomProgressAlphaStartAnimation();
						}
						float strokeStart = (float) (adjustedPercent * .8f);
						bottomProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
						bottomProgress.setArrowScale(Math.min(1f, adjustedPercent));
					} else {
						if (bottomProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mBottomAlphaMaxAnimation)) {
							// Animate the alpha
							startBottomProgressAlphaMaxAnimation();
						}
					}
					float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
					bottomProgress.setProgressRotation(rotation);
					setBottomTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetBottom, true /* requires update */);
				}
				break;
			}
			case MotionEventCompat.ACTION_POINTER_DOWN: {
				final int index = MotionEventCompat.getActionIndex(ev);
				mActivePointerId = MotionEventCompat.getPointerId(ev, index);
				break;
			}

			case MotionEventCompat.ACTION_POINTER_UP:
				onSecondaryPointerUp(ev);
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL: {
				if (mActivePointerId == INVALID_POINTER) {
					if (action == MotionEvent.ACTION_UP) {
						Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
					}
					return false;
				}
				final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				final float y = MotionEventCompat.getY(ev, pointerIndex);
				final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
				if (mIsTopBeingDragged) {
					if (overscrollTop > mTotalDragDistance) {
						setTopRefreshing(true, true /* notify */);
					} else {
						// cancel refresh
						mRefreshing = false;
						topProgress.setStartEndTrim(0f, 0f);
						Animation.AnimationListener listener = null;
						if (!mScale) {
							listener = new Animation.AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationEnd(Animation animation) {
									if (!mScale) {
										startTopScaleDownAnimation(null);
									}
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}

							};
						}
						animateTopOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
						topProgress.showArrow(false);
					}
				}
				if (mIsBottomBeingDragged) {
					if (Math.abs(overscrollTop) > mTotalDragDistance) {
						setBottomRefreshing(true, true /* notify */);
					} else {
						//cancel refresh
						mRefreshing = false;
						bottomProgress.setStartEndTrim(0f, 0f);
						Animation.AnimationListener listener = null;
						if (!mScale) {
							listener = new Animation.AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationEnd(Animation animation) {
									if (!mScale) {
										Log.i(TAG, "onAnimationEnd");
										startBottomScaleDownAnimation(null);
									}
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}

							};
						}
						animateBottomOffsetToStartPosition(mCurrentTargetOffsetBottom, listener);
						bottomProgress.showArrow(false);
					}
				}
				mIsTopBeingDragged = false;
				mIsBottomBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				return false;
			}
		}

		return true;
	}

	private void animateTopOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
		mFrom = from;
		mTopAnimateToCorrectPosition.reset();
		mTopAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
		mTopAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
		if (listener != null) {
			topCircleView.setAnimationListener(listener);
		}
		topCircleView.clearAnimation();
		topCircleView.startAnimation(mTopAnimateToCorrectPosition);
	}

	private void animateBottomOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
		mFrom = from;
		mBottomAnimateToCorrectPosition.reset();
		mBottomAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
		mBottomAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
		if (listener != null) {
			bottomCircleView.setAnimationListener(listener);
		}
		bottomCircleView.clearAnimation();
		bottomCircleView.startAnimation(mBottomAnimateToCorrectPosition);
	}

	private void animateTopOffsetToStartPosition(int from, Animation.AnimationListener listener) {
		if (mScale) {
			// Scale the item back down
			startTopScaleDownReturnToStartAnimation(from, listener);
		} else {
			mFrom = from;
			mTopAnimateToStartPosition.reset();
			mTopAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
			mTopAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
			if (listener != null) {
				topCircleView.setAnimationListener(listener);
			}
			topCircleView.clearAnimation();
			topCircleView.startAnimation(mTopAnimateToStartPosition);
		}
	}

	private void animateBottomOffsetToStartPosition(int from, Animation.AnimationListener listener) {
		if (mScale) {
			// Scale the item back down
			startBottomScaleDownReturnToStartAnimation(from, listener);
		} else {
			mFrom = from;
			mBottomAnimateToStartPosition.reset();
			mBottomAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
			mBottomAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
			if (listener != null) {
				bottomCircleView.setAnimationListener(listener);
			}
			bottomCircleView.clearAnimation();
			bottomCircleView.startAnimation(mBottomAnimateToStartPosition);
		}
	}

	private final Animation mTopAnimateToCorrectPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			int endTarget = 0;
			if (!mUsingCustomStart) {
				endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
			} else {
				endTarget = (int) mSpinnerFinalOffset;
			}
			targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
			int offset = targetTop - topCircleView.getTop();
			setTopTargetOffsetTopAndBottom(offset, false /* requires update */);
		}
	};

	private final Animation mBottomAnimateToCorrectPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			int endTarget = 0;
			if (!mUsingCustomStart) {
				endTarget = (int) (Math.abs(mOriginalOffsetBottom)- mSpinnerFinalOffset);
			} else {
				endTarget = (int) mSpinnerFinalOffset;
			}
			targetTop = (mFrom + (int) ((endTarget-mFrom) * interpolatedTime));
			int offset = targetTop - bottomCircleView.getTop();
			setBottomTargetOffsetTopAndBottom(offset, false /* requires update */);
		}
	};

	private void moveTopToStart(float interpolatedTime) {
		int targetTop = 0;
		targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
		int offset = targetTop - topCircleView.getTop();
		setTopTargetOffsetTopAndBottom(offset, false /* requires update */);
	}

	private void moveBottomToStart(float interpolatedTime) {
		int targetTop = 0;
		targetTop = (mFrom + (int) ((mOriginalOffsetBottom - mFrom) * interpolatedTime));
		int offset = targetTop - bottomCircleView.getTop();
		setBottomTargetOffsetTopAndBottom(offset, false /* requires update */);
	}

	private final Animation mTopAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			moveTopToStart(interpolatedTime);
		}
	};

	private final Animation mBottomAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			moveBottomToStart(interpolatedTime);
		}
	};

	private void startTopScaleDownReturnToStartAnimation(int from, Animation.AnimationListener listener) {
		mFrom = from;
		if (isAlphaUsedForScale()) {
			mStartingScale = topProgress.getAlpha();
		} else {
			mStartingScale = ViewCompat.getScaleX(topCircleView);
		}
		mScaleDownToStartAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				float targetScale = (mStartingScale + (-mStartingScale  * interpolatedTime));
				setTopAnimationProgress(targetScale);
				moveTopToStart(interpolatedTime);
			}
		};
		mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
		if (listener != null) {
			topCircleView.setAnimationListener(listener);
		}
		topCircleView.clearAnimation();
		topCircleView.startAnimation(mScaleDownToStartAnimation);
	}

	private void startBottomScaleDownReturnToStartAnimation(int from, Animation.AnimationListener listener) {
		mFrom = from;
		if (isAlphaUsedForScale()) {
			mStartingScale = bottomProgress.getAlpha();
		} else {
			mStartingScale = ViewCompat.getScaleX(bottomCircleView);
		}
		mScaleDownToStartAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				float targetScale = (mStartingScale + (-mStartingScale  * interpolatedTime));
				setBottomAnimationProgress(targetScale);
				moveBottomToStart(interpolatedTime);
			}
		};
		mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
		if (listener != null) {
			bottomCircleView.setAnimationListener(listener);
		}
		bottomCircleView.clearAnimation();
		bottomCircleView.startAnimation(mScaleDownToStartAnimation);
	}

	private void setTopTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
		topCircleView.bringToFront();
		topCircleView.offsetTopAndBottom(offset);
		mCurrentTargetOffsetTop = topCircleView.getTop();
		if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
			invalidate();
		}
	}

	private void setBottomTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
		bottomCircleView.bringToFront();
		bottomCircleView.offsetTopAndBottom(offset);
		mCurrentTargetOffsetBottom = bottomCircleView.getTop();
		if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
			invalidate();
		}
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		}
	}

	public static final int TOP_REFRESH = 100;
	public static final int BOTTOM_REFRESH = 200;

	@IntDef({TOP_REFRESH, BOTTOM_REFRESH})
	public @interface RefreshType {}

	public interface OnRefreshListener {
		public void onRefresh(final @RefreshType int refreshType);
	}

}
