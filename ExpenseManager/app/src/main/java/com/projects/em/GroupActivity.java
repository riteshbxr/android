package com.projects.em;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.projects.em.DB_CP.DBinfo;
import com.projects.em.ListItemAdapter.OnLVCCheckChangedListener;
import com.projects.utils.SyncActivity;

public class GroupActivity extends ListActivity implements LoaderCallbacks<Cursor>, 
			OnItemClickListener, OnLVCCheckChangedListener, OnItemLongClickListener{
	private static ListViewCustom lstGroups;
	private static ListItemAdapter aa;
	private static List<ListItemClass> groupList;
	protected ActionMode mActionMode;
	private UndoOptions setundo;
	private static final int GROUP_IMPORT_INTENT=3;

	
	@Override 
	protected void onPause() 
	{ 
		super.onPause();
		if(setundo!=null)
			setundo.discardUndo();
	}
	

	private void  handleImport()
	{
		Uri data = getIntent().getData();
		if(data==null)
			return;
		else if(data.getScheme().contentEquals("file")||data.getScheme().contentEquals("content")) // "http"
		{
		final File recievedFile=new File(data.getPath());
//		Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	Helper.importfromJSON(recievedFile,GroupActivity.this);
		        		//ReloadList();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Import Trip(s)").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).setMessage("Do You want to import the trip/gang in the file?["+recievedFile.getPath()+"]").show();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		lstGroups = (ListViewCustom) findViewById(android.R.id.list);
		lstGroups.setOnItemClickListener(this);
		lstGroups.setOnItemLongClickListener(this);
		// lstTrips.setOnItemLongClickListener(this);
		groupList = new ArrayList<ListItemClass>();
		aa = new ListItemAdapter(this, R.layout.listitemgroup, groupList);
		aa.setOnLVCCheckChangedListener(this);
		lstGroups.setAdapter(aa);
		lstGroups.setChoiceMode(ListViewCustom.CHOICE_MODE_NONE);

		// aa.setCheckButtonVisibility(View.INVISIBLE);
		Helper.Loadresources();
		// registerForContextMenu(lstTrips);
		getLoaderManager().initLoader(0, null, this);
		this.setTitle("Treckon Gangs");
		handleImport();
		//new SwipeDismissList(lstTrips,Helper.getSwipeCallback(getContentResolver(),aa,Helper.TRIPS));
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_multi_contextmenu, menu);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position,
			long arg3) {
		Log.w("GroupAct","here in item long click");
		if (mActionMode == null)// click event
		{
			if(groupList.get(position).toInt()==ListItemClass.ID_AddNewItem)
			{
				Intent intent = new Intent(this, UpdateGroupActivity.class);
				intent.putExtra("MODE", DBinfo.MODE_ADD);
				startActivity(intent);
			}
			else
			{
			Intent intent = new Intent(this, TripActivity.class);
			intent.putExtra(DBinfo.CLM_GROUP_NAME, groupList.get(position)
					.toString());
			intent.putExtra(DBinfo.CLM_TRIP_GROUPID, groupList.get(position).toInt());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		aa.notifyDataSetChanged();
	}
	/**
	 * List Cursor Operations
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String projection[] = new String[] { DBinfo.GROUP_ID,
				DBinfo.CLM_GROUP_NAME,
				DBinfo.CLM_GROUP_THEME,DBinfo.CLM_GROUP_TYPE,DBinfo.CLM_GROUP_DESC};
		CursorLoader loader = new CursorLoader(this, DB_CP.GROUP_URI,
				projection, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		groupList.clear();
		ListItemClass newItem;
		while (cursor.moveToNext()) {
			newItem = new ListItemClass(cursor.getInt(0),cursor.getString(1), cursor.getInt(2),cursor.getInt(3),cursor.getString(4));
			// Log.w("homeact",newItem.toString() + "-" + newItem.getid()
			// +" of "+cursor.getCount());
			groupList.add(newItem);
		}
		if(groupList.size()<=0)
		{
			newItem=new ListItemClass(ListItemClass.ID_AddNewItem,"Add New GROUP",0,0,"Click here to add");
			groupList.add(newItem);
		}
		aa.notifyDataSetChanged();
	}

	@Override
	public boolean OnLVCCheckChanged(int position, boolean checked) {
		// TODO Auto-generated method stub
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
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		case R.id.action_addgroup:
			intent = new Intent(this, UpdateGroupActivity.class);
			intent.putExtra("MODE", DBinfo.MODE_ADD);
			startActivity(intent);
			return true;
		case R.id.action_group_contacts:
			intent = new Intent(this, ContactsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.action_group_gotoResults:
			startActivity(Helper.showHisaab(getApplicationContext(), -1,null));
			return true;
		/*case R.id.action_signin:
			intent = new Intent(this, com.projects.sync.SyncActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;*/
		case R.id.action_group_import:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
		    Uri uri = Uri.fromFile(Helper.getDefaultDirectory());
		    intent.setDataAndType(uri, "file/*");
		    startActivityForResult(intent,GROUP_IMPORT_INTENT);
			return true;
		case R.id.action_group_extra:
			intent = new Intent(this, SyncActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// TODO Click and LongClick

	@Override
	protected void onResume() {
		super.onResume();
		//Helper.checkPlayServices(this);
		ReloadList();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK && requestCode==GROUP_IMPORT_INTENT)
		{
			final File recievedFile = Helper.getPathfromIntent(data,getContentResolver());
			Helper.importfromJSON(recievedFile,this);
		}
	}	

	public void ReloadList() {
		getLoaderManager().restartLoader(0, null, this);
		aa.notifyDataSetChanged();
	}
	
	
	
	@Override
	public boolean onItemLongClick(AdapterView<?> listView, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		Log.w("GroupAct","here in item long click");
		if (mActionMode == null)
		{Intent intent = new Intent(this, UpdateGroupActivity.class);
		intent.putExtra("MODE", DBinfo.MODE_EDIT);
		intent.putExtra(DBinfo.GROUP_ID, ((ListItemClass) groupList.get(pos)).toInt());
		startActivity(intent);
			return true;
		}
		else return false;
		
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() { // Called																					
	// called
	// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.group_Context_delete:
				List<Integer> positions=new ArrayList<Integer>(aa.getAllChecked());
				mode.finish();
				String wherecheck = DBinfo.CLM_TRIP_GROUPID+ " IN(";
				for (int i = 0; i < positions.size(); i++)
					wherecheck += groupList.get(positions.get(i)).toInt() + ",";
				wherecheck += "-1)";
				//Toast.makeText(GroupActivity.this,"DELETING:"+wherecheck,Toast.LENGTH_LONG).show();
				ListItemClass lic = DB_CP.getListItemClass(DB_CP.TRIP_URI,wherecheck);
				if (lic == null)
					setundo=new UndoOptions(lstGroups,positions, Helper.getUndoCallback(getContentResolver(), new ArrayList<ListItemClass>(groupList), aa,Helper.GROUPS));
				else
					Toast.makeText(GroupActivity.this,"Some groups can't be deleted, Empty the group before deleting it!!!",Toast.LENGTH_LONG).show();
				return true;
			case R.id.group_Context_SelectAll:
				boolean finishmode = Helper.SelectAll(item, aa);
				if (finishmode)
					mode.finish();
				return finishmode;
			case R.id.group_Context_Hisaab:
				startActivity(Helper
						.showHisaab(getApplicationContext(), Helper.GROUPS,
								Helper.extractIdsfromList(aa.getAllChecked(),
										groupList)));
				mode.finish();
				return true;
			case R.id.group_Context_Share:
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				//sendIntent.setPackage("com.android.bluetooth");
				sendIntent.setType("text/html");
				//sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
				//sendIntent.setType("text/plain");
				Uri uri = Uri.parse("file://" + Helper.exportGroupToFile(Helper.extractIdsfromList(aa.getAllChecked(),groupList)));
				sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
				startActivity(sendIntent);
				mode.finish();
				return true;
			case R.id.group_Context_export:
				/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			    intent.setType("file/*");
			    startActivityForResult(intent,TRIP_EXPORT_INTENT);*/
				Toast.makeText(getApplicationContext(), "Exported to" +Helper.exportGroupToFile(Helper.extractIdsfromList(aa.getAllChecked(),groupList)).getPath(), Toast.LENGTH_LONG).show();
			return true;
			default:
				return false;
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.group_multi_contextmenu, menu);
			return true;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) { // lstEvents.setChoiceMode(ListViewCustom.CHOICE_MODE_NONE);
															// aa.setCheckButtonVisibility(View.INVISIBLE);									// aa.notifyDataSetChanged();
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
}

