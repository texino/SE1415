package it.dei.unipd.esp1415;

import it.dei.unipd.esp1415.views.FloatingActionButton;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.esp1415.R;

/**
 * SessionList class
 * First activity to be display that show and manage a list of sessions
 */
public class SessionList extends ActionBarActivity {

	protected static FloatingActionButton fabButton;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        //set the activity layout
		setContentView(R.layout.session_list);
        //set FAB button
		fabButton = new FloatingActionButton.Builder(this)
		.withDrawable(getResources().getDrawable(R.drawable.ic_plus_grey600_36dp))
		.withButtonColor(Color.WHITE).withGravity(Gravity.BOTTOM | Gravity.RIGHT)
		.withMargins(0, 0, 16, 16).create();
		//set the onClick Listener to FAB button
		fabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!fabButton.isHidden()){
					Log.i("FAB", "FAB button pressed");
					//TODO new intent to third activity
				}
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sessionlist_action_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Log.i("SETTINGS", "Setting action button pressed");
        	//TODO intent to preference activity
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
  
    //FIXME metodo che viene eseguito al click del tasto elimina
    public void deleteSession(View v){
    	Log.i("OnClick", "delete button pressed from dialog");
    	/*DeleteDialog deleteDialog = new DeleteDialog();
    	deleteDialog.show(getSupportFragmentManager(),"delete");*/
    }
}
