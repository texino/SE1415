package it.dei.unipd.esp1415;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.esp1415.R;

public class MainActivity extends ActionBarActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Let's start the service
        //startService(new Intent(this, FirstService.class));
        
        Button next = (Button) findViewById(R.id.Button01);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Context context = getApplicationContext();
				Intent i = new Intent(context, FirstService.class);
		        //i.putExtra("Prova", "Stringa di Prova");
		        context.startService(i);
			}
		});
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
        	String TAG = "TAG MIO";
        	String log = Session.getDateHour();
        	Log.d(TAG, log);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
