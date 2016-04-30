/*
 * Copyright 2013 Roman Nurik, Tim Roes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.projects.em;

import android.content.Context;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.List;



/**
 * A {@link android.view.View.OnTouchListener} that makes the list items in a
 * {@link ListView} dismissable. {@link ListView} is given special treatment
 * because by default it handles touches for its list items... i.e. it's in
 * charge of drawing the pressed state (the list selector), handling list item
 * clicks, etc. 
 * 
 * Read the README file for a detailed explanation on how to use this class.
 */
public final class UndoOptions {
	

	
	// Fixed properties
	public static boolean updateAllowed=true;
	private List<Integer> mCheckedIndex;
	private AbsListView mListView;
	private OnDismissCallback mCallback;
	
	// Transient properties
	private float mDensity;
	private Undoable mUndoAction;
	private Handler mHandler;

	private PopupWindow mUndoPopup;
	private TextView mUndoText;
	private Button mUndoButton;
	private int mAutoHideDelay = 3000;
	private String mDeleteString = "Item deleted";
	private int mDelayedMsgId;
	

	/**
	 * The callback interface used by {@link SwipeDismissListViewTouchListener}
	 * to inform its client about a successful dismissal of one or more list
	 * item positions.
	 */
	public interface OnDismissCallback {

		/**
		 * Called when the user has indicated they she would like to dismiss one
		 * or more list item positions.
		 *
		 * @param listView The originating {@link ListView}.
		 * @param position The position of the item to dismiss.
		 */
		Undoable onDismiss(List<Integer> positions);
	}

	/**
	 * An implementation of this abstract class must be returned by the 
	 * {@link OnDismissCallback#onDismiss(android.widget.ListView, int)} method,
	 * if the user should be able to undo that dismiss. If the action will be undone
	 * by the user {@link #undo()} will be called. That method should undo the previous
	 * deletion of the item and add it back to the adapter. Read the README file for
	 * more details. If you implement the {@link #getTitle()} method, the undo popup
	 * will show an individual title for that item. Otherwise the default title 
	 * (set via {@link #setUndoString(java.lang.String)}) will be shown.
	 */
	public abstract static class Undoable {

		/**
		 * Returns the individual undo message for this item shown in the
		 * popup dialog.
		 * 
		 * @return The individual undo message.
		 */
		public String getTitle() {
			return null;
		}

		/**
		 * Undoes the deletion.
		 */
		public abstract void undo();

		/**
		 * Will be called when this Undoable won't be able to undo anymore,
		 * meaning the undo popup has disappeared from the screen.
		 */
		public void discard() { }
		
		/*
		* for nonundoable bar with masseges
		*
		public boolean showUndoIcon()
		{
			return true;
		}
		*/
	}

	/**
	 * Constructs a new swipe-to-dismiss touch listener for the given list view.
	 *
	 * @param listView The list view whose items should be dismissable.
	 * @param callback The callback to trigger when the user has indicated that
	 * she would like to dismiss one or more list items.
	 * @param mode The mode this list handles multiple undos.
	 */
	public UndoOptions(AbsListView listView,List<Integer> checkedIndex, OnDismissCallback callback) {

		if(listView == null) {
			throw new IllegalArgumentException("listview must not be null.");
		}
		mCheckedIndex=checkedIndex;
		Sort(mCheckedIndex);
		mHandler = new HideUndoPopupHandler();
		mListView = listView;
		mCallback = callback;
		LoadUndoPopup();
		performDismiss(checkedIndex);
	}
	private void Sort(List<Integer> list)
	{
		int temp=0;
		//bubble sort
		for(int i=0;i<list.size()-1;i++)
		for(int j=i+1;j<list.size();j++)
		{
			if(list.get(j)<list.get(i))
			{
				temp=list.get(i);
				list.set(i,list.get(j));
				list.set(j,temp);
			}
		}
	}

	private void LoadUndoPopup()
	{
		mDensity = mListView.getResources().getDisplayMetrics().density;

		// -- Load undo popup --
		LayoutInflater inflater = (LayoutInflater) mListView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.undo_popup, null);
		mUndoButton = (Button)v.findViewById(R.id.undo);
		mUndoButton.setOnClickListener(new UndoHandler());
		mUndoText = (TextView)v.findViewById(R.id.text);

