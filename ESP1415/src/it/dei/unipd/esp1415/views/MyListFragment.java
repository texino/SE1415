package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.activity.CurrentSessionActivity;
import it.dei.unipd.esp1415.activity.SessionDataActivity;
import it.dei.unipd.esp1415.adapters.CustomArrayAdapter;
import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.Utils;
import it.dei.unipd.esp1415.views.RenameDeleteDialog;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.esp1415.R;

public class MyListFragment extends ListFragment {
	
	private List<SessionInfo> items;
	private String TAG = "OnClick"; //TOCLEAN
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get the list of session saved in the storage
		try {
			items = LocalStorage.getSessionInfos();
		} catch (IOException e) {
			Log.i("ERROR", "Error getting session list - LocalStorage");
		}
		//create a session list to test fragment
		int i;
		/*for(i=1; i<100; i++){
        	try{
        		items.add(new SessionInfo(""+i,"Prova"+i,Utils.getDateHour(),i,i, true));
        	}
        	catch(Exception e){}
		}*/
		// initialize and set the list adapter
		setListAdapter(new CustomArrayAdapter(getActivity(), items));
	}
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//setting up onClick listeners
		
		//get the ListView witch handle the click and long press Android events
		ListView lv = getListView();
		
		//setting up single click
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		        onListItemClick(v,pos,id);
		    }
		});
		
		//setting up long click
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
		    @Override
		    public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
		    	return onLongListItemClick(v,pos,id);
		    }
		});
	}
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }
 
    //
    protected void onListItemClick(View v, int pos, long id) {
        Log.i(TAG, "onListItemClick id=" + id);
        SessionInfo info=items.get(pos);
        Intent i;
        if(info.getStatus())
        {
        	i=new Intent(this.getActivity(),CurrentSessionActivity.class);
        	i.putExtra(CurrentSessionActivity.EMPTY_TAG, false);
        	i.putExtra(CurrentSessionActivity.ID_TAG,info.getId());
        }
        else
        {
        	i=new Intent(this.getActivity(),SessionDataActivity.class);
        	i.putExtra(SessionDataActivity.ID_TAG,info.getId());
        }
        startActivity(i);
        //TODO intent that launch activity 2 and add extra info (n' session pressed)
        //if the session is running then display activity 3
    }
    
    protected boolean onLongListItemClick(View v, int pos, long id) {
        Log.i(TAG, "onLongListItemClick id=" + id);
        //FIXME non trova il riferimento alla textview (null pointer exception)
        //EditText text = (EditText) getView().findViewById(R.id.renamesession);
    	SessionInfo item = items.get(pos);
    	//text.setText(item.getName());
    	//create the dialog
    	RenameDeleteDialog dialog = new RenameDeleteDialog(item.getId());
        //set session clicked name to the dialog title
    	dialog.setTitle("Session: " + item.getName());
    	//show the dialog
    	dialog.show(getFragmentManager(),"dialog");
        //returning true means that Android stops event propagation
    	return true;
    }
    //TOCLEAN
    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        
		if (!clicked) {
			ImageView rename = (ImageView) v.findViewById(R.id.rename);
			rename.setVisibility(View.VISIBLE);
			ImageView delete = (ImageView) v.findViewById(R.id.delete);
			delete.setVisibility(View.VISIBLE);
			clicked = true;
		}
		else {
			ImageView rename = (ImageView) v.findViewById(R.id.rename);
			rename.setVisibility(View.INVISIBLE);
			ImageView delete = (ImageView) v.findViewById(R.id.delete);
			delete.setVisibility(View.INVISIBLE);
			clicked = false;
		}
    	// retrieve theListView item
        SessionInfo item = items.get(position);
        
        // do something
        Toast.makeText(getActivity(), item.getName(), Toast.LENGTH_SHORT).show();
   }*/
}