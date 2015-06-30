package it.dei.unipd.esp1415.adapters;

import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.Utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.esp1415.R;

public class CustomArrayAdapter extends ArrayAdapter<SessionInfo> {
	
	public CustomArrayAdapter(Context context, List<SessionInfo> items) {
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
            //viewHolder.picture = (ImageView) convertView.findViewById(R.id.picture);
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
        SessionInfo item = getItem(position);
        //viewHolder.picture.setImageDrawable(item.getPicture());
        viewHolder.name.setText(item.getName());
        viewHolder.duration.setText(""+Utils.convertDuration(item.getDuration()));
        viewHolder.date.setText(item.getDate());
        viewHolder.falls.setText("Cadute: "+item.getNumberOfFalls());
        
        return convertView;
    }
    
    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     * Use this to access elements directly without using findViewById().
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        //ImageView picture;
        TextView name;
        TextView date;
        TextView duration;
        TextView falls;
    }
}
