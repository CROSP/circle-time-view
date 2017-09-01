package net.crosp.libs.android.circletimeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import net.crosp.libraries.android.circletimeview.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Timer;
import java.util.TimerTask;

public class CircleTimeView extends View {

    //region Constants
    private static final String TAG = CircleTimeView.class.getSimpleName();

    // Status
    private static final String BUNDLE_STATE = "circle_view_bundle";
    private static final String BUNDLE_ROTATION_DEGREE_RADIAN = "status_radian";
    private static final String BUNDLE_CURRENT_TIME = "current_time";

    // Default values for attributes
    private static final float DEFAULT_PADDING_INNER_RADIUS = 5;
    private static final int DEFAULT_PADDING_LAP_LABEL = 10;
    private static final float DEFAULT_PADDING_QUARTER_NUMBER = 5;
    private static final float DEFAULT_MARK_SIZE = 10;
    private static final float DEFAULT_QUARTER_MARK_SIZE = (float) (DEFAULT_MARK_SIZE * 1.3);
    private static final float DEFAULT_MARK_LINE_WIDTH = 1f;
    private static final float DEFAULT_CIRCLE_HAND_BUTTON_RADIUS = 7;
    private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 1;
    private static final float DEFAULT_TIMER_NUMBER_TEXT_SIZE = 50;
    private static final float DEFAULT_QUARTER_NUMBER_TEXT_SIZE = 14;
    private static final float DEFAULT_TIMER_TEXT_SIZE = 14;
    private static final float DEFAULT_LAP_LABEL_TEXT_SIZE = 20;
    private static final float DEFAULT_LABEL_TEXT_SIZE = 16;
    private static final float DEFAULT_PADDING_LABEL = 30;
    private static final float DEFAULT_MARGIN_TOP_LAP_LABEL = 10;
    private static final String DEFAULT_LABEL_TEXT = "";

    // Default color
    private static final int DEFAULT_CIRCLE_COLOR = 0xFFBBDEFB;
    private static final int DEFAULT_CIRCLE_BUTTON_COLOR = 0xFFBBDEFB;
    private static final int DEFAULT_LAP_TEXT_COLOR = Color.RED;
    private static final int DEFAULT_LAP_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_CIRCLE_BUTTON_PRESSED_COLOR = 0x00f7ff;
    private static final int DEFAULT_LINE_COLOR = 0xFF93C2EF;
    private static final int DEFAULT_HIGHLIGHT_LINE_COLOR = 0xFF74EEF1;
    private static final int DEFAULT_NUMBER_COLOR = 0xFFBBDEFB;
    private static final int DEFAULT_TIME_NUMBER_COLOR = Color.WHITE;
    private static final int DEFAULT_QUARTER_NUMBER_COLOR = 0xFFBBDEFB;
    private static final int DEFAULT_TIME_COLON_COLOR = Color.BLACK;
    private static final int DEFAULT_LABEL_TEXT_COLOR = Color.BLACK;

    // Clock face
    public static final int DEFAULT_MINUTE_MARK_COUNT = 120;
    public static final int DEFAULT_TIME = 0;
    public static final int DEFAULT_HAND_POSITION_ANGLE = 0;
    public static final String TEXT_MINUTES_45 = "45";
    public static final String TEXT_MINUTES_60 = "60";
    public static final String TEXT_MINUTES_15 = "15";
    public static final String TEXT_MINUTES_30 = "30";
    public static final int MINUTES_QUARTER = 15;


    public static final String TIME_DIAL_FORMAT_WITH_COLON = "%s:%s";
    public static final String TIME_DIAL_FORMAT_WITHOUT_COLON = "%s\u2008%s";
    // Angles

    // Radian points from unit circle

    public static final double UNIT_CIRCLE_FULL = 2 * Math.PI;
    public static final double UNIT_CIRCLE_HALF = Math.PI;
    public static final double UNIT_CIRCLE_FIRST_INTERCEPT = Math.toRadians(90);
    public static final double UNIT_CIRCLE_SECOND_INTERCEPT = Math.toRadians(180);
    public static final double UNIT_CIRCLE_THIRD_INTERCEPT = Math.toRadians(270);
    public static final double UNIT_CIRCLE_SECOND_FOURTH_INTERCEPT = Math.toRadians(360);
    public static final double RADIAN_PER_MARK = (UNIT_CIRCLE_FULL / 60);
    public static final int FULL_CIRCLE_ANGLE_DEGREE = 360;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int MINIMAL_TIME = 0;
    public static final int SECONDS_IN_HOUR = 3600;
    public static final int TIMER_DELAY = 1000;
    public static final int TIMER_DEFAULT_PERIOD = 1000;
    public static final int TIMER_HANDLER_MESSAGE_WHAT = 222;
    public static final String CHAR_TIMER_COLON = ":";

    //endregion

    //region Attributes

    // Dimension
    private float mPaddingInnerRadius;
    private float mPaddingQuarterNumber;
    private float mMarkSize;
    private float mQuarterMarkSize;
    private float mMarkLineWidth;
    private float mCircleButtonRadius;
    private float mQuarterNumbersTextSize;
    private float mCircleStrokeWidth;
    private float mTimeNumbersTextSize;
    private float mLabelTextSize;
    private float mLapLabelTextSize;
    private float mMarginTopLabel;
    private float mLapLabelMarginTop;

    // Color
    private int mCircleColor;
    private int mCircleButtonColor;
    private int mLineColor;
    private int mHighlightLineColor;
    private int mTimeNumberColor;
    private int mCircleButtonPressedColor;
    private int mLapLabelTextColor;
    private int mQuarterNumberColor;
    private int mLapBackgroundColor;
    private int mLabelTextColor;


    // Parameters
    private PointF mCenterPoint = new PointF();
    private PointF mCurrentCircleButtonCenterPoint = new PointF();
    private PointF mInitialCircleButtonCenterPoint = new PointF();
    private PointF mDialTextPoint = new PointF();
    private float mOuterRadius;
    private float mInnerRadius;
    private float mCurrentRadianAngle;
    private float mPreviousTouchAngleRadian;
    private boolean mIsTouchInsideHandButton;
    // Current time in seconds
    private long mCurrentTimeInSeconds = DEFAULT_TIME;
    private long mTimeCoefficient = 1;
    private long mCurrentLapTimeDelta = SECONDS_IN_MINUTE;
    private boolean mStarted;
    private String mLabelText = DEFAULT_LABEL_TEXT;

