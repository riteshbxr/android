package com.projects.em;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.Toast;

import com.projects.em.DB_CP.DBinfo;
import com.projects.em.ListItemAdapter.OnLVCCheckChangedListener;

public class ContactsActivity extends Activity implements
		OnClickListener, LoaderCallbacks<Cursor>, OnLVCCheckChangedListener,OnItemClickListener{
	private ListViewCustom lstMembers;
	private EditText txtSearch;
	private ListItemAdapter aa;
	private Intent me;
	private List<ListItemClass> memberList;
	protected ActionMode mActionMode;
	private int[] SelectedContacts;
	private int TripId = -1;
	private static final int CONTACT_ADD=2,CONTACT_UPDATE=3;
	//enum(CONTACT_PICKER,CONTACT_ADD,CONTACT_UPDATE);
	private String where="1";
	public static final int MODE_GETMEMBERIDS=1;
	private UndoOptions setundo;
	private boolean firstload=true;
	private List<Integer> selectedIds;
		private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() { // Called
																					// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.contacts_Context_delete:
				/*String msg = Helper.DeleteSelectedItems(getContentResolver(),
						aa.getAllChecked(), memberList, Helper.MEMBERS);
				if(msg!="")
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				lstMembers.selectAllorNone(false);
				// mode.finish();// Action picked, so close the CAB
				ReloadList();*/
				List<Integer> positions=new ArrayList<Integer>(aa.getAllChecked());
				mode.finish();
				setundo=new UndoOptions(lstMembers,positions, Helper.getUndoCallback(getContentResolver(), new ArrayList<ListItemClass>(memberList), aa,Helper.MEMBERS));
				return true;
			case R.id.contacts_Context_SelectAll:
				boolean finishmode = Helper.SelectAll(item, aa);
				if (finishmode)
					mode.finish();
				return finishmode;
			default:
				return false;
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.contacts_contextmenu, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			switch (me.getIntExtra("MODE", -1)) {
			case MODE_GETMEMBERIDS:// Member id Asked
				List<Integer> resultList = aa.getAllCheckedIds();
				for(int i=0;i<selectedIds.size();i++)
					if(!resultList.contains(selectedIds.get(i)))
						resultList.add(selectedIds.get(i));
				if (resultList.size() > 0) 
				{
					SelectedContacts = new int[resultList.size()];
					for (int i = 0; i < resultList.size(); i++)
						SelectedContacts[i] = resultList.get(i);
					Intent resultIntent = new Intent();
					// result.putIntegerArrayListExtra("List", shares);
					resultIntent.putExtra("result", SelectedContacts);
					setResult(RESULT_OK, resultIntent);
					finish();
				}
			}
			aa.selectAllorNone(false);
			mActionMode = null;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
	};


	private boolean IsMemberintheList(int[] SelectedMembers, int memberId) {
		if (SelectedMembers != null)
			for (int i = 0; i < SelectedMembers.length; i++)
				if (memberId == SelectedMembers[i])
					return true;
		return false;
	}

	@Override
	public void onClick(View V) {
		/*switch (V.getId()) {
		case R.id.contacts_btnadd:
			
			break;
		}*/
	}
	// TODO Menu Options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}
	
	@Override 
	protected void onPause() 
	{ 
		super.onPause();
		if(setundo!=null)
			setundo.discardUndo();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		lstMembers = (ListViewCustom) findViewById(android.R.id.list);
		txtSearch = (EditText) findViewById(R.id.contacts_addsearch);
		//final List<Integer> temp=new ArrayList<Integer>();
		selectedIds=new ArrayList<Integer>();
		TextWatcher tw=new TextWatcher(){
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) 
		{
			for(int i=0;i<memberList.size();i++)
			{	if(memberList.get(i).getCheck())
				{	if(!selectedIds.contains(memberList.get(i).toInt()))
						selectedIds.add(memberList.get(i).toInt());
				}
				else if (selectedIds.contains(memberList.get(i).toInt()))
					selectedIds.remove((Object)memberList.get(i).toInt());
				}
			where = DBinfo.CLM_MEMBER_NAME + " like '%"+txtSearch.getText().toString()+"%'";
			getLoaderManager().restartLoader(0, null, ContactsActivity.this);
		}
			@Override
			public void afterTextChanged(Editable s) 
			{}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int before, int count) 
			{}
			
		};
		txtSearch.addTextChangedListener(tw);
		me = this.getIntent();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		memberList = new ArrayList<ListItemClass>();
		aa = new ListItemAdapter(this, R.layout.listitemcontacts, memberList);
		aa.setOnLVCCheckChangedListener(this);
		lstMembers.setAdapter(aa);
		// source=me.getStringExtra("source");
		SelectedContacts = me.getIntArrayExtra("values");
		TripId = me.getIntExtra(DBinfo.TRIP_ID, -1);
		lstMembers.setOnItemClickListener(this);
		getLoaderManager().initLoader(0, null, this);
		firstload=true;
		/*SwipeDismissList.OnDismissCallback callback=Helper.getSwipeCallback(getContentResolver(),aa,Helper.MEMBERS);
			new SwipeDismissList(lstMembers,callback);*/
		}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		/*super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contacts, menu);*/
	}

	// List Cursor Operations
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = new String[] { DBinfo.MEMBER_ID,
				DBinfo.CLM_MEMBER_NAME,DBinfo.CLM_MEMBER_PHNO,DBinfo.CLM_MEMBER_EMAIL,DBinfo.CLM_MEMBER_UNAME };
		CursorLoader loader = new CursorLoader(this, DB_CP.MEMBER_URI,
				projection,  where,null,null);
		return loader;
	}


	// TODO Other functions

	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position,long arg3) {
		//Toast.makeText(getApplicationContext(), "PH NO:"+memberList.get(position).getStringItemAsked(ListItemClass.PHNO), Toast.LENGTH_SHORT).show();
		if(mActionMode==null)
			{
				if(memberList.get(position).toInt()==ListItemClass.ID_AddNewItem)
					OpenAddMemberPage(DBinfo.MODE_ADD,position);
				else
					OpenAddMemberPage(DBinfo.MODE_EDIT,position);
			}
		//Helper.getInputDialog(getApplicationContext(), "Edit name or number", "Check it", PromptDialog.EDIT_BOX, defaultVal, outView, NextFocus)
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		memberList.clear();
		
		ListItemClass newItem;
		while (cursor.moveToNext()) {
			newItem = new ListItemClass(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),"",0,"",cursor.getString(4));
				newItem.setCheck(IsMemberintheList(SelectedContacts,cursor.getInt(0)));
				newItem.setChkEnabled(!Helper.IsMemberwithHisaab(TripId,cursor.getInt(0)));
			if(!firstload && newItem.isChkEnabled())
				newItem.setCheck(selectedIds.contains(cursor.getInt(0)));
			memberList.add(newItem);
		}
		firstload=false;
		if(memberList.size()<=0)
		{
			newItem = new ListItemClass(ListItemClass.ID_AddNewItem,"Add New Contact","Click Here","","",0,"","");
			memberList.add(newItem);
		}
		//originalmemberList.clear();
		//originalmemberList.addAll(memberList);
		aa.notifyDataSetChanged();
		/**
		 * This is to open Contexual Mode in case of Loading already clicked
		 * values on Page
		*/
		if (mActionMode == null && aa.getAllChecked().size() > 0 ) {
			mActionMode = this.startActionMode(mActionModeCallback);
		}
		if(mActionMode != null)mActionMode.setTitle(aa.getAllChecked().size() + " Selected");
	}

	@Override
	public boolean OnLVCCheckChanged(int position, boolean checked) {
		// TODO Auto-generated method stub
		if (mActionMode != null)
		{		if (aa.getAllChecked().size() <= 0 && me.getIntExtra("MODE", -1)!= MODE_GETMEMBERIDS)
				mActionMode.finish();
			else
				mActionMode.setTitle(aa.getAllChecked().size() + " Selected");
		}
		else if (mActionMode == null && checked) 
		{		
				mActionMode = this.startActionMode(mActionModeCallback);
				mActionMode.setTitle(aa.getAllChecked().size() + " Selected");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_addnewcontact:
			OpenAddMemberPage(DBinfo.MODE_ADD,-1);
			return true;
		/*case R.id.contacts_done:
			List<Integer> resultList = aa.getAllChecked();
				SelectedContacts = new int[resultList.size()];
				for (int i = 0; i < resultList.size(); i++)
					SelectedContacts[i] = aa.getItem(resultList.get(i))
							.toInt();
				Intent resultIntent = new Intent();
				// result.putIntegerArrayListExtra("List", shares);
				resultIntent.putExtra("result", SelectedContacts);
				setResult(RESULT_OK, resultIntent);
				finish();
				return true;
				*/
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Database Action Activities

	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

	private void ReloadList() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
	
	/**
	 * 
	 * @param Mode //Dbinfo.Mode_ADD or ..
	 * @param memberIndex
	 * @return
	 */
	private boolean OpenAddMemberPage(int Mode, int memberIndex) {
		Intent intent = new Intent(this, MemberActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		switch (Mode) {
		case DBinfo.MODE_ADD:
			intent.putExtra("MODE", Mode);
			intent.putExtra("CALLER", "CONTACTS");
			startActivityForResult(intent, CONTACT_ADD);
			return true;
		case DBinfo.MODE_VIEW:
		case DBinfo.MODE_EDIT:
			intent.putExtra("MODE", Mode);
			intent.putExtra("CALLER", "CONTACTS");
			intent.putExtra(DBinfo.CLM_MEMBER_NAME, memberList.get(memberIndex).getStringItemAsked(ListItemClass.MAIN_ITEM));
			intent.putExtra(DBinfo.CLM_MEMBER_PHNO, memberList.get(memberIndex).getStringItemAsked(ListItemClass.PHNO));
			intent.putExtra(DBinfo.MEMBER_ID, memberList.get(memberIndex).toInt());
			startActivityForResult(intent, CONTACT_UPDATE);
			return true;
		default:
			return false;
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		String name="",number="",email="",uname="";
		int MemberId = -1;
		if (resultCode == RESULT_OK)
		{
			//Setting The Values
			switch (requestCode)
			{

			case CONTACT_ADD:
				name = data.getStringExtra(DBinfo.CLM_MEMBER_NAME);
				number = data.getStringExtra(DBinfo.CLM_MEMBER_PHNO);
				email = data.getStringExtra(DBinfo.CLM_MEMBER_EMAIL);
				uname = data.getStringExtra(DBinfo.CLM_MEMBER_UNAME);
				//Mode=data.getIntExtra("Mode",-1);
				
			break;
			case CONTACT_UPDATE:			
				name = data.getStringExtra(DBinfo.CLM_MEMBER_NAME);
				number = data.getStringExtra(DBinfo.CLM_MEMBER_PHNO);
				MemberId=data.getIntExtra(DBinfo.MEMBER_ID,-1);
				email = data.getStringExtra(DBinfo.CLM_MEMBER_EMAIL);
				uname = data.getStringExtra(DBinfo.CLM_MEMBER_UNAME);
				//Mode=data.getIntExtra("Mode",-1);
			} 
			
			//Checking For Empty Or Repeated Number
			final String MemberName=name,MemberPh=number,MemberEmail=email,MemberUname=uname;
			final ListItemClass lic=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.CLM_MEMBER_NAME+ "='" + name + "' and not("
					+DBinfo.MEMBER_ID+"="+MemberId+")");	
			if (TextUtils.isEmpty(MemberName))
			{	
				Toast.makeText(getApplicationContext(),	"Name Field Can't be Empty", Toast.LENGTH_SHORT).show();		
			}
			else if (lic != null)
			{
				String content="Are You Sure to Overwrite the number of "+lic.toString() +
				"\n\nOld Number:"+lic.getStringItemAsked(ListItemClass.PHNO)+"\nNew Number:"+MemberPh;
				new PromptDialog(this, "Contact Name Already Exists!!", content,PromptDialog.YESNOBOX)
				{
					@Override
					public boolean onOkClicked(View input,View outView, View NextFocus) 
					{
						Helper.UpdateMember(getContentResolver(),lic.toInt(),MemberName,MemberPh,MemberEmail,MemberUname);
						ReloadList();
						return true;
					}
				};
				return;
			}
			else
				{
				//Adding or updating as asked
				switch (requestCode)
				{
				case CONTACT_ADD:
					//String inserted_id =
					Helper.AddMember(getContentResolver(),  name,number, email,uname);
					ReloadList();
					Toast.makeText(	getApplicationContext(),"Member " + name + " Added",Toast.LENGTH_SHORT).show();
					break;
				case CONTACT_UPDATE:
					MemberId=data.getIntExtra(DBinfo.MEMBER_ID,-1);				
					Helper.UpdateMember(getContentResolver(), MemberId, name,number, email,uname);
					ReloadList();
					Toast.makeText(	getApplicationContext(),"Member " + name + " Updated",Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
	}
}

