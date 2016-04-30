package com.projects.em;

import java.util.ArrayList;
import java.util.List;

import com.projects.em.DB_CP.DBinfo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.*;

public class MemberActivity extends Activity implements OnItemSelectedListener{

	private static EditText txtmembername;
	private static EditText txtmemberph;
	private static EditText txtmemberemail;
	private static EditText txtmemberuname;
	private static int MemberId;
	private static int Mode;
	private static final int CONTACT_PICKER=1;
	private static Spinner spnrPh, spnrEmail;
	private static RelativeLayout lytspnrPh, lytspnrEmail;
	private static List<String> lstPh,lstEmail ;
	private static ArrayAdapter<String> aaEmail,aaPh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member);
		txtmembername = (EditText) findViewById(R.id.member_name);
		txtmemberph = (EditText) findViewById(R.id.member_phone);
		txtmemberemail = (EditText) findViewById(R.id.member_email);
		txtmemberuname = (EditText) findViewById(R.id.member_uname);
		
		Intent me = this.getIntent();
		MemberId = me.getIntExtra(DBinfo.MEMBER_ID, -1);
		Mode = me.getIntExtra("MODE", -1);
		if(Mode>DBinfo.MODE_ADD)
			ShowMemberDetails(MemberId);		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		me = this.getIntent();
		spnrPh = (Spinner) findViewById(R.id.phoneSpinner);
		spnrEmail = (Spinner) findViewById(R.id.emailSpinner);
		lytspnrPh = (RelativeLayout) findViewById(R.id.lyt_choosephone);
		lytspnrEmail = (RelativeLayout) findViewById(R.id.lyt_chooseemail);
		spnrPh.setOnItemSelectedListener(this);
		spnrEmail.setOnItemSelectedListener(this);
        lstPh = new ArrayList<String>();
        lstEmail = new ArrayList<String>();
        aaPh=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstPh);
        //aaPh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrPh.setAdapter(aaPh);
        aaEmail=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstEmail);
        //aaEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrEmail.setAdapter(aaEmail);
		
		// LoadSelectedMembers(TripId);
	}
	// TODO OnResume here
	@Override
	protected void onResume() {
		super.onResume();
		/*aaPh.notifyDataSetChanged();
        aaEmail.notifyDataSetChanged();*/
	}

	// TODO Menu Options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.member, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		case R.id.action_updatemember:
			if(TextUtils.isEmpty(txtmembername.getText()))
				Toast.makeText(this.getApplicationContext(),"Name field can't be left blank",Toast.LENGTH_LONG).show();
			else if(TextUtils.isEmpty(txtmemberuname.getText()))
					Toast.makeText(this.getApplicationContext(),"UName field can't be left blank. Enter a unique name for per member",Toast.LENGTH_LONG).show();
			else if (DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.CLM_MEMBER_UNAME+ "='" +  txtmemberuname.getText().toString() + "' and not("
					+DBinfo.MEMBER_ID+"="+MemberId+")") != null)
			{
				Toast.makeText(getApplicationContext(), txtmemberuname.getText().toString() + ":Member UName Already Exists ",	Toast.LENGTH_SHORT).show();
			}
				
			else
			{
				Intent result=new Intent();
				result.putExtra(DBinfo.CLM_MEMBER_NAME, txtmembername.getText().toString());
				result.putExtra(DBinfo.CLM_MEMBER_PHNO, txtmemberph.getText().toString());
				result.putExtra(DBinfo.CLM_MEMBER_EMAIL, txtmemberemail.getText().toString());
				result.putExtra(DBinfo.CLM_MEMBER_UNAME, txtmemberuname.getText().toString());
				result.putExtra(DBinfo.MEMBER_ID, MemberId);
				result.putExtra("Mode", Mode);
				setResult(RESULT_OK, result);
				finish();
			}
		return true;
		case R.id.action_import:
			Intent contacts=new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(contacts, CONTACT_PICKER);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		//this.getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

	}


	
	// TODO Show Trip Details
		private void ShowMemberDetails(int memberid) {
			switch (Mode) {
			case DBinfo.MODE_ADD:// add
				break;
			case DBinfo.MODE_VIEW:// view
			case DBinfo.MODE_EDIT:// Edit
				String where = DBinfo.MEMBER_ID + "=" + Integer.toString(memberid);
				ListItemClass item = DB_CP.getListItemClass(DB_CP.MEMBER_URI, where);
				txtmembername.setText(item.getStringItemAsked(ListItemClass.MAIN_ITEM));
				txtmemberph.setText(item.getStringItemAsked(ListItemClass.PHNO));
				txtmemberemail.setText(item.getStringItemAsked(ListItemClass.EMAIL));
				txtmemberuname.setText(item.getStringItemAsked(ListItemClass.UNAME));
				break;
			default:
			}
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub

			lstPh.clear();
			lstEmail.clear();
			aaPh.clear();
			aaEmail.clear();

			String name="",phone="",email="";
			if (resultCode == RESULT_OK)
			{
				//Setting The Values
				switch (requestCode)
				{
				case CONTACT_PICKER:
					lytspnrEmail.setVisibility(View.VISIBLE);
					lytspnrPh.setVisibility(View.VISIBLE);
					try
					{
						Uri result=data.getData();
						String id=result.getLastPathSegment();
						ContentResolver cr = getContentResolver();
						Cursor cursor=cr.query(result,null,ContactsContract.Contacts._ID+" = "+id,null,null);
						if(cursor.moveToFirst())
							{
							name=cursor.getString(cursor.getColumnIndex(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB ?Contacts.DISPLAY_NAME_PRIMARY :Contacts.DISPLAY_NAME));
							if(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))>0)
							{
								 Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                         new String[]{id}, null);
				                  while (pCur.moveToNext()) 
				                  {
				                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				                        lstPh.add(phone);
				                  }
				                  pCur.close();
							}
							Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);
		                    while (emailCur.moveToNext()) {
		                        email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
		                        //String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));	                      
		                        lstEmail.add(email);
		                    }
		                    emailCur.close();
							}
						txtmembername.setText(name);
				        aaPh.notifyDataSetChanged();
				        aaEmail.notifyDataSetChanged();
				
						}
					catch(Exception e)
					{
						Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					}
					break;
				} 
			}
			
		}

		  public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) 
		  {
		      switch(parent.getId())
		      {
		      case R.id.phoneSpinner:
		    	  txtmemberph.setText(lstPh.get(pos).toString());
		    	  break;
		      case R.id.emailSpinner:
		    	 txtmemberemail.setText(lstEmail.get(pos).toString());
		    	  break;
		    	  default:
		    		  txtmemberph.setText(lstPh.get(pos).toString());
		      }
		        
			  // An item was selected. You can retrieve the selected item using
		        // parent.getItemAtPosition(pos)
		    }

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}

}
