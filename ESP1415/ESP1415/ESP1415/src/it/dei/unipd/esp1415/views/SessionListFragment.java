package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.activity.CurrentSessionActivity;
import it.dei.unipd.esp1415.activity.SessionDataActivity;
import it.dei.unipd.esp1415.adapters.SessionAdapter;
import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.LocalStorage;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * SessionListFragment class: Displays the list of sessions inside a fragment
 */
public class SessionListFragment extends ListFragment {

	private SessionAdapter adapter;
	private List<SessionInfo> items;
	public static RenameDeleteDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get the list of session saved in the storage
		try {
			items = LocalStorage.getSessionInfos();
		} catch (IOException e) {
			Log.i("ERROR", "Error getting session list - LocalStorage");
		}
		// initialize and set the list adapter
		adapter = new SessionAdapter(getActivity(), items);
		setListAdapter(adapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// setting up onClick listeners

		// get the ListView witch handle the click and long press Android events
		ListView lv = getListView();

		// setting up single click
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				onListItemClick(v, pos, id);
			}
		});

		// setting up long click
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
					long id) {
				return onLongListItemClick(v, pos, id);
			}
		});
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// remove the dividers from the ListView of the ListFragment
		getListView().setDivider(null);
	}

	// onListItemClick implemetation
	protected void onListItemClick(View v, int pos, long id) {
		SessionInfo info = items.get(pos);
		Intent i;
		// check if session pressed is running
		if (info.getStatus()) {
			i = new Intent(this.getActivity(), CurrentSessionActivity.class);
			i.putExtra(CurrentSessionActivity.EMPTY_TAG, false);
			i.putExtra(CurrentSessionActivity.ID_TAG, info.getId());
		} else {
			i = new Intent(this.getActivity(), SessionDataActivity.class);
			i.putExtra(SessionDataActivity.ID_TAG, info.getId());
		}
		startActivity(i);
	}

	protected boolean onLongListItemClick(View v, int pos, long id) {
		SessionInfo item = items.get(pos);
		// create the dialog
		dialog = new RenameDeleteDialog(item.getId(), item.getName(),
				item.getName());
		// show the dialog
		dialog.show(getFragmentManager(), "dialog");
		// returning true means that Android stops event propagation
		return true;
	}

	// method that update the fragment during onResume state
	@Override
	public void onResume() {
		super.onResume();
		Log.i("RESUME-FRAGMENT", "onResume performed");
		// get the list of session saved in the storage
		try {
			items = LocalStorage.getSessionInfos();
		} catch (IOException e) {
			Log.i("ERROR", "Error getting session list - LocalStorage");
		}
		// initialize and set the list adapter
		setListAdapter(new SessionAdapter(getActivity(), items));
	}

	// method that update fragment while it is invisible
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			Log.i("HIDDEN-FRAGMENT", "HIDDEN!! performed");
			// get the list of session saved in the storage
			try {
				items = LocalStorage.getSessionInfos();
			} catch (IOException e) {
				Log.i("ERROR", "Error getting session list - LocalStorage");
			}
			// initialize and set the list adapter
			setListAdapter(new SessionAdapter(getActivity(), items));
		}
	}
}