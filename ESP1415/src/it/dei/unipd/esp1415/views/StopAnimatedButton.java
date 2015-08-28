package it.dei.unipd.esp1415.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;

public class StopAnimatedButton extends ImageButton{

	private static final long PLAY_PAUSE_ANIMATION_DURATION = 200;

	private StopButtonDrawable mDrawable;
	private ButtonAnimation a;

	public StopAnimatedButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDrawable = new StopButtonDrawable(context);
	}

	/**
	 * Se true allora l'immagine dovrebbe essere quella di pausa
	 * @param play
	 */
	public void toggle(boolean play) {
		if (a != null && a.hasStarted()) {
			a.cancel();
		}
		a=new ButtonAnimation(play);
		this.setAnimation(a);
		this.startAnimation(a);
	}
	
	@Override 
	public void setImageDrawable(Drawable d)
	{
		mDrawable=(StopButtonDrawable)d;
		StateListDrawable s=new StateListDrawable();
		s.addState(new int[]{},mDrawable);
		super.setImageDrawable(s);
	}

	private class ButtonAnimation extends Animation
	{
		private boolean clicked;

		public ButtonAnimation(boolean isClicked)
		{
			this.clicked=isClicked;
			setDuration(PLAY_PAUSE_ANIMATION_DURATION);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,Transformation t) {
			if(clicked)
				mDrawable.setProgress(1-interpolatedTime);
			else
				mDrawable.setProgress(interpolatedTime);
		}
	}
}