    // Clock configuration
    private int mMinuteMarkCount = DEFAULT_MINUTE_MARK_COUNT;
    private boolean mIsMultiLapRotationEnabled = true;
    private boolean mShowLaps = false;

    @TimeFormat
    private int mTimeFormat = FORMAT_SECONDS_MINUTES;
    //endregion

    //region Variables

    private CircleTimeListener mCircleTimeListener;
    private CircleTimerListener mCircleTimerListener;
    @TimeMode
    private int mCurrentTimeMode = MODE_NORMAL;
    // Paint
    private Paint mCirclePaint;
    private Paint mHighlightLinePaint;
    private Paint mLinePaint;
    private Paint mCircleButtonPaint;
    private Paint mCircleButtonPressedPaint;
    private Paint mQuarterNumberPaint;
    private Paint mLabelTextPaint;
    private Paint mTimeNumbersPaint;
    private Paint mLapLabelTextPaint;
    private Paint mLapLabelBackgroundPaint;
    // Rects
    private Rect mLapLabelBackgroundRect;
    // TimerTask
    private Timer mTimer = new Timer();

    private boolean mShowTimeColon = true;
    private long mTimeBeforeHandTouch;
    private long mLapsAfterTouch = 0;

    private TimerTask mDefaultTimerTask = new DefaultTimerTask();

