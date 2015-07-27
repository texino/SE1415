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
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class FallDataActivity extends Activity
{
	//Views
	private GraphicView graphic;
	private TextView textLong,textLat,textName,textDate;
	private boolean notified;

	private static final String TAG="FALL DATA ACTIVITY";
	public static final String SESSION_ID_TAG="sessionId";
	public static final String FALL_ID_TAG="fallId";

	private Context actContext;
	private String sessionId,fallId;

	public void onSaveInstanceState(Bundle state)
	{
		state.putString("sessionName", textName.getText().toString());
		state.putString("fallDate", textDate.getText().toString());
		state.putBoolean("notified", notified);
		graphic.saveStatusOnBundle(state);
		super.onSaveInstanceState(state);
	}
	
	public void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		textName.setText(state.getString("sessionName"));
		textDate.setText(state.getString("fallDate"));
		notified=state.getBoolean("notified");
		graphic.restoreStatusFromBundle(state);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"ON CREATE");
		setLayout();//Imposta layout ed eventi
		actContext=this;		
		if(savedInstanceState==null)//l'activity è creata da zero
		{
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
			DataArray data=fall.getAccelDatas();
			graphic.setData(data);
		}
		else//abbiamo già dei dati
			onRestoreInstanceState(savedInstanceState);
	}

	private void setLayout()
	{
		setContentView(R.layout.activity_fall_data_layout);
		textLong=(TextView)findViewById(R.id.text_long);
		textLat=(TextView)findViewById(R.id.text_lat);
		textName=(TextView)findViewById(R.id.text_name);
		textDate=(TextView)findViewById(R.id.text_date);
		graphic=(GraphicView)findViewById(R.id.graphic);
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