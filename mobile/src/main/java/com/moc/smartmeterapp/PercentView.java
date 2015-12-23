package com.moc.smartmeterapp;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.moc.smartmeterapp.ui.Limiter;

/**
 * Created by philipp on 07.12.2015.
 */
public class PercentView extends View {
    private final int ANGLE_ORIENTATION = 90;
    private final int STICK_MARGIN = 17;
    private final int INNER_MARGIN = 28;
    private final int OUTTER_MARGIN = 18;
    private final int TEXT_INNER_MARGIN = 25;
    private final int TEXT_SIZE = 35;
    private final int TEXT_SIZE_SMALL = 25;
    private final int TEXT_SIZE_BIG = 80;
    private final int LAST_TEXT_ANGLE = 350;
    private final int PIN_PADDING = 50;
    private final int LIMIT_PADDING = 45;

    //event stuff
    private Limiter limiter;

    //animator
    private ValueAnimator animator;

    //data stuff
    private float value;
    private String text;
    private int width;
    private int height;
    private int radius;
    private RectF rectF;

    //status stuff
    private boolean valuesToText;

    //paint stuff
    private Paint backgroundPaint;
    private Paint foregroundPaint;

    public PercentView(Context context) {
        super(context);
        init();
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.WHITE);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(Color.GREEN);

        animator = new ValueAnimator(this);
        value = 0;
        text = "";
        rectF = new RectF();

        valuesToText = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();

        int center_x = width/2;
        int center_y= height/2;

        if(height >= width) {
            radius = center_x-OUTTER_MARGIN;
        } else {
            radius = center_y-OUTTER_MARGIN;
        }

        canvas.drawCircle(center_x, center_y, radius, backgroundPaint);

        rectF.set(center_x - radius + LIMIT_PADDING, center_y - radius + LIMIT_PADDING, center_x + radius - LIMIT_PADDING, center_y + radius - LIMIT_PADDING);
        if(value > 0 && value < 100) {
            canvas.drawArc(rectF, 0, 100*value/360, true, foregroundPaint);
        } else if (value <= 0) {
            canvas.drawArc(rectF, 0, 0, true, foregroundPaint);
        } else if (value >= 100) {
            canvas.drawArc(rectF, 0, 360, true, foregroundPaint);
        }

        canvas.drawCircle(center_x, center_y, radius/4, backgroundPaint);

        if(text != null) {
            //TODO: draw centered text https://chris.banes.me/2014/03/27/measuring-text/
        }
    }

    public void setValue(float value) {
        //this.value = value;
        if(animator.getAnimationState() == animator.ANIMATIONSTATE_NONE) {
            animator.animate(this.value, value);
        }
    }

    public void setLimiter(Limiter limiter) {
        this.limiter = limiter;
    }

    public void setText(String text) {
        if(valuesToText == false) {
            this.text = text;
        }
    }

    public void enableValueText(boolean enable) {
        valuesToText = enable;
    }

    public void setBGColor(int color) {
        this.backgroundPaint.setColor(color);
    }

    public void preRender(float value) {
        this.value = value;

        if(limiter != null)
            limiter.setValue(value);

        if(valuesToText)
            text = String.valueOf(value);
    }


    //*******************************CLASS*********************************************

    private class ValueAnimator implements Runnable {

        private PercentView percentView;
        private Handler handler;

        //Fill Anim
        private int delayFill;
        private float current;
        private float target;
        private float relativStep;
        private boolean positiv;

        private TimeInterpolator interpolator;
        private int frame;
        private int FramesTotal;
        private float frameToOvershoot;

        public int getAnimationState() {
            return animationState;
        }

        private int animationState;
        public static final int ANIMATIONSTATE_NONE = 0;
        public static final int ANIMATIONSTATE_ANIMATE = 2;

        public ValueAnimator(PercentView percentView) {
            this.percentView = percentView;
            handler = new Handler();
            delayFill = 0;
            animationState = ANIMATIONSTATE_NONE;
            interpolator = new DecelerateInterpolator();
            frame = 0;
            FramesTotal = 60;
            frameToOvershoot = 1f / FramesTotal;
            relativStep = 0;
            positiv = true;
        }

        public void animate(float from, float to) {
            positiv = true;
            current = from;
            target = to;

            relativStep = target-current;

            if(relativStep < 0) {
                relativStep *= -1;
                positiv = false;
            }

            Log.d("calculated ste: ", String.valueOf(relativStep) + " diretion" + String.valueOf(positiv));
            animationState = ANIMATIONSTATE_ANIMATE;
            run();
        }

        @Override
        public void run() {
            switch (animationState) {
                case ANIMATIONSTATE_ANIMATE:
                    frame ++;
                    if (frame < FramesTotal) {
                        if(positiv) {
                            percentView.preRender(current + relativStep*interpolator.getInterpolation(frame * frameToOvershoot));
                        } else {
                            percentView.preRender(current - relativStep*interpolator.getInterpolation(frame * frameToOvershoot));
                        }
                        percentView.invalidate();
                        handler.postDelayed(this, delayFill);
                    } else {
                        this.animationState = ANIMATIONSTATE_NONE;
                        frame = 0;
                    }
                    break;
            }
        }
    }
}
