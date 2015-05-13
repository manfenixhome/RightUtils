package com.rightutils.rightutils.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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

/**
 * Created by Anton Maniskevich on 5/13/15.
 */
public class RightSwipeRefreshLayour extends ViewGroup {
	// Maps to ProgressBar.Large style
	public static final int LARGE = MaterialProgressDrawable.LARGE;
	// Maps to ProgressBar default style
	public static final int DEFAULT = MaterialProgressDrawable.DEFAULT;

	private static final String LOG_TAG = RightSwipeRefreshLayour.class.getSimpleName();

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
	// Whether or not the starting offset has been determined.
	private boolean mOriginalOffsetCalculated = false;

	private float mInitialMotionY;
	private boolean mIsBeingDragged;
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

	private MaterialProgressDrawable topProgress;
	private MaterialProgressDrawable bottomProgress;

	private Animation mScaleAnimation;

	private Animation mScaleDownAnimation;

	private Animation mAlphaStartAnimation;

	private Animation mAlphaMaxAnimation;

	private Animation mScaleDownToStartAnimation;

	private float mSpinnerFinalOffset;

	private boolean mNotify;

	private int mCircleWidth;

	private int mCircleHeight;

	// Whether the client has set a custom starting position;
	private boolean mUsingCustomStart;

	private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
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
						mListener.onRefresh();
					}
				}
			} else {
				topProgress.stop();
				topCircleView.setVisibility(View.GONE);
				setColorViewAlpha(MAX_ALPHA);
				// Return the circle to its start position
				if (mScale) {
					setAnimationProgress(0 /* animation complete and view is hidden */);
				} else {
					setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop,
							true /* requires update */);
				}
			}
			mCurrentTargetOffsetTop = topCircleView.getTop();
		}
	};

	private void setColorViewAlpha(int targetAlpha) {
		topCircleView.getBackground().setAlpha(targetAlpha);
		topProgress.setAlpha(targetAlpha);
	}

	/**
	 * The refresh indicator starting and resting position is always positioned
	 * near the top of the refreshing content. This position is a consistent
	 * location, but can be adjusted in either direction based on whether or not
	 * there is a toolbar or actionbar present.
	 *
	 * @param scale Set to true if there is no view at a higher z-order than
	 *            where the progress spinner is set to appear.
	 * @param start The offset in pixels from the top of this view at which the
	 *            progress spinner should appear.
	 * @param end The offset in pixels from the top of this view at which the
	 *            progress spinner should come to rest after a successful swipe
	 *            gesture.
	 */
	public void setProgressViewOffset(boolean scale, int start, int end) {
		mScale = scale;
		topCircleView.setVisibility(View.GONE);
		mOriginalOffsetTop = mCurrentTargetOffsetTop = start;
		mSpinnerFinalOffset = end;
		mUsingCustomStart = true;
		topCircleView.invalidate();
	}

	/**
	 * The refresh indicator resting position is always positioned near the top
	 * of the refreshing content. This position is a consistent location, but
	 * can be adjusted in either direction based on whether or not there is a
	 * toolbar or actionbar present.
	 *
	 * @param scale Set to true if there is no view at a higher z-order than
	 *            where the progress spinner is set to appear.
	 * @param end The offset in pixels from the top of this view at which the
	 *            progress spinner should come to rest after a successful swipe
	 *            gesture.
	 */
	public void setProgressViewEndTarget(boolean scale, int end) {
		mSpinnerFinalOffset = end;
		mScale = scale;
		topCircleView.invalidate();
	}

	/**
	 * One of DEFAULT, or LARGE.
	 */
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

	/**
	 * Simple constructor to use when creating a SwipeRefreshLayout from code.
	 *
	 * @param context
	 */
	public RightSwipeRefreshLayour(Context context) {
		this(context, null);
	}

	/**
	 * Constructor that is called when inflating SwipeRefreshLayout from XML.
	 *
	 * @param context
	 * @param attrs
	 */
	public RightSwipeRefreshLayour(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		mMediumAnimationDuration = getResources().getInteger(
				android.R.integer.config_mediumAnimTime);

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

	/**
	 * Set the listener to be notified when a refresh is triggered via the swipe
	 * gesture.
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	/**
	 * Pre API 11, alpha is used to make the progress circle appear instead of scale.
	 */
	private boolean isAlphaUsedForScale() {
		return android.os.Build.VERSION.SDK_INT < 11;
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
				endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
			} else {
				endTarget = (int) mSpinnerFinalOffset;
			}
			setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true /* requires update */);
			mNotify = false;
			startScaleUpAnimation(mRefreshListener);
		} else {
			setRefreshing(refreshing, false /* notify */);
		}
	}

	private void startScaleUpAnimation(Animation.AnimationListener listener) {
		topCircleView.setVisibility(View.VISIBLE);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			// Pre API 11, alpha is used in place of scale up to show the
			// progress circle appearing.
			// Don't adjust the alpha during appearance otherwise.
			topProgress.setAlpha(MAX_ALPHA);
		}
		mScaleAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				setAnimationProgress(interpolatedTime);
			}
		};
		mScaleAnimation.setDuration(mMediumAnimationDuration);
		if (listener != null) {
			topCircleView.setAnimationListener(listener);
		}
		topCircleView.clearAnimation();
		topCircleView.startAnimation(mScaleAnimation);
	}

	/**
	 * Pre API 11, this does an alpha animation.
	 * @param progress
	 */
	private void setAnimationProgress(float progress) {
		if (isAlphaUsedForScale()) {
			setColorViewAlpha((int) (progress * MAX_ALPHA));
		} else {
			ViewCompat.setScaleX(topCircleView, progress);
			ViewCompat.setScaleY(topCircleView, progress);
		}
	}

	private void setRefreshing(boolean refreshing, final boolean notify) {
		if (mRefreshing != refreshing) {
			mNotify = notify;
			ensureTarget();
			mRefreshing = refreshing;
			if (mRefreshing) {
				animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
			} else {
				startScaleDownAnimation(mRefreshListener);
			}
		}
	}

	private void startScaleDownAnimation(Animation.AnimationListener listener) {
		mScaleDownAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime, Transformation t) {
				setAnimationProgress(1 - interpolatedTime);
			}
		};
		mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
		topCircleView.setAnimationListener(listener);
		topCircleView.clearAnimation();
		topCircleView.startAnimation(mScaleDownAnimation);
	}

	private void startProgressAlphaStartAnimation() {
		mAlphaStartAnimation = startAlphaAnimation(topProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
	}

	private void startProgressAlphaMaxAnimation() {
		mAlphaMaxAnimation = startAlphaAnimation(topProgress.getAlpha(), MAX_ALPHA);
	}

	private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
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

	/**
	 * Set the background color of the progress spinner disc.
	 *
	 * @param colorRes Resource id of the color.
	 */
	public void setProgressBackgroundColor(int colorRes) {
		topCircleView.setBackgroundColor(colorRes);
		topProgress.setBackgroundColor(getResources().getColor(colorRes));
	}

	/**
	 * @deprecated Use {@link #setColorSchemeResources(int...)}
	 */
	@Deprecated
	public void setColorScheme(int... colors) {
		setColorSchemeResources(colors);
	}

	/**
	 * Set the color resources used in the progress animation from color resources.
	 * The first color will also be the color of the bar that grows in response
	 * to a user swipe gesture.
	 *
	 * @param colorResIds
	 */
	public void setColorSchemeResources(int... colorResIds) {
		final Resources res = getResources();
		int[] colorRes = new int[colorResIds.length];
		for (int i = 0; i < colorResIds.length; i++) {
			colorRes[i] = res.getColor(colorResIds[i]);
		}
		setColorSchemeColors(colorRes);
	}

	/**
	 * Set the colors used in the progress animation. The first
	 * color will also be the color of the bar that grows in response to a user
	 * swipe gesture.
	 *
	 * @param colors
	 */
	public void setColorSchemeColors(int... colors) {
		ensureTarget();
		topProgress.setColorSchemeColors(colors);
	}

	/**
	 * @return Whether the SwipeRefreshWidget is actively showing refresh
	 *         progress.
	 */
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

	/**
	 * Set the distance to trigger a sync in dips
	 *
	 * @param distance
	 */
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
		if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
			mOriginalOffsetCalculated = true;
			mCurrentTargetOffsetTop = mOriginalOffsetTop = -topCircleView.getMeasuredHeight();
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

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		ensureTarget();

		final int action = MotionEventCompat.getActionMasked(ev);

		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}

		if (!isEnabled() || mReturningToStart || canChildScrollUp() || mRefreshing) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				setTargetOffsetTopAndBottom(mOriginalOffsetTop - topCircleView.getTop(), true);
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mIsBeingDragged = false;
				final float initialMotionY = getMotionEventY(ev, mActivePointerId);
				if (initialMotionY == -1) {
					return false;
				}
				mInitialMotionY = initialMotionY;

			case MotionEvent.ACTION_MOVE:
				if (mActivePointerId == INVALID_POINTER) {
					Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
					return false;
				}

				final float y = getMotionEventY(ev, mActivePointerId);
				if (y == -1) {
					return false;
				}
				final float yDiff = y - mInitialMotionY;
				if (yDiff > mTouchSlop && !mIsBeingDragged) {
					mIsBeingDragged = true;
					topProgress.setAlpha(STARTING_PROGRESS_ALPHA);
				}
				break;

			case MotionEventCompat.ACTION_POINTER_UP:
				onSecondaryPointerUp(ev);
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mIsBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				break;
		}

		return mIsBeingDragged;
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

		if (!isEnabled() || mReturningToStart || canChildScrollUp()) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mIsBeingDragged = false;
				break;

			case MotionEvent.ACTION_MOVE: {
				final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				if (pointerIndex < 0) {
					Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
					return false;
				}

				final float y = MotionEventCompat.getY(ev, pointerIndex);
				final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
				if (mIsBeingDragged) {
					topProgress.showArrow(true);
					float originalDragPercent = overscrollTop / mTotalDragDistance;
					if (originalDragPercent < 0) {
						return false;
					}
					float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
					float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
					float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
					float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset
							- mOriginalOffsetTop : mSpinnerFinalOffset;
					float tensionSlingshotPercent = Math.max(0,
							Math.min(extraOS, slingshotDist * 2) / slingshotDist);
					float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
							(tensionSlingshotPercent / 4), 2)) * 2f;
					float extraMove = (slingshotDist) * tensionPercent * 2;

					int targetY = mOriginalOffsetTop
							+ (int) ((slingshotDist * dragPercent) + extraMove);
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
							setAnimationProgress(overscrollTop / mTotalDragDistance);
						}
						if (topProgress.getAlpha() > STARTING_PROGRESS_ALPHA
								&& !isAnimationRunning(mAlphaStartAnimation)) {
							// Animate the alpha
							startProgressAlphaStartAnimation();
						}
						float strokeStart = (float) (adjustedPercent * .8f);
						topProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
						topProgress.setArrowScale(Math.min(1f, adjustedPercent));
					} else {
						if (topProgress.getAlpha() < MAX_ALPHA
								&& !isAnimationRunning(mAlphaMaxAnimation)) {
							// Animate the alpha
							startProgressAlphaMaxAnimation();
						}
					}
					float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
					topProgress.setProgressRotation(rotation);
					setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop,
							true /* requires update */);
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
						Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
					}
					return false;
				}
				final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				final float y = MotionEventCompat.getY(ev, pointerIndex);
				final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
				mIsBeingDragged = false;
				if (overscrollTop > mTotalDragDistance) {
					setRefreshing(true, true /* notify */);
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
									startScaleDownAnimation(null);
								}
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

						};
					}
					animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
					topProgress.showArrow(false);
				}
				mActivePointerId = INVALID_POINTER;
				return false;
			}
		}

		return true;
	}

	private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
		mFrom = from;
		mAnimateToCorrectPosition.reset();
		mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
		mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
		if (listener != null) {
			topCircleView.setAnimationListener(listener);
		}
		topCircleView.clearAnimation();
		topCircleView.startAnimation(mAnimateToCorrectPosition);
	}

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
				topCircleView.setAnimationListener(listener);
			}
			topCircleView.clearAnimation();
			topCircleView.startAnimation(mAnimateToStartPosition);
		}
	}

	private final Animation mAnimateToCorrectPosition = new Animation() {
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
			setTargetOffsetTopAndBottom(offset, false /* requires update */);
		}
	};

	private void moveToStart(float interpolatedTime) {
		int targetTop = 0;
		targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
		int offset = targetTop - topCircleView.getTop();
		setTargetOffsetTopAndBottom(offset, false /* requires update */);
	}

	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			moveToStart(interpolatedTime);
		}
	};

	private void startScaleDownReturnToStartAnimation(int from,
													  Animation.AnimationListener listener) {
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
				setAnimationProgress(targetScale);
				moveToStart(interpolatedTime);
			}
		};
		mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
		if (listener != null) {
			topCircleView.setAnimationListener(listener);
		}
		topCircleView.clearAnimation();
		topCircleView.startAnimation(mScaleDownToStartAnimation);
	}

	private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
		topCircleView.bringToFront();
		topCircleView.offsetTopAndBottom(offset);
		mCurrentTargetOffsetTop = topCircleView.getTop();
		if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
			invalidate();
		}
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		}
	}

	/**
	 * Classes that wish to be notified when the swipe gesture correctly
	 * triggers a refresh should implement this interface.
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}
}
