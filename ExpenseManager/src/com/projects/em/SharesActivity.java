package com.projects.em;

import java.util.ArrayList;
import java.util.List;

import com.projects.em.DB_CP.DBinfo;
import com.projects.em.ListItemAdapter.OnLVCCheckChangedListener;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SharesActivity extends ListActivity implements
		LoaderCallbacks<Cursor>, OnLVCCheckChangedListener,OnItemLongClickListener {
	private static final int LOADER_ID = 0;
	private ListViewCustom lstmembers;
	private ListItemAdapter aa;
	private List<ListItemClass> members;
	private String EventName;
	private int TripId, EventId;
	private float totalBill = 0;
	private float[] allValues;
	private static Intent me;
	protected ActionMode mActionMode;
	private static final int INTENT_CALC=1;

	private boolean Equalize(float total) {
		float share = 0;
		int count = lstmembers.getCount();
		share = (count != 0 ? total / count : 0);
		// share=((int)(share*10))/10;
		View curMember;
		EditText Amount;
		for (int i = 0; i < count; i++) {
			curMember = lstmembers.getChildAt(i);
			Amount = (EditText) curMember.findViewById(R.id.EditTextItem);
			Amount.setText(String.format("%.1f", share));
		}
		return true;
	}

	private boolean Equalize(float total, List<Integer> indexofmembers) {
		float share = 0;
		int count = indexofmembers.size();
		share = (count != 0 ? total / count : 0);
		// share=((int)(share*10))/10;
		View curMember;
		EditText Amount;
		for (int i = 0; i < count; i++) {
			curMember = lstmembers.getChildAt(indexofmembers.get(i));
			Amount = (EditText) curMember.findViewById(R.id.EditTextItem);
			Amount.setText(String.format("%.1f", share));
		}
		return true;
	}

	private float[] getSharedValues() {
		String str;
		float values[];
		values = new float[lstmembers.getCount()];
		View curMember;
		EditText Amount;
		for (int i = 0; i < lstmembers.getCount(); i++) {
			curMember = lstmembers.getChildAt(i);
			Amount = (EditText) curMember.findViewById(R.id.EditTextItem);
			str = Amount.getText().toString();
			values[i] = Helper.getFloatIfParsable(str);// str.isEmpty()?0:Float.parseFloat(str);
		}
		return values;
	}

	private float getSum() {
		return Helper.getSharesSum(getSharedValues());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.w("Loading Shares","from act shares");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shares);
		lstmembers = (ListViewCustom) findViewById(android.R.id.list);
		lstmembers.setOnItemLongClickListener(this);
		members = new ArrayList<ListItemClass>();
		aa = new ListItemAdapter(this, R.layout.listshares, members);
		aa.setOnLVCCheckChangedListener(this);
		lstmembers.setAdapter(aa);
		// TripName=me.getStringExtra(DBinfo.CLM_TRIP_NAME);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		me = getIntent();

		TripId = me.getIntExtra(DBinfo.TRIP_ID, -1);
		EventName = me.getStringExtra(DBinfo.CLM_EVENT_NAME);
		totalBill = me.getFloatExtra("Amount", 0);
		allValues = me.getFloatArrayExtra("AllValues");
		int editmode=me.getIntExtra("Mode", DBinfo.MODE_ADD);
		if (editmode == DBinfo.MODE_EDIT || editmode==DBinfo.MODE_VIEW)// Edit or view
			EventId = me.getIntExtra(DBinfo.EVENT_ID, -1);
		this.setTitle(EventName + "(" + String.format("%.1f", totalBill) + ")");
		if (getLoaderManager().getLoader(LOADER_ID) != null) {
			getLoaderManager().restartLoader(LOADER_ID, null, this);
		} 
		else {
			getLoaderManager().initLoader(LOADER_ID, null, this);
		}

	}

	// TODO List Cursor Operations
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// String[] projection = new String[]{
		// DBinfo.MEMBER_ID,DBinfo.CLM_MEMBER_NAME};
		String query = "select " + DBinfo.DB_TABLE_MEMBERS + "."
				+ DBinfo.MEMBER_ID + " , " + DBinfo.CLM_MEMBER_NAME;
		query += " from " + DBinfo.DB_TABLE_MEMBERS + ","
				+ DBinfo.DB_TABLE_TRIPMEMBERS;
		query += " where " + DBinfo.DB_TABLE_MEMBERS + "." + DBinfo.MEMBER_ID;
		query += " = " + DBinfo.DB_TABLE_TRIPMEMBERS + "." + DBinfo.MEMBER_ID;
		query += " and " + DBinfo.DB_TABLE_TRIPMEMBERS + "." + DBinfo.TRIP_ID;
		query += " = " + Integer.toString(TripId);
		CursorLoader loader = new CursorLoader(this, DB_CP.RAW_URI, null,
				query, null, null);
		return loader;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shares, menu);
		return true;
	}

	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int i = 0;
		members.clear();
		while (cursor.moveToNext()) {
			ListItemClass newItemPaid = new ListItemClass(cursor.getInt(0),
					cursor.getString(1),"","","",0,"","");
			float amount = 0;
			if (allValues != null)// Current value just changed and reloaded
				newItemPaid.setItemAsked(ListItemClass.BILL, allValues[i]);
			else if (Helper.EventsAmountExists(EventId)) {
				Toast.makeText(
						getApplicationContext(),
						"Loading amounts Directly from database(Should not be here)",
						Toast.LENGTH_SHORT).show();
				if (me.getIntExtra("type", -1) == 1)// paid event
					amount = Helper.AmountofMemberforEvent(EventId,
							newItemPaid.toInt(), 1);
				else if (me.getIntExtra("type", -1) == 2)// shared event
					amount = -Helper.AmountofMemberforEvent(EventId,
							newItemPaid.toInt(), 2);
				if (amount > 0)
					newItemPaid.setItemAsked(ListItemClass.BILL, amount);
			} else
				// value not saved in database
				newItemPaid.setItemAsked(ListItemClass.BILL, 0);
			members.add(newItemPaid);
			i++;
		}
		aa.notifyDataSetChanged();
	//	SetOnClick();
	}

	@Override
	public boolean OnLVCCheckChanged(int position, boolean checked) {
		// TODO Auto-generated method stub
		// eventList.get(position).setCheck(true);
		/*
		 * if (mActionMode != null) if( aa.getAllChecked().size()<=0)
		 * mActionMode.finish(); else
		 * mActionMode.setTitle(aa.getAllChecked().size() + " selected"); else
		 * if(mActionMode == null && checked) { mActionMode =
		 * this.startActionMode(mActionModeCallback);
		 * mActionMode.setTitle(aa.getAllChecked().size() + " selected"); }
		 */
		Equalize(0);
		Equalize(totalBill, aa.getAllChecked());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_shares_equalize:
			Equalize(totalBill);
			return false;
		case R.id.action_shares_reset:
			Equalize(0);
			//SetOnClick();
			return false;
		case R.id.action_Show_Calc:
			//EditText et = (EditText) view.findViewById(R.id.EditTextItem);
			Intent intent=new Intent(this,CalcActivity.class);
			String rem=(String.format("%.1f", totalBill - getSum()));
			String total=(String.format("%.1f", totalBill));
			intent.putExtra("rem", rem);//string
			intent.putExtra("total", total);//string
			//intent.putExtra("Value", et.getText().toString());//String
//			intent.putExtra("EditTextPosition", position);//int
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
	//		Log.w("From ItemLong Click","Here2");
	// 
			return false;
		case R.id.action_shares_done:
			allValues = getSharedValues();
			float sum = Helper.getSharesSum(allValues);
			/*if (Math.abs(sum - totalBill) >= 1) {
				Toast.makeText(	getApplicationContext(),String.valueOf(totalBill - sum)+ " to be adjusted\nTotal is not adding up",	Toast.LENGTH_LONG).show();
			} */
			//else
				Intent result = new Intent();
				// result.putIntegerArrayListExtra("List", shares);
				result.putExtra("Floats", allValues);
				result.putExtra("Total", sum);
				setResult(RESULT_OK, result);
				finish();
				return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//getLoaderManager().restartLoader(0, null, this);
	}


	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.w("From Calc", resultCode +":Result Code:"+RESULT_OK);
		if (resultCode == RESULT_OK)
		{
			Log.w("From Calc", requestCode +":Request Code:"+INTENT_CALC);
			//Setting The Values
			switch (requestCode)
			{
				case INTENT_CALC:
					int EditTextPosition=data.getIntExtra("EditTextPosition", -1);
					String strvalue=data.getStringExtra("Value");
					//Log.w("From Calc", EditTextPosition +":Edittesxtpos");
					View curMember;
					EditText txtAmount;
					curMember = lstmembers.getChildAt(EditTextPosition);
					txtAmount = (EditText) curMember.findViewById(R.id.EditTextItem);
					//Log.w("From Calc", txtAmount.getText().toString() +":Edittesxtval:"+strvalue);
					txtAmount.setText(strvalue);
					break;
			}		
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> listview, View view, int position,
			long id) {
				EditText et = (EditText) view.findViewById(R.id.EditTextItem);
				float myValue = Helper.getFloatIfParsable(et.getText().toString());
				Intent intent=new Intent(this,CalcActivity.class);
				String rem=(String.format("%.1f", totalBill - getSum()+myValue));
				String total=(String.format("%.1f", totalBill));
				intent.putExtra("rem", rem);//string
				intent.putExtra("total", total);//string
				intent.putExtra("Value", et.getText().toString());//String
				intent.putExtra("EditTextPosition", position);//int
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent,INTENT_CALC);

		return false;
	}

	/*
	 * private ActionMode.Callback mActionModeCallback = new
	 * ActionMode.Callback() { // Called when the action mode is created;
	 * startActionMode() was called
	 * 
	 * @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	 * // Inflate a menu resource providing context menu items MenuInflater
	 * inflater = mode.getMenuInflater();
	 * inflater.inflate(R.menu.shares_contextmenu, menu); return true; } //
	 * Called each time the action mode is shown. Always called after
	 * onCreateActionMode, but // may be called multiple times if the mode is
	 * invalidated.
	 * 
	 * @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	 * { return false; // Return false if nothing is done }
	 * 
	 * // Called when the user selects a contextual menu item
	 * 
	 * @Override public boolean onActionItemClicked(ActionMode mode, MenuItem
	 * item) { switch (item.getItemId()) { case R.id.shares_Context_SelectAll:
	 * //lstEvents.selectAllorNone(true); if(item.getTitle()=="SELECT NONE") {
	 * item.setTitle("SELECT ALL"); lstmembers.selectAllorNone(false);
	 * mode.finish(); } else {
	 * //item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	 * item.setTitle("SELECT NONE"); lstmembers.selectAllorNone(true); //
	 * //item.setIcon(off); } return false; case R.id.shares_Context_Equify:
	 * Equalize(0); Equalize(totalBill, aa.getAllChecked()); mode.finish();
	 * return false; default: return false; } }
	 * 
	 * @Override public void onDestroyActionMode(ActionMode mode) {
	 * //lstEvents.setChoiceMode(ListViewCustom.CHOICE_MODE_NONE); //
	 * aa.setCheckButtonVisibility(View.INVISIBLE); //aa.notifyDataSetChanged();
	 * lstmembers.selectAllorNone(false); mActionMode = null;
	 * 
	 * } }; //TODO Get BILL AMOUNT
	 * 
	 * private boolean LoadAmounts(float values[]) { View curMember; EditText
	 * Amount; for(int i=0;i<lstmembers.getCount();i++) {
	 * 
	 * curMember = lstmembers.getChildAt(i); if(curMember!=null) { Amount =
	 * (EditText) curMember.findViewById(R.id.EditTextItem); if(i<values.length)
	 * Amount.setText(String.valueOf(values[i])); } else
	 * Toast.makeText(getApplicationContext(), "curMember not found at " +
	 * String.valueOf(i), Toast.LENGTH_SHORT).show(); }
	 * 
	 * return true; }
	 */
}
