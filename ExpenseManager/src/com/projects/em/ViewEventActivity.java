package com.projects.em;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.projects.em.DB_CP.DBinfo;

public class ViewEventActivity extends Activity{
	private static String EventName;
	private static int EventId;
	private static int TripId;

//	private static String TripName;
	protected ActionMode mActionMode;
	private static TextView lbleventname;
	private static TextView lbleventplacedate;
	private static TextView lbleventexpense;
	private static TextView lbleventdesc;
	private static TextView lbleventpaidby;
	private static TextView lbleventsharedby;
	private static ListItemClass Event;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewevent);
		lbleventname = (TextView) findViewById((R.id.lbleventname));
		lbleventplacedate = (TextView) findViewById((R.id.lbleventplacedate));
		lbleventexpense = (TextView) findViewById((R.id.lbleventexpense));
		lbleventdesc = (TextView) findViewById((R.id.lbleventdesc));
		lbleventpaidby = (TextView) findViewById((R.id.lbleventpaidby));
		lbleventsharedby = (TextView) findViewById((R.id.lbleventsharedby));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.viewevent, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent me = this.getIntent();
		EventId = me.getIntExtra(DBinfo.EVENT_ID, -1);
		TripId = me.getIntExtra(DBinfo.TRIP_ID, -1);
		Event = DB_CP.getListItemClass(DB_CP.EVENT_URI, DBinfo.EVENT_ID + "="
		
				+ EventId);
		/*lytEventDetails.setBackgroundResource(TripTheme.getListDrawableId(
				TripTheme.DRAWABLEFOR_TripDetails,
				Trip.getIntItemAsked(ListItemClass.THEME)));*/
		if(Event!=null)
		{
			if(TripId<0)
				TripId=Event.getIntItemAsked(ListItemClass.TRIPID_FOREVENT);
			EventName = Event.getStringItemAsked(ListItemClass.MAIN_ITEM);
			//TripName = Helper.getTitleFromId(TripId,Helper.TRIPS);
			lbleventname.setText(EventName);			
			lbleventplacedate.setText(Event.getStringItemAsked(ListItemClass.PLACE)
					+ ", " + Helper.getFormattedDateFormatted(
					Event.getStringItemAsked(ListItemClass.DATE),
					"dd MMM yyyy (EEE)", "d/M/yyyy"));
			lbleventdesc.setText( Event.getStringItemAsked(ListItemClass.DESC));
			lbleventexpense.setText("Total Expense:"+ Double.toString(Event.getAmount()));
			showmembers();
			this.setTitle(EventName);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_eventhisaab:
				startActivity(Helper.showHisaab(getApplicationContext(),Helper.EVENTS, EventId, TripId));
				overridePendingTransition(R.anim.popup_show, R.anim.popup_hide);
				return true;
			case R.id.action_editevent:
				OpenEditEventPage();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * @param Mode 0 for add, 1 for view, 2 for edit
	 * @param eventIndex
	 * @return
	 */
	private boolean OpenEditEventPage() {
		Intent intent = new Intent(this, UpdateEventActivity.class);
			intent.putExtra("MODE", DBinfo.MODE_EDIT);
			intent.putExtra("CALLER", "VIEWEVENT");
			intent.putExtra(DBinfo.CLM_EVENT_NAME, EventName);
			intent.putExtra(DBinfo.EVENT_ID, EventId);
			intent.putExtra(DBinfo.TRIP_ID, TripId);
			startActivity(intent);
			return true;
	}

	private void showmembers() {
		String query2="";
		String query = "SELECT " + DBinfo.CLM_MEMBER_NAME + ","
					+ DBinfo.CLM_SHARE_AMOUNT + "," + DBinfo.MEMBER_ID;
		query += " FROM " + DBinfo.DB_TABLE_MEMBERS + ","
				+ DBinfo.DB_TABLE_SHARES;
		query += " WHERE (" + DBinfo.MEMBER_ID + " = "
				+ DBinfo.CLM_PAIDBY_MEMBERID;
		query += " OR " + DBinfo.MEMBER_ID + " = "
				+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
		query += " AND " + DBinfo.EVENT_ID + " = " + String.valueOf(EventId);
		query2=query;
		query += " AND " + DBinfo.CLM_SHARE_AMOUNT + " > 0 ";
		query2 += " AND " + DBinfo.CLM_SHARE_AMOUNT + " < 0 ";
		
		
		Cursor cursor = DB_CP.query(query);
		float money;
		String content="";
		while (cursor.moveToNext()) {
			money = (float) cursor.getDouble(1);
//			memberId = cursor.getInt(2);
			//ItemsFormat = money < 0 ? ListItemClass.PAY_THEME: ListItemClass.RECIEVE_THEME;
			content+=cursor.getString(0)+" : "+String.format("%.2f", money)+"\n";		
		}
		lbleventpaidby.setText(content);
		content="";
		cursor = DB_CP.query(query2);
		while (cursor.moveToNext()) {
			money = (float) cursor.getDouble(1);
//			memberId = cursor.getInt(2);
			//ItemsFormat = money < 0 ? ListItemClass.PAY_THEME: ListItemClass.RECIEVE_THEME;
			content+=cursor.getString(0)+" : "+String.format("%.2f", Math.abs(money))+"\n";		
		}
		lbleventsharedby.setText(content);
	}
}
