package it.dei.unipd.esp1415.adapters;

import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.Utils;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.esp1415.R;

/**
 * SessionAdapter class: Adapter provides access to the data session items. The
 * Adapter is also responsible for making a View for each item in the data set.
 */
public class SessionAdapter extends ArrayAdapter<SessionInfo> {
	private static Context appContext;

	public SessionAdapter(Context context, List<SessionInfo> items) {
		super(context, R.layout.single_session_layout, items);
		appContext = context;
	}

	// Get a View that displays the data at the specified position in the data
	// set.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			// inflate the GridView item layout
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.single_session_layout,
					parent, false);

			// initialize the view holder
			viewHolder = new ViewHolder();
			viewHolder.picture = (ImageView) convertView
					.findViewById(R.id.picture);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			viewHolder.row = (RelativeLayout) convertView
					.findViewById(R.id.itembackground);
			viewHolder.duration = (TextView) convertView
					.findViewById(R.id.duration);
			viewHolder.falls = (TextView) convertView.findViewById(R.id.falls);
			convertView.setTag(viewHolder);
		} else {
			// recycle the already inflated view
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// update the item view
		SessionInfo item = getItem(position);
		if (item.getStatus()) {
			viewHolder.row.setBackgroundColor(appContext.getResources().getColor(R.color.row_running_session_color));
		} else
			viewHolder.row.setBackgroundColor(appContext.getResources().getColor(R.color.row_color));

		try {
			Bitmap b=LocalStorage.getSessionImage(appContext,item.getId());
			if(b!=null)
				viewHolder.picture.setImageBitmap(b);
			if(item.getStatus())
				viewHolder.picture.setImageDrawable(appContext.getResources().getDrawable(R.drawable.image_session_running));
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("ERROR",
					"Errore nella lettura del file immagine - LocalStorage");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Log.i("ERROR", "Uno dei parametri è null - LocalStorage");
		}
		viewHolder.name.setText(item.getName());
		viewHolder.duration.setText(Utils.convertDuration(item.getDuration()));
		viewHolder.date.setText(item.getDate());
		viewHolder.falls.setText(""+item.getNumberOfFalls());

		return convertView;
	}

	/**
	 * The view holder design pattern prevents using findViewById() repeatedly
	 * in the getView() method of the adapter. Use this to access elements
	 * directly without using findViewById().
	 * 
	 * @see http 
	 *      ://developer.android.com/training/improving-layouts/smooth-scrolling
	 *      .html#ViewHolder
	 */
	private static class ViewHolder {
		ImageView picture;
		TextView name;
		TextView date;
		TextView duration;
		TextView falls;
		RelativeLayout row;
	}
}
