package it.dei.unipd.esp1415.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.example.esp1415.R;

public class StopButtonDrawable extends Drawable {

	private final Path mLeftPauseBar = new Path();
	private final Path mRightPauseBar = new Path();
	private final Paint mPaint = new Paint(),circlePaint=new Paint();
	private final RectF mBounds = new RectF();
	private float pauseBarWidth;
	private float pauseBarHeight;
	private float pauseBarDistance;
	private float pBHM,pBHm;

	private float width;
	private float height;

	private float mProgress;
	private boolean mIsPlay;
	private Context context;

	public StopButtonDrawable(Context context) {
		this.context=context;
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.WHITE);
		circlePaint.setAntiAlias(true);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(context.getResources().getColor(R.color.main_color));
		pauseBarWidth = 15;
		pauseBarHeight = 30;
		pauseBarDistance = 0;
		mIsPlay=true;
		mProgress=1;
	}
	
	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mBounds.set(bounds);
		width = mBounds.width();
		height = mBounds.height();
		pauseBarWidth = (width/5);
		pauseBarHeight = (width/5)*(2.5f);
		pBHM=(width/5)*(2.5f);
		pBHm=pBHM/2;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(width/2, height/2, width/2,circlePaint);
        mLeftPauseBar.rewind();
        mRightPauseBar.rewind();
        pauseBarHeight=lerp(pBHM,pBHm,1-mProgress);

        // The current distance between the two pause bars.
        //final float barDist = lerp(pauseBarDistance, 0, mProgress);
        float barDist=0;
        // The current width of each pause bar.
        //final float barWidth = lerp(pauseBarWidth, pauseBarHeight / 2f, mProgress);
        float barWidth=pauseBarHeight/2f;
        // The current position of the left pause bar's top left coordinate.
        final float firstBarTopLeft = lerp(0,barWidth,0);
        // The current position of the right pause bar's top right coordinate.
        final float secondBarTopRight = lerp(2*barWidth+barDist,barWidth + barDist,0);

        // Draw the left pause bar. The left pause bar transforms into the
        // top half of the play button triangle by animating the position of the
        // rectangle's top left coordinate and expanding its bottom width.
        mLeftPauseBar.moveTo(0, 0);
        mLeftPauseBar.lineTo(firstBarTopLeft, -pauseBarHeight);
        mLeftPauseBar.lineTo(barWidth, -pauseBarHeight);
        mLeftPauseBar.lineTo(barWidth, 0);
        mLeftPauseBar.close();

        // Draw the right pause bar. The right pause bar transforms into the
        // bottom half of the play button triangle by animating the position of the
        // rectangle's top right coordinate and expanding its bottom width.
        mRightPauseBar.moveTo(barWidth + barDist, 0);
        mRightPauseBar.lineTo(barWidth + barDist, -pauseBarHeight);
        mRightPauseBar.lineTo(secondBarTopRight, -pauseBarHeight);
        mRightPauseBar.lineTo(2 * barWidth + barDist, 0);
        mRightPauseBar.close();

        canvas.save();

        // Translate the play button a tiny bit to the right so it looks more centered.

        // (1) Pause --> Play: rotate 0 to 90 degrees clockwise.
        // (2) Play --> Pause: rotate 90 to 180 degrees clockwise.
        final float rotationProgress = mIsPlay ? 1 - mProgress : mProgress;
        final float startingRotation = mIsPlay ? 90 : 0;
        canvas.rotate(lerp(startingRotation, startingRotation + 90, rotationProgress), width / 2f, height / 2f);

        // Position the pause/play button in the center of the drawable's bounds.
        canvas.translate(width / 2f - ((2 * barWidth + barDist) / 2f), height / 2f + (pauseBarHeight / 2f));

        // Draw the two bars that form the animated pause/play button.
        canvas.drawPath(mLeftPauseBar, mPaint);
        canvas.drawPath(mRightPauseBar, mPaint);

        canvas.restore();
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
		circlePaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
		circlePaint.setColorFilter(cf);
		invalidateSelf();
	}

	@Override
	public boolean onStateChange(int[] states)
	{
		circlePaint.setColor(0xffff0000);
		int length=states.length;
		for(int i=0;i<length;i++)
			if(states[i]==android.R.attr.state_pressed)
				circlePaint.setColor(0xff990000);
		invalidateSelf();
		return true;
	}

	public boolean isPlay()
	{
		return mIsPlay;
	}
	
	public void setProgress(float progress)
	{
		mProgress=progress;
		invalidateSelf();
	}
	
	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	/**
	 * Linear interpolate between a and b with parameter t.
	 */
	private static float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}
}