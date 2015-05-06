package it.dei.unipd.esp1415;

import it.dei.unipd.esp1415.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.appcompat.R;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MyListFragment extends ListFragment {
	private List<Session> items;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // initialize the items list
        items = new ArrayList<Session>();
        Resources resources = getResources();
        
        items.add(new Session("Prova1",Utils.getDateHour(),"10:15",15, resources.getDrawable(R.drawable.abc_btn_radio_material)));
        items.add(new Session("Prova2",Utils.getDateHour(),"10:15",15, resources.getDrawable(R.drawable.abc_btn_radio_material)));
        /*items.add(new ListViewItem(resources.getDrawable(R.drawable.bebo), getString(R.string.bebo), getString(R.string.bebo_description)));
        :
        mItems.add(new ListViewItem(resources.getDrawable(R.drawable.youtube), getString(R.string.youtube), getString(R.string.youtube_description)));
        */
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
        // retrieve theListView item
        Session item = items.get(position);
        
        // do something
        Toast.makeText(getActivity(), item.getName(), Toast.LENGTH_SHORT).show();
    }
	/*@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Session[] values = 
			{new Session("Prova","22/04/15","10:15",15, R.drawable.abc_ab_share_pack_holo_dark),
				new Session("Prova","22/04/15","10:15",15, R.drawable.abc_ab_share_pack_holo_dark)};
		ArrayAdapter<Session> adapter = new ArrayAdapter<Session>(getActivity(),
				R.layout.single_session_layout, values);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Object clickedObj = l.getAdapter().getItem(position);
		Log.i("onItemClick", clickedObj.toString());
	}*/
}