package it.dei.unipd.esp1415.activity;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.LocalStorage;

import com.example.esp1415.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectedActivity extends Activity{

	private List<SessionInfo> list;
	private SessionInfo info;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button b=(Button)findViewById(R.id.button1);
		
		try {
			JSONObject json=new JSONObject("{}");
			System.out.println("ho convertito {}");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("non ho convertito {}");
		}
		try {
			JSONObject json=new JSONObject("[]");
			System.out.println("ho convertito []");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("non ho convertito []");
		}
		
		try {
			list=LocalStorage.getSessionInfos();
			for(int i=0;i<list.size();i++)
			{
				info=list.get(i);
				Log.d("ACTIVITY START","Session:\nSessionId:"+info.getId()+"\nName:"+info.getName()
						+"\nDate:"+info.getDate()+"\nRunning:"+info.getStatus()+"\nNumber:"+info.getNumberOfFalls());
			}
		}catch (IOException e) {
			Toast.makeText(this, "Error FILE", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		b.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if((info!=null)&&(info.getStatus()))
				{
					Intent i=new Intent(ConnectedActivity.this,CurrentSessionActivity.class);
					i.putExtra(CurrentSessionActivity.EMPTY_TAG,false);
					i.putExtra(CurrentSessionActivity.ID_TAG,info.getId());
					i.putExtra(CurrentSessionActivity.NAME_TAG,info.getName());
					i.putExtra(CurrentSessionActivity.DATE_TAG,info.getDate());
					i.putExtra(CurrentSessionActivity.DURATION_TAG,info.getDuration());
					startActivity(i);
					finish();
				}
				else
				{
					Intent i=new Intent(ConnectedActivity.this,CurrentSessionActivity.class);
					i.putExtra(CurrentSessionActivity.EMPTY_TAG,true);
					startActivity(i);
					finish();
				}
			}});
	}
}