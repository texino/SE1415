package it.dei.unipd.esp1415;

import it.dei.unipd.esp1415.exceptions.IOException;
import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.LocalStorage;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.esp1415.R;

public class MainActivity extends ActionBarActivity {

	private Context context;
	private static List<SessionInfo> sessionList;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		context = getApplicationContext();
		// get the list of session saved in the storage
		try{
			sessionList = LocalStorage.getSessionInfos(context);
		}
		catch(IOException e){}
		catch(IllegalArgumentException e){}
		
		/*ActionBar actionBar = getSupportActionBar();
        actionBar.show();*/
        //TOCLEAN
        /*Button next = (Button) findViewById(R.id.Button01);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Context context = getApplicationContext();
				Intent i = new Intent(context, FirstService.class);
		        startActivity(i);
				
		        String TAG = "TAG MIO";
	        	String log = Utils.getDateHour();
	        	Log.d(TAG, log);
			}
		});*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*//metodo che viene eseguito al click del tasto elimina
    public void onDeleteClick(View v){
    	DeleteDialog deleteDialog = new DeleteDialog();
    	deleteDialog.show(getSupportFragmentManager(),"delete");
    }*/
    //TOCLEAN
}
