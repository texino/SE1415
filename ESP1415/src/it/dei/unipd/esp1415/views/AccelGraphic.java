package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.objects.AccelPoint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Classe per il grafico dei dati dell'accelerometro in un secondo
 */
public class AccelGraphic extends View {

	private Context context;

	public AccelGraphic(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public AccelGraphic(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AccelGraphic(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		//TODO Inizializzazione della view
		this.context=context;
	}

	public void setData(AccelPoint[] points)
	{
		//TODO disegna il grafico basandosi sui dati dell'array passato
	}
}