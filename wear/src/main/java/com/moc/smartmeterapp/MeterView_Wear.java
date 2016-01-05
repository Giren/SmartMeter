package com.moc.smartmeterapp;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class MeterView_Wear extends View {
	private final int ANGLE_ORIENTATION = 90;
	private final int STICK_MARGIN = 17;
	private final int INNER_MARGIN = 0;
	private final int OUTTER_MARGIN = 0;
	private final int TEXT_INNER_MARGIN = 40;
	private final int TEXT_SIZE = 25;
	private final int TEXT_SIZE_SMALL = 25;
	private final int TEXT_SIZE_BIG = 25;
    private final int TEXT_SIZE_HUGE = 150;
	private final int LAST_TEXT_ANGLE = 350;
	private final int PIN_PADDING = 50;
	private final int LIMIT_PADDING = 0;
	//animator
	private PinAnimator animator;

	//temp stuff
	private Point tempPoint;
	private String tempText;
	private Rect textBounds;
	private int tempCountSticks;
	private int tempAvg;
	private float tempValue;
	private double tempAngle;
	private float mTextHeight;
	private RectF rectF;
	private int tempAngleA;
	private int tempAngleB;

	//paint stuff
	private Paint linePaint;
	private Paint backgroundPaint;
	private Paint limitPaint;
	private TextPaint textPaint;
	private TextPaint textPaintBig;
    private TextPaint textPaintHuge;
	private TextPaint avgPaint;

	//data stuff
	private float value;
	private int height;
	private int width;
	private int min;
	private int max;
	private int avg;
	private int ticks;
	private double step;
	private int angle;
	private int offsetAngle;
	private int radius;
	private int longTickEach;
	private String text;
	private boolean valuesToText;

	//event stuff
	private Limiter limiter;

	public MeterView_Wear(Context context) {
		super(context);
		init();
	}

	public MeterView_Wear(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MeterView_Wear(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		animator = new PinAnimator(this);

		textBounds = new Rect();

		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setStrokeWidth(5);
		linePaint.setColor(Color.BLACK);

		backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		backgroundPaint.setColor(Color.WHITE);

		limitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		limitPaint.setColor(Color.RED);

		textPaint = new TextPaint();
		textPaint.set(linePaint);
		textPaint.setTextSize(TEXT_SIZE);
		textPaint.setTextAlign(Align.CENTER);

		textPaintBig = new TextPaint();
		textPaintBig.setTextSize(TEXT_SIZE_BIG);

        textPaintHuge = new TextPaint();
        textPaintHuge.setTextSize(TEXT_SIZE_HUGE);

		avgPaint = new TextPaint();
		avgPaint.set(linePaint);
		avgPaint.setColor(Color.RED);
		avgPaint.setTextSize(TEXT_SIZE_SMALL);
		avgPaint.setTextAlign(Align.CENTER);

		rectF = new RectF();
		height = getHeight();
		width = getWidth();
		min = 0;
		max = 270;
		avg = 80;
		angle = 270;
		offsetAngle = 0;
		longTickEach = 10;
		ticks = 90;
		value = 0;
		avg = 0;
		text = "";

		valuesToText = true;
	}

	private Point calcLine(int x, int y, int radius, float startangle, float sweepangle) {
		radius-=INNER_MARGIN*2;
		double angle = ((startangle + sweepangle+ANGLE_ORIENTATION) * Math.PI / 180);
		return new Point((int)(x + radius * Math.cos(angle)), (int)(y + radius * Math.sin(angle)));
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

		if(limiter != null) {
			for(Limit l : limiter.getLimits()){
				rectF.set(center_x-radius+LIMIT_PADDING, center_y-radius+LIMIT_PADDING, center_x+radius-LIMIT_PADDING, center_y+radius-LIMIT_PADDING);
				limitPaint.setColor(l.getColor());
				tempAngleA = l.getMin() * angle/max + offsetAngle+ANGLE_ORIENTATION;
				tempAngleB = l.getMax() * angle/max + offsetAngle+ANGLE_ORIENTATION;
				tempAngleB -= tempAngleA;
				canvas.drawArc(rectF, tempAngleA , tempAngleB, true, limitPaint);
			}
		}

		step = angle / ticks;
		tempCountSticks = 0;

		for(float i=0, j=0; i<=angle; i+=step, j++) {

			if(j >= longTickEach-1 || i==0) {
				j=0;
				tempPoint = calcLine(center_x, center_y, radius, i, offsetAngle);
				tempCountSticks++;
			}
			else{
				tempPoint = calcLine(center_x, center_y, radius-STICK_MARGIN, i, offsetAngle);
			}

			//mPaint.setColor(ColorUtils.HSLToColor(new float[]{(float) i, (float) 2.0, (float) 1.5}));
			canvas.drawLine(center_x, center_y, tempPoint.x, tempPoint.y, linePaint);
		}

		if(avg != 0) {
			tempAvg = avg * angle/max;
			tempPoint = calcLine(center_x, center_y, radius, tempAvg, offsetAngle);
			canvas.drawLine(center_x, center_y, tempPoint.x, tempPoint.y, avgPaint);

			tempText = String.valueOf("AVG");
			textPaint.getTextBounds(tempText, 0, tempText.length(), textBounds);
			mTextHeight = textBounds.height();
			tempPoint = calcLine(center_x, center_y, radius+INNER_MARGIN, tempAvg, offsetAngle);
			canvas.drawText(tempText, tempPoint.x, tempPoint.y + (mTextHeight / 2f), avgPaint);
		}

		canvas.drawCircle(center_x, center_y, radius-INNER_MARGIN*2-STICK_MARGIN*2, backgroundPaint);

		if(angle >= LAST_TEXT_ANGLE) {
			tempAngle = angle - step;
		} else {
			tempAngle = angle;
		}

		for(int i=0, k=0, j=0; i<=tempAngle; i+=step, j++) {
			if(j >= longTickEach-1 || i==0) {
				j=0;
				tempText = String.valueOf(k*max/(tempCountSticks-1));
				textPaint.getTextBounds(tempText, 0, tempText.length(), textBounds);
				mTextHeight = textBounds.height(); // Use height from getTextBounds()

				tempPoint = calcLine(center_x, center_y, radius-INNER_MARGIN-TEXT_INNER_MARGIN-TEXT_SIZE, i, offsetAngle);
				// Later when you draw...
				canvas.drawText(tempText, tempPoint.x, tempPoint.y + (mTextHeight / 2f), textPaint);
				k++;
			}
		}

		if( value >= min && value <= max && max != 0) {
			tempValue = value * angle/max;
			tempPoint = calcLine(center_x, center_y, radius-PIN_PADDING, tempValue, offsetAngle);
			canvas.drawLine(center_x, center_y, tempPoint.x, tempPoint.y, linePaint);
		}

		//canvas.drawCircle(center_x, center_y, radius/4, backgroundPaint);

		if(text != null) {
            float mTextWidth, mTextHeight;
            Rect textBounds = new Rect();
            mTextWidth = textPaintBig.measureText(text);
            mTextHeight = textBounds.height();
            textPaintBig.getTextBounds(text, 0, text.length(), textBounds);

            canvas.drawText(text, // Text to display
                    center_x - (mTextWidth / 2f),
                    center_y + (mTextHeight / 2f) + 2 * radius / 3,
                    textPaintBig
            );
		}

		tempText = null;
		tempPoint = null;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		if(limiter != null) {
			for(Limit l : limiter.getLimits()) {
				if(l.getMax() > max) {
					max = l.getMax();
				}
			}
		}

		if ( max > 0){
			this.max = max;
		}
	}

	public void setAverage(int avg) {
		this.avg = avg;
	}

	public void setTicks(int ticks, int longTickEach) {
		setTicks(ticks);
		setLongTickEach(longTickEach);
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public void setLongTickEach(int longTickEach) {
		this.longTickEach = longTickEach;
	}

	public void setOffsetAngle(int offsetAngle) {
		this.offsetAngle = offsetAngle;
	}

	public void setAngle(int angle){
		this.angle = angle;
	}

	public void setValue(float value) {
		//this.value = value;
		if(animator.getAnimationState() == PinAnimator.ANIMATIONSTATE_NONE) {
			animator.animate(this.value, value);
		}
	}

	public void setLimiter(Limiter limiter) {
		if(limiter != null) {
			for(Limit l : limiter.getLimits()) {
				if(l.getMax() > max) {
					max = l.getMax();
				}
			}
		}
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

	public void setTickColor(int color) {
		this.linePaint.setColor(color);
	}

	public void setAVGColor(int color) {
		this.avgPaint.setColor(color);
	}

	public void setTextColor(int color) {
		this.textPaint.setColor(color);
		this.textPaintBig.setColor(color);
	}

	public void preRender(float value) {
		this.value = value;

		if(limiter != null)
			limiter.setValue(value);

		if(valuesToText)
			text = String.valueOf(value);
	}


	//*******************************CLASS*********************************************

	private class PinAnimator implements Runnable {

		private MeterView_Wear meterView;
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

		public PinAnimator(MeterView_Wear meterView) {
			this.meterView = meterView;
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
							meterView.preRender(current + relativStep*interpolator.getInterpolation(frame * frameToOvershoot));
						} else {
							meterView.preRender(current - relativStep*interpolator.getInterpolation(frame * frameToOvershoot));
						}
						meterView.invalidate();
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
