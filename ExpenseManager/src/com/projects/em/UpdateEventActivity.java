package com.projects.em;

import java.util.ArrayList;
import java.util.Calendar;

import com.projects.em.R;
import com.projects.em.DB_CP.DBinfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateEventActivity extends Activity implements OnClickListener,OnFocusChangeListener {
	private static Button btnPaidBy;
	private static Button btnSharedBy;
	
	private static boolean IsUpdating = false;
	//private static String TripName;
	private static int TripId;
	private static String EventName;
	private static int EventId;
	private int Mode = DBinfo.MODE_ADD;
	private static ArrayList<ListItemClass> members;
	private static Intent me;
	private float[] PaidAmounts, SharedAmounts;
	private float[] DBPaidAmounts, DBSharedAmounts;
	private static EditText txteventname,txttotalbill,txtdate,txtplace,txtdesc;

	// Base Functions
	@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_addevent);
			members = new ArrayList<ListItemClass>();

			txteventname = (EditText) findViewById(R.id.addevent_txteventname);
			txttotalbill = (EditText) findViewById(R.id.addevent_txtpaid);
			txtdate = (EditText) findViewById(R.id.addevent_txtdate);
			txtplace = (EditText) findViewById(R.id.addevent_txtplace);
			txtdesc = (EditText) findViewById(R.id.addevent_txtdesc);
			btnPaidBy = (Button) findViewById(R.id.btn_paidby);
			btnSharedBy = (Button) findViewById(R.id.btn_sharedby);
			btnPaidBy.setOnClickListener(this);
			btnSharedBy.setOnClickListener(this);
			txtdate.setOnClickListener(this);
			txtdate.setOnFocusChangeListener(this);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			
			me = this.getIntent();
			Mode = me.getIntExtra("MODE", -1);
			//TripName = me.getStringExtra(DBinfo.CLM_TRIP_NAME);
			TripId = me.getIntExtra(DBinfo.TRIP_ID, -1);

			members.clear();
			members = GetMembers(TripId);
			Calendar cal=Calendar.getInstance();
			ListItemClass trip=DB_CP.getListItemClass(DB_CP.TRIP_URI,DBinfo.TRIP_ID+"="+TripId);
			switch (Mode) {
			case DBinfo.MODE_ADD_Prefilled:
				IsUpdating = false;
				
				txtdate.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
				txtplace.setText(trip.getStringItemAsked(ListItemClass.PLACE));
				txteventname.setText(me.getStringExtra(DBinfo.CLM_EVENT_NAME));
				btnSharedBy.setText(me.getStringExtra("Shared_Label"));
				btnPaidBy.setText(me.getStringExtra("Paid_Label"));
				this.setTitle("Add Payment");
				break;
			case DBinfo.MODE_ADD:// add
				IsUpdating = false;		 
				txtdate.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
				txtplace.setText(trip.getStringItemAsked(ListItemClass.PLACE));
				this.setTitle("Add Event");
				break;
			case DBinfo.MODE_VIEW:// view
			case DBinfo.MODE_EDIT:// Edit
				//txtdate.setText(trip.getStringItemAsked(ListItemClass.DATE));
				//txtplace.setText(trip.getStringItemAsked(ListItemClass.PLACE));
				EventName = me.getStringExtra(DBinfo.CLM_EVENT_NAME);
				EventId = me.getIntExtra(DBinfo.EVENT_ID, -1);
				DBPaidAmounts = Helper.AmountsforEvent(EventId, 1, members);
				DBSharedAmounts = Helper.AmountsforEvent(EventId, 2, members);
				IsUpdating = true;
				this.setTitle("Update Event");
				ShowEvent();
				break;
			default:
			}
			
		}
	
	@Override
	protected void onResume() {	
		super.onResume();
	}
	
	private void validateData()
	{
		float totalBill = Helper.getFloatIfParsable(txttotalbill.getText().toString());
		if (Math.abs(Helper.getSharesSum(PaidAmounts) - totalBill) > 1) 
			btnPaidBy.setBackgroundResource(R.drawable.btnred);
		else
			btnPaidBy.setBackgroundResource(R.drawable.btngreen);
		
		if (Math.abs(Helper.getSharesSum(SharedAmounts) - totalBill) > 1)
			btnSharedBy.setBackgroundResource(R.drawable.btnred);
		else
			btnSharedBy.setBackgroundResource(R.drawable.btngreen);
	}
	
	private ArrayList<ListItemClass> GetMembers(int TripId) {
		// TODO Auto-generated method stub
		ArrayList<ListItemClass> memberList = new ArrayList<ListItemClass>();
		String sql = "select " + DBinfo.DB_TABLE_MEMBERS + "."
				+ DBinfo.MEMBER_ID + " , " + DBinfo.CLM_MEMBER_NAME;
		sql += " from " + DBinfo.DB_TABLE_MEMBERS + ","
				+ DBinfo.DB_TABLE_TRIPMEMBERS;
		sql += " where " + DBinfo.DB_TABLE_MEMBERS + "." + DBinfo.MEMBER_ID;
		sql += " = " + DBinfo.DB_TABLE_TRIPMEMBERS + "." + DBinfo.MEMBER_ID;
		sql += " and " + DBinfo.DB_TABLE_TRIPMEMBERS + "." + DBinfo.TRIP_ID;
		sql += " = " + Integer.toString(TripId);
		Cursor cursor = DB_CP.query(sql);
		while (cursor.moveToNext()) {
			ListItemClass member = new ListItemClass(cursor.getInt(0),cursor.getString(1),"","","",0,"","");
			memberList.add(member);
		}
		return memberList;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case 1:
				PaidAmounts = data.getFloatArrayExtra("Floats");
				if(TextUtils.isEmpty(txttotalbill.getText().toString()))
					txttotalbill.setText(String.valueOf(data.getFloatExtra("Total", 0)));
				break;
			case 2:
				SharedAmounts = data.getFloatArrayExtra("Floats");
				if(TextUtils.isEmpty(txttotalbill.getText().toString()))
					txttotalbill.setText(String.valueOf(data.getFloatExtra("Total", 0)));
			}	
			validateData();
		}
		// Toast.makeText(getApplicationContext(), String.valueOf(values[0]),
		// Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onFocusChange(View view, boolean isOnFous) {
		if (view.getId() == R.id.addevent_txtdate && isOnFous == true) {
			onClick(view);
		}
	}
	// TODO On click Event
	@Override
	public void onClick(View V) {
		String str = txttotalbill.getText().toString();
		// float amount=str.isEmpty()?0:Float.parseFloat(str);
		float amount = Helper.getFloatIfParsable(str);
		Intent intent = new Intent(this, SharesActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("Amount", amount);
		intent.putExtra(DBinfo.TRIP_ID, TripId);
		intent.putExtra(DBinfo.EVENT_ID, EventId);
		intent.putExtra(DBinfo.CLM_EVENT_NAME, txteventname.getText()
				.toString());
		intent.putExtra("Mode", Mode);
		switch (V.getId()) 
		{
		case R.id.addevent_txtdate:
			Helper.getInputDialog(this, "Date",	"Select date", PromptDialog.DATE_DLG,txtdate.getText().toString(),txtdate,txtplace);

			break;
		case R.id.btn_paidby:
			intent.putExtra("type", 1);
			if (PaidAmounts != null)
				intent.putExtra("AllValues", PaidAmounts);
			else if (DBPaidAmounts != null)
				intent.putExtra("AllValues", DBPaidAmounts);
			startActivityForResult(intent, 1);
			break;
		case R.id.btn_sharedby:
			intent.putExtra("type", 2);
			if (SharedAmounts != null) 
				intent.putExtra("AllValues", SharedAmounts);
			else if (DBSharedAmounts != null)
				intent.putExtra("AllValues", DBSharedAmounts);
			startActivityForResult(intent, 2);
			
			/*else if (DBSharedAmounts != null) {
				startActivityForResult(intent, 2);
			else
				Toast.makeText(getApplicationContext(),
						"Enter the PaidBy Values First", Toast.LENGTH_SHORT)
						.show();
			*/
			break;
		
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.addevent, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_EventHisaab:
			intent = new Intent(this, HisaabActivity.class);
			intent.putExtra("TYPE", Helper.EVENTS);
			intent.putExtra(DBinfo.TRIP_ID, TripId);
			intent.putExtra(DBinfo.EVENT_ID, EventId);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
					| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.action_EventDone:
			
			float totalBill = Helper.getFloatIfParsable(txttotalbill.getText().toString());
			EventName = txteventname.getText().toString();
			if (PaidAmounts == null && DBPaidAmounts != null)
					PaidAmounts = DBPaidAmounts;
			if (SharedAmounts == null && DBSharedAmounts != null)
				SharedAmounts = DBSharedAmounts;
			validateData();
			if (TextUtils.isEmpty(EventName)|| TextUtils.isEmpty(txttotalbill.getText().toString())) 
			{
				Toast.makeText(getApplicationContext(),"Fields Can't be Empty", Toast.LENGTH_SHORT).show();
				return super.onOptionsItemSelected(item);
			}
			else if (DB_CP.getListItemClass(DB_CP.EVENT_URI, DBinfo.CLM_EVENT_NAME + "='" + EventName + "' and "	+ DBinfo.TRIP_ID + "=" + TripId+ " and not("
					+DBinfo.EVENT_ID+"="+EventId+")") != null)
			{
				if (TripId < 0)
					Toast.makeText(getApplicationContext(),	EventName + ":An Unepected no TripAssigned error",Toast.LENGTH_SHORT).show();
				Toast.makeText(getApplicationContext(),	EventName + ":Event Name Already Exists in this trip",Toast.LENGTH_SHORT).show();
				return super.onOptionsItemSelected(item);
			}
			else if (PaidAmounts == null) {
					Toast.makeText(getApplicationContext(),	"Please enter Paids details", Toast.LENGTH_SHORT).show();
					return super.onOptionsItemSelected(item);
			}
			else if (SharedAmounts == null) {
					Toast.makeText(getApplicationContext(),"Please enter Shared details", Toast.LENGTH_SHORT).show();
					return super.onOptionsItemSelected(item);
			}
			else if (Math.abs(Helper.getSharesSum(PaidAmounts) - totalBill) > 1)
			{
				Toast.makeText(getApplicationContext(),	"Total of Paid amount is not adding up",Toast.LENGTH_SHORT).show();
				return super.onOptionsItemSelected(item);
				
			}
			else if (Math.abs(Helper.getSharesSum(SharedAmounts) - totalBill) > 1) {
				Toast.makeText(getApplicationContext(),	"Total of Shared amount is not adding up",	Toast.LENGTH_SHORT).show();
				return super.onOptionsItemSelected(item);
			}
					
			//Test if updating or adding new
			else if (IsUpdating == true) {
				Helper.UpdateEvent(getContentResolver(), EventId, txteventname.getText().toString(), txttotalbill.getText().toString(),
						txtdate.getText().toString(),txtplace.getText().toString(),txtdesc.getText().toString(),"",
						members, PaidAmounts, SharedAmounts);
				Toast.makeText(getApplicationContext(),EventName + ":Event Updated ", Toast.LENGTH_SHORT).show();
				finish();
			} 
		 else {
				Helper.AddEvent(getContentResolver(), txteventname.getText().toString(), txttotalbill.getText().toString(), TripId,
						txtdate.getText().toString(),txtplace.getText().toString(),txtdesc.getText().toString(),"",
						members, PaidAmounts, SharedAmounts);
				Toast.makeText(getApplicationContext(),	EventName + ":Event Added ", Toast.LENGTH_SHORT).show();
				finish();
			}
			/*
			 * intent = new Intent(this, EventActivity.class);
			 * intent.putExtra(DBinfo.CLM_TRIP_NAME, TripName);
			 * intent.putExtra(DBinfo.TRIP_ID, TripId);
			 * //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			 * intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			 * startActivity(intent);
			 */
			default:
		}
		return super.onOptionsItemSelected(item);
	}

	// TODO On Pause Here
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		switch (me.getIntExtra("MODE", -1)) {
		case 1:
		case 2:
			menu.getItem(0).setVisible(true);
			break;
		case 0:// add
		default:
			menu.getItem(0).setVisible(false);
			break;
		}
		return true;
	}


	private void ShowEvent() {
		ListItemClass item = DB_CP.getListItemClass(DB_CP.EVENT_URI,DBinfo.EVENT_ID + "=" + EventId);
		txteventname.setText(item.getStringItemAsked(ListItemClass.MAIN_ITEM));
		double bill = item.getAmount();
		txttotalbill.setText(String.valueOf(bill));
		txtdate.setText(item.getStringItemAsked(ListItemClass.DATE));
		txtplace.setText(item.getStringItemAsked(ListItemClass.PLACE));
		txtdesc.setText(item.getStringItemAsked(ListItemClass.DESC));
	}
}