package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.exceptions.NoSuchFallException;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.views.GraphicView;

import java.io.IOException;
import com.example.esp1415.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FallDataActivity extends Activity
{
	//Views
	private GraphicView graphic;
	private TextView textLong,textLat,textName,textDate;
	private ImageView imageSess,imageFall;
	private boolean notified;

	private static final String TAG="FALL DATA ACTIVITY";
	public static final String SESSION_ID_TAG="sessionId";
	public static final String FALL_ID_TAG="fallId";

	private Context actContext;
	private String sessionId,fallId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayout();//Imposta layout ed eventi
		actContext=this;
		Bundle extras=getIntent().getExtras();
		sessionId=extras.getString(SESSION_ID_TAG);
		fallId=extras.getString(FALL_ID_TAG);
		boolean ok=false;
		FallData fall=null;
		try {
			fall=LocalStorage.getFallData(sessionId, fallId);
			ok=true;
		} catch (IllegalArgumentException e) {
			Toast.makeText(actContext,R.string.error_arguments,Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(actContext,R.string.error_file_writing,Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (NoSuchFallException e) {
			e.printStackTrace();
		}
		if(!ok){//c'è stato un errore
			this.finish();
			return;}
		textLong.setText(""+fall.getLongitude());
		textLat.setText(""+fall.getLatitude());
		textName.setText(fall.getSessionName());
		textDate.setText(fall.getDate());
		notified=fall.isNotified();
		if(notified)
			imageFall.setImageDrawable(this.getResources().getDrawable(R.drawable.image_notified_ok));
		try {
			Bitmap b=LocalStorage.getSessionImage(this,sessionId);
			if(b!=null)
				imageSess.setImageBitmap(b);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DataArray data=fall.getAccelDatas();
		graphic.setData(data);
	}

	private void setLayout()
	{
		setContentView(R.layout.activity_fall_data_layout);
		textLong=(TextView)findViewById(R.id.text_long);
		textLat=(TextView)findViewById(R.id.text_lat);
		textName=(TextView)findViewById(R.id.text_name);
		textDate=(TextView)findViewById(R.id.text_date);
		graphic=(GraphicView)findViewById(R.id.graphic);
		imageSess=(ImageView)findViewById(R.id.image_session);
		imageFall=(ImageView)findViewById(R.id.image_notified);
	}

	public void onStart()
	{
		super.onStart();
		Log.d(TAG,"ON START");
	}

	public void onResume()
	{
		super.onResume();
		Log.d(TAG,"ON RESUME");
		graphic.setScaleToOneSecond();
	}
}