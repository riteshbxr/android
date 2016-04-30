package com.projects.em;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
/**
 * helper for Prompt-Dialog creation
 */

public abstract class PromptDialog extends AlertDialog.Builder implements
		OnClickListener {

	private View input,output,next;
	private int viewType;
	public static final int EDIT_BOX = 1, DATE_DLG = 2, LIST_DLG = 3,YESNOBOX=4;
	
	
	
	/**
	 * @param context
	 * @param title String
	 * @param message String
	 * @param view View
	 * @param viewtype integer (1-EditBox, 2-Date Dialog)
	 */
	public PromptDialog(Context context, String title, String message,
			View view, int viewtype,View Destview,View NextFocus) {
		super(context);
		setTitle(title);
		setMessage(message);
		setPositiveButton("Next", this);
		viewType = viewtype;
		if (viewType != 3)
			setNegativeButton("Cancel", this);
		this.setView(null);
		switch (viewType) {
		case EDIT_BOX:// Edit Box
			input = (EditText) view;
			break;
		case DATE_DLG:// Date Dialog
			input = (DatePicker) view;
			break;
		case LIST_DLG:// List Dialog
			input = (ListViewCustom) view;
			break;
		case YESNOBOX:// List Dialog
			//input = new TextView(context);
			setPositiveButton("YES", this);
			setNegativeButton("NO", this);
			setCancelable(true);
			break;
		}
		if(Destview!=null)	output=(EditText) Destview;
		if(NextFocus!=null)	next=NextFocus;
		if(input!=null) setView(input);	
		this.show();
	}

	public PromptDialog(Context context, String title, String message, int viewtype) {
		this(context,title,message,null,viewtype,null,null);	
	}

	public int getviewType() {
		return viewType;
	}

	/**
	 * will be called when "cancel" pressed. closes the dialog. can be
	 * overridden.
	 * @param dialog
	 */

	public void onCancelClicked(DialogInterface dialog) {
		dialog.dismiss();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
//			if (onYesClickedListener != null)onYesClickedListener.OnYesClicked(viewType);
			onOkClicked(input,output,next); {
				dialog.dismiss();
			}
		} else {
			onCancelClicked(dialog);
		}
	}

	/**
	 * called when "OK" pressed.
	 * @param input
	 * @return true, if the dialog should be closed. false, if not.
	 */
	abstract public boolean onOkClicked(View input,View outView,View NextFocus);
}
