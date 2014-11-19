package com.rightutils.rightutils.gestures;

/**
 * Created by eKreative on 6/17/14.
 */

import android.content.Context;
import android.view.MotionEvent;

public abstract class BaseGestureDetector {

	protected final Context mContext;
	protected boolean mGestureInProgress;
	protected MotionEvent mPrevEvent;
	protected MotionEvent mCurrEvent;
	protected float mCurrPressure;
	protected float mPrevPressure;
	protected long mTimeDelta;


	protected static final float PRESSURE_THRESHOLD = 0.67f;


	public BaseGestureDetector(Context context) {
		mContext = context;
	}


	public boolean onTouchEvent(MotionEvent event) {
		final int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
		if (!mGestureInProgress) {
			handleStartProgressEvent(actionCode, event);
		} else {
			handleInProgressEvent(actionCode, event);
		}
		return true;
	}

	protected abstract void handleStartProgressEvent(int actionCode, MotionEvent event);

	protected abstract void handleInProgressEvent(int actionCode, MotionEvent event);

	protected void updateStateByEvent(MotionEvent curr) {
		final MotionEvent prev = mPrevEvent;

		// Reset mCurrEvent
		if (mCurrEvent != null) {
			mCurrEvent.recycle();
			mCurrEvent = null;
		}
		mCurrEvent = MotionEvent.obtain(curr);

		// Delta time
		mTimeDelta = curr.getEventTime() - prev.getEventTime();

		// Pressure
		mCurrPressure = curr.getPressure(curr.getActionIndex());
		mPrevPressure = prev.getPressure(prev.getActionIndex());
	}

	protected void resetState() {
		if (mPrevEvent != null) {
			mPrevEvent.recycle();
			mPrevEvent = null;
		}
		if (mCurrEvent != null) {
			mCurrEvent.recycle();
			mCurrEvent = null;
		}
		mGestureInProgress = false;
	}

	public boolean isInProgress() {
		return mGestureInProgress;
	}

	public long getTimeDelta() {
		return mTimeDelta;
	}

	public long getEventTime() {
		return mCurrEvent.getEventTime();
	}

}
