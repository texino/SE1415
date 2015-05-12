package it.dei.unipd.esp1415;

import it.dei.unipd.esp1415.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.esp1415.R;

public class MyListFragment extends ListFragment {
	private List<Session> items;
	private boolean clicked = false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // initialize the items list
        items = new ArrayList<Session>();
        Resources resources = getResources();
        
        int i;
        for(i=1; i<100; i++)
        	items.add(new Session("Prova"+i,Utils.getDateHour(),"1"+i+":1"+i,i, resources.getDrawable(R.drawable.ic_launcher)));
        
        // initialize and set the list adapter
        setListAdapter(new CustomArrayAdapter(getActivity(), items));
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }
 
    @Override
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
    	/*// retrieve theListView item
        Session item = items.get(position);
        
        // do something
        Toast.makeText(getActivity(), item.getName(), Toast.LENGTH_SHORT).show();*/
    }
}