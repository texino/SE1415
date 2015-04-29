package it.dei.unipd.esp1415;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.esp1415.R;

public class CustomArrayAdapter extends ArrayAdapter<Session> {
	public CustomArrayAdapter(Context context, List<Session> items) {
        super(context, R.layout.single_session_layout, items);
    }
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        
        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.single_session_layout, parent, false);
            
            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.picture);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
            viewHolder.falls = (TextView) convertView.findViewById(R.id.falls);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view 
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        // update the item view
        Session item = getItem(position);
        viewHolder.picture.setImageDrawable(item.getPicture());
        viewHolder.name.setText(item.getName());
        viewHolder.duration.setText(item.getDuration());
        viewHolder.date.setText(item.getDate());
        viewHolder.falls.setText(item.getFalls());
        
        return convertView;
    }
    
    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     * 
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        ImageView picture;
        TextView name;
        TextView date;
        TextView duration;
        TextView falls;
    }
	/*// we keep track of model objects in a local variable for our convenience
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
	}*/
}
