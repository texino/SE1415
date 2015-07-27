package it.dei.unipd.esp1415.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;

public class PlayAnimatedButton extends ImageButton{

	private static final long PLAY_PAUSE_ANIMATION_DURATION = 200;

	private PlayButtonDrawable mDrawable;
	private ButtonAnimation a;

	public PlayAnimatedButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDrawable = new PlayButtonDrawable(context);
		mDrawable.setProgress(1);
	}

	/**
	 * Avvisa che il pulsante è premuto
	 * @param play True se deve essere imposto il simbolo di play
	 */
	public void toggle(boolean play) {
		if(mDrawable!=null)
			mDrawable.setPlay(play);
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
		mDrawable=(PlayButtonDrawable)d;
		StateListDrawable s=new StateListDrawable();
		s.addState(new int[]{},mDrawable);
		super.setImageDrawable(s);
	}

	private class ButtonAnimation extends Animation
	{
		private boolean play;

		/**
		 * Inizializza l'animazione
		 * @param play True se si deve trasformare nel simbolo di play
		 */
		public ButtonAnimation(boolean play)
		{
			this.play=play;
			setDuration(PLAY_PAUSE_ANIMATION_DURATION);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,Transformation t) {
			if(play)
				mDrawable.setProgress(interpolatedTime);
			else
				mDrawable.setProgress(1-interpolatedTime);
		}
	}
}