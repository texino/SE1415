package it.dei.unipd.esp1415.adapters;

import it.dei.unipd.esp1415.activity.FallDataActivity;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.objects.FallInfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.esp1415.R;

public class FallAdapter extends ArrayAdapter<FallInfo> implements Filterable{

	private Activity activity;
	private ArrayList<FallInfo> infos;
	private int layoutResourceId;
	private String sessionId;
	

	/**
	 * Create an Adapter for the specified list of boards
	 * @param context The context that creates this adapter 
	 * @param data The list (in form of array) of the boards to adapt
	 */
	public FallAdapter(Context context,ArrayList<FallInfo> data,String sessionId) 
	{
		super(context, R.layout.row_fall_info, data);//creare un layout per una riga della lista e lo passo 
		this.layoutResourceId = R.layout.row_fall_info;
		infos = data;
		activity=(Activity)(context);
		this.sessionId=sessionId;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public FallInfo getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		//position: the position in the list of the view to visualize
		//convertView: an old view that we can reuse
		//parent: the list for this view
		View row=convertView;
		Holder holder;
		//the returned view must have data attached to it
		if(convertView==null)
		{
			//there isn't a view to reuse
			LayoutInflater inflater=(activity.getLayoutInflater());
			//we create a new view based on the layout specified
			row=inflater.inflate(layoutResourceId,parent,false);
			//we attach data to it
			//create holder linked to this row
			holder=createHolder(row);
			row.setTag(holder);
		}
		else
		{
			holder=(Holder)row.getTag();
		}
		//the row is created and the holder is associated to it
		populateHolder(holder,position);
		//setListener for the row
		row.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickRow(v);
			}
		});
		return row;
	}
	
	private void clickRow(View row)
	{
		Intent i=new Intent(activity,FallDataActivity.class);
		i.putExtra(FallDataActivity.SESSION_ID_TAG,sessionId);
		i.putExtra(FallDataActivity.FALL_ID_TAG,infos.get(((Holder)(row.getTag())).position).getId());
		activity.startActivity(i);
	}

	/**
	 * Personalize the holder with the data of this user
	 * @param holder The holder to personalize
	 * @param user The user from which take data
	 */
	private Holder createHolder(View row)
	{
		Holder holder = new Holder();
		holder.dateTimeFall = (TextView) row.findViewById(R.id.date_time_fall);
		holder.notifica = (ImageView) row.findViewById(R.id.notifica);
		return holder;
	}

	private void populateHolder(Holder holder,final int pos)
	{
		final FallInfo fall=infos.get(pos);

		holder.dateTimeFall.setText(fall.getDate());
		holder.position=pos;
		if(fall.isNotified())
		     holder.notifica.setImageResource(R.drawable.image_notified_ok);
		else
			 holder.notifica.setImageResource(R.drawable.image_notified_ko);
	}
	


	private static class   Holder {
		TextView dateTimeFall;
		ImageView notifica;
		int position;
	}
}