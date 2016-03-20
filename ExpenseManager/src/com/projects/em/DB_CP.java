package com.projects.em;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DB_CP extends ContentProvider {
	// TODO DBinfo CLass
		public class DBinfo {
			static final String DB_NAME = "hissab.db";
			static final int DB_VERSION = 5;

			
			// Columns of the Table Member
			static final String DB_TABLE_MEMBERS = "members";
			public static final String MEMBER_ID = "_memberid";
			public static final String CLM_MEMBER_NAME = "_membername";
			public static final String CLM_MEMBER_PHNO = "_memberphno";
			public static final String CLM_MEMBER_EMAIL = "_memberemail";
			public static final String CLM_MEMBER_TYPE = "_membertype";
			public static final String CLM_MEMBER_PIC = "_memberpic";
			public static final String CLM_MEMBER_DESC = "_memberdesc";
			public static final String CLM_MEMBER_UNAME = "_memberuname"; 
			
			//columns of table group
			static final String DB_TABLE_GROUPS = "groups";
			public static final String GROUP_ID = "_tripid";
			public static final String CLM_GROUP_NAME = "_groupname";
			public static final String CLM_GROUP_DESC = "_groupdesc";
			public static final String CLM_GROUP_TYPE = "_grouptype";
			public static final String CLM_GROUP_THEME = "_grouptheme";

			// Columns of the Table TRIP
			static final String DB_TABLE_TRIPS = "trips";
			public static final String TRIP_ID = "_tripid";
			public static final String CLM_TRIP_NAME = "_tripname";
			public static final String CLM_TRIP_DATE = "_date";
			public static final String CLM_TRIP_PLACE = "_place";
			public static final String CLM_TRIP_PIC = "_picFile";
			public static final String CLM_TRIP_THEME = "_theme";
			public static final String CLM_TRIP_DESC = "_tripdesc";
			public static final String CLM_TRIP_TYPE = "_triptype";
			public static final String CLM_TRIP_GROUPID = "_tripgroupid";
			
			// Columns of the Table EVENTS
			static final String DB_TABLE_EVENTS = "events";
			public static final String EVENT_ID = "_eventid";
			public static final String CLM_EVENT_NAME = "_eventname";
			public static final String CLM_EVENT_BILL = "_bill";
			public static final String CLM_EVENT_DATE = "_eventdate";
			public static final String CLM_EVENT_PLACE = "_eventplace";
			public static final String CLM_EVENT_TYPE = "_eventtype";
			public static final String CLM_EVENT_PIC = "_eventpic";
			public static final String CLM_EVENT_DESC = "_eventdesc";

			public static final String DB_TABLE_SHARES = "shares";
			public static final String SHARE_ID = "_shareid";
			public static final String CLM_SHAREDBY_MEMBERID = "_sharedby";
			public static final String CLM_PAIDBY_MEMBERID = "_paidby";
			public static final String CLM_SHARE_AMOUNT = "_sharedamount";;

			public static final String DB_TABLE_TRIPMEMBERS = "tripmember";
			public static final String TRIPMEMBER_ID = "_tripmemberid";

			public static final int MODE_ADD = 0;
			public static final int MODE_ADD_Prefilled = 3;
			public static final int MODE_VIEW = 1;
			public static final int MODE_EDIT = 2;
		}
	private static class DBHelper extends SQLiteOpenHelper {
		// Columns of the Table Member
		// SQL Statement to create a new table

		private static final String CREATE_MEMBERS = "create table "
				+ DBinfo.DB_TABLE_MEMBERS + " (" 
				+ DBinfo.MEMBER_ID	+ " integer primary key autoincrement, "
				+ DBinfo.CLM_MEMBER_NAME + " text not null, "
				+ DBinfo.CLM_MEMBER_PHNO + " text , "
				+ DBinfo.CLM_MEMBER_EMAIL + " text , "
				+ DBinfo.CLM_MEMBER_PIC + " text , "
				+ DBinfo.CLM_MEMBER_TYPE + " integer default 0, "
				+ DBinfo.CLM_MEMBER_DESC + " text, "
				+ DBinfo.CLM_MEMBER_UNAME + " text "
				+ ");";
		// DBinfo.CLM_MEMBER_GROUP + " text, " +
		// DBinfo.CLM_MEMBER_TYPE + " text);";
		private static final String CREATE_GROUPS = "create table "
				+ DBinfo.DB_TABLE_GROUPS + " (" 
				+ DBinfo.GROUP_ID	+ " integer primary key autoincrement, " 
				+ DBinfo.CLM_GROUP_NAME	+ " text not null, " 
				+ DBinfo.CLM_GROUP_THEME	+ " integer default 0, " 
				+ DBinfo.CLM_GROUP_TYPE	+ " integer default 0, "
				+ DBinfo.CLM_GROUP_DESC	+ " text "
						+");";

		private static final String CREATE_TRIPS = "create table "
				+ DBinfo.DB_TABLE_TRIPS + " (" 
				+ DBinfo.TRIP_ID	+ " integer primary key autoincrement, " 
				+ DBinfo.CLM_TRIP_NAME	+ " text not null, " 
				+ DBinfo.CLM_TRIP_DATE + " date, "
				+ DBinfo.CLM_TRIP_PIC + " text , " 
				+ DBinfo.CLM_TRIP_PLACE	+ " text ,"
				+ DBinfo.CLM_TRIP_THEME	+ " integer default 0, " 
				+ DBinfo.CLM_TRIP_TYPE	+ " integer default 0, "
				+ DBinfo.CLM_TRIP_DESC	+ " text, "
				+ DBinfo.CLM_TRIP_GROUPID	+ " integer default 0 "
						+");";

		private static final String CREATE_EVENTS = "create table "
				+ DBinfo.DB_TABLE_EVENTS + " (" + DBinfo.EVENT_ID
				+ " integer primary key autoincrement, "
				+ DBinfo.CLM_EVENT_NAME + " text not null, "
				+ DBinfo.CLM_EVENT_BILL + " double default 0, " 
				+ DBinfo.TRIP_ID + " integer, "
				+ DBinfo.CLM_EVENT_DATE + " date, "
				+ DBinfo.CLM_EVENT_PLACE + " text , "
				+ DBinfo.CLM_EVENT_PIC + " text , "
				+ DBinfo.CLM_EVENT_TYPE + " integer default 0,"
				+ DBinfo.CLM_EVENT_DESC + " text "
				+");";

		private static final String CREATE_SHARES = "create table "
				+ DBinfo.DB_TABLE_SHARES + " ( " + DBinfo.SHARE_ID
				+ " integer primary key autoincrement, " + DBinfo.EVENT_ID
				+ " integer, " + DBinfo.CLM_PAIDBY_MEMBERID + " integer,"
				+ DBinfo.CLM_SHAREDBY_MEMBERID + " integer, "
				+ DBinfo.CLM_SHARE_AMOUNT + " double default 0);";

		private static final String CREATE_TRIPMEMBERS = "create table "
				+ DBinfo.DB_TABLE_TRIPMEMBERS + " ( " + DBinfo.TRIPMEMBER_ID
				+ " integer primary key autoincrement, " + DBinfo.TRIP_ID
				+ " integer, " + DBinfo.MEMBER_ID + " integer);";

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs to
		// create new
		@Override
		public void onCreate(SQLiteDatabase db) {
			// db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_MEMBERS);

			db.execSQL(CREATE_MEMBERS);
			db.execSQL(CREATE_GROUPS);
			db.execSQL(CREATE_TRIPS);
			db.execSQL(CREATE_EVENTS);
			db.execSQL(CREATE_SHARES);
			db.execSQL(CREATE_TRIPMEMBERS);
			
			ContentValues values = new ContentValues();
			values.put(DBinfo.CLM_GROUP_NAME, "Main List");
			values.put(DBinfo.CLM_GROUP_THEME, 0);
			values.put(DBinfo.CLM_GROUP_DESC, "Defult Group Auto Created");
			db.insert(DBinfo.DB_TABLE_GROUPS, null, values);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			Log.w("TaskDBAdapter", "Downgrading from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
		/*	db.execSQL("DROP TABLE IF EXISTS " + DBinfo.DB_TABLE_MEMBERS);
			db.execSQL("DROP TABLE IF EXISTS " + DBinfo.DB_TABLE_TRIPS);
			db.execSQL("DROP TABLE IF EXISTS " + DBinfo.DB_TABLE_EVENTS);
			db.execSQL("DROP TABLE IF EXISTS " + DBinfo.DB_TABLE_SHARES);
			db.execSQL("DROP TABLE IF EXISTS " + DBinfo.DB_TABLE_TRIPMEMBERS);*/
			// Create a new one.
			//onCreate(db);
		}

		// Called when there is a database version mismatch meaning that
		// the version of the database on disk needs to be upgraded to
		// the current version.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log the version upgrade

			// The simplest case is to drop the old table and create a new
			// one.COPY all the data here to prevent any loss during upgrade
			switch (oldVersion) {
			case 0:
			case 1:
			case 2:
				Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion
						+ " to " + newVersion
						+ ", which will add new columns to Trips");
				
			/*	String IfNotExists="IF NOT EXISTS(SELECT NULL FROM INFORMATION_SCHEMA.COLUMNS WHERE " ;
				IfNotExists+=" table_name='"+DBinfo.DB_TABLE_TRIPS+"'";
				IfNotExists+=" AND table_schema='"+DBinfo.DB_NAME+"'";
				IfNotExists+=" AND column_name='"+DBinfo.CLM_TRIP_THEME+"'";
				IfNotExists+=" AND table_schema='"+DBinfo.DB_NAME+"')";
				IfNotExists+=" THEN ";*/
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_TRIPS + " ADD "+ DBinfo.CLM_TRIP_THEME + " integer default 0");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_TRIPS + " ADD "+ DBinfo.CLM_TRIP_TYPE + " integer default 0");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_TRIPS + " ADD "+ DBinfo.CLM_TRIP_DESC + " text ");
						
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_MEMBERS + " ADD "	+ DBinfo.CLM_MEMBER_PHNO + " text ");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_MEMBERS + " ADD "+ DBinfo.CLM_MEMBER_EMAIL + " text ");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_MEMBERS + " ADD "+ DBinfo.CLM_MEMBER_PIC + " text ");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_MEMBERS + " ADD "+ DBinfo.CLM_MEMBER_TYPE + " integer default 0");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_MEMBERS + " ADD "+ DBinfo.CLM_MEMBER_DESC + " text ");
						
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_EVENTS + " ADD "+ DBinfo.CLM_EVENT_DATE + " date");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_EVENTS + " ADD "+ DBinfo.CLM_EVENT_PLACE + " text ");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_EVENTS + " ADD "+ DBinfo.CLM_EVENT_PIC + " text ");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_EVENTS + " ADD "+ DBinfo.CLM_EVENT_TYPE + " integer default 0");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_EVENTS + " ADD "+ DBinfo.CLM_EVENT_DESC + " text ");
			case 3:
				Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion
						+ " to " + newVersion
						+ ", to accomodate userid registration");
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_MEMBERS + " ADD "+ DBinfo.CLM_MEMBER_UNAME + " text ");
			case 4:
				db.execSQL(" ALTER TABLE " + DBinfo.DB_TABLE_TRIPS + " ADD "+ DBinfo.CLM_TRIP_GROUPID + " integer default 1");
				db.execSQL(CREATE_GROUPS);
				
				ContentValues values = new ContentValues();
				values.put(DBinfo.CLM_GROUP_NAME, "Main List");
				values.put(DBinfo.CLM_GROUP_THEME, 0);
				values.put(DBinfo.CLM_GROUP_DESC, "Defult Group Auto Created");
				db.insert(DBinfo.DB_TABLE_GROUPS, null, values);
			}

		}

		public void runquery(SQLiteDatabase db, String query) {
			db.execSQL(query);
		}
	}

	

	public static final Uri MEMBER_URI = Uri
			.parse("content://com.projects.em/members");
	public static final Uri TRIP_URI = Uri
			.parse("content://com.projects.em/trips");
	public static final Uri GROUP_URI = Uri
			.parse("content://com.projects.em/groups");
	public static final Uri EVENT_URI = Uri
			.parse("content://com.projects.em/events");
	public static final Uri SHARE_URI = Uri
			.parse("content://com.projects.em/shares");
	public static final Uri SHARESUM_URI = Uri
			.parse("content://com.projects.em/sharesum");
	public static final Uri TRIPMEMBER_URI = Uri
			.parse("content://com.projects.em/tripmembers");
	
	public static final Uri RAW_URI = Uri
			.parse("content://com.projects.em/raw");
	
	// Create the constants used to differentiate between the different URI
	// requests.
	private static final int MEMBER_ALLROWS = 1;
	private static final int MEMBER_SINGLE_ROW = 2;
	private static final int TRIP_ALLROWS = 3;
	private static final int TRIP_SINGLE_ROW = 4;
	private static final int EVENT_ALLROWS = 5;
	private static final int EVENT_SINGLE_ROW = 6;
	private static final int SHARE_ALLROWS = 7;
	private static final int SHARE_SINGLE_ROW = 8;
	private static final int TRIPMEMBER_ALLROWS = 9;
	private static final int TRIPMEMBER_SINGLE_ROW = 10;
	private static final int SHARE_SUM_ALLROWS = 11;
	private static final int GROUP_ALLROWS = 12;
	private static final int GROUP_SINGLE_ROW = 13;
	private static final int RAW = 0;

	private static final UriMatcher uriMatcher;

	// Populate the UriMatcher object, where a URI ending in elements' will
	// correspond to a request for all items, and 'elements
	// /[rowID]' represents a single row.
	/**
	 * 
	 */

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.projects.em", "members", MEMBER_ALLROWS);
		uriMatcher.addURI("com.projects.em", "members/#", MEMBER_SINGLE_ROW);
		uriMatcher.addURI("com.projects.em", "trips", TRIP_ALLROWS);
		uriMatcher.addURI("com.projects.em", "trips/#", TRIP_SINGLE_ROW);
		uriMatcher.addURI("com.projects.em", "groups", GROUP_ALLROWS);
		uriMatcher.addURI("com.projects.em", "groups/#", GROUP_SINGLE_ROW);
		uriMatcher.addURI("com.projects.em", "events", EVENT_ALLROWS);
		uriMatcher.addURI("com.projects.em", "events/#", EVENT_SINGLE_ROW);
		uriMatcher.addURI("com.projects.em", "shares", SHARE_ALLROWS);
		uriMatcher.addURI("com.projects.em", "shares/#", SHARE_SINGLE_ROW);
		uriMatcher.addURI("com.projects.em", "sharesum", SHARE_SUM_ALLROWS);
		uriMatcher.addURI("com.projects.em", "tripmembers", TRIPMEMBER_ALLROWS);
		uriMatcher.addURI("com.projects.em", "tripmembers/#",TRIPMEMBER_SINGLE_ROW);
		uriMatcher.addURI("com.projects.em", "raw", RAW);
	}

	private static DBHelper dbHelper;
	public static ListItemClass getListItemClass(Uri uri, String where) {
		SQLiteDatabase db;
		String projection[];
		ListItemClass newItem = null;
		db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (uriMatcher.match(uri)) {
		case MEMBER_SINGLE_ROW:
		case MEMBER_ALLROWS:
			projection = new String[] { DBinfo.MEMBER_ID,DBinfo.CLM_MEMBER_NAME,DBinfo.CLM_MEMBER_PHNO,DBinfo.CLM_MEMBER_EMAIL,
					DBinfo.CLM_MEMBER_PIC,DBinfo.CLM_MEMBER_TYPE,DBinfo.CLM_MEMBER_DESC,DBinfo.CLM_MEMBER_UNAME};
			queryBuilder.setTables(DBinfo.DB_TABLE_MEMBERS);
			Cursor Membercursor = queryBuilder.query(db, projection, where,null, null, null, null);
			if (Membercursor.getCount() != 0) {
				Membercursor.moveToFirst();
				newItem = new ListItemClass(
						Membercursor.getInt(0),	Membercursor.getString(1),Membercursor.getString(2),Membercursor.getString(3),
						Membercursor.getString(4),Membercursor.getInt(5),Membercursor.getString(6),Membercursor.getString(7));
				return newItem;
			}
			break;
		case GROUP_SINGLE_ROW:
		case GROUP_ALLROWS:
			projection = new String[] { DBinfo.GROUP_ID, DBinfo.CLM_GROUP_NAME, DBinfo.CLM_GROUP_THEME,DBinfo.CLM_GROUP_TYPE,DBinfo.CLM_GROUP_DESC };
			queryBuilder.setTables(DBinfo.DB_TABLE_GROUPS);
			Cursor Groupcursor = queryBuilder.query(db, projection, where, null,
					null, null, null);
			if (Groupcursor.getCount() != 0) {
				Groupcursor.moveToFirst();
				newItem = new ListItemClass(
						Groupcursor.getInt(0),
						Groupcursor.getString(1),
						Groupcursor.getInt(2),
						Groupcursor.getInt(3),
						Groupcursor.getString(4)
						);
				return newItem;
			}
			break;
		case TRIP_SINGLE_ROW:
		case TRIP_ALLROWS:
			projection = new String[] { DBinfo.TRIP_ID, DBinfo.CLM_TRIP_NAME,DBinfo.CLM_TRIP_DATE, DBinfo.CLM_TRIP_PLACE,
					DBinfo.CLM_TRIP_PIC, DBinfo.CLM_TRIP_THEME,DBinfo.CLM_TRIP_TYPE,DBinfo.CLM_TRIP_DESC, DBinfo.CLM_TRIP_GROUPID };
			queryBuilder.setTables(DBinfo.DB_TABLE_TRIPS);
			Cursor Tripcursor = queryBuilder.query(db, projection, where, null,
					null, null, null);
			if (Tripcursor.getCount() != 0) {
				Tripcursor.moveToFirst();
				newItem = new ListItemClass(
						Tripcursor.getInt(0),
						Tripcursor.getString(1),
						Tripcursor.getString(2),
						Tripcursor.getString(3),
						Tripcursor.getString(4),
						Tripcursor.getInt(5),
						Tripcursor.getInt(6),
						Tripcursor.getString(7),
						Tripcursor.getInt(8)
						);
				return newItem;
			}
			break;
		case EVENT_SINGLE_ROW:
		case EVENT_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_EVENTS);
			projection = new String[] { DBinfo.EVENT_ID, DBinfo.CLM_EVENT_NAME,DBinfo.CLM_EVENT_BILL, DBinfo.TRIP_ID,
					DBinfo.CLM_EVENT_DATE,DBinfo.CLM_EVENT_PLACE,DBinfo.CLM_EVENT_TYPE,DBinfo.CLM_EVENT_PIC,DBinfo.CLM_EVENT_DESC};
			Cursor EventCursor = queryBuilder.query(db, projection, where,
					null, null, null, null);
			if (EventCursor.getCount() != 0) {
				EventCursor.moveToFirst();
				newItem = new ListItemClass
						(
						EventCursor.getInt(0),
						EventCursor.getString(1),
						EventCursor.getFloat(2),
						EventCursor.getInt(3),
						EventCursor.getString(4),
						EventCursor.getString(5),
						//EventCursor.getInt(6),
						EventCursor.getString(7),
						EventCursor.getString(8));
				return newItem;
			}
			break;
		case SHARE_SINGLE_ROW:
		case SHARE_ALLROWS:
			projection = new String[] { DBinfo.SHARE_ID, DBinfo.EVENT_ID,DBinfo.CLM_PAIDBY_MEMBERID, DBinfo.CLM_SHAREDBY_MEMBERID,
					 DBinfo.CLM_SHARE_AMOUNT};
		
			queryBuilder.setTables(DBinfo.DB_TABLE_SHARES);
			Cursor ShareCursor = queryBuilder.query(db, projection, where,null, null, null, null);
			if (ShareCursor.getCount() != 0) {
				ShareCursor.moveToFirst();
				newItem = new ListItemClass(ShareCursor.getInt(0),
						ShareCursor.getInt(1), ShareCursor.getInt(2),
						ShareCursor.getInt(3), ShareCursor.getFloat(4));
				return newItem;
			}
			break;
		case SHARE_SUM_ALLROWS:
			projection = new String[] { DBinfo.SHARE_ID, DBinfo.EVENT_ID,DBinfo.CLM_PAIDBY_MEMBERID, DBinfo.CLM_SHAREDBY_MEMBERID,
					"SUM(" + DBinfo.CLM_SHARE_AMOUNT + ")"};
		
			queryBuilder.setTables(DBinfo.DB_TABLE_SHARES);
			Cursor ShareSumCursor = queryBuilder.query(db, projection, where,null, null, null, null);
			if (ShareSumCursor.getCount() != 0) {
				ShareSumCursor.moveToFirst();
				newItem = new ListItemClass(ShareSumCursor.getInt(0),
						ShareSumCursor.getInt(1), ShareSumCursor.getInt(2),
						ShareSumCursor.getInt(3), ShareSumCursor.getFloat(4));
				return newItem;
			}
			break;

		case TRIPMEMBER_SINGLE_ROW:
		case TRIPMEMBER_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_TRIPMEMBERS);
			projection = new String[] { DBinfo.TRIPMEMBER_ID, DBinfo.TRIP_ID,
					DBinfo.MEMBER_ID };
			Cursor cursor = queryBuilder.query(db, projection, where, null,
					null, null, null);
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				newItem = new ListItemClass(cursor.getInt(0),
						cursor.getInt(1),
						cursor.getInt(2));
				return newItem;
			}
			break;
		}
		return newItem;
	}

	// TODO GetitemClass
	public static ArrayList<ListItemClass> getListItemClassArray(Uri uri,
			String where) {

		SQLiteDatabase db;
		ArrayList<ListItemClass> newItems;
		newItems = new ArrayList<ListItemClass>();
		String[] projection;
		// Log.w("DB_CP","Uri Number:(Matching)" + uriMatcher.match(uri));
		db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String sortOrder=null;
		switch (uriMatcher.match(uri)) {
		case MEMBER_SINGLE_ROW:
		case MEMBER_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_MEMBERS);
			projection = new String[] { DBinfo.MEMBER_ID,DBinfo.CLM_MEMBER_NAME,DBinfo.CLM_MEMBER_PHNO,DBinfo.CLM_MEMBER_EMAIL,
					DBinfo.CLM_MEMBER_PIC,DBinfo.CLM_MEMBER_TYPE,DBinfo.CLM_MEMBER_DESC,DBinfo.CLM_MEMBER_UNAME};
			sortOrder = "UPPER(" + DBinfo.CLM_MEMBER_NAME + ")" + " ASC";
			Cursor Membercursor = queryBuilder.query(db, projection, where,
					null, null, null,sortOrder);
			while (Membercursor.moveToNext())
				newItems.add(new ListItemClass(Membercursor.getInt(0),	Membercursor.getString(1),Membercursor.getString(2),Membercursor.getString(3),
						Membercursor.getString(4),Membercursor.getInt(5),Membercursor.getString(6),Membercursor.getString(7)));
			break;
		case TRIP_SINGLE_ROW:
		case TRIP_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_TRIPS);
			projection = new String[] { DBinfo.TRIP_ID, DBinfo.CLM_TRIP_NAME,DBinfo.CLM_TRIP_DATE, DBinfo.CLM_TRIP_PLACE,
					DBinfo.CLM_TRIP_PIC, DBinfo.CLM_TRIP_THEME,DBinfo.CLM_TRIP_TYPE,DBinfo.CLM_TRIP_DESC,DBinfo.CLM_TRIP_GROUPID };
			sortOrder =  Helper.formatSQLdate(DBinfo.CLM_TRIP_DATE) + " DESC";
			Cursor Tripcursor = queryBuilder.query(db, projection, where, null,
					null, null, sortOrder);
			
			while (Tripcursor.moveToNext())
				newItems.add(new ListItemClass(Tripcursor
						.getInt(0), Tripcursor
						.getString(1), Tripcursor
						.getString(2), Tripcursor
						.getString(3), Tripcursor
						.getString(4), Tripcursor
						.getInt(5), Tripcursor
						.getInt(6),Tripcursor
						.getString(7),
						Tripcursor.getInt(8)));
			break;
		case GROUP_SINGLE_ROW:
		case GROUP_ALLROWS:
			projection = new String[] { DBinfo.GROUP_ID, DBinfo.CLM_GROUP_NAME, DBinfo.CLM_GROUP_THEME,DBinfo.CLM_GROUP_TYPE,DBinfo.CLM_GROUP_DESC };
			queryBuilder.setTables(DBinfo.DB_TABLE_GROUPS);
			sortOrder = "UPPER(" + DBinfo.CLM_GROUP_NAME + ")" + " ASC";
			Cursor Groupcursor = queryBuilder.query(db, projection, where, null,
					null, null, sortOrder);
			
			while (Groupcursor.moveToNext())
				newItems.add(new ListItemClass(
						Groupcursor.getInt(0),
						Groupcursor.getString(1),
						Groupcursor.getInt(2),
						Groupcursor.getInt(3),
						Groupcursor.getString(4)));
			break;
		case EVENT_SINGLE_ROW:
		case EVENT_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_EVENTS);
			projection = new String[] { DBinfo.EVENT_ID, DBinfo.CLM_EVENT_NAME,
					DBinfo.CLM_EVENT_BILL, DBinfo.TRIP_ID };
			sortOrder = Helper.formatSQLdate(DBinfo.CLM_EVENT_DATE) +" ASC,"+DBinfo.EVENT_ID + " DESC";
			Cursor EventCursor = queryBuilder.query(db, projection, where,
					null, null, null, sortOrder);
			int EventTaskIndex[] = {
					EventCursor.getColumnIndexOrThrow(DBinfo.EVENT_ID),
					EventCursor.getColumnIndexOrThrow(DBinfo.CLM_EVENT_NAME),
					EventCursor.getColumnIndexOrThrow(DBinfo.CLM_EVENT_BILL),
					EventCursor.getColumnIndexOrThrow(DBinfo.TRIP_ID) };
			while (EventCursor.moveToNext())
				newItems.add(new ListItemClass(EventCursor
						.getInt(EventTaskIndex[0]), EventCursor
						.getString(EventTaskIndex[1]), EventCursor
						.getFloat(EventTaskIndex[2]), EventCursor
						.getInt(EventTaskIndex[3]),"","","",""));
			break;
		case SHARE_SINGLE_ROW:
		case SHARE_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_SHARES);
			projection = new String[] { DBinfo.SHARE_ID, DBinfo.EVENT_ID,
					DBinfo.CLM_PAIDBY_MEMBERID, DBinfo.CLM_SHAREDBY_MEMBERID,
					DBinfo.CLM_SHARE_AMOUNT };
			sortOrder =  Helper.formatSQLdate(DBinfo.CLM_EVENT_DATE) +" ASC,"+DBinfo.EVENT_ID + " DESC,"+DBinfo.SHARE_ID + " DESC";
			Cursor ShareCursor = queryBuilder.query(db, projection, where,
					null, null, null, null);
			int ShareTaskIndex[] = {
					ShareCursor.getColumnIndexOrThrow(DBinfo.SHARE_ID),
					ShareCursor.getColumnIndexOrThrow(DBinfo.EVENT_ID),
					ShareCursor
							.getColumnIndexOrThrow(DBinfo.CLM_PAIDBY_MEMBERID),
					ShareCursor
							.getColumnIndexOrThrow(DBinfo.CLM_SHAREDBY_MEMBERID),
					ShareCursor.getColumnIndexOrThrow(DBinfo.CLM_SHARE_AMOUNT) };
			while (ShareCursor.moveToNext())
			{
				newItems.add(new ListItemClass(ShareCursor
						.getInt(ShareTaskIndex[0]), ShareCursor
						.getInt(ShareTaskIndex[1]), ShareCursor
						.getInt(ShareTaskIndex[2]), ShareCursor
						.getInt(ShareTaskIndex[3]), ShareCursor
						.getFloat(ShareTaskIndex[4])));
			}
			break;
		case TRIPMEMBER_SINGLE_ROW:
		case TRIPMEMBER_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_TRIPMEMBERS);
			projection = new String[] { DBinfo.TRIPMEMBER_ID, DBinfo.TRIP_ID,
					DBinfo.MEMBER_ID };
			Cursor cursor = queryBuilder.query(db, projection, where, null,
					null, null, null);
			int TaskIndex[] = {
					cursor.getColumnIndexOrThrow(DBinfo.TRIPMEMBER_ID),
					cursor.getColumnIndexOrThrow(DBinfo.TRIP_ID),
					cursor.getColumnIndexOrThrow(DBinfo.MEMBER_ID) };
			while (cursor.moveToNext())
				newItems.add(new ListItemClass(cursor.getInt(TaskIndex[0]),
						cursor.getInt(TaskIndex[1]), cursor
								.getInt(TaskIndex[2])));
			break;
		}
		return newItems;
	}

	// TODO QUERY
	static public Cursor query(String query) {
		SQLiteDatabase db;
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}

		Cursor cursor = db.rawQuery(query, null);
		return cursor;
	}

	public static boolean RunQuery(String query) {
		SQLiteDatabase db;
		db = dbHelper.getWritableDatabase();
		dbHelper.runquery(db, query);
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int deleteCount = 0;
		String rowID = null;
		switch (uriMatcher.match(uri)) {
		case MEMBER_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			selection = DBinfo.MEMBER_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case MEMBER_ALLROWS:
			if (selection == null)
				selection = "1";
			deleteCount = db.delete(DBinfo.DB_TABLE_MEMBERS, selection,
					selectionArgs);
			break;
		case TRIP_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			selection = DBinfo.TRIP_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case TRIP_ALLROWS:
			if (selection == null)
				selection = "1";
			deleteCount = db.delete(DBinfo.DB_TABLE_TRIPS, selection,
					selectionArgs);
			break;
		case GROUP_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			selection = DBinfo.GROUP_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case GROUP_ALLROWS:
			if (selection == null)
				selection = "1";
			deleteCount = db.delete(DBinfo.DB_TABLE_GROUPS, selection,
					selectionArgs);
			break;
		case EVENT_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			selection = DBinfo.EVENT_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case EVENT_ALLROWS:
			if (selection == null)
				selection = "1";
			deleteCount = db.delete(DBinfo.DB_TABLE_EVENTS, selection,
					selectionArgs);
			break;
		case SHARE_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			selection = DBinfo.SHARE_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case SHARE_ALLROWS:
			if (selection == null)
				selection = "1";
			deleteCount = db.delete(DBinfo.DB_TABLE_SHARES, selection,
					selectionArgs);
			break;
		case TRIPMEMBER_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			selection = DBinfo.TRIPMEMBER_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case TRIPMEMBER_ALLROWS:
			if (selection == null)
				selection = "1";
			deleteCount = db.delete(DBinfo.DB_TABLE_TRIPMEMBERS, selection,
					selectionArgs);
			break;
		}
		// To return the number of deleted items you must specify a where
		// clause. To delete all rows and return a value pass in "1".
		// Notify any observers of the change in the data set.
		getContext().getContentResolver().notifyChange(uri, null);
		// Return the number of deleted items.
		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {
		// Return a string that identifies the MIME type for a Content Provider
		// URI
		switch (uriMatcher.match(uri)) {
		case MEMBER_ALLROWS:
		case TRIP_ALLROWS:
		case GROUP_ALLROWS:
		case EVENT_ALLROWS:
		case SHARE_ALLROWS:
		case SHARE_SUM_ALLROWS:
		case TRIPMEMBER_ALLROWS:
		case RAW:
			return "vnd.android.cursor.dir/vnd.em.elemental";
		case MEMBER_SINGLE_ROW:
		case EVENT_SINGLE_ROW:
		case TRIP_SINGLE_ROW:
		case GROUP_SINGLE_ROW:
		case SHARE_SINGLE_ROW:
		case TRIPMEMBER_SINGLE_ROW:
			return "vnd.android.cursor.item/vnd.em.elemental";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Open a read / write database to support the transaction.
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// To add empty rows to your database by passing in an empty Content
		// Values object you must use the null column hack parameter
		// to specify the name of the column that can be set to null.
		String nullColumnHack = null;
		long id = -1;// No entry id
		Uri insertedId = ContentUris.withAppendedId(uri, id);

		switch (uriMatcher.match(uri)) {
		case MEMBER_SINGLE_ROW:
		case MEMBER_ALLROWS:
			id = db.insert(DBinfo.DB_TABLE_MEMBERS, nullColumnHack, values);
			Log.w("DB_CP", "Adding Member");
			break;
		case TRIP_SINGLE_ROW:
		case TRIP_ALLROWS:
			id = db.insert(DBinfo.DB_TABLE_TRIPS, nullColumnHack, values);
			break;
		case GROUP_SINGLE_ROW:
		case GROUP_ALLROWS:
			id = db.insert(DBinfo.DB_TABLE_GROUPS, nullColumnHack, values);
			break;
		case EVENT_SINGLE_ROW:
		case EVENT_ALLROWS:
			id = db.insert(DBinfo.DB_TABLE_EVENTS, nullColumnHack, values);
			break;
		case SHARE_SINGLE_ROW:
		case SHARE_ALLROWS:
			id = db.insert(DBinfo.DB_TABLE_SHARES, nullColumnHack, values);
			break;
		case TRIPMEMBER_SINGLE_ROW:
		case TRIPMEMBER_ALLROWS:
			id = db.insert(DBinfo.DB_TABLE_TRIPMEMBERS, nullColumnHack, values);
			break;
		}
		// Log.w("DB_CPinsert", "inserted at"+ Integer.toString((int) id));
		// Insert the values into the table Notify any observers of the change
		// in the data set.
		if (id > -1) {
			insertedId = ContentUris.withAppendedId(uri, id);
			getContext().getContentResolver().notifyChange(insertedId, null);// Construct
																				// and
																				// return
																				// the
																				// URI
																				// of
																				// the
																				// newly
																				// inserted
																				// row.
			// Log.w("DB_CPinsert", "inserted at"+ insertedId);
		}
		return insertedId;
	}

	// TODO RunQuery

	@Override
	public boolean onCreate() {
		// Construct the underlying database.Defer opening the database until
		// you need to perform a query or transaction.
		dbHelper = new DBHelper(getContext(), DBinfo.DB_NAME, null,
				DBinfo.DB_VERSION);
		return true;
	}

	// TODO DBHELPER Class
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Open the database.
		// Log.w("DB_CP","Uri Number(query):" + uriMatcher.match(uri));
		SQLiteDatabase db;
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
		// Replace these with valid SQL statements if necessary.
		String groupBy = null;
		String having = null;
		// Use an SQLite Query Builder to simplify constructing the database
		// query.
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		// If this is a row query, limit the result set to the passed in row.
		String rowID;
		switch (uriMatcher.match(uri)) {
		case MEMBER_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBinfo.MEMBER_ID + "=" + rowID);
		case MEMBER_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_MEMBERS);
			if (sortOrder == null)
				sortOrder = "UPPER(" + DBinfo.CLM_MEMBER_NAME + ")" + " ASC";
			break;
		case TRIP_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBinfo.TRIP_ID + "=" + rowID);
		case TRIP_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_TRIPS);
			if (sortOrder == null)
				sortOrder =  Helper.formatSQLdate(DBinfo.CLM_TRIP_DATE) + " DESC";
			break;
		case GROUP_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBinfo.GROUP_ID + "=" + rowID);
		case GROUP_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_GROUPS);
			if (sortOrder == null)
				sortOrder = "UPPER(" + DBinfo.CLM_GROUP_NAME + ")" + " ASC";
			break;
		case EVENT_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBinfo.EVENT_ID + "=" + rowID);
		case EVENT_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_EVENTS);
	if (sortOrder == null)
		sortOrder = Helper.formatSQLdate(DBinfo.CLM_EVENT_DATE) +" ASC,"+DBinfo.EVENT_ID + " DESC";
			break;
		case SHARE_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBinfo.SHARE_ID + "=" + rowID);
		case SHARE_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_SHARES);
			if (sortOrder == null)
				sortOrder =  Helper.formatSQLdate(DBinfo.CLM_EVENT_DATE) +" ASC,"+DBinfo.EVENT_ID + " DESC,"+DBinfo.SHARE_ID + " DESC";
			break;
		case TRIPMEMBER_SINGLE_ROW:
			rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBinfo.TRIPMEMBER_ID + "=" + rowID);
		case TRIPMEMBER_ALLROWS:
			queryBuilder.setTables(DBinfo.DB_TABLE_TRIPMEMBERS);
			break;
		case RAW:
			return query(selection);
		}
		// Execute the query.
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, groupBy, having, sortOrder);
		// Return the result Cursor.
		return cursor;
	}

	// TODO Update
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		// Open a read / write database to support the transaction.
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// If this is a row URI, limit the deletion to the specified row.
		int updateCount = 0;
		String rowID = null;
		switch (uriMatcher.match(uri)) {
		case MEMBER_SINGLE_ROW:
			rowID = uri.getPathSegments().get(MEMBER_SINGLE_ROW);
			selection = DBinfo.MEMBER_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case MEMBER_ALLROWS:
			updateCount = db.update(DBinfo.DB_TABLE_MEMBERS, values, selection,
					selectionArgs);// Perform the update.
			break;
		case TRIP_SINGLE_ROW:
			rowID = uri.getPathSegments().get(TRIP_SINGLE_ROW);
			selection = DBinfo.TRIP_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case TRIP_ALLROWS:
			updateCount = db.update(DBinfo.DB_TABLE_TRIPS, values, selection,
					selectionArgs);// Perform the update.
			break;
		case GROUP_SINGLE_ROW:
			rowID = uri.getPathSegments().get(GROUP_SINGLE_ROW);
			selection = DBinfo.GROUP_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case GROUP_ALLROWS:
			updateCount = db.update(DBinfo.DB_TABLE_GROUPS, values, selection,
					selectionArgs);// Perform the update.
			break;
		case EVENT_SINGLE_ROW:
			rowID = uri.getPathSegments().get(EVENT_SINGLE_ROW);
			selection = DBinfo.EVENT_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case EVENT_ALLROWS:
			updateCount = db.update(DBinfo.DB_TABLE_EVENTS, values, selection,
					selectionArgs);// Perform the update.
			break;
		case SHARE_SINGLE_ROW:
			rowID = uri.getPathSegments().get(SHARE_SINGLE_ROW);
			selection = DBinfo.SHARE_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case SHARE_ALLROWS:
			updateCount = db.update(DBinfo.DB_TABLE_SHARES, values, selection,
					selectionArgs);// Perform the update.
			break;
		case TRIPMEMBER_SINGLE_ROW:
			rowID = uri.getPathSegments().get(TRIPMEMBER_SINGLE_ROW);
			selection = DBinfo.TRIPMEMBER_ID
					+ "="
					+ rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
		case TRIPMEMBER_ALLROWS:
			updateCount = db.update(DBinfo.DB_TABLE_TRIPMEMBERS, values,
					selection, selectionArgs);// Perform the update.

		}
		// Notify any observers of the change in the data set.
		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}

	public static int[] getAllTripIds() {
		// TODO Auto-generated method stub
		int[] tripIds;
		List<ListItemClass> AllTrips =getListItemClassArray(TRIP_URI, " 1 ");
		tripIds=new int[AllTrips.size()];
		for(int i=0;i<AllTrips.size();i++)
			tripIds[i]=AllTrips.get(i).toInt();
		return tripIds;
	}
	public static String[] getAllGroupNames() {
		// TODO Auto-generated method stub
		String[] groupNames;
		List<ListItemClass> allGroups =getListItemClassArray(GROUP_URI, " 1 ");
		groupNames=new String[allGroups.size()];
		for(int i=0;i<allGroups.size();i++)
			groupNames[i]=allGroups.get(i).toString();
		return groupNames;
	}
	public static int[] getAllGroupIds() {
		// TODO Auto-generated method stub
		int[] groupNames;
		List<ListItemClass> allGroups =getListItemClassArray(GROUP_URI, " 1 ");
		groupNames=new int[allGroups.size()];
		for(int i=0;i<allGroups.size();i++)
			groupNames[i]=allGroups.get(i).toInt();
		return groupNames;
	}
	/*
	 * //TODO parcelDesc
	 * 
	 * @Override public ParcelFileDescriptor openFile(Uri uri, String mode)
	 * throws FileNotFoundException {
	 * 
	 * // Find the row ID and use it as a filename. String rowID =
	 * uri.getPathSegments().get(1);
	 * 
	 * // Create a file object in the application's external // files directory.
	 * 
	 * File file =null;
	 * 
	 * 
	 * // If the file doesn't exist, create it now. if (!file.exists()) { try {
	 * file.createNewFile(); } catch (IOException e) { Log.d(TAG,
	 * "File creation failed: " + e.getMessage()); } }
	 * 
	 * // Translate the mode parameter to the corresponding Parcel File //
	 * Descriptor open mode. int fileMode = 0; if (mode.contains("w")) fileMode
	 * |= ParcelFileDescriptor.MODE_WRITE_ONLY; if (mode.contains("r")) fileMode
	 * |= ParcelFileDescriptor.MODE_READ_ONLY; if (mode.contains("+")) fileMode
	 * |= ParcelFileDescriptor.MODE_APPEND;
	 * 
	 * // Return a Parcel File Descriptor that represents the file. return
	 * ParcelFileDescriptor.open(file, fileMode); }
	 */
}
