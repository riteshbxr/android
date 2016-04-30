package com.projects.em;

import java.util.ArrayList;
import java.util.HashMap;

import com.projects.em.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpListItemAdapter extends BaseExpandableListAdapter {
	private class ViewHolder {
		TextView[] textViews;

		public ViewHolder() {
			textViews = new TextView[3];
		}
	}

	private Context _context;
	private ArrayList<ListItemClass> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<Integer, ArrayList<ListItemClass>> _listDataChild;

	public ExpListItemAdapter(Context context,
			ArrayList<ListItemClass> listDataHeader,
			HashMap<Integer, ArrayList<ListItemClass>> listDataChild) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listDataChild;
	}

	@Override
	public ListItemClass getChild(int groupPosition, int childPosition) {
		int key = this._listDataHeader.get(groupPosition).toInt();
		return this._listDataChild.get(key).get(childPosition);

	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int key = this._listDataHeader.get(groupPosition).toInt();
		return this._listDataChild.get(key).size();
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View ListItemview, ViewGroup parent) {
		final ListItemClass item = (ListItemClass) getChild(groupPosition,
				childPosition);
		String textValue[] = item.getStringValues();
		if (ListItemview == null) {
			ViewHolder newviews = new ViewHolder();
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ListItemview = infalInflater.inflate(R.layout.listitemresultchild,
					null);
			newviews.textViews[0] = (TextView) ListItemview
					.findViewById(R.id.MainItem);
			newviews.textViews[1] = (TextView) ListItemview
					.findViewById(R.id.SubItem1);
			newviews.textViews[2] = (TextView) ListItemview
					.findViewById(R.id.SubItem2);
			ListItemview.setTag(newviews);
		}

		ViewHolder views = (ViewHolder) ListItemview.getTag();
		for (int i = 0; i < 3; i++)
			if (views.textViews[i] != null)
				views.textViews[i].setText(textValue[i]);
		views.textViews[1].setText("@" + textValue[1]);
		if (views.textViews[2] != null)
			switch (item.getIntItemAsked(ListItemClass.THEME)) {
			case ListItemClass.PAY_THEME:
				views.textViews[2].setTextColor(Color.rgb(150, 0, 0));
				break;
			case ListItemClass.RECIEVE_THEME:
				views.textViews[2].setTextColor(Color.rgb(0, 150, 0));
				break;
			default:
			}
		return ListItemview;
	}

	@Override
	public ListItemClass getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View ListItemview, ViewGroup parent) {
		ListItemClass item = getGroup(groupPosition);
		String textValue[] = item.getStringValues();
		if (ListItemview == null) {
			ViewHolder newviews = new ViewHolder();
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ListItemview = infalInflater.inflate(R.layout.listitemresult, null);
			newviews.textViews[0] = (TextView) ListItemview
					.findViewById(R.id.MainItem);
			newviews.textViews[1] = (TextView) ListItemview
					.findViewById(R.id.SubItem1);
			newviews.textViews[2] = (TextView) ListItemview
					.findViewById(R.id.SubItem2);
			ListItemview.setTag(newviews);
		}

		ViewHolder views = (ViewHolder) ListItemview.getTag();
		for (int i = 0; i < 3; i++)
			if (views.textViews[i] != null)
				views.textViews[i].setText(textValue[i]);

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

		return ListItemview;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}