package it.dei.unipd.esp1415;

import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.Utils;
import it.dei.unipd.esp1415.views.RenameDeleteDialog;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.esp1415.R;

public class MyListFragment extends ListFragment {
	private List<SessionInfo> items;
	String TAG = "OnClick";
	//private boolean clicked = false; TOCLEAN
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // initialize the items list
        items = new ArrayList<SessionInfo>();
        //Resources resources = getResources();
        
        int i;
        for(i=1; i<100; i++)
        	items.add(new SessionInfo(""+i,"Prova"+i,Utils.getDateHour(),i,i, true));
        
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
		/*OnItemLongClickListener listener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				SessionInfo item = items.get(position);
				Toast.makeText(getActivity().getBaseContext(),
						"Long Clicked " + item.getName(), Toast.LENGTH_SHORT)
						.show();
				DeleteDialog deleteDialog = new DeleteDialog();
		    	deleteDialog.show(getFragmentManager(),"delete");
				return false;
			}
		};

		getListView().setOnItemLongClickListener(listener);*/

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
    }
    
    protected boolean onLongListItemClick(View v, int pos, long id) {
        Log.i(TAG, "onLongListItemClick id=" + id);
        EditText text = (EditText) getView().findViewById(R.id.renamesession);
    	SessionInfo item = items.get(pos);
    	text.setText(item.getName());
    	RenameDeleteDialog dialog = new RenameDeleteDialog();
        dialog.show(getFragmentManager(),"dialog");
        //returning true means that Android stops event propagation
    	return true;
    }
    
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