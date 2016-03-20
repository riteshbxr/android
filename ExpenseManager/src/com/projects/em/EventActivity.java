package com.projects.em;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.em.DB_CP.DBinfo;
import com.projects.em.ListItemAdapter.OnLVCCheckChangedListener;

public class EventActivity extends ListActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener,
		OnLVCCheckChangedListener {
	private static ListViewCustom lstEvents;
	private static ListItemAdapter aa;
	private static List<ListItemClass> eventList;
	private static RelativeLayout lyt_AddEvent;
	private static String TripName;
	private static int TripId;
	protected ActionMode mActionMode;
	private static TextView lbltripname;
	private static TextView lbltripplacedate;
	private static RelativeLayout lytEventDetails;
	private static ListItemClass Trip;
	private static UndoOptions setundo;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() { // Called
																					// when
																					// the
																					// action
																					// mode
																					// is
																					// created;
																					// startActionMode()
																					// was
																					// called

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.event_Context_delete:
				List<Integer> positions=new ArrayList<Integer>(aa.getAllChecked());
				mode.finish();
				setundo=new UndoOptions(lstEvents,positions, Helper.getUndoCallback(getContentResolver(), new ArrayList<ListItemClass>(eventList), aa,Helper.EVENTS));
				
				/*Helper.DeleteSelectedItems(getContentResolver(), aa.getAllChecked(), eventList,
						Helper.EVENTS);
				mode.finish();// Action picked, so close the CAB
				ReloadList();*/
				return true;
			case R.id.event_Context_SelectAll:
				boolean finishmode = Helper.SelectAll(item, aa);
				if (finishmode)
					mode.finish();
				return finishmode;
			case R.id.event_Context_Hisaab:
				startActivity(Helper.showHisaab(getApplicationContext(),
						Helper.EVENTS, Helper.extractIdsfromList(
								aa.getAllChecked(), eventList), TripId));
				mode.finish();
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.event_contextmenu, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) { // lstEvents.setChoiceMode(ListViewCustom.CHOICE_MODE_NONE);
															// aa.setCheckButtonVisibility(View.INVISIBLE);
															// aa.notifyDataSetChanged();
			aa.selectAllorNone(false);
			mActionMode = null;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
			// Return false if nothing is done
		}
	};

	@Override
	public void onClick(View V) {
		switch (V.getId()) {
		case R.id.lyt_addactivity:
			if(Helper.getTripMembers(TripId).length>0)
				OpenAddEventPage(DBinfo.MODE_ADD, -1);// Add mode
			else
				Toast.makeText(getApplicationContext(), "Please add members to trip before adding an activity", Toast.LENGTH_LONG).show();
			
			break;
		case R.id.LytEventDetails:
			OpenAddTripPage(1, TripId);
			break;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		lstEvents = (ListViewCustom) findViewById(android.R.id.list);
		lyt_AddEvent = (RelativeLayout) findViewById((R.id.lyt_addactivity));
		lytEventDetails = (RelativeLayout) findViewById(R.id.LytEventDetails);
		eventList = new ArrayList<ListItemClass>();
		lbltripname = (TextView) findViewById((R.id.tripname));
		lbltripplacedate = (TextView) findViewById((R.id.tripplacedate));
		lytEventDetails.setOnClickListener(this);

		aa = new ListItemAdapter(this, R.layout.listitemevent, eventList);
		aa.setOnLVCCheckChangedListener(this);

		lstEvents.setAdapter(aa);
		lstEvents.setOnItemClickListener(this);
		lyt_AddEvent.setOnClickListener(this);
		// aa.setCheckButtonVisibility(View.INVISIBLE);
		lstEvents.setChoiceMode(ListViewCustom.CHOICE_MODE_NONE);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// registerForContextMenu(lstEvents);
		getLoaderManager().initLoader(0, null, this);
		//setundo=new SwipeDismissList(lstEvents,Helper.getSwipeCallback(getContentResolver(),aa,Helper.EVENTS));
		this.setTitle("Events");
	}
	
	@Override 
	protected void onPause() 
	{ 
		super.onPause();
		if(setundo!=null)
			setundo.discardUndo();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_contextmenu, menu);
	}

	// List Cursor Operations
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = new String[] { DBinfo.EVENT_ID,DBinfo.CLM_EVENT_NAME, DBinfo.CLM_EVENT_BILL, DBinfo.TRIP_ID };
		String where = DBinfo.TRIP_ID + "=" + Integer.toString(TripId);
		CursorLoader loader = new CursorLoader(this, DB_CP.EVENT_URI,
				projection, where, null, null);
		return loader;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int index, long arg3) {
		if (mActionMode == null)// click event
		{
			if(eventList.get(index).toInt()==ListItemClass.ID_AddNewItem)
				OpenAddEventPage(DBinfo.MODE_ADD, -1);// A mode
			else
				OpenAddEventPage(DBinfo.MODE_VIEW, index);// View mode
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		eventList.clear();
		ListItemClass newItem;
		while (cursor.moveToNext()) {
			newItem = new ListItemClass(
					cursor.getInt(0),cursor.getString(1),
					cursor.getFloat(2), cursor.getInt(3),"","","","");
			eventList.add(newItem);
		}
		/*if(eventList.size()<=0)
		{
			newItem = new ListItemClass(
				ListItemClass.AddNewItem,"Add New Event",
				0, 0,"","",ListItemClass.AddNewItem,"","");
		
		newItem.setItemAsked(ListItemClass.SUBITEM_1,"Click here to add");
		eventList.add(newItem);
		}*/
		aa.notifyDataSetChanged();
		showmemberswithcost();
	}

	@Override
	public boolean OnLVCCheckChanged(int position, boolean checked) {
		// TODO Auto-generated method stub
		// eventList.get(position).setCheck(true);
		if (mActionMode != null)
			if (aa.getAllChecked().size() <= 0)
				mActionMode.finish();
			else
				mActionMode.setTitle(aa.getAllChecked().size() + " selected");
		else if (mActionMode == null && checked) {
			mActionMode = this.startActionMode(mActionModeCallback);
			mActionMode.setTitle(aa.getAllChecked().size() + " selected");
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_triphisaab:
			startActivity(Helper.showHisaab(getApplicationContext(),
					Helper.TRIPS, TripId, TripId));
			return true;
		case R.id.action_addPayment:
			OpenAddEventPage(DBinfo.MODE_ADD_Prefilled,-1);
			return true;
			/*
			 * case R.id.action_showtriphisaab: Intent intent=new
			 * Intent(this,HisaabActivity.class); intent.putExtra("TYPE",
			 * Helper.TRIPS); intent.putExtra(DBinfo.TRIP_ID, TripId);
			 * intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.
			 * FLAG_ACTIVITY_REORDER_TO_FRONT); startActivity(intent); return
			 * true;
			 */
		case R.id.action_export:
			/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		    intent.setType("file/*");
		    startActivityForResult(intent,TRIP_EXPORT_INTENT);*/
			Toast.makeText(getApplicationContext(), "Exported to" +Helper.exportToFile(TripId).getPath(), Toast.LENGTH_LONG).show();
		return true;
		case R.id.action_send:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			//sendIntent.setPackage("com.android.bluetooth");
			sendIntent.setType("text/html");
			//sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
			//sendIntent.setType("text/plain");
			Uri uri = Uri.parse("file://" + Helper.exportToFile(TripId));
			sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
			startActivity(sendIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent me = this.getIntent();
		TripId = me.getIntExtra(DBinfo.TRIP_ID, -1);
		Trip = DB_CP.getListItemClass(DB_CP.TRIP_URI, DBinfo.TRIP_ID + "="
				+ TripId);
		/*lytEventDetails.setBackgroundResource(TripTheme.getListDrawableId(
				TripTheme.DRAWABLEFOR_TripDetails,
				Trip.getIntItemAsked(ListItemClass.THEME)));*/
		TripName = Trip.getStringItemAsked(ListItemClass.MAIN_ITEM);
		lbltripname.setText(TripName);
		lbltripplacedate.setText(Trip
				.getStringItemAsked(ListItemClass.PLACE)
				+ ", "
				+ Helper.getFormattedDateFormatted(
						Trip.getStringItemAsked(ListItemClass.DATE),
						"dd MMM yyyy (EEE)", "d/M/yyyy"));

		// ListItemClass lic=DB_CP.getListItemClass(DB_CP.TRIP_URI,
		// DBinfo.TRIP_ID+"="+TripId);

		/*
		 * TextView lblName=(TextView)findViewById(R.id.trip_lblname); TextView
		 * lblDate=(TextView)findViewById(R.id.trip_lbldate); TextView
		 * lblPlace=(TextView)findViewById(R.id.trip_lblplace);
		 * 
		 * lblName.setText(lic.getStringItemAsked(ListItemClass.MAIN_ITEM));
		 * lblDate.setText(lic.getStringItemAsked(ListItemClass.SUBITEM_1));
		 * lblPlace.setText(lic.getStringItemAsked(ListItemClass.SUBITEM_2));
		 */
	//	ScaleAnimation anim = new ScaleAnimation( 1f, 1f,0f,1f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0f);
	//	anim.setDuration(1000); 
	//	lytEventDetails.startAnimation(anim);
		getLoaderManager().restartLoader(0, null, this);
	}

	/**
	 * 
	 * @param Mode 0 for add, 1 for view, 2 for edit
	 * @param eventIndex
	 * @return
	 */
	private boolean OpenAddEventPage(int Mode, int eventIndex) {
		Intent intent = new Intent(this, UpdateEventActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra(DBinfo.CLM_TRIP_NAME, TripName);
		intent.putExtra(DBinfo.TRIP_ID, TripId);
		switch (Mode) {
		case DBinfo.MODE_ADD_Prefilled:
			intent.putExtra("Shared_Label", "Recieved By");
			intent.putExtra("Paid_Label", "Paid By");
			intent.putExtra(DBinfo.CLM_EVENT_NAME, "Payment_1");
		case DBinfo.MODE_ADD:
			intent.putExtra("MODE", Mode);
			intent.putExtra("CALLER", "EVENT");
			startActivity(intent);
			return true;
		case DBinfo.MODE_VIEW:
			intent = new Intent(this, ViewEventActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra(DBinfo.CLM_TRIP_NAME, TripName);
			intent.putExtra(DBinfo.TRIP_ID, TripId);
			intent.putExtra("MODE", Mode);
			intent.putExtra("CALLER", "EVENT");
			intent.putExtra(DBinfo.CLM_EVENT_NAME, eventList.get(eventIndex)
					.getStringItemAsked(ListItemClass.MAIN_ITEM));
			intent.putExtra(DBinfo.EVENT_ID, eventList.get(eventIndex).toInt());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case DBinfo.MODE_EDIT:
			intent.putExtra("MODE", Mode);
			intent.putExtra("CALLER", "EVENT");
			intent.putExtra(DBinfo.CLM_EVENT_NAME, eventList.get(eventIndex)
					.getStringItemAsked(ListItemClass.MAIN_ITEM));
			intent.putExtra(DBinfo.EVENT_ID, eventList.get(eventIndex).toInt());
			startActivity(intent);
			return true;
		default:

			return false;
		}

	}

	/**
	 * 
	 * @param Mode
	 *            0 for add, 1 for view, 2 for edit
	 */
	private boolean OpenAddTripPage(int Mode, int tripid) {
		Intent intent = new Intent(this, UpdateTripActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra(DBinfo.TRIP_ID, TripId);
		switch (Mode) {
		case DBinfo.MODE_ADD:// Add
			intent.putExtra("MODE", Mode);
			intent.putExtra("CALLER", "EVENT");
			startActivity(intent);
			return true;
		case DBinfo.MODE_VIEW:// View
		case DBinfo.MODE_EDIT:// Edit
			intent.putExtra("MODE", Mode);
			intent.putExtra("CALLER", "EVENT");
			startActivity(intent);
			return true;
		default:
			return false;
		}
	}

	/*private void ReloadList() {
		getLoaderManager().restartLoader(0, null, this);
	}*/

	private void showmemberswithcost() {
		TextView lblTripMembers = (TextView) findViewById(R.id.tripmember);
		String query2="";
		String query = "SELECT " + DBinfo.CLM_MEMBER_NAME + ","
				+"SUM("+ DBinfo.CLM_SHARE_AMOUNT + ")," + DBinfo.MEMBER_ID;
		query += " FROM " + DBinfo.DB_TABLE_MEMBERS + ","
				+ DBinfo.DB_TABLE_SHARES+","
				+ DBinfo.DB_TABLE_EVENTS;
		query += " WHERE (" + DBinfo.MEMBER_ID + " = "
				+ DBinfo.CLM_PAIDBY_MEMBERID;
		query += " OR " + DBinfo.MEMBER_ID + " = "
				+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
		query += " AND " + DBinfo.DB_TABLE_EVENTS + "."+DBinfo.EVENT_ID+" = "
				+DBinfo.DB_TABLE_SHARES + "."+DBinfo.EVENT_ID;
		query += " AND " + DBinfo.TRIP_ID + " = " + String.valueOf(TripId);
		
		query2=query;
		query += " AND " + DBinfo.CLM_SHARE_AMOUNT + " > 0 "
				+" GROUP BY "+ DBinfo.CLM_PAIDBY_MEMBERID
				+" ORDER BY "+ DBinfo.CLM_MEMBER_NAME+" ASC";
		query2 += " AND " + DBinfo.CLM_SHARE_AMOUNT + " < 0 "
				+" GROUP BY "+ DBinfo.CLM_SHAREDBY_MEMBERID
				+" ORDER BY "+ DBinfo.CLM_MEMBER_NAME+" ASC";
		
		Cursor cursor = DB_CP.query(query);
		float money;
		String content="Paid by:\n";
		while (cursor.moveToNext()) {
			money = (float) cursor.getDouble(1);
//			memberId = cursor.getInt(2);
			//ItemsFormat = money < 0 ? ListItemClass.PAY_THEME: ListItemClass.RECIEVE_THEME;
			content+=cursor.getString(0)+" : "+String.format("%.2f", money)+"\n";		
		}
		content+="\nShared By:\n";
		cursor = DB_CP.query(query2);
		while (cursor.moveToNext()) {
			money = (float) cursor.getDouble(1);
//			memberId = cursor.getInt(2);
			//ItemsFormat = money < 0 ? ListItemClass.PAY_THEME: ListItemClass.RECIEVE_THEME;
			content+=cursor.getString(0)+" : "+String.format("%.2f", Math.abs(money))+"\n";		
		}
		lblTripMembers.setText(content);
	}
	/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		File recievedFile;
		switch(requestCode)
		{
		case TRIP_EXPORT_INTENT:
			if(resultCode==RESULT_OK)	
			{
    			Uri selectedFileUri = data.getData();
                recievedFile = Helper.getPathfromUri(selectedFileUri,getContentResolver());
                Helper.exportToFile(TripId,recievedFile);
			}
			else
			{
				recievedFile=Helper.exportToFile(TripId);
			}
			Toast.makeText(getApplicationContext(), "Exported to" +recievedFile.getPath(), Toast.LENGTH_LONG).show();
			break;
		/*case TRIP_SHARE_INTENT:
			if(resultCode==RESULT_OK)	
			{
    		}
    	
		}
		
	}	
	*/
	

	/*private void showmembers() {
		TextView lblTripMembers = (TextView) findViewById(R.id.tripmember);
		ArrayList<ListItemClass> lstmembers = DB_CP.getListItemClassArray(
				DB_CP.TRIPMEMBER_URI, DBinfo.TRIP_ID + "=" + TripId);
		String names = "Members: \n";
		for (int i = 0; i < lstmembers.size(); i++) {
			if (i > 0)
				names += "\n";
			names += Helper.getTitleFromId(
					lstmembers.get(i).getIntItemAsked(ListItemClass.MEMBERID_FORTRIP),
					Helper.MEMBERS);

			// names+=lstmembers.get(i).getIntItemAsked(ListItemClass.REF_ID2);
		}
		if (lstmembers.size() <= 0)
			names += "None";
		lblTripMembers.setText(names);
	}
*/
}
