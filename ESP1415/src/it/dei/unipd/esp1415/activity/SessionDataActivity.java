package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.adapters.FallAdapter;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.esp1415.R;

public class SessionDataActivity extends Activity {

	public static final String ID_TAG="sessionId";
	public static final String NAME_TAG = "name";
	public static final String DURATION_TAG = "duration";
	public static final String DATE_TAG = "date";

	private String id;
	private String nameSession;
	private final Context con =this;
	private TextView date,nameS,durata;
	private ListView lista;
	private ImageView sessionImm;
	AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_data_layout);
		date=(TextView)findViewById(R.id.text_date);
		nameS=(TextView)findViewById(R.id.text_name);
		durata=(TextView)findViewById(R.id.durata);
		lista=(ListView)findViewById(R.id.fall_list);
		sessionImm=(ImageView)findViewById(R.id.image);

		Button cancellaSessione=(Button)findViewById(R.id.button_delete);
		Button rinominaSessione=(Button)findViewById(R.id.button_rename);
		//Prendi dagli extra la sessionId
		id=getIntent().getExtras().getString(ID_TAG);

		try {
			Bitmap btm=null;
			btm = LocalStorage.getSessionImage(this,id);
			if(btm!=null)
				sessionImm.setImageBitmap(btm);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		cancellaSessione.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelClicked();}
		});

		rinominaSessione.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				renameClicked();
			}});
	}

	private void cancelClicked()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.dialog_delete_session_layout,null);
		builder.setView(vg);
		((Button)vg.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{LocalStorage.deleteSession(id);
				Toast.makeText(getApplicationContext(),getResources().getString(R.string.texts_deleted_session), Toast.LENGTH_SHORT).show();
				}catch(NoSuchSessionException e){
					Toast.makeText(getApplicationContext(),R.string.error_inexistent_session, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				alertDialog.dismiss();
				alertDialog=null;
				finish();
			}});
		((Button)vg.findViewById(R.id.button_ko)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				alertDialog=null;
			}});
		alertDialog=builder.create();
		alertDialog.setCancelable(true);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
	}

	private void renameClicked()
	{
		final	Dialog dialog=new Dialog(con);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.dialog_rename_session_layout);
		final EditText cambia=(EditText)dialog.findViewById(R.id.edit_name);
		cambia.setText(nameS.getText().toString());
		final	Button buttonConferma = (Button) dialog.findViewById(R.id.button_ok);
		buttonConferma.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String cambiaNomeSessione=cambia.getText().toString();
				try {
					LocalStorage.renameSession(id, cambiaNomeSessione);
					nameS.setText(cambiaNomeSessione);
				} catch (IllegalArgumentException e) {
					Toast.makeText(getApplicationContext(),R.string.error_arguments, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (NoSuchSessionException e) {
					Toast.makeText(getApplicationContext(),R.string.error_inexistent_session, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (java.io.IOException e) {
					Toast.makeText(getApplicationContext(),R.string.error_file_writing, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				finally{
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.texts_changed_name)+" : "+cambiaNomeSessione, Toast.LENGTH_SHORT).show();}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void onResume(){
		super.onResume();
		try {
			SessionData data=LocalStorage.getSessionData(id);
			nameSession =data.getName();
			int duration=data.getDuration();
			String dataS=data.getDate();
			nameS.setText(nameSession);
			durata.setText(Utils.convertDuration(duration));
			date.setText(dataS);
			Log.d("ACTIVITY SECOND",""+data.getFalls());
			ArrayList<FallInfo> falls=data.getFalls();
			ArrayList<FallInfo> orderedFalls=new ArrayList<FallInfo>();
			int s=falls.size();
			if(s!=0)
			{
				for(int i=s-1;i>=0;i--)
					orderedFalls.add(falls.get(i));
				FallAdapter adapter = new FallAdapter(this,orderedFalls,id);
				lista.setAdapter(adapter);
			}
			else{
				Drawable d=getResources().getDrawable(R.drawable.image_empty_list);
				d.setColorFilter(0x77ffffff,PorterDuff.Mode.SRC_OVER);
				((ImageView)findViewById(R.id.image_bkg)).setImageDrawable(d);
			}
		} catch (IllegalArgumentException e) {
			Toast.makeText(getApplicationContext(),R.string.error_arguments, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			finish();
		} catch (NoSuchSessionException e) {
			Toast.makeText(getApplicationContext(),R.string.error_inexistent_session, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			finish();
		} catch (java.io.IOException e) {
			Toast.makeText(getApplicationContext(),R.string.error_file_writing, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			finish();
		}
	}
}