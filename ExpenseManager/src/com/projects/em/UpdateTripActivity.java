package com.projects.em;

import java.util.Calendar;

import com.projects.em.DB_CP.DBinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class UpdateTripActivity extends Activity implements
		OnClickListener, OnFocusChangeListener {

	private static EditText txttripname;
	private static EditText txttripdate;
	private static EditText txttripplace;
	private static RelativeLayout lytChooseMember;
	// private static ImageView imageView;
	private static int TripId;
	private static int groupId;
	private static int Mode;
	private int[] SelectedContacts;
	public static RadioButton colorButtons[];
	public static RadioGroup ColorOptions;
	//private static TripTheme selectedTheme = new TripTheme(0);

	// private static final int TAKE_PICTURE = 1;
	// private static File TripPicFile;

	// TODO On Create here

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK)
			if (requestCode == 1)// Asking Contacts
			{
				SelectedContacts = data.getIntArrayExtra("result");
				showmembers();
			}
		// Toast.makeText(getApplicationContext(), String.valueOf(values[0]),
		// Toast.LENGTH_SHORT).show();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View V) {
		switch (V.getId()) {
		case R.id.addtrip_txtdate:
			Helper.getInputDialog(UpdateTripActivity.this, "Date",
					"Select date", PromptDialog.DATE_DLG,txttripdate.getText().toString(),txttripdate,txttripplace);
			break;
		case R.id.lyt_choosemember:
			/*
			 * dlgMemberList=getInputDialog(AddTripActivity.this,
			 * "Select members", "Check members in trip", 3);
			 * dlgMemberList.show();
			 */
			Intent intent = new Intent(this, ContactsActivity.class);// Get
																		// contacts
																		// Id
																		// List
			//intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("MODE", ContactsActivity.MODE_GETMEMBERIDS);// Just selected member ids
			intent.putExtra("source", "UpdateTripActivity");
			intent.putExtra("values", SelectedContacts);
			intent.putExtra(DBinfo.TRIP_ID, TripId);
			intent.putExtra(DBinfo.CLM_TRIP_GROUPID, groupId);
			startActivityForResult(intent, 1);
			break;
	/*	case R.id.imgblue:
		case R.id.imgbrown:
		case R.id.imggreen:
		case R.id.imgorange:
		case R.id.imgpink:
		case R.id.imgpurple:
		case R.id.imgred:
		case R.id.imgskyblue:
		case R.id.imgyellow:
			selectedTheme.setThemeForOptionId(V.getId());
			break;
*/
		}
	}

	// TODO onCLick Events For All Views

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatetrip);
		txttripname = (EditText) findViewById(R.id.addtrip_txttripname);
		txttripplace = (EditText) findViewById(R.id.addtrip_txtplace);
		txttripdate = (EditText) findViewById(R.id.addtrip_txtdate);
		lytChooseMember = (RelativeLayout) findViewById(R.id.lyt_choosemember);
		txttripdate.setOnClickListener(this);
		lytChooseMember.setOnClickListener(this);
		txttripdate.setOnFocusChangeListener(this);
		// lstMembers=(ListViewCustom)findViewById(android.R.id.list);

		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		
		// LoadSelectedMembers(TripId);
		SelectedContacts=Helper.getTripMembers(TripId);
		ShowTripDetails(TripId);
		showmembers();
		/*colorButtons = new RadioButton[9];
		colorButtons[0] = (RadioButton) findViewById(R.id.imgblue);
		colorButtons[1] = (RadioButton) findViewById(R.id.imgbrown);
		colorButtons[2] = (RadioButton) findViewById(R.id.imggreen);
		colorButtons[3] = (RadioButton) findViewById(R.id.imgorange);
		colorButtons[4] = (RadioButton) findViewById(R.id.imgpink);
		colorButtons[5] = (RadioButton) findViewById(R.id.imgpurple);
		colorButtons[6] = (RadioButton) findViewById(R.id.imgred);
		colorButtons[7] = (RadioButton) findViewById(R.id.imgskyblue);
		colorButtons[8] = (RadioButton) findViewById(R.id.imgyellow);
		for (int i = 0; i < colorButtons.length; i++) {
			colorButtons[i].setOnClickListener(this);
		}*/
	}

	// TODO Menu Options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_trip, menu);
		return true;
	}

	@Override
	public void onFocusChange(View view, boolean isOnFous) {
		if (view.getId() == R.id.addtrip_txtdate && isOnFous == true) {
			onClick(view);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		case R.id.action_addtripdone:
			String TripName = txttripname.getText().toString();
			
			if (TextUtils.isEmpty(TripName)|| TextUtils.isEmpty(txttripplace.getText().toString())|| TextUtils.isEmpty(txttripdate.getText().toString())) 
			{
				Toast.makeText(getApplicationContext(),
						"Fields Can't be Empty", Toast.LENGTH_SHORT).show();
			}
			else if (DB_CP.getListItemClass(DB_CP.TRIP_URI, DBinfo.CLM_TRIP_NAME+ "='" +  txttripname.getText().toString() + "' and not("
					+DBinfo.TRIP_ID+"="+TripId+")") != null)
			{
				Toast.makeText(getApplicationContext(), txttripname.getText().toString() + ":Trip Name Already Exists ",	Toast.LENGTH_SHORT).show();
			}
			/*else if (SelectedContacts.length <=0)// aa.getAllChecked().size()<=0)
			{
				Toast.makeText(getApplicationContext(),"Please add and select At least one Contact",Toast.LENGTH_SHORT).show();		
			}*/
			else switch(Mode)
			{
			case DBinfo.MODE_EDIT:
			case DBinfo.MODE_VIEW:
				Helper.UpdateTrip(getContentResolver(), TripId, txttripname.getText().toString(),txttripdate.getText().toString(), txttripplace
								.getText().toString(), SelectedContacts,0);
				Toast.makeText(	getApplicationContext(),"Trip " + txttripname.getText().toString() + " Updated",Toast.LENGTH_SHORT).show();
				/*
				 * Intent intent = new Intent(this, HomeActivity.class);
				 * intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				 * startActivity(intent);
				 */
				finish();
				break;
			case DBinfo.MODE_ADD_Prefilled:
			case DBinfo.MODE_ADD:
				Helper.AddTrip(getContentResolver(),TripName, txttripdate.getText().toString(),txttripplace.getText().toString(),
							SelectedContacts, 0,groupId);
				finish();
				/*int TripId = Helper.getIntIfParsable(inserted_id);
				if (TripId > -1) 
				{
					Intent intent = new Intent(this, EventActivity.class);
					intent.putExtra(DBinfo.CLM_TRIP_NAME, TripName);
					intent.putExtra(DBinfo.TRIP_ID, TripId);
					// Toast.makeText(getApplicationContext(), "Trip "
					// +txttripname.getText().toString() +
					// " Added, \nAdd Events to it",
					// Toast.LENGTH_SHORT).show();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
					finish();
				}*/
			break;
		default:
			//Mode Switch Block
			Toast.makeText(getApplicationContext(),"No Mode Selected " + Mode,	Toast.LENGTH_SHORT).show();
		}
	default:
		//Item Clicked Switch Block
	}
		return super.onOptionsItemSelected(item);
}

	@Override
	protected void onPause() {
		super.onPause();
		//this.getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

	}

	// TODO OnResume here
	@Override
	protected void onResume() {
		super.onResume();
		Intent me = this.getIntent();
		TripId = me.getIntExtra(DBinfo.TRIP_ID, -1);
		Mode = me.getIntExtra("MODE", -1);
		groupId=me.getIntExtra(DBinfo.CLM_TRIP_GROUPID, 1);
	}

	private void showmembers() {
		TextView lblTripMembers = (TextView) findViewById(R.id.tripmember);
		String names = "";
		boolean flag = false;
		// Toast.makeText(getApplicationContext(),
		// "("+SelectedContacts.length+")", Toast.LENGTH_SHORT).show();
		for (int i = 0; i < SelectedContacts.length; i++) {
			if (flag == true)
				names += "\n";
			names += Helper.getTitleFromId(SelectedContacts[i], Helper.MEMBERS);
			flag = true;
		}
		lblTripMembers.setText(names);
	}

	// TODO Show Trip Details
	private void ShowTripDetails(int tripid) {
		switch (Mode) {
		case DBinfo.MODE_ADD:// add
			// Do Something to Add the values
			Calendar cal=Calendar.getInstance();
			txttripdate.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
			// ActAddUpdate.setTitle("Done");
			this.setTitle("Add Trip");
			break;
		case DBinfo.MODE_VIEW:// view
		case DBinfo.MODE_EDIT:// Edit
			// ActAddUpdate.setTitle("Update");

			// String[] projection = new String[]{
			// DBinfo.TRIP_ID,DBinfo.CLM_TRIP_NAME,DBinfo.CLM_TRIP_DATE,DBinfo.CLM_TRIP_PLACE};
			String where = DBinfo.TRIP_ID + "=" + Integer.toString(tripid);
			ListItemClass item = DB_CP.getListItemClass(DB_CP.TRIP_URI, where);
			txttripname.setText(item
					.getStringItemAsked(ListItemClass.MAIN_ITEM));
			txttripdate.setText(item
					.getStringItemAsked(ListItemClass.DATE));
			txttripplace.setText(item
					.getStringItemAsked(ListItemClass.PLACE));
			showmembers();
			this.setTitle("Update Trip");
			//selectedTheme.setTheme(item.getIntItemAsked(ListItemClass.THEME));
			// Do Something to Update the values
			//colorButtons[selectedTheme.getTheme()].setChecked(true);
			break;
		default:
		}
	}

	public boolean UpdateDate(DatePicker DP) {
		String DPDate = Integer.toString(DP.getDayOfMonth()) + "/"
				+ Integer.toString(DP.getMonth() + 1) + "/"
				+ Integer.toString(DP.getYear());
		txttripdate.setText(DPDate);
		return true;
	}
}
