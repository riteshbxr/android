package com.projects.em;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewCustom extends ListView {
	public int curChoiceMode = CHOICE_MODE_MULTIPLE;

	public ListViewCustom(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ListViewCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ListViewCustom(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setChoiceMode(int choiceMode) {
		// TODO Auto-generated method stub
		super.setChoiceMode(choiceMode);
		curChoiceMode = choiceMode;
		switch (choiceMode) {
		case CHOICE_MODE_NONE:
			//((ListItemAdapter)getAdapter()).selectAllorNone(false);
			break;
		case CHOICE_MODE_SINGLE:
		case CHOICE_MODE_MULTIPLE:
			// for(int i=0;i<this.getCount();i++)
			// setCheckedAt(i,false);
			break;
		}

	}
}
