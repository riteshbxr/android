package com.projects.em;

import java.util.ArrayList;
import java.util.HashMap;
import com.projects.em.DB_CP.DBinfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.database.Cursor;

public class HisaabActivity extends ExpandableListActivity implements OnChildClickListener{
	private ExpandableListView lstResults;
	private ExpListItemAdapter aa;
	private ArrayList<ListItemClass> listDataHeader;
	private HashMap<Integer, ArrayList<ListItemClass>> listDataChild;
	private Intent me;
	private String emailContent="",tripsName="",smscontent="",smsaddress="",emailaddress="";
	int tripid = -1;
	int[] tripids=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final);
		lstResults = (ExpandableListView) findViewById(android.R.id.list);
		me = this.getIntent();
		prepareListData();
		aa = new ExpListItemAdapter(this, listDataHeader, listDataChild);
		lstResults.setAdapter(aa);
		lstResults.setClickable(true);
		lstResults.setOnChildClickListener(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_sendSMS:
			shareDetails(0);
			break;
		case R.id.action_sendEmail:
			shareDetails(1);
	}
		return true;
	}
	
	private void shareDetails(int msgtype)//sms=0,email=1,email html=2
	{
		//String subject="";
		switch(msgtype)
		{
		case 0:
			startActivity(Helper.SendSMS(smsaddress,smscontent));
		break;
		case 1:
			if(me.getIntExtra("TYPE", -1)== Helper.TRIPS ||me.getIntExtra("TYPE", -1)==-1)
			{
				if(tripid>0)
					startActivity(Helper.sendTripViaEmail(emailaddress, "Hisaab for "+tripsName, emailContent,tripid));
				else if(tripids!=null)
				{
					startActivity(Helper.sendTripViaEmail(emailaddress, "Hisaab for " +tripsName, emailContent,tripids));
				}
			}
			else
				startActivity(Helper.sendEmail(emailaddress, "Hisaab for "+tripsName, emailContent));
			
			
		}
		
	}
	

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 
	 * Preparing the list data
	 * 
	 * 
	 **/
	private void prepareListData() {
		int eventid = -1;
		listDataHeader = new ArrayList<ListItemClass>();
		listDataChild = new HashMap<Integer, ArrayList<ListItemClass>>();
		ArrayList<ListItemClass> ListChild = null;
		ListItemClass newItem = null;
		float money = 0;
		int ItemsFormat = 0;
		Cursor cursor, cursorChild;

		String query = "";
		int memberId = 0,EventId=0;
		String TripName="",EventName="",
		tmpTripName="",prevTripName="";
		switch (me.getIntExtra("TYPE", -1)) {
		case Helper.TRIPS:
			tripid = me.getIntExtra(DBinfo.TRIP_ID, -1);
			tripids = me.getIntArrayExtra(DBinfo.TRIP_ID + "Array");
			String tripIn = "";
			if (tripid <= 0 && tripids == null) {
				Toast.makeText(getApplicationContext(),
						"Invalid Event, Showing Full Hissab",
						Toast.LENGTH_SHORT).show();
				break;
			}
			if (tripid > 0) {
				// Tripname=Helper.getTitleFromId(tripid, Helper.TRIPS);
				tripIn = "(" + String.valueOf(tripid) + ")";
			} else if (tripids != null) {
				// Tripname="Selected Trips";
				tripIn = "(";
				for (int i = 0; i < tripids.length; i++)
					{
						tripIn += String.valueOf(tripids[i]) + ",";
					}
				tripIn += "-1)";
			}
			// labels=new
			// String[]{Tripname,"All in the Trip","All in the Trip"};

			query = "SELECT " + DBinfo.CLM_MEMBER_NAME + ", SUM("
					+ DBinfo.CLM_SHARE_AMOUNT + ")," + DBinfo.MEMBER_ID;
			query += " FROM " + DBinfo.DB_TABLE_MEMBERS + ","
					+ DBinfo.DB_TABLE_SHARES + "," + DBinfo.DB_TABLE_EVENTS;
			query += " WHERE (" + DBinfo.MEMBER_ID + " = "
					+ DBinfo.CLM_PAIDBY_MEMBERID;
			query += " OR " + DBinfo.MEMBER_ID + " = "
					+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
			query += " AND " + DBinfo.TRIP_ID + " in " + tripIn;
			query += " AND " + DBinfo.DB_TABLE_SHARES + "." + DBinfo.EVENT_ID
					+ " = " + DBinfo.DB_TABLE_EVENTS + "." + DBinfo.EVENT_ID;
			query += " GROUP BY " + DBinfo.MEMBER_ID;
			query += " HAVING SUM("+ DBinfo.CLM_SHARE_AMOUNT + ")<>0";
			query += " ORDER BY " + "UPPER(" + DBinfo.CLM_MEMBER_NAME + ")" + " ASC";

			//TODO

			cursor = DB_CP.query(query);

			while (cursor.moveToNext()) {
				money = (float) cursor.getDouble(1);
				memberId = cursor.getInt(2);
				ItemsFormat = money < 0 ? ListItemClass.PAY_THEME:ListItemClass.RECIEVE_THEME;
				newItem = new ListItemClass(memberId, cursor.getString(0),String.format("%.2f", Math.abs(money)),ItemsFormat,money);
				newItem.setItemAsked(ListItemClass.THEME, ItemsFormat);
				smscontent+=cursor.getString(0)+" ("+String.format("%.2f", money)+"),\n";
				smsaddress+=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.MEMBER_ID+"="+memberId).getStringItemAsked(ListItemClass.PHNO)+";";
				
				emailContent+="NAME:" +cursor.getString(0)+"("+String.format("%.2f", money)+")";		
				emailaddress+=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.MEMBER_ID+"="+memberId).getStringItemAsked(ListItemClass.EMAIL)+";";
				


				listDataHeader.add(newItem);
				/*
				 * Now I have to Add Children with All Event Details
				 */
				query = "SELECT " + DBinfo.CLM_EVENT_NAME + ", SUM("
						+ DBinfo.CLM_SHARE_AMOUNT + ") ,"
						+ DBinfo.DB_TABLE_EVENTS + "." + DBinfo.EVENT_ID + ","
						+ DBinfo.DB_TABLE_EVENTS + "." + DBinfo.TRIP_ID;
				query += " FROM " + DBinfo.DB_TABLE_EVENTS + ","
						+ DBinfo.DB_TABLE_SHARES+","
								+ DBinfo.DB_TABLE_TRIPS;
				query += " WHERE (" + memberId + " = "
						+ DBinfo.CLM_PAIDBY_MEMBERID;
				query += " OR " + memberId + " = "
						+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
				query += " AND " + DBinfo.DB_TABLE_TRIPS + "."+DBinfo.TRIP_ID + " in " + tripIn;
				query += " AND " + DBinfo.DB_TABLE_SHARES + "."
						+ DBinfo.EVENT_ID + " =" + DBinfo.DB_TABLE_EVENTS + "."
						+ DBinfo.EVENT_ID;
				query+=" AND "+ DBinfo.DB_TABLE_EVENTS + "." + DBinfo.TRIP_ID
						+"="+ DBinfo.DB_TABLE_TRIPS + "." + DBinfo.TRIP_ID;
				query += " GROUP BY " + DBinfo.DB_TABLE_EVENTS + "."
						+ DBinfo.EVENT_ID;

				query += " ORDER BY " + Helper.formatSQLdate(DBinfo.CLM_TRIP_DATE)	+" DESC"
						+","+Helper.formatSQLdate(DBinfo.CLM_EVENT_DATE)	+" DESC";

				
				cursorChild = DB_CP.query(query);
				ListChild = new ArrayList<ListItemClass>();
				while (cursorChild.moveToNext()) {
					money = (float) cursorChild.getDouble(1);
					ItemsFormat = money < 0 ? ListItemClass.PAY_THEME
							: ListItemClass.RECIEVE_THEME;
					EventId=cursorChild.getInt(2);
					EventName=cursorChild.getString(0);
					TripName=Helper.getTitleFromId(cursorChild.getInt(3), Helper.TRIPS);
					if(prevTripName.equals(TripName))
						tmpTripName="\n  >>";
					else
						tmpTripName="\nTRIP:"+TripName+"\n  >>";
					newItem = new ListItemClass(EventId,EventName
							,TripName, String.format("%.2f", Math.abs(money)), ItemsFormat,money);
					if(money!=0)
					{
						ListChild.add(newItem);
						emailContent+=tmpTripName+EventName+":"+ String.format("%.2f",money);
					}
					prevTripName=TripName;
				}
				prevTripName="";
				listDataChild.put(memberId, ListChild);				
				emailContent+="\n\n";
			}
			break;

		case Helper.EVENTS:
			tripid = me.getIntExtra(DBinfo.TRIP_ID, -1);
			eventid = me.getIntExtra(DBinfo.EVENT_ID, -1);
			int[] eventids = me.getIntArrayExtra(DBinfo.EVENT_ID + "Array");
			String eventIn = "";
			if (tripid <= 0 || (eventid <= 0 && eventids.length <= 0)) {
				Toast.makeText(getApplicationContext(),
						"Invalid Event, Showing Full Hissab",
						Toast.LENGTH_SHORT).show();
				break;
			}
			if (eventid > 0) {
				eventIn = "(" + String.valueOf(eventid) + ")";
			} else if (eventids != null) {
				eventIn = "(";
				for (int i = 0; i < eventids.length; i++)
					eventIn += String.valueOf(eventids[i]) + ",";
				eventIn += "-1)";
			}
			if (tripid > 0)
				// Tripname=Helper.getTitleFromId(tripid, Helper.TRIPS);
				tripsName=Helper.getTitleFromId(tripid, Helper.TRIPS);
				// labels=new String[]{Tripname,Eventname,"All in the Trip"};

				query = "SELECT " + DBinfo.CLM_MEMBER_NAME + ", SUM("
						+ DBinfo.CLM_SHARE_AMOUNT + ") ," + DBinfo.MEMBER_ID;
			query += " FROM " + DBinfo.DB_TABLE_MEMBERS + ","
					+ DBinfo.DB_TABLE_SHARES;
			query += " WHERE (" + DBinfo.MEMBER_ID + " = "
					+ DBinfo.CLM_PAIDBY_MEMBERID;
			query += " OR " + DBinfo.MEMBER_ID + " = "
					+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
			query += " AND " + DBinfo.EVENT_ID + " in " + eventIn;
			query += " GROUP BY " + DBinfo.MEMBER_ID;
			query += " HAVING SUM("+ DBinfo.CLM_SHARE_AMOUNT + ")<>0";
			query += " ORDER BY " + "UPPER(" + DBinfo.CLM_MEMBER_NAME + ")" + " ASC";
			
			//TODO
			cursor = DB_CP.query(query);
			
			while (cursor.moveToNext()) {
				money = (float) cursor.getDouble(1);
				memberId = cursor.getInt(2);
				ItemsFormat = money < 0 ? ListItemClass.PAY_THEME: ListItemClass.RECIEVE_THEME;
				
				newItem = new ListItemClass(memberId, cursor.getString(0),
				String.format("%.2f", Math.abs(money)), ItemsFormat,money);
				listDataHeader.add(newItem);
				smscontent+=cursor.getString(0)+" ("+String.format("%.2f", money)+"),\n";
				smsaddress+=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.MEMBER_ID+"="+memberId).getStringItemAsked(ListItemClass.PHNO)+";";
				
				emailContent+="NAME:" +cursor.getString(0)+"("+String.format("%.2f", money)+")";		
				emailaddress+=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.MEMBER_ID+"="+memberId).getStringItemAsked(ListItemClass.EMAIL)+";";
				
				/*
				 * Now I have to Add Children with All Event Details
				 */

				query = "SELECT " + DBinfo.CLM_EVENT_NAME + ", SUM("
						+ DBinfo.CLM_SHARE_AMOUNT + ") ,"
						+ DBinfo.DB_TABLE_EVENTS + "." + DBinfo.EVENT_ID;
				query += " FROM " + DBinfo.DB_TABLE_EVENTS + ","
						+ DBinfo.DB_TABLE_SHARES;
				query += " WHERE (" + memberId + " = "
						+ DBinfo.CLM_PAIDBY_MEMBERID;
				query += " OR " + memberId + " = "
						+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
				query += " AND " + DBinfo.DB_TABLE_SHARES + "."
						+ DBinfo.EVENT_ID + " =" + DBinfo.DB_TABLE_EVENTS + "."
						+ DBinfo.EVENT_ID;
				query += " AND " + DBinfo.DB_TABLE_EVENTS + "."
						+ DBinfo.EVENT_ID + " in " + eventIn;
				query += " GROUP BY " + DBinfo.DB_TABLE_EVENTS + "."
						+ DBinfo.EVENT_ID;

				query += " ORDER BY " + DBinfo.DB_TABLE_EVENTS + "." 
						+ DBinfo.EVENT_ID	+" DESC";
				cursorChild = DB_CP.query(query);
				ListChild = new ArrayList<ListItemClass>();
				while (cursorChild.moveToNext()) {
					money = (float) cursorChild.getDouble(1);
					ItemsFormat = money < 0 ? ListItemClass.PAY_THEME
							: ListItemClass.RECIEVE_THEME;
					EventId=cursorChild.getInt(2);
					EventName=cursorChild.getString(0);
					TripName=Helper.getTitleFromId(tripid, Helper.TRIPS);
					if(prevTripName.equals(TripName))
						tmpTripName="\n  >>";
					else
						tmpTripName="\nTRIP:"+TripName+"\n  >>";
					newItem = new ListItemClass(EventId,EventName
							, TripName, String.format("%.2f", Math.abs(money)), ItemsFormat,money);
					if(money!=0)
					{
						ListChild.add(newItem);
						emailContent+=tmpTripName+EventName+":"+ String.format("%.2f",money);
					}
					prevTripName=TripName;
				}
				prevTripName="";
				listDataChild.put(memberId, ListChild);
				emailContent+="\n\n";
			}

			break;
		case -1:
			tripids = DB_CP.getAllTripIds();
			if(tripids.length<=0) 
			{
				Toast.makeText(getApplicationContext(), "No Trip Selected", Toast.LENGTH_LONG).show();
				finish();
				break;
			}
			
			tripsName=Helper.getTitleFromId(tripids[0], Helper.TRIPS)+" and "+(tripids.length-1)+" others";
			query = "SELECT " + DBinfo.CLM_MEMBER_NAME + ", SUM("
					+ DBinfo.CLM_SHARE_AMOUNT + ")," + DBinfo.MEMBER_ID;
			query += " FROM " + DBinfo.DB_TABLE_MEMBERS + ","
					+ DBinfo.DB_TABLE_SHARES + "," + DBinfo.DB_TABLE_EVENTS;
			
			query += " WHERE (" + DBinfo.MEMBER_ID + " = "
					+ DBinfo.CLM_PAIDBY_MEMBERID;
			query += " OR " + DBinfo.MEMBER_ID + " = "
					+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
			query += " AND " + DBinfo.DB_TABLE_SHARES + "." + DBinfo.EVENT_ID
					+ " =" + DBinfo.DB_TABLE_EVENTS + "." + DBinfo.EVENT_ID;
			query += " GROUP BY " + DBinfo.MEMBER_ID;
			query += " HAVING SUM("+ DBinfo.CLM_SHARE_AMOUNT + ")<>0";
			query += " ORDER BY " + "UPPER(" + DBinfo.CLM_MEMBER_NAME + ")" + " ASC";
			cursor = DB_CP.query(query);

			//TODO
			while (cursor.moveToNext()) {
				money = (float) cursor.getDouble(1);
				memberId = cursor.getInt(2);
				ItemsFormat = money < 0 ? ListItemClass.PAY_THEME
						: ListItemClass.RECIEVE_THEME;
				
				newItem = new ListItemClass(memberId, cursor.getString(0),String.format("%.2f", Math.abs(money)), ItemsFormat,money);
				
				listDataHeader.add(newItem);
				smscontent+=cursor.getString(0)+" ("+String.format("%.2f", money)+"),\n";
				smsaddress+=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.MEMBER_ID+"="+memberId).getStringItemAsked(ListItemClass.PHNO)+";";
				
				emailContent+="NAME:" +cursor.getString(0)+"("+String.format("%.2f", money)+")";		
				emailaddress+=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.MEMBER_ID+"="+memberId).getStringItemAsked(ListItemClass.EMAIL)+";";

				/*
				 * Now I have to Add Children with All Event Details
				 */

				query = "SELECT " + DBinfo.CLM_EVENT_NAME + ", SUM("
						+ DBinfo.CLM_SHARE_AMOUNT + ") ,"
						+ DBinfo.DB_TABLE_EVENTS + "." + DBinfo.EVENT_ID + ","
						+ DBinfo.DB_TABLE_EVENTS + "." + DBinfo.TRIP_ID;
				query += " FROM " + DBinfo.DB_TABLE_EVENTS + ","
						+ DBinfo.DB_TABLE_SHARES+ ","
								+ DBinfo.DB_TABLE_TRIPS;
				query += " WHERE (" + memberId + " = "
						+ DBinfo.CLM_PAIDBY_MEMBERID;
				query += " OR " + memberId + " = "
						+ DBinfo.CLM_SHAREDBY_MEMBERID + ")";
				query += " AND " + DBinfo.DB_TABLE_SHARES + "."
						+ DBinfo.EVENT_ID + " =" + DBinfo.DB_TABLE_EVENTS + "."
						+ DBinfo.EVENT_ID;
				query+="  AND "+ DBinfo.DB_TABLE_EVENTS + "." + DBinfo.TRIP_ID
						+"="+ DBinfo.DB_TABLE_TRIPS + "." + DBinfo.TRIP_ID;
				query += " GROUP BY " + DBinfo.DB_TABLE_EVENTS + "."
						+ DBinfo.EVENT_ID;

				query += " ORDER BY " + Helper.formatSQLdate(DBinfo.CLM_TRIP_DATE)	+" DESC"
						+","+Helper.formatSQLdate(DBinfo.CLM_EVENT_DATE)+" DESC";
				cursorChild = DB_CP.query(query);
				ListChild = new ArrayList<ListItemClass>();
				tripsName="";
				while (cursorChild.moveToNext()) {
					money = (float) cursorChild.getDouble(1);
					ItemsFormat = money < 0 ? ListItemClass.PAY_THEME
							: ListItemClass.RECIEVE_THEME;
					EventId=cursorChild.getInt(2);
					EventName=cursorChild.getString(0);
					TripName=Helper.getTitleFromId(cursorChild.getInt(3), Helper.TRIPS);
					if(prevTripName.equals(TripName))
						tmpTripName="\n  >>";
					else
						{
							tmpTripName="\nTRIP:"+TripName+"\n  >>";
						}
					newItem = new ListItemClass(EventId,EventName
							, TripName, String.format("%.2f", Math.abs(money)), ItemsFormat,money);
					
					if(money!=0)
					{
						ListChild.add(newItem);
						emailContent+=tmpTripName+EventName+":"+ String.format("%.2f",money);
					}
					prevTripName=TripName;				
				}
				prevTripName="";
				listDataChild.put(memberId, ListChild);
				emailContent+="\n\n";
			}
			break;
		}
	}

	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
			int childPosition, long id) {
		ListItemClass lic=(ListItemClass) aa.getChild(groupPosition, childPosition);
		Intent intent = new Intent(this, ViewEventActivity.class);
		intent.putExtra("MODE", DBinfo.MODE_VIEW);
		intent.putExtra("CALLER", "HISAAB");
		intent.putExtra(DBinfo.CLM_EVENT_NAME, lic.toString());
		intent.putExtra(DBinfo.EVENT_ID, lic.toInt());
		intent.putExtra(DBinfo.TRIP_ID, lic.getIntItemAsked(ListItemClass.TRIPID_FOREVENT));
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		return true;
	}

}
