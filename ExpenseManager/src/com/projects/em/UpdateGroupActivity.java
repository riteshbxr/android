package com.projects.em;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.em.DB_CP.DBinfo;


public class UpdateGroupActivity extends Activity implements
		OnClickListener {

	private static EditText txtgroupname;
	private static EditText txtgroupdesc;
	// private static ImageView imageView;
	private static int groupId;
	private static int Mode;
	private int[] SelectedContacts;
	public static RadioButton colorButtons[];
	public static RadioGroup ColorOptions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updategroup);
		txtgroupname = (EditText) findViewById(R.id.addtrip_txtgroupname);
		txtgroupdesc = (EditText) findViewById(R.id.addtrip_txtgroupdesc);
		//lytChooseMember = (RelativeLayout) findViewById(R.id.lyt_choosegroupmember);
	//	lytChooseMember.setOnClickListener(this);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// LoadSelectedMembers(TripId);
		//SelectedContacts=Helper.getGroupMembers(GroupId);
		
		//showmembers();
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
		getMenuInflater().inflate(R.menu.add_group, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		case R.id.action_addgroupdone:
			String groupName = txtgroupname.getText().toString();
			
			if (TextUtils.isEmpty(groupName)) 
			{
				Toast.makeText(getApplicationContext(),
						"Fields Can't be Empty", Toast.LENGTH_SHORT).show();
			}
			else if (DB_CP.getListItemClass(DB_CP.GROUP_URI, DBinfo.CLM_GROUP_NAME+ "='" +  txtgroupname.getText().toString() + "' and not("
					+DBinfo.TRIP_ID+"="+groupId+")") != null)
			{
				Toast.makeText(getApplicationContext(), txtgroupname.getText().toString() + ":GROUP Name Already Exists ",	Toast.LENGTH_SHORT).show();
			}
			/*else if (SelectedContacts.length <=0)// aa.getAllChecked().size()<=0)
			{
				Toast.makeText(getApplicationContext(),"Please add and select At least one Contact",Toast.LENGTH_SHORT).show();		
			}*/
			else switch(Mode)
			{
			case DBinfo.MODE_EDIT:
			case DBinfo.MODE_VIEW:
				Helper.UpdateGroup(getContentResolver(), groupId, txtgroupname.getText().toString(),0,0,
								txtgroupdesc.getText().toString(), SelectedContacts);
				Toast.makeText(	getApplicationContext(),"Group" + txtgroupname.getText().toString() + " Updated",Toast.LENGTH_SHORT).show();
				/*
				 * Intent intent = new Intent(this, HomeActivity.class);
				 * intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				 * startActivity(intent);
				 */
				finish();
				break;
			case DBinfo.MODE_ADD_Prefilled:
			case DBinfo.MODE_ADD:
				Helper.AddGroup(getContentResolver(),groupName, 0,0,txtgroupdesc.getText().toString(),
							SelectedContacts);
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
		groupId = me.getIntExtra(DBinfo.CLM_TRIP_GROUPID, 1);
		Mode = me.getIntExtra("MODE", -1);
		showGroupDetails(groupId);
	}

	private void showmembers(int abc) {
		TextView lblGroupMembers = (TextView) findViewById(R.id.groupmember);
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
		lblGroupMembers.setText(names);
	}

	// TODO Show Trip Details
	private void showGroupDetails(int groupid) {
		switch (Mode) {
		case DBinfo.MODE_ADD:// add
			this.setTitle("Add Group");
			break;
		case DBinfo.MODE_VIEW:// view
		case DBinfo.MODE_EDIT:// Edit
			String where = DBinfo.GROUP_ID+ "=" + Integer.toString(groupid);
			ListItemClass item = DB_CP.getListItemClass(DB_CP.GROUP_URI, where);
			txtgroupname.setText(item
					.getStringItemAsked(ListItemClass.MAIN_ITEM));
			txtgroupdesc.setText(item
					.getStringItemAsked(ListItemClass.DESC));
			//showmembers();
			this.setTitle("Update Trip");
			break;
		default:
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK)
			if (requestCode == 1)// Asking Contacts
			{
				SelectedContacts = data.getIntArrayExtra("result");
				//showmembers();
			}
		// Toast.makeText(getApplicationContext(), String.valueOf(values[0]),
		// Toast.LENGTH_SHORT).show();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View V) {
		switch (V.getId()) {

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
			intent.putExtra("source", "AddGroupActivity");
			intent.putExtra("values", SelectedContacts);
			intent.putExtra(DBinfo.GROUP_ID, groupId);
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

}
