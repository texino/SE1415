package it.dei.unipd.esp1415;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.esp1415.R;

public class CustomArrayAdapter extends ArrayAdapter<Session> {
	// we keep track of model objects in a local variable for our convenience
	private ArrayList<Session> items;
	private Context context;

	public CustomArrayAdapter(Context context, int textViewResourceId,
			ArrayList<Session> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// performance issue (see Google I/O video in references)
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.single_session_layout, null);
		}
		// position contains the index of the array for
		// the associated item so we retrieve the Contact
		Session c = this.items.get(position);
		if (c != null) {
			TextView nameTextView = (TextView) v.findViewById(R.id.name);
			nameTextView.setText(c.getName());
			TextView nameTextView1 = (TextView) v.findViewById(R.id.date);
			nameTextView.setText(c.getName());
			TextView nameTextView2 = (TextView) v.findViewById(R.id.duration);
			nameTextView.setText(c.getName());
			TextView nameTextView3 = (TextView) v.findViewById(R.id.falls);
			nameTextView.setText(c.getName());
			//ImageView picImageView = (ImageView) v.findViewById(R.id.picture);
			//picImageView.setImageBitmap(c.getPicture());
		}
		return v;
	}
}