		mUndoPopup = new PopupWindow(v);
		mUndoPopup.setAnimationStyle(R.style.fade_animation);
        // Get scren width in dp and set width respectively
        int xdensity = (int)(mListView.getContext().getResources().getDisplayMetrics().widthPixels / mDensity);
        if(xdensity < 300) {
    		mUndoPopup.setWidth((int)(mDensity * 280));
        } else if(xdensity < 350) {
            mUndoPopup.setWidth((int)(mDensity * 300));
        } else if(xdensity < 500) {
            mUndoPopup.setWidth((int)(mDensity * 330));
        } else {
            mUndoPopup.setWidth((int)(mDensity * 450));
        }
		mUndoPopup.setHeight((int)(mDensity * 56));
		// -- END Load undo popup --
		
	}
	private void performDismiss( final List<Integer> dismissPosition) {
		// Animate the dismissed list item to zero-height and fire the dismiss callback when
		// all dismissed list item animations have completed. This triggers layout on each animation
		// frame; in the future we may want to do something smarter and more performant.
			mUndoAction=mCallback.onDismiss(dismissPosition);
			changePopupText();
			changeButtonLabel();
			// Show undo popup
			mUndoPopup.showAtLocation(mListView,Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,0, (int)(mDensity * 15));
			// Queue the dismiss only if require
		if(mUndoPopup.isShowing()) {	
			// Send a delayed message to hide popup
			updateAllowed=false;
			mHandler.sendMessageDelayed(mHandler.obtainMessage(mDelayedMsgId), 
										mAutoHideDelay);
		}
		}

	/**
	 * Returns an {@link android.widget.AbsListView.OnScrollListener} to be
	 * added to the {@link ListView} using
	 * {@link ListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener)}.
	 * If a scroll listener is already assigned, the caller should still pass
	 * scroll changes through to this listener. This will ensure that this
	 * {@link SwipeDismissListViewTouchListener} is paused during list view
	 * scrolling.</p>
	 *
	 * @see {@link SwipeDismissListViewTouchListener}
	 
	private AbsListView.OnScrollListener makeScrollListener() {
		return new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				//setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {
			}
		};
	}
*/
	/**
	 * Sets the time in milliseconds after which the undo popup automatically 
	 * disappears.
	 * 
	 * @param delay Delay in milliseconds.
	 */
	public void setAutoHideDelay(int delay) {
		mAutoHideDelay = delay;
	}


	/**
	 * Sets the string shown in the undo popup. This will only show if
	 * the {@link Undoable} returned by the {@link OnDismissCallback} returns
	 * {@code null} from its {@link Undoable#getTitle()} method.
	 * 
	 * @param msg The string shown in the undo popup.
	 */
	public void setUndoString(String msg) {
		mDeleteString = msg;
	}



    /**
     * Discard all stored undos and hide the undo popup dialog.
     */
    public void discardUndo(){
    	if(mUndoPopup!=null)
	    	if(mUndoPopup.isShowing())
			{
	    		try{
	    			mUndoPopup.dismiss();
	    		}
	    		catch (Exception e) {
					// TODO: handle exception
	    			Log.w("Undo Options", "Error while closing undo box:"+e.getMessage());
				}
	    		mUndoAction.discard();
			}
	    updateAllowed=true;
    }

	/**
	 * Changes text in the popup depending on stored undos.
	 */
	private void changePopupText() {
		mUndoText.setText(mDeleteString);
	}

	private void changeButtonLabel() {
		mUndoButton.setText( mListView.getResources().getString(R.string.undo));
	}

	/**
	 * Takes care of undoing a dismiss. This will be added as a 
	 * {@link View.OnClickListener} to the undo button in the undo popup.
	 */
	private class UndoHandler implements View.OnClickListener {

		public void onClick(View v) {
				mUndoAction.undo();
			if(mUndoPopup!=null)
				mUndoPopup.dismiss();
				updateAllowed=true;
		}
		
	}
	
	/**
	 * Handler used to hide the undo popup after a special delay.
	 */
	private class HideUndoPopupHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == mDelayedMsgId && updateAllowed==false) {
				// Call discard on any element
				discardUndo();
				if(mUndoPopup!=null && mUndoPopup.isShowing())
					mUndoPopup.dismiss();
			}
		}
	}
}
