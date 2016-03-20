package com.projects.em;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.projects.em.DB_CP.DBinfo;
import com.projects.em.ListItemAdapter.OnLVCCheckChangedListener;
import com.projects.utils.SyncActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TripActivity extends ListActivity implements LoaderCallbacks<Cursor>, OnItemClickListener, OnLVCCheckChangedListener{
	private static ListViewCustom lstTrips;
	private static ListItemAdapter aa;
	private static List<ListItemClass> tripList;
	protected ActionMode mActionMode;
	private UndoOptions setundo;
	private static final int TRIP_IMPORT_INTENT=2;
	private static int groupId=1;
	private static String groupName="Treckon";
	private static ListItemClass Group;
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
		setContentView(R.layout.activity_home);
		lstTrips = (ListViewCustom) findViewById(android.R.id.list);
		lstTrips.setOnItemClickListener(this);
		// lstTrips.setOnItemLongClickListener(this);
		tripList = new ArrayList<ListItemClass>();
		aa = new ListItemAdapter(this, R.layout.listitemtrip, tripList);
		aa.setOnLVCCheckChangedListener(this);
		lstTrips.setAdapter(aa);
		lstTrips.setChoiceMode(ListViewCustom.CHOICE_MODE_NONE);
		
		// aa.setCheckButtonVisibility(View.INVISIBLE);
		Helper.Loadresources();
		// registerForContextMenu(lstTrips);
		getLoaderManager().initLoader(0, null, this);
		handleImport();
		//new SwipeDismissList(lstTrips,Helper.getSwipeCallback(getContentResolver(),aa,Helper.TRIPS));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void  handleImport()
	{
		Uri data = getIntent().getData();
		if(data==null)
			return;
		else if(data.getScheme().contentEquals("file")||data.getScheme().contentEquals("content")) // "http"
		{
		final File path=new File(data.getPath());
//		Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        		new Helper.importTrip(path,(Activity) getApplicationContext(),groupId).execute();
		        		//ReloadList();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Import Trip").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).setMessage("Do You want to import the trip in the file?["+path.getPath()+"]").show();
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.trip_contextmenu, menu);
	}

	/**
	 * List Cursor Operations
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String projection[] = new String[] { DBinfo.TRIP_ID,
				DBinfo.CLM_TRIP_NAME,DBinfo.CLM_TRIP_DATE				
			,
				DBinfo.CLM_TRIP_PLACE, DBinfo.CLM_TRIP_PIC,
				DBinfo.CLM_TRIP_THEME,DBinfo.CLM_TRIP_TYPE,DBinfo.CLM_TRIP_GROUPID };
		CursorLoader loader = new CursorLoader(this, DB_CP.TRIP_URI,
				projection,DBinfo.CLM_TRIP_GROUPID+"="+groupId, null, null);
		return loader;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trip, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position,
			long arg3) {
		
		if (mActionMode == null)// click event
		{
			if(tripList.get(position).toInt()==ListItemClass.ID_AddNewItem)
			{
				Intent intent = new Intent(this, UpdateTripActivity.class);
				intent.putExtra(DBinfo.CLM_TRIP_GROUPID,groupId);
				intent.putExtra("MODE", DBinfo.MODE_ADD);
				startActivity(intent);
			}
			else
			{
			Intent intent = new Intent(this, EventActivity.class);
			intent.putExtra(DBinfo.CLM_TRIP_NAME, tripList.get(position)
					.toString());
			intent.putExtra(DBinfo.TRIP_ID, tripList.get(position).toInt());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		aa.notifyDataSetChanged();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		tripList.clear();
		ListItemClass newItem;
		while (cursor.moveToNext()) {
			newItem = new ListItemClass(cursor.getInt(0),cursor.getString(1), cursor.getString(2),
					cursor.getString(3), cursor.getString(4), cursor.getInt(5),cursor.getInt(6),"",cursor.getInt(7));
			// Log.w("homeact",newItem.toString() + "-" + newItem.getid()
			// +" of "+cursor.getCount());
			tripList.add(newItem);
		}
		if(tripList.size()<=0)
		{
			newItem=new ListItemClass(ListItemClass.ID_AddNewItem,"Add New Trip","","Click here to add","",0,0,"",0);
			tripList.add(newItem);
		}
		aa.notifyDataSetChanged();

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
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_addtrip:
			intent = new Intent(this, UpdateTripActivity.class);
			intent.putExtra("MODE", DBinfo.MODE_ADD);
			intent.putExtra(DBinfo.CLM_TRIP_GROUPID,groupId);
			startActivity(intent);
			return true;
		case R.id.action_contacts:
			intent = new Intent(this, ContactsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.action_gotoResults:
			startActivity(Helper.showHisaab(getApplicationContext(), -1, null,-1));
			return true;
		/*case R.id.action_signin:
			intent = new Intent(this, com.projects.sync.SyncActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;*/
		case R.id.action_import:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			
		    Uri uri = Uri.fromFile(Helper.getDefaultDirectory());
		    intent.setDataAndType(uri, "file/*");
		    startActivityForResult(intent,TRIP_IMPORT_INTENT);
			return true;
		case R.id.action_extra:
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
		Intent me = this.getIntent();
		groupId = me.getIntExtra(DBinfo.CLM_TRIP_GROUPID, 1);
		Group = DB_CP.getListItemClass(DB_CP.GROUP_URI, DBinfo.GROUP_ID + "="
				+ groupId);
		groupName=Group.getStringItemAsked(ListItemClass.MAIN_ITEM);
		this.setTitle(groupName);
		getLoaderManager().restartLoader(0, null, this);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		File recievedFile;
		if(resultCode==RESULT_OK && requestCode==TRIP_IMPORT_INTENT)
		{
                recievedFile = Helper.getPathfromIntent(data,getContentResolver());
                if(recievedFile!=null)
                {
                	new Helper.importTrip(recievedFile,this,groupId).execute();

                }
                else
                {
                	Toast.makeText(getApplicationContext(), "Invalid File", Toast.LENGTH_LONG).show();
                }
                	
                
     
		}
	}	
	
	

	public void ReloadList() {
		getLoaderManager().restartLoader(0, null, this);
		aa.notifyDataSetChanged();
	}
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() { // Called																					
		// called
		// Called when the user selects a contextual menu item
			@Override
			public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.home_Context_delete:
				//	Toast.makeText(getApplicationContext() ,"Finally deleted:"+aa.getAllChecked().size(),Toast.LENGTH_SHORT ).show();
					List<Integer> positions=new ArrayList<Integer>(aa.getAllChecked());
					mode.finish();
					new UndoOptions(lstTrips,positions, Helper.getUndoCallback(getContentResolver(), new ArrayList<ListItemClass>(tripList), aa,Helper.TRIPS));
					return true;
				case R.id.home_Context_SelectAll:
					boolean finishmode = Helper.SelectAll(item, aa);
					if (finishmode)
						mode.finish();
					return finishmode;
				case R.id.home_Context_Hisaab:
					startActivity(Helper
							.showHisaab(getApplicationContext(), Helper.TRIPS,
									Helper.extractIdsfromList(aa.getAllChecked(),
											tripList), -1));
					mode.finish();
					return true;
				case R.id.home_Context_Share:
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					//sendIntent.setPackage("com.android.bluetooth");
					sendIntent.setType("text/html");
					//sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
					//sendIntent.setType("text/plain");
					Uri uri = Uri.parse("file://" + Helper.exportToFile(Helper.extractIdsfromList(aa.getAllChecked(),tripList)));
					sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
					startActivity(sendIntent);
					mode.finish();
					return true;
				case R.id.home_Context_export:
					/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				    intent.setType("file/*");
				    startActivityForResult(intent,TRIP_EXPORT_INTENT);*/
					Toast.makeText(getApplicationContext(), "Exported to" +Helper.exportToFile(Helper.extractIdsfromList(aa.getAllChecked(),tripList)).getPath(), Toast.LENGTH_LONG).show();
				case R.id.home_Context_changegroup:
					//mode.finish();
					int chkItem=0;
					//String items[]=DB_CP.getAllGroupNames();
					final List<ListItemClass> allGroups = DB_CP.getListItemClassArray(DB_CP.GROUP_URI, " 1 ");
					ListItemAdapter aaGroups=new ListItemAdapter(TripActivity.this,R.layout.listitemgroup,allGroups);
					//int itemIds[]=DB_CP.getAllGroupIds();
					aaGroups.setCheckButtonVisibility(View.GONE);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TripActivity.this);
					alertDialogBuilder.setTitle("Select Group");
					alertDialogBuilder.setSingleChoiceItems(aaGroups,chkItem, 
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int index) {
									Helper.movetogroup(getContentResolver(), Helper.extractIdsfromList(aa.getAllChecked(),tripList), allGroups.get(index).toInt());
									Toast.makeText(getApplicationContext(),"Selected items moved!!", Toast.LENGTH_LONG).show();
									dialog.dismiss();
									mode.finish();
									ReloadList();
								}
							}
					);
					alertDialogBuilder.show();

					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate a menu resource providing context menu items
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.trip_contextmenu, menu);
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

