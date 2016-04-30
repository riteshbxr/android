package com.ritesh.imei;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Settings.System;

public class IMEIProvider extends ContentProvider {
    public IMEIProvider() {
        super();
    }

    /**
     * @desc Class to define all Strings and other variables
     */
    public static final class IMEIContract{
        private static String authority="content://com.ritesh.com.ritesh.imei.provider";
        private static final String DBNAME="LE.db";
        private static final int DBversion=1;
        public static abstract class IMEITable implements BaseColumns {
            public static final String TABLE_NAME = "imeiTable";
            public static final String CLM_imeiItemName = "imeiName";
            public static final String CLM_imeiNo = "imeiNo";
            public static final String CLM_imeiCo = "imeiCompany";
            public static final String CLM_imeiModel = "imeiModel";
            public static final String CLM_imeiDesc = "imeiDesc";
            public static final String CLM_imeiDate = "imeiDate";
            public static final String CLM_imeiCustDetail = "imeiCust";
        }

        private static final String SQL_CREATE_IMEI="CREATE TABLE "+ IMEITable.TABLE_NAME+"("
            + IMEITable._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IMEITable.CLM_imeiNo+ " TEXT UNIQUE NOT NULL, "
            + IMEITable.CLM_imeiItemName+ " TEXT DEFAULT 'N A', "
            + IMEITable.CLM_imeiCo+ " TEXTDEFAULT 'N A', "
            + IMEITable.CLM_imeiModel+ " TEXT DEFAULT 'N A', "
            + IMEITable.CLM_imeiCustDetail+ " TEXT DEFAULT 'N A', "
            + IMEITable.CLM_imeiDate+ " DATE DEFAULT datetime('now'), "
            + IMEITable.CLM_imeiDesc+ " TEXT DEFAULT 'No Desc..'";

        private static final int IMEI_ALLROWS = 1;
        private static final int IMEI_SINGLE_ROW = 2;
        private static UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        static {
            uriMatcher.addURI("content://com.ritesh.com.ritesh.imei", IMEIContract.IMEITable.TABLE_NAME,IMEI_ALLROWS);
            uriMatcher.addURI("content://com.ritesh.com.ritesh.imei", IMEIContract.IMEITable.TABLE_NAME+"/#",IMEI_SINGLE_ROW);
        }


    }

    /**
     * @desc Class for Databaae level management
     */
    protected static final class DBOpener extends SQLiteOpenHelper{
        DBOpener(Context context)
        {
            super(context, IMEIContract.DBNAME,null, IMEIContract.DBversion);
        }

        /**
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(IMEIContract.SQL_CREATE_IMEI);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldID, int newID) {
            switch(oldID)
            {
                case 1:
                    //all the changes after version 1
            }
        }
        public void runquery(SQLiteDatabase db, String query) {
            db.execSQL(query);
        }
    }

    /**
     * All the Overrides for content provider
     */
    private DBOpener dbOpener;
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db= dbOpener.getWritableDatabase();
        boolean match=false;
        int count=0;
        switch (IMEIContract.uriMatcher.match(uri)){

            case IMEIContract.IMEI_SINGLE_ROW:
                selection+="_ID ="+uri.getLastPathSegment();
            case IMEIContract.IMEI_ALLROWS:
                match=true;
                break;
        }
        if(match)
        {
            count=db.delete(IMEIContract.IMEITable.TABLE_NAME, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public String getType(Uri uri) {
        return getContext().getContentResolver().getType(System.CONTENT_URI);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db= dbOpener.getWritableDatabase();
        long inserted_id=-1;
        Uri id= ContentUris.withAppendedId(uri, inserted_id);
        switch (IMEIContract.uriMatcher.match(uri)){
            case IMEIContract.IMEI_ALLROWS:
                inserted_id=db.insert(IMEIContract.IMEITable.TABLE_NAME,null, values);
                if (inserted_id > -1) {
                    id = ContentUris.withAppendedId(uri, inserted_id);
                    getContext().getContentResolver().notifyChange(id, null);
                }
        }
        return id;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db= dbOpener.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String having=null;
        String groupBy=null;
        switch (IMEIContract.uriMatcher.match(uri)){
            case IMEIContract.IMEI_ALLROWS:
                sortOrder=sortOrder.isEmpty()? IMEIContract.IMEITable.CLM_imeiDate+" ASC":sortOrder;
                    break;
            case IMEIContract.IMEI_SINGLE_ROW:
                selection+="_ID ="+uri.getLastPathSegment();
                break;
        }
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);
        // Return the result Cursor.
        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db= dbOpener.getWritableDatabase();
        boolean match=false;
        int count=0;
        switch (IMEIContract.uriMatcher.match(uri)){
            case IMEIContract.IMEI_SINGLE_ROW:
                selection+="_ID ="+uri.getLastPathSegment();
            case IMEIContract.IMEI_ALLROWS:
                match=true;
                break;
        }
        if(match)
        {
            count=db.update(IMEIContract.IMEITable.TABLE_NAME,values,selection,selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }
}