    private Handler mTimerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TIMER_HANDLER_MESSAGE_WHAT) {
                if (mCurrentTimeInSeconds > 0) {
                    // Decrease time for a second
                    mCurrentTimeInSeconds--;
                    setCurrentTime(mCurrentTimeInSeconds, mTimeFormat);
                    if (mCircleTimerListener != null) {
                        mCircleTimerListener.onTimerTimeValueChanged(mCurrentTimeInSeconds);
                    }
                } else {
                    stopTimer();
                }
                // Invert the show time colon flag
                mShowTimeColon = !mShowTimeColon;
                invalidate();
            }
        }
    };


    private LapDataProvider mLapLabelDataProvider = new LapDataProvider() {
        @Override
        public String getLapLabelText(long currentTimeInSeconds) {
            return String.valueOf(getHoursFromSeconds(mCurrentTimeInSeconds));
        }
    };
    //endregion

    //region Inner classes

    // Time Mode

    public static final int MODE_NORMAL = 0;
    public static final int MODE_TIMER = 1;
    public static final int MODE_MANUAL_SETUP = 2;


    @IntDef({MODE_NORMAL, MODE_TIMER, MODE_MANUAL_SETUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeMode {
    }

    // Time Units
    public static final int FORMAT_SECONDS_MINUTES = 0;
    public static final int FORMAT_MINUTES_HOURS = 1;

    @IntDef({FORMAT_SECONDS_MINUTES, FORMAT_MINUTES_HOURS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeFormat {
    }

    public interface LapDataProvider {
        String getLapLabelText(long currentTimeInSeconds);
    }

    private class DefaultTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mTimerHandler != null) {
                Message message = mTimerHandler.obtainMessage(TIMER_HANDLER_MESSAGE_WHAT);
                if (message != null) {
                    message.sendToTarget();
                }
            }
        }
    }

    public interface CircleTimerListener {
        /**
         * Send notification about mTimer stop
         */
        void onTimerStop();

        /**
         * Send notification about mTimer start
         *
         * @param time initial time
         */
        void onTimerStart(long time);

        /**
         * Timer time value change event
         *
         * @param time current time
         */
        void onTimerTimeValueChanged(long time);

    }
    // Listener interface

    public interface CircleTimeListener {

        /**
         * Called when a user manually set time by moving the clock hand and released finger (touch up event)
         *
         * @param time time
         */
        void onTimeManuallySet(long time);


        /**
         * Called when a user manually sets time by moving the clock hand
         *
         * @param time time
         */
        void onTimeManuallyChanged(long time);

        void onTimeUpdated(long time);

    }

    //endregion

    //region Constructors

    public CircleTimeView(Context context) {
        super(context);
        setupView();
    }

    public CircleTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(attrs);
        setupView();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public CircleTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(attrs);
        setupView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleTimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttributes(attrs);
        setupView();
    }
    //endregion

    //region Public methods


    // Getters and setters


    public int getHighlightLineColor() {
        return mHighlightLineColor;
    }

    public void setHighlightLineColor(int highlightLineColor) {
        mHighlightLineColor = highlightLineColor;
        mHighlightLinePaint.setColor(mHighlightLineColor);
    }

    public float getMarkSize() {
        return mMarkSize;
    }

    public void setMarkSize(float markSize) {
        mMarkSize = markSize;
    }

    public int getCircleButtonColor() {
        return mCircleButtonColor;
    }

    public void setCircleButtonColor(int circleButtonColor) {
        mCircleButtonColor = circleButtonColor;
        mCircleButtonPaint.setColor(mCircleButtonColor);
    }

    public int getCircleButtonPressedColor() {
        return mCircleButtonPressedColor;
    }

    public void setCircleButtonPressedColor(int circleButtonPressedColor) {
        mCircleButtonPressedColor = circleButtonPressedColor;
        mCircleButtonPressedPaint.setColor(mCircleButtonPressedColor);
    }

    public int getCircleColor() {
        return mCircleColor;
    }

    public void setCircleColor(int circleColor) {
        mCircleColor = circleColor;
        mCirclePaint.setColor(mCircleColor);
    }

    public float getCircleStrokeWidth() {
        return mCircleStrokeWidth;
    }

    public void setCircleStrokeWidth(float circleStrokeWidth) {
        mCircleStrokeWidth = circleStrokeWidth;
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
    }

    public float getCircleButtonRadius() {
        return mCircleButtonRadius;
    }

    public void setCircleButtonRadius(float circleButtonRadius) {
        mCircleButtonRadius = circleButtonRadius;
    }

    public float getInnerRadius() {
        return mInnerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        mInnerRadius = innerRadius;
    }

    public String getLabelText() {
        return mLabelText;
    }

    public int getLapBackgroundColor() {
        return mLapBackgroundColor;
    }

    public void setLapBackgroundColor(int lapBackgroundColor) {
        mLapBackgroundColor = lapBackgroundColor;
        mLapLabelBackgroundPaint.setColor(mLapBackgroundColor);
    }

    public float getLabelTextSize() {
        return mLabelTextSize;
    }

    public void setLabelTextSize(float labelTextSize) {
        mLabelTextSize = labelTextSize;
        mLabelTextPaint.setTextSize(mLabelTextSize);
    }

    public int getLapLabelTextColor() {
        return mLapLabelTextColor;
    }

    public void setLapLabelTextColor(int lapLabelTextColor) {
        mLapLabelTextColor = lapLabelTextColor;
        mLapLabelTextPaint.setColor(mLapLabelTextColor);
    }

    public float getLapLabelMarginTop() {
        return mLapLabelMarginTop;
    }

    public void setLapLabelMarginTop(float lapLabelMarginTop) {
        mLapLabelMarginTop = lapLabelMarginTop;
        // Trigger rect update
        mLapLabelBackgroundRect = null;
    }

    public float getLapLabelTextSize() {
        return mLapLabelTextSize;
    }

    public void setLapLabelTextSize(float lapLabelTextSize) {
        mLapLabelTextSize = lapLabelTextSize;
        mLapLabelTextPaint.setTextSize(mLapLabelTextSize);
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
        mLinePaint.setColor(mLineColor);
    }

    public float getMarginTopLabel() {
        return mMarginTopLabel;
    }

    public void setMarginTopLabel(float marginTopLabel) {
        mMarginTopLabel = marginTopLabel;
    }

    public int getMinuteMarkCount() {
        return mMinuteMarkCount;
    }

    public void setMinuteMarkCount(int minuteMarkCount) {
        mMinuteMarkCount = minuteMarkCount;
    }

    public int getTimeFormat() {
        return mTimeFormat;
    }

    public void setTimeFormat(int timeFormat) {
        mTimeFormat = timeFormat;
    }

    public float getMarkLineWidth() {
        return mMarkLineWidth;
    }

    public void setMarkLineWidth(float markLineWidth) {
        mMarkLineWidth = markLineWidth;
        mLinePaint.setStrokeWidth(mMarkLineWidth);
        mHighlightLinePaint.setStrokeWidth(mMarkLineWidth);
    }

    public float getOuterRadius() {
        return mOuterRadius;
    }

    public void setOuterRadius(float outerRadius) {
        mOuterRadius = outerRadius;
    }

    public float getPaddingInnerRadius() {
        return mPaddingInnerRadius;
    }

    public void setPaddingInnerRadius(float paddingInnerRadius) {
        mPaddingInnerRadius = paddingInnerRadius;
    }

    public float getQuarterMarkSize() {
        return mQuarterMarkSize;
    }

    public void setQuarterMarkSize(float quarterMarkSize) {
        mQuarterMarkSize = quarterMarkSize;
    }

    public float getPaddingQuarterNumber() {
        return mPaddingQuarterNumber;
    }

    public void setPaddingQuarterNumber(float paddingQuarterNumber) {
        mPaddingQuarterNumber = paddingQuarterNumber;
    }


    public int getLabelTextColor() {
        return mLabelTextColor;
    }

    public void setLabelTextColor(int labelTextColor) {
        mLabelTextColor = labelTextColor;
        mLabelTextPaint.setColor(mLabelTextColor);
    }

    public int getQuarterNumberColor() {
        return mQuarterNumberColor;
    }

    public void setQuarterNumberColor(int quarterNumberColor) {
        mQuarterNumberColor = quarterNumberColor;
        mQuarterNumberPaint.setColor(mQuarterNumberColor);
    }

    public int getTimeNumberColor() {
        return mTimeNumberColor;
    }

    public void setTimeNumberColor(int timeNumberColor) {
        mTimeNumberColor = timeNumberColor;
        mTimeNumbersPaint.setColor(mTimeNumberColor);
    }

    public float getQuarterNumbersTextSize() {
        return mQuarterNumbersTextSize;
    }

    public void setQuarterNumbersTextSize(float quarterNumbersTextSize) {
        mQuarterNumbersTextSize = quarterNumbersTextSize;
        mQuarterNumberPaint.setTextSize(mQuarterNumbersTextSize);
    }

    public float getTimeNumbersTextSize() {
        return mTimeNumbersTextSize;
    }

    public void setTimeNumbersTextSize(float timeNumbersTextSize) {
        mTimeNumbersTextSize = timeNumbersTextSize;
        mTimeNumbersPaint.setTextSize(mTimeNumbersTextSize);
    }

    public CircleTimerListener getCircleTimerListener() {
        return mCircleTimerListener;
    }

    public void setCircleTimerListener(CircleTimerListener circleTimerListener) {
        mCircleTimerListener = circleTimerListener;
    }


    public void setCurrentTimeMode(@TimeMode int timeMode) {
        this.mCurrentTimeMode = timeMode;
    }


    public
    @TimeMode
    int getCurrentTimeMode() {
        return this.mCurrentTimeMode;
    }

    /**
     * start mTimer
     */
    public void startTimer() {
        if (mCurrentTimeInSeconds > 0 && !mStarted) {
            mCurrentTimeMode = MODE_TIMER;
            if (mTimer == null) {
                mTimer = new Timer();
            }
            if (mDefaultTimerTask == null) {
                mDefaultTimerTask = new DefaultTimerTask();
            }
            mTimer.schedule(mDefaultTimerTask, TIMER_DELAY, TIMER_DEFAULT_PERIOD);
            mStarted = true;
            if (this.mCircleTimerListener != null) {
                this.mCircleTimerListener.onTimerStart(mCurrentTimeInSeconds);
            }
        }
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mStarted = false;
            if (mCircleTimerListener != null) {
                mCircleTimerListener.onTimerStop();
            }
            mTimer = null;
            mDefaultTimerTask = null;
        }
    }

    public void resetTime() {
        mCurrentTimeInSeconds = 0;
        mCurrentRadianAngle = 0;
        setCurrentTime(mCurrentTimeInSeconds, mTimeFormat);
        invalidate();
    }

    /**
     * Set current time with format
     *
     * @param time       time
     * @param timeFormat format of time
     */
    public void setCurrentTime(long time, @TimeFormat int timeFormat) {
        updateCurrentTime(time);
        this.mTimeFormat = timeFormat;
        setTimeValues(timeFormat);
        rotateButtonHandToCurrentAngle();
        invalidate();
    }

    /**
     * Set current time with the default format
     *
     * @param time time in seconds
     */
    public void setCurrentTime(long time) {
        this.setCurrentTime(time, FORMAT_SECONDS_MINUTES);
    }

    /**
     * Set label text by resource id
     *
     * @param id int id value
     */
    public void setLabelText(@StringRes int id) {
        if (id > 0) {
            setLabelText(getResources().getString(id));
        }
    }

    /**
     * Display lap label
     */
    public void showLapsLabel() {
        this.mShowLaps = true;
    }

    /**
     * Hide lap label
     */
    public void hideLapsLabel() {
        this.mShowLaps = false;
    }

    /**
     * Set label text
     *
     * @param value String value
     */
    public void setLabelText(String value) {
        if (value != null) {
            mLabelText = value;
        }
        invalidate();
    }

    /**
     * set mTimer listener
     *
     * @param mCircleTimerListener
     */
    public void setCircleTimeListener(CircleTimeListener mCircleTimerListener) {
        this.mCircleTimeListener = mCircleTimerListener;
    }

    /**
     * Get current time in seconds
     *
     * @return current time in seconds
     */
    public long getCurrentTimeInSeconds() {
        return mCurrentTimeInSeconds;
    }

    public LapDataProvider getLapLabelDataProvider() {
        return mLapLabelDataProvider;
    }

    public void setLapLabelDataProvider(LapDataProvider lapLabelDataProvider) {
        mLapLabelDataProvider = lapLabelDataProvider;
    }

    //endregion

    //region Private methods

    //region Lifecycle methods

    @Override
    protected void onDraw(Canvas canvas) {
        drawFaceCircle(canvas);
        drawMinuteMarkings(canvas);
        drawQuarterMinutesLabels(canvas);
        drawClockMovableHandButton(canvas);
        PointF bottomPositionOfText = drawClockTime(canvas);
        PointF bottomPointOfLapLabel = drawLapLabel(canvas, bottomPositionOfText);
        drawClockLabel(canvas, bottomPointOfLapLabel);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.setCenterPoint(width, height);
        this.setInitialCurrentTime();
        // Radius
        this.calculateOuterRadius(width, height);
        this.calculateInnerRadius(width, height);
        this.setInitialHandButtonPoint();
        setMeasuredDimension(width, height);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                handleTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                handleTouchUp(event);
                break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        mTimerHandler = null;
    }
    //endregion

    //region Initialization methods
    private void setupView() {
        initView();
        initDefaultConstants();
    }


    private void parseAttributes(AttributeSet attributeSet) {
        // Attribute initialization
        final TypedArray attrs = getContext().obtainStyledAttributes(attributeSet, R.styleable.CircleTimeView, 0, 0);
        try {
            Context currentContext = getContext();
            mCircleButtonRadius = attrs.getDimension(R.styleable.CircleTimeView_ctvCircleHandButtonRadius, getRealUnitValueDp(DEFAULT_CIRCLE_HAND_BUTTON_RADIUS, currentContext));
            mCircleStrokeWidth = attrs.getDimension(R.styleable.CircleTimeView_ctvCircleStrokeWidth, getRealUnitValueDp(DEFAULT_CIRCLE_STROKE_WIDTH, currentContext));
            mMarkLineWidth = attrs.getDimension(R.styleable.CircleTimeView_ctvMarkLineWidth, getRealUnitValueDp(DEFAULT_MARK_LINE_WIDTH, currentContext));
            mPaddingQuarterNumber = attrs.getDimension(R.styleable.CircleTimeView_ctvPaddingQuarterNumber, getRealUnitValueDp(DEFAULT_PADDING_QUARTER_NUMBER, currentContext));
            mMarginTopLabel = attrs.getDimension(R.styleable.CircleTimeView_ctvMarginTopLabel, getRealUnitValueDp(DEFAULT_PADDING_LABEL, currentContext));
            mLapLabelMarginTop = attrs.getDimension(R.styleable.CircleTimeView_ctvLapLabelMarginTop, getRealUnitValueDp(DEFAULT_MARGIN_TOP_LAP_LABEL, currentContext));
            mPaddingInnerRadius = attrs.getDimension(R.styleable.CircleTimeView_ctvPaddingInnerRadius, getRealUnitValueDp(DEFAULT_PADDING_INNER_RADIUS, currentContext));
            mTimeNumbersTextSize = attrs.getDimension(R.styleable.CircleTimeView_ctvTimeNumbersTextSize, getRealUnitValueDp(DEFAULT_TIMER_NUMBER_TEXT_SIZE, currentContext));
            mQuarterNumbersTextSize = attrs.getDimension(R.styleable.CircleTimeView_ctvQuarterNumberTextSize, getRealUnitValueDp(DEFAULT_QUARTER_NUMBER_TEXT_SIZE, currentContext));
            mLabelTextSize = attrs.getDimension(R.styleable.CircleTimeView_ctvLabelTextSize, getRealUnitValueDp(DEFAULT_LABEL_TEXT_SIZE, currentContext));
            mLapLabelTextSize = attrs.getDimension(R.styleable.CircleTimeView_ctvLapLabelTextSize, getRealUnitValueDp(DEFAULT_LAP_LABEL_TEXT_SIZE, currentContext));
            mMarkSize = attrs.getDimension(R.styleable.CircleTimeView_ctvMarkSize, getRealUnitValueDp(DEFAULT_MARK_SIZE, currentContext));
            mQuarterMarkSize = attrs.getDimension(R.styleable.CircleTimeView_ctvQuarterMarkSize, getRealUnitValueDp(DEFAULT_QUARTER_MARK_SIZE, currentContext));
            mMinuteMarkCount = attrs.getInteger(R.styleable.CircleTimeView_ctvMinutesMarkCount, DEFAULT_MINUTE_MARK_COUNT);
            //noinspection WrongConstant
            mCurrentTimeMode = attrs.getInteger(R.styleable.CircleTimeView_ctvTimeMode, MODE_NORMAL);
            //noinspection WrongConstant
            mTimeFormat = attrs.getInteger(R.styleable.CircleTimeView_ctvTimeFormat, FORMAT_SECONDS_MINUTES);
            mCurrentTimeInSeconds = attrs.getInteger(R.styleable.CircleTimeView_ctvCurrentTimeInSeconds, DEFAULT_TIME);
            // Set default color or read xml attributes
            mCircleColor = attrs.getColor(R.styleable.CircleTimeView_ctvCircleColor, DEFAULT_CIRCLE_COLOR);
            mCircleButtonColor = attrs.getColor(R.styleable.CircleTimeView_ctvCircleButtonColor, DEFAULT_CIRCLE_BUTTON_COLOR);
            mCircleButtonPressedColor = attrs.getColor(R.styleable.CircleTimeView_ctvCirclePressedButtonColor, DEFAULT_CIRCLE_BUTTON_COLOR);
            mLapLabelTextColor = attrs.getColor(R.styleable.CircleTimeView_ctvLapTextColor, DEFAULT_LAP_TEXT_COLOR);
            mLapBackgroundColor = attrs.getColor(R.styleable.CircleTimeView_ctvLapBackgroundColor, DEFAULT_LAP_BACKGROUND_COLOR);
            mLineColor = attrs.getColor(R.styleable.CircleTimeView_ctvLineColor, DEFAULT_LINE_COLOR);
            mHighlightLineColor = attrs.getColor(R.styleable.CircleTimeView_ctvHighlightLineColor, DEFAULT_HIGHLIGHT_LINE_COLOR);
            mQuarterNumberColor = attrs.getColor(R.styleable.CircleTimeView_ctvQuarterNumberColor, DEFAULT_QUARTER_NUMBER_COLOR);
            mTimeNumberColor = attrs.getColor(R.styleable.CircleTimeView_ctvTimeNumberColor, DEFAULT_TIME_NUMBER_COLOR);
            mLabelTextColor = attrs.getColor(R.styleable.CircleTimeView_ctvLabelTextColor, DEFAULT_LABEL_TEXT_COLOR);
            mLabelText = attrs.getString(R.styleable.CircleTimeView_ctvLabelText);
            mLabelText = mLabelText == null ? DEFAULT_LABEL_TEXT : mLabelText;
            mIsMultiLapRotationEnabled = attrs.getBoolean(R.styleable.CircleTimeView_ctvMultiLapRotation, true);
            mShowLaps = attrs.getBoolean(R.styleable.CircleTimeView_ctvShowLaps, false);
        } catch (Exception ignore) {
            Log.e("CircleTimeView", ignore.getMessage());
        } finally {
            // Release resources
            attrs.recycle();
        }

    }

    private void initView() {

        // Init all paints
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleButtonPressedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mQuarterNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeNumbersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLapLabelBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLapLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Init circle paint
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);

        // Init circle hand button paint
        mCircleButtonPaint.setColor(mCircleButtonColor);
        mCircleButtonPaint.setAntiAlias(true);
        mCircleButtonPaint.setStyle(Paint.Style.FILL);

        // Init circle hand button paint
        mCircleButtonPressedPaint.setColor(mCircleButtonPressedColor);
        mCircleButtonPressedPaint.setAntiAlias(true);
        mCircleButtonPressedPaint.setStyle(Paint.Style.FILL);

        // LapLabel Text paint
        mLapLabelTextPaint.setColor(mLapLabelTextColor);
        mLapLabelTextPaint.setTextAlign(Paint.Align.CENTER);
        mLapLabelTextPaint.setTextSize(mLapLabelTextSize);

        // LapLabel Background paint
        mLapLabelBackgroundPaint.setColor(mLapBackgroundColor);
        mLapLabelBackgroundPaint.setAntiAlias(true);
        mLapLabelBackgroundPaint.setStyle(Paint.Style.FILL);

        // LinePaint
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mMarkLineWidth);

        // HighlightLinePaint
        mHighlightLinePaint.setColor(mHighlightLineColor);
        mHighlightLinePaint.setStrokeWidth(mMarkLineWidth);

        // Time Quarter number paint
        mQuarterNumberPaint.setColor(mQuarterNumberColor);
        mQuarterNumberPaint.setTextSize(mQuarterNumbersTextSize);
        mQuarterNumberPaint.setTextAlign(Paint.Align.CENTER);

        // Label text paint
        mLabelTextPaint.setColor(mLabelTextColor);
        mLabelTextPaint.setTextSize(mLabelTextSize);
        mLabelTextPaint.setTextAlign(Paint.Align.CENTER);

        // Time numbers paint
        mTimeNumbersPaint.setColor(mTimeNumberColor);
        mTimeNumbersPaint.setTextAlign(Paint.Align.CENTER);
        mTimeNumbersPaint.setTextSize(mTimeNumbersTextSize);

    }

    private void initDefaultConstants() {
        mCurrentRadianAngle = (float) Math.toRadians(DEFAULT_HAND_POSITION_ANGLE);
    }

    private void setInitialHandButtonPoint() {
        // Set initial point of the hand button at the 12 oclock
        mInitialCircleButtonCenterPoint.x = mCenterPoint.x;
        mInitialCircleButtonCenterPoint.y = mCenterPoint.y - mInnerRadius - mCircleButtonRadius;
        // Set current to inital
        mCurrentCircleButtonCenterPoint.x = mInitialCircleButtonCenterPoint.x;
        mCurrentCircleButtonCenterPoint.y = mInitialCircleButtonCenterPoint.y;
    }

    private void setInitialCurrentTime() {
        setCurrentTime(mCurrentTimeInSeconds, mTimeFormat);
    }

    private void setCenterPoint(int width, int height) {
        this.mCenterPoint.x = width / 2;
        this.mCenterPoint.y = height / 2;
    }

    private void initTimeBeforeTouch() {
        // Find remainder
        long timeRemainder = 0;
        switch (mTimeFormat) {
            case FORMAT_SECONDS_MINUTES:
                timeRemainder = mCurrentTimeInSeconds % SECONDS_IN_MINUTE;
                break;
            case FORMAT_MINUTES_HOURS:
                timeRemainder = mCurrentTimeInSeconds % SECONDS_IN_HOUR;
                break;
            default:
                break;
        }
        // Set touch to the 12 hour point to add just a rotation angle while touch move
        mTimeBeforeHandTouch = mCurrentTimeInSeconds - timeRemainder;
        // Set laps to 0
        mLapsAfterTouch = 0;
    }

    //endregion

    //region Drawing methods
    protected void drawClockMovableHandButton(Canvas canvas) {
        if (isTouchingInsideHandButton()) {
            canvas.drawCircle(mCurrentCircleButtonCenterPoint.x, mCurrentCircleButtonCenterPoint.y, mCircleButtonRadius, mCircleButtonPressedPaint);
        } else {
            canvas.drawCircle(mCurrentCircleButtonCenterPoint.x, mCurrentCircleButtonCenterPoint.y, mCircleButtonRadius, mCircleButtonPaint);
        }
    }

    protected void drawFaceCircle(Canvas canvas) {
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mInnerRadius, mCirclePaint);
    }

    protected PointF drawLapLabel(Canvas canvas, PointF bottomPositionOfDialText) {
        if (mShowLaps && mLapLabelDataProvider != null) {
            String textToDraw = mLapLabelDataProvider.getLapLabelText(mCurrentTimeInSeconds);
            if (mLapLabelBackgroundRect == null) {
                mLapLabelBackgroundRect = initLapLabelBackgroundRect(bottomPositionOfDialText, DEFAULT_PADDING_LAP_LABEL);
            }
            float distanceFromBaseLine = getDistanceFromBaseline(mLapLabelTextPaint);
            // Reuse ex
            canvas.drawRect(mLapLabelBackgroundRect, mLapLabelBackgroundPaint);
            canvas.drawText(textToDraw, bottomPositionOfDialText.x, mLapLabelBackgroundRect.centerY() + distanceFromBaseLine, mLapLabelTextPaint);
            bottomPositionOfDialText.y = mLapLabelBackgroundRect.bottom;
        }
        return bottomPositionOfDialText;
    }

    private Rect initLapLabelBackgroundRect(PointF bottomPositionOfDialText, int paddingRect) {
        Rect lapLabelBackgroundRect = getTextBounds(mLapLabelTextPaint, "999");
        lapLabelBackgroundRect.set(
                (int) bottomPositionOfDialText.x - lapLabelBackgroundRect.width() / 2 - paddingRect,
                (int) ((int) bottomPositionOfDialText.y - lapLabelBackgroundRect.height() / 2 - paddingRect + mLapLabelMarginTop),
                (int) bottomPositionOfDialText.x + lapLabelBackgroundRect.width() / 2 + paddingRect,
                (int) ((int) bottomPositionOfDialText.y + lapLabelBackgroundRect.height() / 2 + paddingRect + mLapLabelMarginTop));
        return lapLabelBackgroundRect;
    }

    protected void drawClockLabel(Canvas canvas, PointF bottomPointOfLapLabel) {
        if (mLabelText != null && !mLabelText.isEmpty()) {
            canvas.drawText(mLabelText, bottomPointOfLapLabel.x, bottomPointOfLapLabel.y + getTextBounds(mLabelTextPaint, mLabelText).height() + mMarginTopLabel, mLabelTextPaint);
        }
    }


    protected PointF drawClockTime(Canvas canvas) {
        // Format time
        String formattedTime = formatCurrentTime();
        float distanceFromBaseLine = getDistanceFromBaseline(mTimeNumbersPaint);
        Rect textBounds = getTextBounds(mLapLabelTextPaint, formattedTime);
        mDialTextPoint.x = mCenterPoint.x;
        mDialTextPoint.y = mCenterPoint.y + distanceFromBaseLine;
        if (mShowTimeColon) {
            canvas.drawText(CHAR_TIMER_COLON, mDialTextPoint.x, mDialTextPoint.y, mTimeNumbersPaint);
        }
        canvas.drawText(formattedTime, mDialTextPoint.x, mDialTextPoint.y, mTimeNumbersPaint);
        // Reuse pointf object and set it to the bottom position of the drawn text
        mDialTextPoint.y = mDialTextPoint.y + textBounds.height();
        return mDialTextPoint;
    }

    protected void drawQuarterMinutesLabels(Canvas canvas) {
        // Number it is rubbish code
        float offsetFromCenterX = mInnerRadius - mPaddingInnerRadius - mPaddingQuarterNumber;
        float offsetFromCenterY =
                (mInnerRadius - mPaddingInnerRadius - mPaddingQuarterNumber);
        float distanceFromTextBaseLine = +getDistanceFromBaseline(mQuarterNumberPaint);
        canvas.drawText(TEXT_MINUTES_60, mCenterPoint.x, mCenterPoint.y - offsetFromCenterY + distanceFromTextBaseLine, mQuarterNumberPaint);
        canvas.drawText(TEXT_MINUTES_30, mCenterPoint.x, mCenterPoint.y + offsetFromCenterY + distanceFromTextBaseLine, mQuarterNumberPaint);
        canvas.drawText(TEXT_MINUTES_15, mCenterPoint.x + offsetFromCenterX - distanceFromTextBaseLine / 2, mCenterPoint.y + distanceFromTextBaseLine, mQuarterNumberPaint);
        canvas.drawText(TEXT_MINUTES_45, mCenterPoint.x - offsetFromCenterX + distanceFromTextBaseLine / 2, mCenterPoint.y + distanceFromTextBaseLine,
                mQuarterNumberPaint);
    }

    protected void drawMinuteMarkings(Canvas canvas) {
        int minutesPerMark = (mMinuteMarkCount / MINUTES_IN_HOUR);
        for (int i = 0; i < mMinuteMarkCount; i++) {
            canvas.save();
            canvas.rotate(FULL_CIRCLE_ANGLE_DEGREE / mMinuteMarkCount * i, mCenterPoint.x, mCenterPoint.y);
            boolean isHighlightedMark = isHighlightedMark(i);
            if (isMinuteMark(i, minutesPerMark)) {
                if (isHighlightedMark) {
                    canvas.drawLine(mCenterPoint.x, getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius,
                            mCenterPoint.x,
                            getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius +
                                    mQuarterMarkSize, mHighlightLinePaint);
                } else {
                    canvas.drawLine(mCenterPoint.x, getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius,
                            mCenterPoint.x,
                            getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius +
                                    mQuarterMarkSize, mLinePaint);
                }
            } else {
                if (isHighlightedMark) {
                    canvas.drawLine(mCenterPoint.x, getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius,
                            mCenterPoint.x,
                            getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius + mMarkSize,
                            mHighlightLinePaint);
                } else {
                    canvas.drawLine(mCenterPoint.x, getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius,
                            mCenterPoint.x,
                            getMeasuredHeight() / 2 - mOuterRadius + mCircleStrokeWidth / 2 + mPaddingInnerRadius + mMarkSize,
                            mLinePaint);
                }
            }
            canvas.restore();
        }
    }

    //endregion

    //region Touch methods
    private void handleTouchDown(MotionEvent event) {

        if (isTouchHandMoveAllowed()) {
            initTimeBeforeTouch();
            if (isPointInsideCircle(mCurrentCircleButtonCenterPoint.x, mCurrentCircleButtonCenterPoint.y, event.getX(), event.getY(), mCircleButtonRadius)) {
                mIsTouchInsideHandButton = true;
                mPreviousTouchAngleRadian = getRadianOfThePoint(event.getX(), event.getY());
            }
        }
    }

    private void handleTouchMove(MotionEvent event) {
        if (isTouchingInsideHandButton()) {
            float touchPointRadianAngle = getRadianOfThePoint(event.getX(), event.getY());
            // Prevent crossing the 12 hour point (90 degrees) clockwise or count laps
            if (mPreviousTouchAngleRadian > UNIT_CIRCLE_THIRD_INTERCEPT && touchPointRadianAngle < UNIT_CIRCLE_FIRST_INTERCEPT) {
                if (mIsMultiLapRotationEnabled) {
                    onLapPassedClockwise();
                } else {
                    mPreviousTouchAngleRadian -= UNIT_CIRCLE_FULL;
                }
                // Prevent crossing the 12 hour point (90 degrees) counter clockwise or decrease laps
            } else if (mPreviousTouchAngleRadian < UNIT_CIRCLE_FIRST_INTERCEPT && touchPointRadianAngle > UNIT_CIRCLE_THIRD_INTERCEPT) {
                // Prevent negative laps
                if (mIsMultiLapRotationEnabled && mCurrentTimeInSeconds > MINIMAL_TIME) {
                    onLapPassedAnticlockwise();
                } else {
                    mPreviousTouchAngleRadian = (float) (touchPointRadianAngle + (touchPointRadianAngle - UNIT_CIRCLE_FULL) - mPreviousTouchAngleRadian);
                }
            }
            mCurrentRadianAngle += (touchPointRadianAngle - mPreviousTouchAngleRadian);

            if (mCurrentRadianAngle > UNIT_CIRCLE_FULL) {
                mCurrentRadianAngle = (float) (UNIT_CIRCLE_FULL);
            } else if (mCurrentRadianAngle < 0) {
                mCurrentRadianAngle = 0;
            }

            if (mCircleTimeListener != null) {
                mCircleTimeListener.onTimeManuallyChanged(getCurrentTimeInSeconds());
            }
            // Rotating clockwise
            if (mPreviousTouchAngleRadian < touchPointRadianAngle) {
                onClockHandRotatedClockwise();
            }
            // Rotating anticlockwise
            else {
                onClockHandRotatedAntiClockwise();
            }
            updateCurrentTime(mCurrentTimeInSeconds);
            // Rotate hand button
            rotateButtonHandToCurrentAngle();
            mPreviousTouchAngleRadian = touchPointRadianAngle;
            invalidate();
        }
    }

    private void handleTouchUp(MotionEvent event) {
        if (isTouchHandMoveAllowed()) {
            mIsTouchInsideHandButton = false;
            initTimeBeforeTouch();
            if (mCircleTimeListener != null) {
                mCircleTimeListener.onTimeManuallySet(getCurrentTimeInSeconds());
            }
            invalidate();
        }

    }

    //endregion

    //region Rotation callbacks
    protected void onClockHandRotatedClockwise() {
        long timeByRotation = getTimeByRotationAngle();
        mCurrentTimeInSeconds = mTimeBeforeHandTouch + timeByRotation * mTimeCoefficient + (mCurrentLapTimeDelta * mLapsAfterTouch);
    }

    protected void onClockHandRotatedAntiClockwise() {
        long timeByRotation = getTimeByRotationAngle();
        mCurrentTimeInSeconds = mTimeBeforeHandTouch + timeByRotation * mTimeCoefficient + (mCurrentLapTimeDelta * mLapsAfterTouch);
    }

    protected void onLapPassedClockwise() {
        if (mCurrentTimeInSeconds > getTimeSecondsCoefficient(mTimeFormat)) {
            mLapsAfterTouch++;
        }
    }

    protected void onLapPassedAnticlockwise() {
        if (mCurrentTimeInSeconds > getTimeSecondsCoefficient(mTimeFormat)) {
            mLapsAfterTouch--;
        }
    }
    //endregion

    //region Helper private methods

    private void setTimeValues(@TimeFormat int timeFormat) {
        this.mCurrentRadianAngle = convertTimeIntoRadianAngle(mCurrentTimeInSeconds, timeFormat);
        this.mTimeCoefficient = getTimeSecondsCoefficient(timeFormat);
        this.mCurrentLapTimeDelta = getTimeLapDeltaByTimeFormat(timeFormat);
    }

    private void updateCurrentTime(long time) {
        this.mCurrentTimeInSeconds = time;
        if (mCircleTimeListener != null) {
            mCircleTimeListener.onTimeUpdated(time);
        }
    }

    private String formatCurrentTime() {
        // Calculate time for both dial parts
        int firstPartValue = 0;
        int secondPartValue = 0;
        switch (mTimeFormat) {
            case FORMAT_SECONDS_MINUTES:
                firstPartValue = getMinutesFromSeconds(mCurrentTimeInSeconds);
                secondPartValue = (int) (mCurrentTimeInSeconds % SECONDS_IN_MINUTE);
                break;
            case FORMAT_MINUTES_HOURS:
                firstPartValue = getHoursFromSeconds(mCurrentTimeInSeconds);
                secondPartValue = getMinutesFromSeconds(mCurrentTimeInSeconds);
                break;
            default:
                break;
        }
        // Format time string
        String firstPartText = formatTimeValueToString(firstPartValue);
        String secondPartText = formatTimeValueToString(secondPartValue);
        return String.format(TIME_DIAL_FORMAT_WITHOUT_COLON, firstPartText, secondPartText);

    }

    private boolean isHighlightedMark(int minutes) {
        return FULL_CIRCLE_ANGLE_DEGREE / mMinuteMarkCount * minutes <= Math.toDegrees(mCurrentRadianAngle);
    }

    private void rotateButtonHandToCurrentAngle() {
        rotatePoint(mCenterPoint, mInitialCircleButtonCenterPoint, mCurrentCircleButtonCenterPoint, mCurrentRadianAngle);
    }

    private boolean isMinuteMark(int minutes, int minutesPerMark) {
        // Casting to float because of auto to int conversion
        return ((float) minutes / minutesPerMark) % MINUTES_QUARTER == 0;
    }

    private boolean isTouchHandMoveAllowed() {
        return isEnabled() && mCurrentTimeMode == MODE_MANUAL_SETUP;
    }

    private boolean isTouchingInsideHandButton() {
        return mIsTouchInsideHandButton && isEnabled() && mCurrentTimeMode == MODE_MANUAL_SETUP;

    }

    // Get radian angle of the point on the circle
    private float getRadianOfThePoint(float x, float y) {
        // Find angle between point of touch and center
        float alpha = (float) Math.atan((x - mCenterPoint.x) / (mCenterPoint.y - y));
        // Add additional rotation angle depending on current position
        if (x > mCenterPoint.x && y > mCenterPoint.y) {
            // Quadrant 2
            alpha += UNIT_CIRCLE_HALF;
        } else if (x < mCenterPoint.x && y > mCenterPoint.y) {
            // Quadrant 3
            alpha += UNIT_CIRCLE_HALF;
        } else if (x < mCenterPoint.x && y < mCenterPoint.y) {
            // Quadrant 4
            alpha = (float) (UNIT_CIRCLE_FULL + alpha);
        }
        return alpha;
    }

    protected float widthForMarkCircleLine() {
        return Math.max(mQuarterMarkSize, Math.max(mMarkSize, mCircleButtonRadius * 2));
    }

    protected float widthOfOuterStrokeLine() {
        return widthForMarkCircleLine() + mPaddingInnerRadius;
    }

    protected void calculateOuterRadius(int width, int height) {
        int size = Math.min(height, width);
        this.mOuterRadius = size / 2 - mCircleStrokeWidth / 2;
    }

    protected void calculateInnerRadius(int width, int height) {
        int size = Math.min(height, width);
        this.mInnerRadius = size / 2 - mCircleStrokeWidth / 2 - mPaddingInnerRadius - widthForMarkCircleLine() - mCircleButtonRadius;
    }

    protected long getTimeByRotationAngle() {
        return (long) (60 / (UNIT_CIRCLE_FULL) * mCurrentRadianAngle);
    }
    //endregion

    //endregion


    //region State methods

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_STATE, super.onSaveInstanceState());
        bundle.putFloat(BUNDLE_ROTATION_DEGREE_RADIAN, mCurrentRadianAngle);
        bundle.putLong(BUNDLE_CURRENT_TIME, mCurrentTimeInSeconds);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_STATE));
            mCurrentRadianAngle = bundle.getFloat(BUNDLE_ROTATION_DEGREE_RADIAN);
            mCurrentTimeInSeconds = bundle.getLong(BUNDLE_CURRENT_TIME);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //endregion

    //region Static helper methods

    public static boolean isPointInsideCircle(float cX, float cY, float pX, float pY, float radius) {
        return (cX - pX) * (cX - pX) + (cY - pY) * (cY - pY) < radius * radius;
    }

    public static PointF rotatePoint(PointF centerPoint, PointF initialPoint, PointF targetPoint, double rotationAngleInRadians) {
        double angleSin = Math.sin(rotationAngleInRadians);
        double angleCos = Math.cos(rotationAngleInRadians);
        float rotatedX = (float) (angleCos * (initialPoint.x - centerPoint.x) - angleSin * (initialPoint.y - centerPoint.y) + centerPoint.x);
        float rotatedY = (float) (angleSin * (initialPoint.x - centerPoint.x) + angleCos * (initialPoint.y - centerPoint.y) + centerPoint.y);
        targetPoint.x = rotatedX;
        targetPoint.y = rotatedY;
        return targetPoint;
    }


    public static int getHoursFromSeconds(long timeInSeconds) {
        return (int) timeInSeconds / 3600;
    }

    public static int getMinutesFromSeconds(long timeInSeconds) {
        return (int) (timeInSeconds % 3600) / 60;
    }

    public static long getTimeLapDeltaByTimeFormat(@TimeFormat int timeFormat) {
        switch (timeFormat) {
            case FORMAT_SECONDS_MINUTES:
                return SECONDS_IN_MINUTE;
            case FORMAT_MINUTES_HOURS:
                return SECONDS_IN_HOUR;
            default:
                return SECONDS_IN_MINUTE;
        }
    }

    public static float getFontHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, 1, rect);
        return rect.height();
    }

    public static float getFontHeight(Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }

    public static Rect getTextBounds(Paint textPaint, String text) {
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    public static float convertTimeIntoRadianAngle(long timeInSeconds, @TimeFormat int timeFormat) {
        float timeAngle;

        switch (timeFormat) {
            case FORMAT_SECONDS_MINUTES:
                int secondsReminder = (int) (timeInSeconds % SECONDS_IN_MINUTE);
                timeAngle = (float) (secondsReminder * RADIAN_PER_MARK);
                break;
            case FORMAT_MINUTES_HOURS:
                int minutesRemainder = getMinutesFromSeconds(timeInSeconds) % MINUTES_IN_HOUR;
                timeAngle = (float) (minutesRemainder * RADIAN_PER_MARK);
                break;
            default:
                timeAngle = (float) UNIT_CIRCLE_FULL;
                break;
        }
        return timeAngle;
    }

    public static float getDistanceFromBaseline(Paint textPaint) {
        return Math.abs((textPaint.descent() + textPaint.ascent()) / 2);
    }

    public static float getTextHeight(Paint textPaint) {
        return Math.abs(textPaint.ascent()) + Math.abs(textPaint.descent());
    }

    public static long getTimeSecondsCoefficient(@TimeFormat int timeFormat) {
        switch (timeFormat) {
            case FORMAT_SECONDS_MINUTES:
                return 1;
            case FORMAT_MINUTES_HOURS:
                return SECONDS_IN_MINUTE;
            default:
                return 1;
        }
    }

    public static float getRealUnitValueDp(float value, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context
                .getResources().getDisplayMetrics());
    }

    public static String formatTimeValueToString(int timeValue) {
        return String.format("%02d", timeValue);
    }


    //endregion

}
