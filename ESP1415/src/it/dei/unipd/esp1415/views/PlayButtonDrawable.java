package it.dei.unipd.esp1415.views;

import com.example.esp1415.R;

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

public class PlayButtonDrawable extends Drawable {

	private final Path leftPauseBar = new Path();
	private final Path rightPauseBar = new Path();
	private final Paint symbolPaint = new Paint(),circlePaint=new Paint();
	private final RectF bounds = new RectF();
	private float pauseBarWidth;
	private float pauseBarHeight;
	private float pauseBarDistance;

	private float width;
	private float height;

	private float progress;
	private boolean play;
	private Context context;
	
	/**
	 * Crea l'immagine del pulsante nello stato specificato
	 * @param context
	 * @param play True se il simbolo del pulsante è play
	 */
	public PlayButtonDrawable(Context context,boolean play) {
		init(context,play);
	}

	private void init(Context context,boolean play)
	{
		this.context=context;
		symbolPaint.setAntiAlias(true);
		symbolPaint.setStyle(Paint.Style.FILL);
		symbolPaint.setColor(Color.WHITE);
		circlePaint.setAntiAlias(true);
		circlePaint.setStyle(Paint.Style.FILL);
		pauseBarWidth = 10;
		pauseBarHeight = 30;
		pauseBarDistance = 10;

		this.play=play;
		if(play)
			progress=1;
		else
			progress=0;
	}

	/**
	 * Crea l'immagine del pulsante col simbolo di play
	 * @param context
	 */
	public PlayButtonDrawable(Context context) {
		init(context,true);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		this.bounds.set(bounds);
		width = this.bounds.width();
		height = this.bounds.height();
		pauseBarWidth = (width/5);
		pauseBarHeight = (width/5)*(2.5f);
		pauseBarDistance = pauseBarWidth;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(width/2,height/2,width/2,circlePaint);
		leftPauseBar.rewind();
		rightPauseBar.rewind();

		//Distanza attuale fra le due barre
		final float barDist = lerp(pauseBarDistance,-1,progress);
		//Larghezza attuale delle due barre
		final float barWidth = lerp(pauseBarWidth,pauseBarHeight/2f,progress);

		final float firstBarTopLeft = lerp(0,barWidth,progress);
		final float secondBarTopRight = lerp(2*barWidth+barDist,barWidth+barDist,progress);

		leftPauseBar.moveTo(0, 0);
		leftPauseBar.lineTo(firstBarTopLeft,-pauseBarHeight);
		leftPauseBar.lineTo(barWidth,-pauseBarHeight);
		leftPauseBar.lineTo(barWidth,0);
		leftPauseBar.close();

		rightPauseBar.moveTo(barWidth + barDist, 0);
		rightPauseBar.lineTo(barWidth + barDist,-pauseBarHeight);
		rightPauseBar.lineTo(secondBarTopRight,-pauseBarHeight);
		rightPauseBar.lineTo(2 * barWidth + barDist, 0);
		rightPauseBar.close();

		canvas.save();

		canvas.translate(lerp(0,pauseBarHeight/8f,progress), 0);

		final float rotationProgress = play ? 1 - progress : progress;
		final float startingRotation = play ? 90 : 0;
		canvas.rotate(lerp(startingRotation, startingRotation + 90, rotationProgress),width/2f,height/2f);

		canvas.translate(width/2f-((2*barWidth+barDist)/2f),height/2f+(pauseBarHeight/2f));

		canvas.drawPath(leftPauseBar,symbolPaint);
		canvas.drawPath(rightPauseBar,symbolPaint);

		canvas.restore();
	}

	@Override
	public void setAlpha(int alpha) {
		symbolPaint.setAlpha(alpha);
		circlePaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		symbolPaint.setColorFilter(cf);
		circlePaint.setColorFilter(cf);
		invalidateSelf();
	}

	@Override
	public boolean onStateChange(int[] states)
	{
		if(play)
		{
			circlePaint.setColor(context.getResources().getColor(R.color.play_normal_color));
			int length=states.length;
			for(int i=0;i<length;i++)
				if(states[i]==android.R.attr.state_pressed)
					circlePaint.setColor(context.getResources().getColor(R.color.play_pressed_color));
		}
		else
		{
			circlePaint.setColor(context.getResources().getColor(R.color.pause_normal_color));
			int length=states.length;
			for(int i=0;i<length;i++)
				if(states[i]==android.R.attr.state_pressed)
					circlePaint.setColor(context.getResources().getColor(R.color.pause_pressed_color));
		}
		invalidateSelf();
		return true;
	}

	/**
	 * Imposta il tipo di simbolo
	 * @param play True se si vuole che il simbolo sia play
	 */
	public void setPlay(boolean play)
	{
		this.play=play;
		this.progress=1;
	}

	/**
	 * @param progress 1 se il simbolo deve essere Play 
	 * 0 se deve essere Pause
	 */
	public void setProgress(float progress)
	{
		this.progress=progress;
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