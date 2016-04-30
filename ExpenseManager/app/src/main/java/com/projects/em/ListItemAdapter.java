package com.projects.em;

import java.util.ArrayList;
import java.util.List;

import com.projects.em.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListItemAdapter extends ArrayAdapter<ListItemClass>
{


	public interface OnLVCCheckChangedListener {
		public boolean OnLVCCheckChanged(int position, boolean checked);
	}

	// class for caching the views in \a row
	private class ViewHolder {
		TextView[] textViews;
		CheckBox chk;
		// ImageView imgView;
		EditText txtshared;
		//RelativeLayout listitemlayout;
		public ViewHolder() {
			textViews = new TextView[3];
		}
	}

	private final int resource;
	// private static final int LongClick=1;
	// private static final int Click=0;
	// public static final int Checked=3;
	private List<ListItemClass> items;
	private ListItemClass item;
	private RelativeLayout ListItemview;
	private int chkVisibility = View.VISIBLE;
	private Context context;
	// DECLARED IN CUSTOM VIEW
	private OnLVCCheckChangedListener onLVCCheckChangedListener;

	public ListItemAdapter(Context context, int resource,
			List<ListItemClass> items) {
		super(context, resource, items);
		this.resource = resource;
		this.context = context;
		this.items=items;
	}

	public List<Integer> getAllChecked() {
		List<Integer> listChecked=new ArrayList<Integer>();
		for(int i=0;i<items.size();i++)
		if(items.get(i).getCheck())
			listChecked.add(i);
		return listChecked;
	}
	public List<Integer> getAllCheckedIds()
	{
		List<Integer> listChecked=new ArrayList<Integer>();
		for(int i=0;i<items.size();i++)
			if(items.get(i).getCheck())
				listChecked.add(items.get(i).toInt());
		return listChecked;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		item = items.get(position);
		switch(item.getIntItemAsked(ListItemClass.CLASSFOR))
		{
		case ListItemClass.CLASSFOR_EVENTS:
			item.setItemAsked(ListItemClass.str_BILL,String.valueOf(item.getAmount()));
		case ListItemClass.CLASSFOR_GROUP:
			item.setItemAsked(ListItemClass.PLACE,item.getStringItemAsked(ListItemClass.DESC));
		}
	//	ListItemAdapter aa=(ListItemAdapter) ((ListViewCustom) convertView).getAdapter();
		String textValue[] = item.getStringValues();
		float amount = item.getAmount();
		// Log.w("adapt","View asked at Pos "+
		// Integer.toString(position)+",Click state "+ item.getCheck());
		if (convertView == null) {
			ListItemview = new RelativeLayout(context);
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater) context.getSystemService(inflater);
			li.inflate(resource, ListItemview);
			ViewHolder newviews = new ViewHolder();
			newviews.textViews[0] = (TextView) ListItemview
					.findViewById(R.id.MainItem);
			newviews.textViews[1] = (TextView) ListItemview
					.findViewById(R.id.SubItem1);
			newviews.textViews[2] = (TextView) ListItemview
					.findViewById(R.id.SubItem2);
			newviews.chk = (CheckBox) ListItemview.findViewById(R.id.chk);
			newviews.txtshared = (EditText) ListItemview
					.findViewById(R.id.EditTextItem);
			//newviews.listitemlayout = (RelativeLayout) ListItemview.findViewById(R.id.listitemlayout);
			// newviews.imgView=(ImageView)ListItemview.findViewById(R.id.imgtrip);
			ListItemview.setTag(newviews);

		} else
			ListItemview = (RelativeLayout) convertView;
		ViewHolder views = (ViewHolder) ListItemview.getTag();
								
		for (int i = 0; i < 3; i++)
			if (views.textViews[i] != null)
				views.textViews[i].setText(textValue[i]);
		//if(views.textViews[1] != null) views.textViews[1].setText(item.getIntItemAsked(ListItemClass.ID)+"");//+"";
		
		if (views.txtshared != null)
			{
				views.txtshared.setText(String.format("%.1f", amount));
		}
		// Log.w("Hisaab", ""+item.getIntItemAsked(ListItemClass.FORMATS));
		if (views.textViews[1] != null)
			switch (item.getIntItemAsked(ListItemClass.THEME)) {
			case ListItemClass.PAY_THEME:
				views.textViews[1].setText("To Pay:");
				views.textViews[1].setTextColor(Color.rgb(150, 0, 0));
				views.textViews[2].setTextColor(Color.rgb(150, 0, 0));
				break;
			case ListItemClass.RECIEVE_THEME:
				views.textViews[1].setText("To Recieve:");
				views.textViews[1].setTextColor(Color.rgb(0, 150, 0));
				views.textViews[2].setTextColor(Color.rgb(0, 150, 0));
				break;
			default:
			}
		/*if (views.listitemlayout != null ) {
			views.listitemlayout.setBackgroundResource(TripTheme
					.getListDrawableId(TripTheme.DRAWABLEFOR_TripList,item.getIntItemAsked(ListItemClass.THEME)));
		}*/

	
		if (views.chk != null) {
				if(item.toInt()==ListItemClass.ID_AddNewItem)
					views.chk.setVisibility(View.GONE);
				else
					views.chk.setVisibility(chkVisibility);
			views.chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton checkview,
						boolean checked) {
					// TODO Auto-generated method stub
					if (checked) {
						// checkview.setChecked(true);
						items.get(position).setCheck(true);
					} else {
						// checkview.setChecked(false);
						items.get(position).setCheck(false);
					}
					if (onLVCCheckChangedListener != null)
						onLVCCheckChangedListener.OnLVCCheckChanged(position,
								checked);
				}
			});
			views.chk.setChecked(items.get(position).getCheck());//listFunctions(0, position));
		//	aa.insert(item,position);
			notifyDataSetChanged();
			views.chk.setEnabled(item.isChkEnabled());
		}
	//	Animation anim=AnimationUtils.loadAnimation(context,R.anim.popup_show);

	return ListItemview;
	
	}

	
	/*public int getCheckedItemCount() {
		int count = 0;
		for (int i = 0; i < this.getCount(); i++) {
			if (items.get(i).getCheck())
				count++;
		}
		return count;
	}
*/

	/**
	 * 
	 * @param ALL
	 *            ( True to select All, false to Select None
	 * @return (If Action was succesfull on all items)
	 */
	public boolean selectAllorNone(boolean ALL) {
		boolean AllSuccess = true;
		// aa.setAllChecked(ALL);
		for (int i = 0; i < this.getCount(); i++) {
			if (getItem(i).isChkEnabled()) {
				items.get(i).setCheck(ALL);//aa.listFunctions(ALL ? 1 : 2, i);
			} else
				AllSuccess = false;
		}
		notifyDataSetChanged();
		return AllSuccess;
	}


	public void setCheckButtonVisibility(int visibility) {
		chkVisibility = visibility;
			}

	// ALLOWS YOU TO SET LISTENER && INVOKE THE OVERIDING METHOD
	// FROM WITHIN ACTIVITY
	public void setOnLVCCheckChangedListener(OnLVCCheckChangedListener listener) {
		onLVCCheckChangedListener = listener;
	}


}
