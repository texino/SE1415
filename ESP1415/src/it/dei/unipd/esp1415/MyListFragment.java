package it.dei.unipd.esp1415;

import com.example.esp1415.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyListFragment extends ListFragment {
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*Session[] values = 
			{new Session("Prova","22/04/15","10:15",15, R.drawable.abc_ab_share_pack_holo_dark),
				new Session("Prova","22/04/15","10:15",15, R.drawable.abc_ab_share_pack_holo_dark)};
		ArrayAdapter<Session> adapter = new ArrayAdapter<Session>(getActivity(),
				R.layout.single_session_layout, values);
		setListAdapter(adapter);*/
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Object clickedObj = l.getAdapter().getItem(position);
		Log.i("onItemClick", clickedObj.toString());
	}
}