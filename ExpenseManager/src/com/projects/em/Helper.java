package com.projects.em;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.projects.em.DB_CP.DBinfo;
/**
 * This Class is a helper class for all common Functions to be used all over the
 * project
 * 
 */
public class Helper {
	public static int ic_SelectAll, ic_SelectNone;
	public static final int TRIPS = 0;
	public static final int EVENTS = 1;;
	public static final int MEMBERS = 2;
	public static final int GROUPS = 3;
	
	public static final int FILESAVEINTENT = 1;
	
	public static final String JSONTYPE="TreckonTripType";
	public static final int JSONTYPE_SINGLETRIP = 1;
	public static final int JSONTYPE_TRIPARRAY = 2;
	 public static  String SENDER_ID;
	public static class MyPreferences
	 {
		 public static final String EXTRA_MESSAGE = "message";
		 public static final String PROPERTY_REG_ID = "registration_id";
		 public static final String PROPERTY_UNAME = "uname";
		 public static final String PROPERTY_PASS = "pass";
		 public static final String PROPERTY_APP_VERSION = "appVersion";
		 public static final String TAG = "Preferences";

		 /**
		  * @return Application's {@code SharedPreferences}.
		  */
		 public static SharedPreferences getAppPreferences(Activity activity) {
		  // This sample app persists the registration ID in shared preferences, but
		     // how you store the regID in your app is up to you.
		     return activity.getApplicationContext().getSharedPreferences(null, Context.MODE_PRIVATE);
		 }
		 public static SharedPreferences getAppPreferences(Context context) {
			  // This sample app persists the registration ID in shared preferences, but
			     // how you store the regID in your app is up to you.
			     return context.getSharedPreferences(null, Context.MODE_PRIVATE);
			 } 
		 /**
		  * Gets the current registration ID for application on GCM service.
		  * <p>
		  * If result is empty, the app needs to register.
		  *
		  * @return registration ID, or empty string if there is no existing
		  *         registration ID.
		  */
		 public static String getRegistrationId(Activity context) {
			 SENDER_ID=context.getResources().getString(R.string.GCMSenderid);
			 final SharedPreferences prefs = getAppPreferences(context);
		     String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		     if (registrationId.isEmpty()) {
		         Log.i(TAG, "Registration not found.");
		         return "";
		     }
		     // Check if app was updated; if so, it must clear the registration ID
		     // since the existing regID is not guaranteed to work with the new
		     // app version.
		     int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		     int currentVersion = getAppVersion(context.getApplicationContext());
		     if (registeredVersion != currentVersion) {
		         Log.i(TAG, "App version changed.");
		         return "";
		     }
		     return registrationId;
		 }
		 public static String getuname(Activity context) {
		     return getAppPreferences(context).getString(PROPERTY_UNAME, "");
		 }
		 public static String getpass(Activity context) {
		     return getAppPreferences(context).getString(PROPERTY_PASS, "");
		 }
		 /**
		  * @return Application's version code from the {@code PackageManager}.
		  */
		 public static int getAppVersion(Context context) {
		     try {
		         PackageInfo packageInfo = context.getPackageManager()
		                 .getPackageInfo(context.getPackageName(), 0);
		         return packageInfo.versionCode;
		     } catch (NameNotFoundException e) {
		         // should never happen
		         throw new RuntimeException("Could not get package name: " + e);
		     }
		 }
		 
		 public static void setPreferences(Context context, String preference,String value)
		 {
	     	final SharedPreferences prefs = Helper.MyPreferences.getAppPreferences(context);
	        SharedPreferences.Editor editor = prefs.edit();
	        editor.putString(preference, value);
	        editor.commit();
		 }
		 
		 public static String getPreferences(Context context, String preference)
		 {
			 return getAppPreferences(context).getString(preference,"");
		 }
	 }
	
	public static class ExcutelinkOnline extends AsyncTask<String, Void, String> {
		private String link;
		public ExcutelinkOnline(String link) {
		  this.link=link;
			 }
        
		@Override
        protected String doInBackground(String... params) {
		   SendMessageOnline(link);
		return link;
        }
	}
        
	public static void SendMessageOnline(String link)
	{
		  URI url = null;
		  try {
		   url = new URI(link);
		   
		  } catch (URISyntaxException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		   
		  } 
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpGet request = new HttpGet();
		  request.setURI(url);
		  try {
		   httpclient.execute(request);
		  } catch (ClientProtocolException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		   
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		   
		  }
		  catch(Exception e)
		  	{
			  e.printStackTrace();		
		  	}
	}
	
	
	
	public static final class ThumbnailDetails {
		public static final int width = 100;
		public static final int height = 100;
	}
	public class EVENTCLASS
	{
		int eventId;
		ListItemClass eventDetails;
		List<ListItemClass> shares;
		public EVENTCLASS() {
			eventId=-1;
			eventDetails=new ListItemClass();
			shares=new ArrayList<ListItemClass>();
			//shares=new ArrayList<ListItemClass>();
		}
		
		public void setfromEventID(int EventId)
		{
			eventId=EventId;
			eventDetails=DB_CP.getListItemClass(DB_CP.EVENT_URI, DBinfo.EVENT_ID +"="+eventId);
	//Addding Shares
			shares=DB_CP.getListItemClassArray(DB_CP.SHARE_URI, DBinfo.EVENT_ID +"="+eventId);			
			// TODO Auto-generated constructor stub
		}
		private JSONArray getJSONforSHARES()
		{
			JSONArray jsonShares=new JSONArray();
			try {
				for(int i=0;i<this.shares.size();i++)
				{
					JSONObject obj = new JSONObject();
					obj.put(DBinfo.SHARE_ID, this.shares.get(i).toInt());
					obj.put(DBinfo.EVENT_ID, this.shares.get(i).getIntItemAsked(ListItemClass.EVENTID_FORSHARES));
					obj.put(DBinfo.CLM_SHAREDBY_MEMBERID, this.shares.get(i).getIntItemAsked(ListItemClass.SHAREDBY_MEMBERID));
					obj.put(DBinfo.CLM_PAIDBY_MEMBERID, this.shares.get(i).getIntItemAsked(ListItemClass.PAIDBY_MEMBERID));
					obj.put(DBinfo.CLM_SHARE_AMOUNT, this.shares.get(i).getAmount());
					jsonShares.put(obj);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("JSOn Share ERROR",e.getMessage());
			}
			return(jsonShares);
		}
		public JSONObject getJSONforEvent()
		{
			JSONObject obj = new JSONObject();
	        try {
				obj.put(DBinfo.EVENT_ID, this.eventDetails.toInt());
				obj.put(DBinfo.CLM_EVENT_NAME, this.eventDetails.getStringItemAsked(ListItemClass.MAIN_ITEM));
				obj.put(DBinfo.TRIP_ID, this.eventDetails.getIntItemAsked(ListItemClass.TRIPID_FOREVENT));
				obj.put(DBinfo.CLM_EVENT_PLACE, this.eventDetails.getStringItemAsked(ListItemClass.PLACE));
				obj.put(DBinfo.CLM_EVENT_DATE, this.eventDetails.getStringItemAsked(ListItemClass.DATE));
				obj.put(DBinfo.CLM_EVENT_DESC, this.eventDetails.getStringItemAsked(ListItemClass.DESC));
				obj.put(DBinfo.CLM_EVENT_BILL, this.eventDetails.getAmount());
				obj.put(DBinfo.DB_TABLE_SHARES, this.getJSONforSHARES());
	        } 
	        catch (JSONException e) 
	        {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("JSOn Event ERROR",e.getMessage());
	        }
		    return obj;
		}
		public void setEventFromJSON(JSONObject jObj)
		{
			eventDetails=new ListItemClass();
				try 
				{
			        this.eventDetails.setItemAsked(ListItemClass.ID, jObj.getInt(DBinfo.EVENT_ID));
//			        this.eventDetails.setItemAsked(ListItemClass.OLD_ID, jObj.getInt(DBinfo.EVENT_ID));
			        this.eventDetails.setItemAsked(ListItemClass.MAIN_ITEM, jObj.getString(DBinfo.CLM_EVENT_NAME));
			        this.eventDetails.setItemAsked(ListItemClass.PLACE, jObj.getString(DBinfo.CLM_EVENT_PLACE));
			        this.eventDetails.setItemAsked(ListItemClass.DATE, jObj.getString(DBinfo.CLM_EVENT_DATE));
			        this.eventDetails.setItemAsked(ListItemClass.BILL, jObj.getDouble(DBinfo.CLM_EVENT_BILL));
			        this.eventDetails.setItemAsked(ListItemClass.DESC, jObj.getString(DBinfo.CLM_EVENT_DESC));				
			        JSONArray parsedData = jObj.getJSONArray(DBinfo.DB_TABLE_SHARES);
			        for(int i = 0; i<parsedData.length(); ++i)
			        {
			            JSONObject Jshare=parsedData.getJSONObject(i);
			        	ListItemClass newShare=new ListItemClass(Jshare.getInt(DBinfo.SHARE_ID),Jshare.getInt(DBinfo.EVENT_ID),
			            		Jshare.getInt(DBinfo.CLM_PAIDBY_MEMBERID),Jshare.getInt(DBinfo.CLM_SHAREDBY_MEMBERID),(float) Jshare.getDouble(DBinfo.CLM_SHARE_AMOUNT));
			            shares.add(newShare);
			        }
			    } catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("Shares from JSON", e.getMessage());
				}			    
		}
	}
	
	
	public class TRIPCLASS
	{
		int tripId,groupId;
		ListItemClass tripDetails;
		List<EVENTCLASS> events;
		List<ListItemClass> members;
		List<Integer> tripMembers,tripMembersOld;
		public TRIPCLASS(int groupId) {
			tripId=-1;
			this.groupId=groupId;
			tripDetails=new ListItemClass();
			members=new ArrayList<ListItemClass>();
			events=new ArrayList<Helper.EVENTCLASS>();
			tripMembers=new ArrayList<Integer>();
			tripMembersOld=new ArrayList<Integer>();
		}
		
		
		private boolean AddMembers(Context context)
		{
			ContentResolver cr=context.getContentResolver();
			int newid=0;
			String value="";
			ListItemClass tempMember=null;
			for (ListItemClass item : members) {
				value=item.getStringItemAsked(ListItemClass.UNAME);
				if(value.isEmpty())
				{
					Toast.makeText(context, item.toString() +" does not have its uname specified\nplease specify uname for all Trip contacts before exporting/sending",Toast.LENGTH_LONG).show();
					return false;
				}
				
				tempMember=DB_CP.getListItemClass(DB_CP.MEMBER_URI,
						DBinfo.CLM_MEMBER_UNAME+"='"+item.getStringItemAsked(ListItemClass.UNAME)+"'");
				if(tempMember==null)
				{	
					value=item.getStringItemAsked(ListItemClass.PHNO);
					String query=DBinfo.CLM_MEMBER_PHNO+" like ('%' ||substr('"+value+"',-9)) and "+DBinfo.CLM_MEMBER_UNAME + " is NULL" ;
//					Log.w("PH NO",query);
					if (value.length()>3) tempMember=DB_CP.getListItemClass(DB_CP.MEMBER_URI,query);
					
					if(tempMember==null )
					{
						value=item.getStringItemAsked(ListItemClass.EMAIL);
						if (value.length()>3) tempMember=DB_CP.getListItemClass(DB_CP.MEMBER_URI,
								DBinfo.CLM_MEMBER_EMAIL+"='"+value+"' and "+DBinfo.CLM_MEMBER_UNAME + " is NULL");
					}
				}
				
				if(tempMember==null)
				{
				newid=getIntIfParsable(
				AddMember(cr, item.getStringItemAsked(ListItemClass.MAIN_ITEM),
					item.getStringItemAsked(ListItemClass.PHNO),
					item.getStringItemAsked(ListItemClass.EMAIL),
					item.getStringItemAsked(ListItemClass.UNAME)
					));
				}
				else
					{
						newid=tempMember.toInt();
						//Log.w("Name Matching", "DB/JSON:"+tempMember.toString()+"/"+item.toString());
					}

				tripMembers.add(newid);
				tripMembersOld.add(item.toInt());
				item.setItemAsked(ListItemClass.ID,newid);
				//item.setItemAsked(ListItemClass.OLD_ID, item.toInt());
			}
			return true;
					
		}
		
		public boolean AddTrip(Context context)
		{
			return AddTrip(context,0);
		}
		public boolean AddTrip(Context context, int TripSerialNo)
		{
			ContentResolver cr=context.getContentResolver();
			ListItemClass tempTrip=DB_CP.getListItemClass(DB_CP.TRIP_URI,
					DBinfo.CLM_TRIP_NAME+"='"+tripDetails.toString()+"'");
			
			if(tempTrip!=null)
			{
				if(TripSerialNo>0)
					return false;//Toast.makeText(context, "Duplicate for Trip No "+TripSerialNo+" ("+tripDetails.toString()+") found. Please rename before importing",Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(context, "Duplicate for Trip ("+tripDetails.toString()+") found. Please rename before importing",Toast.LENGTH_LONG).show();
				return false;
			}
			if(this.AddMembers(context)==false)
			{
				if(TripSerialNo>0)
					return false;//Toast.makeText(context, "Trip"+TripSerialNo+" ("+tripDetails.toString()+") Not imported\nError while adding members",Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(context, "Trip ("+tripDetails.toString()+") Not imported\nError while adding members, Please Check Member Details to be exported",Toast.LENGTH_LONG).show();
				return false;
			}
			
			int[] membersArray=new int[tripMembers.size()];
			for(int i=0;i<tripMembers.size();i++)
				membersArray[i]=tripMembers.get(i);
				String id=Helper.AddTrip(cr, tripDetails.getStringItemAsked(ListItemClass.MAIN_ITEM), 
					tripDetails.getStringItemAsked(ListItemClass.DATE),
					tripDetails.getStringItemAsked(ListItemClass.PLACE),
					membersArray, 0,groupId);
//			tripDetails.setItemAsked(ListItemClass.OLD_ID, tripDetails.toInt());
			tripId= getIntIfParsable(id);
			tripDetails.setItemAsked(ListItemClass.ID,tripId);
			this.AddEvents(cr,tripId);
			if(TripSerialNo>0)
				return true;//Toast.makeText(context, "Trip No:"+TripSerialNo+"("+tripDetails.toString()+") imported Successfully",Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(context, "Trip ("+tripDetails.toString()+") imported Successfully",Toast.LENGTH_LONG).show();
				
			return true;
			
		}
		private void AddEvents(ContentResolver cr,int TripIdforevent)
		{
			String id="";
			ListItemClass share;
			int oldPaidId=-1,oldSharedId=-1;
			for (EVENTCLASS item : events) {
			    ListItemClass event=item.eventDetails;
			    for (int j=0;j<item.shares.size();j++)
			    {
			    	share=item.shares.get(j);
			    	oldPaidId=share.getIntItemAsked(ListItemClass.PAIDBY_MEMBERID);
			    	oldSharedId=share.getIntItemAsked(ListItemClass.SHAREDBY_MEMBERID);
			    	for (int i=0;i<tripMembersOld.size();i++)
				    {
			    		if(tripMembersOld.get(i)==oldPaidId)
			    			item.shares.get(j).setItemAsked(ListItemClass.PAIDBY_MEMBERID,tripMembers.get(i));
			    		else if(tripMembersOld.get(i)==oldSharedId)
			    			item.shares.get(j).setItemAsked(ListItemClass.SHAREDBY_MEMBERID,tripMembers.get(i));
				    }
			    }
			    id=Helper.AddEvent(cr, event.getStringItemAsked(ListItemClass.MAIN_ITEM),
					event.getAmount(), 
					TripIdforevent,
					event.getStringItemAsked(ListItemClass.DATE),
					event.getStringItemAsked(ListItemClass.PLACE), 
					event.getStringItemAsked(ListItemClass.DESC),
					event.getStringItemAsked(ListItemClass.PIC_FILE),item.shares);
				
				//event.setItemAsked(ListItemClass.OLD_ID, item.eventDetails.toInt());
			    item.eventId=getIntIfParsable(id);
				event.setItemAsked(ListItemClass.ID, item.eventId);
				event.setItemAsked(ListItemClass.TRIPID_FOREVENT, TripIdforevent);
			}
		}
		
		
		public void setfromTripID(int TripId)
		{
			tripId=TripId;
			tripDetails=DB_CP.getListItemClass(DB_CP.TRIP_URI, DBinfo.TRIP_ID+"="+tripId);
	//Adding Events
			Cursor cursor = DB_CP.query("SELECT " + DBinfo.EVENT_ID + " FROM " + DBinfo.DB_TABLE_EVENTS+
										" WHERE " + DBinfo.TRIP_ID + " = "+ tripId);
			EVENTCLASS newEvent;
			while (cursor.moveToNext()) 
			{
				newEvent=new EVENTCLASS();
				newEvent.setfromEventID(cursor.getInt(0));
				events.add(newEvent);
			}	
	//Adding Members
			Cursor cursor2 = DB_CP.query("SELECT " + DBinfo.MEMBER_ID + " FROM " + DBinfo.DB_TABLE_TRIPMEMBERS+
							" WHERE " + DBinfo.TRIP_ID + " = "+ tripId);
			ListItemClass newMember;
			while (cursor2.moveToNext()) 
			{
				newMember=DB_CP.getListItemClass(DB_CP.MEMBER_URI, DBinfo.MEMBER_ID+"="+cursor2.getInt(0));
				members.add(newMember);
			}
		}
		private JSONArray getJSONforTripMembers()
		{
			JSONArray jsonMembers=new JSONArray();
			try {
				for(ListItemClass member:members)
				{
					JSONObject obj = new JSONObject();
					obj.put(DBinfo.MEMBER_ID, member.toInt());
					obj.put(DBinfo.CLM_MEMBER_NAME, member.getStringItemAsked(ListItemClass.MAIN_ITEM));
					obj.put(DBinfo.CLM_MEMBER_EMAIL, member.getStringItemAsked(ListItemClass.EMAIL));
					obj.put(DBinfo.CLM_MEMBER_PHNO, member.getStringItemAsked(ListItemClass.PHNO));
					obj.put(DBinfo.CLM_MEMBER_DESC, member.getStringItemAsked(ListItemClass.DESC));
					obj.put(DBinfo.CLM_MEMBER_UNAME, member.getStringItemAsked(ListItemClass.UNAME));
					jsonMembers.put(obj);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("members",e.toString());
			}
			return(jsonMembers);
		}
		
		
		public JSONObject getJSONforTrip()
		{
		    	 JSONObject obj = new JSONObject();
			        try {
			        	obj.put(JSONTYPE, JSONTYPE_SINGLETRIP);
						obj.put(DBinfo.TRIP_ID, this.tripDetails.toInt());
						obj.put(DBinfo.CLM_TRIP_NAME, this.tripDetails.getStringItemAsked(ListItemClass.MAIN_ITEM));
						obj.put(DBinfo.CLM_TRIP_PLACE, this.tripDetails.getStringItemAsked(ListItemClass.PLACE));
						obj.put(DBinfo.CLM_TRIP_DATE, this.tripDetails.getStringItemAsked(ListItemClass.DATE));
						obj.put(DBinfo.CLM_TRIP_DESC, this.tripDetails.getStringItemAsked(ListItemClass.DESC));
						//Log.w("Trip JSOn Creation",DBinfo.CLM_TRIP_DESC+":"+obj.getString(DBinfo.CLM_TRIP_DESC));
						JSONArray jsonEvents=new JSONArray();
						for(int i=0;i<this.events.size();i++)
						{
							jsonEvents.put(this.events.get(i).getJSONforEvent());
						}
						obj.put(DBinfo.DB_TABLE_EVENTS, jsonEvents);
						obj.put(DBinfo.DB_TABLE_MEMBERS, this.getJSONforTripMembers());
		
			        } 
			        catch (JSONException e) 
			        {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.w("Trip JSOn Creation",e.getMessage());
			        }
		    return obj;
		}
		
		private boolean setTripFromJSON(JSONObject jObj)
		{
				try 
				{
					if(jObj.isNull(JSONTYPE)) return false;
					else if(jObj.getInt(JSONTYPE)!=JSONTYPE_SINGLETRIP) return false;
					
					this.tripDetails.setItemAsked(ListItemClass.ID, jObj.getInt(DBinfo.TRIP_ID));
			        this.tripDetails.setItemAsked(ListItemClass.MAIN_ITEM, jObj.getString(DBinfo.CLM_TRIP_NAME));
			        this.tripDetails.setItemAsked(ListItemClass.PLACE, jObj.getString(DBinfo.CLM_TRIP_PLACE));
			        this.tripDetails.setItemAsked(ListItemClass.DATE, jObj.getString(DBinfo.CLM_TRIP_DATE));
			        this.tripDetails.setItemAsked(ListItemClass.DESC, jObj.getString(DBinfo.CLM_TRIP_DESC));
//Members			        
			         JSONArray parsedData = jObj.getJSONArray(DBinfo.DB_TABLE_MEMBERS);
			         members.clear();
			        for(int i = 0; i<parsedData.length(); ++i)
			        {
			        	JSONObject jMembers=parsedData.getJSONObject(i);
			            ListItemClass newMember=new ListItemClass(jMembers.getInt(DBinfo.MEMBER_ID),
			            		jMembers.getString(DBinfo.CLM_MEMBER_NAME),
			            		jMembers.getString(DBinfo.CLM_MEMBER_PHNO),
			            		jMembers.getString(DBinfo.CLM_MEMBER_EMAIL),"",0,
			            		jMembers.getString(DBinfo.CLM_MEMBER_DESC),
			            		jMembers.isNull(DBinfo.CLM_MEMBER_UNAME)?"":jMembers.getString(DBinfo.CLM_MEMBER_UNAME));
			            members.add(newMember);
			        }
//Events
			        parsedData = jObj.getJSONArray(DBinfo.DB_TABLE_EVENTS);
			        events.clear();
			        for(int i = 0; i<parsedData.length(); ++i)
			        {
			            EVENTCLASS NewEvent = new EVENTCLASS();
			            NewEvent.setEventFromJSON(parsedData.getJSONObject(i));
			            events.add(NewEvent);
			        }
			        
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("JSOn Member ERROR",e.getMessage());
				}			    
				return true;
		}
	
	}
	//Class TRIPCLASS ENDS HERE

	public static String AddEvent(ContentResolver cr, 
			String name, String bill,int TripId,String EventDate,String EventPlace, String EventDesc,String EventPic,
			List<ListItemClass> members, float[] memberPaid,float[] memberShared) 
	{
		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_EVENT_NAME, name);
		values.put(DBinfo.CLM_EVENT_BILL, bill);
		values.put(DBinfo.TRIP_ID, TripId);
		values.put(DBinfo.CLM_EVENT_DATE,EventDate);
		values.put(DBinfo.CLM_EVENT_PLACE, EventPlace);
		values.put(DBinfo.CLM_EVENT_TYPE, 0);
		values.put(DBinfo.CLM_EVENT_DESC, EventDesc);
		values.put(DBinfo.CLM_EVENT_PIC, EventPic);
		
		Uri inserted_uri = cr.insert(DB_CP.EVENT_URI, values);
		String insertedId = (inserted_uri.getPathSegments().get(1));
		// Inserting Paid/shared list in Shares Table
		UpdateShares(cr, Helper.getIntIfParsable(insertedId), members,memberPaid, memberShared);
		return (insertedId);
	}

	public static String AddMember(ContentResolver cr, String name, String number, String email,String uname) {

		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_MEMBER_NAME, name);
		values.put(DBinfo.CLM_MEMBER_PHNO, number);
		values.put(DBinfo.CLM_MEMBER_EMAIL, email);
		values.put(DBinfo.CLM_MEMBER_DESC, "");
		values.put(DBinfo.CLM_MEMBER_UNAME, uname);
		Uri inserted_uri = cr.insert(DB_CP.MEMBER_URI, values);
		return (inserted_uri.getPathSegments().get(1));
	}

	public static String AddEvent(ContentResolver cr, 
			String name, Float bill,int TripId,String EventDate,String EventPlace, String EventDesc,String EventPic,
			 List<ListItemClass> shares) 
	{
		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_EVENT_NAME, name);
		values.put(DBinfo.CLM_EVENT_BILL, bill);
		values.put(DBinfo.TRIP_ID, TripId);
		values.put(DBinfo.CLM_EVENT_DATE,EventDate);
		values.put(DBinfo.CLM_EVENT_PLACE, EventPlace);
		values.put(DBinfo.CLM_EVENT_TYPE, 0);
		values.put(DBinfo.CLM_EVENT_DESC, EventDesc);
		values.put(DBinfo.CLM_EVENT_PIC, EventPic);
		
		Uri inserted_uri = cr.insert(DB_CP.EVENT_URI, values);
		String insertedId = (inserted_uri.getPathSegments().get(1));
		// Inserting Paid/shared list in Shares Table
		//float amount = Helper.getFloatIfParsable(bill);// bill.isEmpty()?Float.parseFloat(bill):0;
		for (ListItemClass share:shares)
		{
			share.setItemAsked(ListItemClass.EVENTID_FORSHARES, getIntIfParsable(insertedId));
			
		}
		UpdateShares(cr, Helper.getIntIfParsable(insertedId), shares);
		return (insertedId);
	}

	public static void UpdateMember(ContentResolver cr, int Memberid,String name, String number, String email,String uname) {

		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_MEMBER_NAME, name);
		values.put(DBinfo.CLM_MEMBER_PHNO, number);
		values.put(DBinfo.CLM_MEMBER_EMAIL, email);
		values.put(DBinfo.CLM_MEMBER_DESC, "");
		values.put(DBinfo.CLM_MEMBER_UNAME, uname);
		cr.update(DB_CP.MEMBER_URI, values,DBinfo.MEMBER_ID+"="+Memberid,null);
		
	}

	public static String AddTrip(ContentResolver cr, String name, String _date,
			String place, int[] selectedMembers, int Theme,int GroupId) {
		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_TRIP_NAME, name);
		values.put(DBinfo.CLM_TRIP_DATE, _date);
		values.put(DBinfo.CLM_TRIP_PLACE, place);
		values.put(DBinfo.CLM_TRIP_THEME, Theme);
		values.put(DBinfo.CLM_TRIP_DESC, "");
		values.put(DBinfo.CLM_TRIP_GROUPID, GroupId);
		Uri inserted_uri = cr.insert(DB_CP.TRIP_URI, values);
		// int TripId=Integer.parseInt((inserted_uri.getPathSegments().get(1)));

		// Inserting Friendlist in Shares Table
		int id = Helper.getIntIfParsable(inserted_uri.getPathSegments().get(1));
		UpdateTripMember(cr, id, selectedMembers);// Integer.parseInt((inserted_uri.getPathSegments().get(1)))*/,CheckedList,memberList);
		return (inserted_uri.getPathSegments().get(1));
	}
	
	public static String AddGroup(ContentResolver cr, String name, int Theme,int type,String desc, int[] selectedMembers) {
		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_GROUP_NAME, name);
		values.put(DBinfo.CLM_GROUP_THEME, Theme);
		values.put(DBinfo.CLM_GROUP_DESC, "");
		Uri inserted_uri = cr.insert(DB_CP.GROUP_URI, values);
		
		return (inserted_uri.getPathSegments().get(1));
	}

	/**
	 * 
	 * @param eventId
	 *            [EventId]
	 * @param memberId
	 * @param type
	 *            [1:paidby, 2:shared by]
	 * @return
	 */
	public static float AmountofMemberforEvent(int eventId, int memberId,
			int type) {
		// projection=new String[]{ DBinfo.TRIP_ID,DBinfo.MEMBER_ID};
		ListItemClass lic = new ListItemClass();
		float amount = 0;
		String where = DBinfo.EVENT_ID + "=" + Integer.toString(eventId);
		switch (type) {
		case 1:// Paidbyamount
			where += " AND " + DBinfo.CLM_PAIDBY_MEMBERID + " = "
					+ Integer.toString(memberId);
			lic = DB_CP.getListItemClass(DB_CP.SHARE_URI, where);
			if (lic != null)
				amount = lic.getAmount();
			break;
		case 2:// Shared byamount
			where += " AND " + DBinfo.CLM_SHAREDBY_MEMBERID + "="
					+ Integer.toString(memberId);
			lic = DB_CP.getListItemClass(DB_CP.SHARE_URI, where);
			if (lic != null)
				amount = lic.getAmount();
			break;
		}
		return amount;
	}

	/**
	 * 
	 * @param eventId
	 *            ID for which SHare details needed
	 * @param type
	 *            Type of Share Detail Needed(1:Paidby,2:shared by)
	 * @return
	 */
	public static float[] AmountsforEvent(int eventId, int type,
			ArrayList<ListItemClass> members) {
		float[] amounts = null;
		if (members.size() > 0) {
			amounts = new float[members.size()];
			for (int i = 0; i < members.size(); i++)
				amounts[i] = Math.abs(AmountofMemberforEvent(eventId, members
						.get(i).toInt(), type));

		}
		return amounts;
	}
	/**
	 * @param cr
	 *            ContentResolver[getContentResolver()]
	 * @param aa
	 *            Array Adapter
	 * @param list
	 *            Array List of the ListItemClass
	 * @param type
	 *            0 for Trip,1 for Events, 2 for Members
	 */
	public static String DeleteSelectedItems(ContentResolver cr,
			int itemId, int type) {
		String msg = "";
		String where = "";
		switch (type) {
		case TRIPS:
			where = DBinfo.TRIP_ID + "="+itemId;
			DB_CP.RunQuery("delete from " + DBinfo.DB_TABLE_SHARES + " where "
					+ DBinfo.EVENT_ID + " " + "in(select " + DBinfo.EVENT_ID
					+ " from " + DBinfo.DB_TABLE_EVENTS + " where " + where
					+ ")");
			cr.delete(DB_CP.EVENT_URI, where, null);
			cr.delete(DB_CP.TRIPMEMBER_URI, where, null);
			cr.delete(DB_CP.TRIP_URI, where, null);

			// TODO:Delete all Events and shares associated with it
			break;
		case EVENTS:
			where = DBinfo.EVENT_ID + "=" + itemId;
			cr.delete(DB_CP.SHARE_URI, where, null);
			cr.delete(DB_CP.EVENT_URI, where, null);
			// TODO:Delete all Events and shares associated with it
			break;
		case MEMBERS:
			ListItemClass lic;
				where = DBinfo.MEMBER_ID + "= -1";
				// TODO:Check if Member is in some trip
				lic = DB_CP.getListItemClass(DB_CP.TRIPMEMBER_URI,
				DBinfo.MEMBER_ID + "=" + itemId);
				if (lic == null)
					where =DBinfo.MEMBER_ID+"="+Integer.toString(itemId);
				else
					msg = Helper.getTitleFromId(
									lic.getIntItemAsked(ListItemClass.MEMBERID_FORTRIP),
									MEMBERS);
			cr.delete(DB_CP.MEMBER_URI, where, null);
		}
		if (msg != "")
			msg += " Can't be deleted as they have their trips saved";
		// aa.notifyDataSetChanged();
		return msg;
	}

	static boolean isMemberInTrip(int MemberId)
	{
		ListItemClass lic = DB_CP.getListItemClass(DB_CP.TRIPMEMBER_URI,
									 DBinfo.MEMBER_ID + "=" + MemberId);
		if (lic == null)
			return false;
		else
			return true;
	}
	
	/**
	 * @param cr
	 *            ContentResolver[getContentResolver()]
	 * @param aa
	 *            Array Adapter
	 * @param list
	 *            Array List of the ListItemClass
	 * @param type
	 *            0 for Trip,1 for Events, 2 for Members
	 */
	public static String DeleteSelectedItems(ContentResolver cr,
			List<Integer> checkedIndex, List<ListItemClass> list, int type) {
		ListItemClass lic ;
		String msg = "";
		String where = "";
		switch (type) {
		case GROUPS:
			String wherecheck = DBinfo.CLM_TRIP_GROUPID+ " IN(";
			for (int i = 0; i < checkedIndex.size(); i++)
				wherecheck += list.get(checkedIndex.get(i)).toInt() + ",";
			wherecheck += "-1)";
			where = DBinfo.GROUP_ID+ " IN(";
			for (int i = 0; i < checkedIndex.size(); i++)
				where += list.get(checkedIndex.get(i)).toInt() + ",";
			where += "-1)";
			lic = DB_CP.getListItemClass(DB_CP.TRIP_URI,wherecheck);
			if (lic == null)
				cr.delete(DB_CP.GROUP_URI, where, null);
			else
				msg += "Some groups can't be deleted, Empty the group before deleting it!!!";
			
			break;
		case TRIPS:
			where = DBinfo.TRIP_ID + " IN(";
			for (int i = 0; i < checkedIndex.size(); i++)
				where += list.get(checkedIndex.get(i)).toInt() + ",";
			where += "-1)";

			DB_CP.RunQuery("delete from " + DBinfo.DB_TABLE_SHARES + " where "
					+ DBinfo.EVENT_ID + " " + "in(select " + DBinfo.EVENT_ID
					+ " from " + DBinfo.DB_TABLE_EVENTS + " where " + where
					+ ")");
			cr.delete(DB_CP.EVENT_URI, where, null);
			cr.delete(DB_CP.TRIPMEMBER_URI, where, null);
			cr.delete(DB_CP.TRIP_URI, where, null);
			// TODO:Delete all Events and shares associated with it
			break;
		case EVENTS:
			where = DBinfo.EVENT_ID + " IN(";
			for (int i = 0; i < checkedIndex.size(); i++)
				where += Integer.toString(list.get(checkedIndex.get(i)).toInt()) + ",";
			where += "-1)";
			cr.delete(DB_CP.SHARE_URI, where, null);
			cr.delete(DB_CP.EVENT_URI, where, null);
			// TODO:Delete all Events and shares associated with it
			break;
		case MEMBERS:
			int id = -1;
			where = DBinfo.MEMBER_ID + " IN(";
			for (int i = 0; i < checkedIndex.size(); i++) {
				// TODO:Check if Member is in some trip
				id = list.get(checkedIndex.get(i)).toInt();
				lic = DB_CP.getListItemClass(DB_CP.TRIPMEMBER_URI,
						DBinfo.MEMBER_ID + "=" + id);
				if (lic == null)
					where += Integer.toString(id) + ",";
				else if (msg != "")
					msg += ","+ Helper.getTitleFromId(lic.getIntItemAsked(ListItemClass.MEMBERID_FORTRIP),	MEMBERS);
				else
					msg = Helper.getTitleFromId(lic.getIntItemAsked(ListItemClass.MEMBERID_FORTRIP),MEMBERS);
			}
			where += "-1)";
			cr.delete(DB_CP.MEMBER_URI, where, null);
			if (msg != "")
				msg += " Can't be deleted as they have their trips saved";
		}

		return msg;
	}

	public static boolean EventsAmountExists(int eventId) {
		ListItemClass lic = new ListItemClass();
		String where = DBinfo.EVENT_ID + "=" + eventId;
		lic = DB_CP.getListItemClass(DB_CP.SHARE_URI, where);
		if (lic != null)
			return true;
		else
			return false;
	}

	public static Date getDateIfParsable(String str) {
		Date formatted=getDateIfParsable(str, "d/M/yyyy");
		if(formatted!=null)
			return formatted;
		formatted=getDateIfParsable(str, "dd/MM/yyyy");
		if(formatted!=null)
			return formatted;
		return null;
	}

	public static Date getDateIfParsable(String str, String Format) {
		Date f = null;

			try {
				f = new SimpleDateFormat(Format,Locale.US).parse(str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return f;
 
	}

	public static String getFormattedDateFormatted(String strdate,
			String DestFormat, String SrcFormat) {
		String sdf="00/00/0000";
		try{
		sdf= (new SimpleDateFormat(DestFormat,Locale.US).format(getDateIfParsable(
				strdate, SrcFormat)));
		}
finally
		{return sdf;}
		
	}

	public static float getFloatIfParsable(String str) {
		float f = 0;
		try {
			f = Float.parseFloat(str);
		} catch (NumberFormatException ex) {
			f = 0;
		}
			return f;
		
	}

	public static int getIntIfParsable(String str) {
		int f = 0;
		try {
			f = Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			f = 0;
		} 
			return f;
	}

	public static float getSharesSum(float[] values) {
		float sum = 0;
		if(values!=null)
			for (int i = 0; i < values.length; i++)
				sum += values[i];
		return sum;
	}

	public static String getTitleFromId(int id, int type) {
		ListItemClass LIC = null;
		String where = "";
		switch (type) {
		case TRIPS:
			where = DBinfo.TRIP_ID + "=" + Integer.toString(id);
			LIC = DB_CP.getListItemClass(DB_CP.TRIP_URI, where);
			break;
		case EVENTS:
			where = DBinfo.EVENT_ID + "=" + Integer.toString(id);
			LIC = DB_CP.getListItemClass(DB_CP.EVENT_URI, where);
			break;
		case MEMBERS:
			where = DBinfo.MEMBER_ID + "=" + Integer.toString(id);
			LIC = DB_CP.getListItemClass(DB_CP.MEMBER_URI, where);
			break;
		}
		if (LIC != null)
			return (LIC.getStringItemAsked(ListItemClass.MAIN_ITEM));
		else
			return ("#NA");
	}

	public static boolean IsMemberwithHisaab(int tripid, int memberid) {
		float paidsum = 0;
		float sharedsum = 0;
		ListItemClass lic;
		String in = "(";
		String where = "";
		ArrayList<ListItemClass> events = DB_CP.getListItemClassArray(
				DB_CP.EVENT_URI, DBinfo.TRIP_ID + "=" + tripid);
		for (int i = 0; i < events.size(); i++) {
			in += events.get(i).toInt() + ",";
		}
		in += "-1)";
		where = DBinfo.EVENT_ID + " in " + in + " AND "
				+ DBinfo.CLM_PAIDBY_MEMBERID + " = " + memberid;
		lic = DB_CP.getListItemClass(DB_CP.SHARE_URI, where);
		if (lic != null)
			paidsum = lic.getAmount();
		// Log.w("Helper", "Paid Sum="+paidsum+":"+where+":"+
		// getTitleFromId(memberid, MEMBERS));
		where = DBinfo.EVENT_ID + " in " + in + " AND "
				+ DBinfo.CLM_SHAREDBY_MEMBERID + " = " + memberid;
		lic = DB_CP.getListItemClass(DB_CP.SHARESUM_URI, where);
		if (lic != null)
			sharedsum = lic.getAmount();
		// Log.w("Helper", "Shared Sum="+sharedsum+":"+where+":"+
		// getTitleFromId(memberid, MEMBERS));

		if (Math.abs(paidsum) + Math.abs(sharedsum) != 0)
			return true;
		else
			return false;
	}

	public static int[] extractIdsfromList(List<Integer> lst,
			List<ListItemClass> lic) {
		int[] IntArray = new int[lst.size()];
		for (int i = 0; i < lst.size(); i++)
			IntArray[i] = lic.get(lst.get(i)).toInt();
		return IntArray;
	}

	public static void Loadresources() {
		ic_SelectAll = R.drawable.ic_select_all;
		ic_SelectNone = R.drawable.ic_select_none;

	}

	/**
	 * For Contexual Menus everywhere
	 * 
	 * @param item
	 * @param lst
	 * @return should it finish mode after selection
	 */
	public static boolean SelectAll(MenuItem item, ListItemAdapter aa) {
		if (item.getTitle() == "SELECT NONE") {
			item.setTitle("SELECT ALL");
			item.setIcon(Helper.ic_SelectAll);
			return (aa.selectAllorNone(false));
		} else {
			item.setTitle("SELECT NONE");
			item.setIcon(Helper.ic_SelectNone);
			aa.selectAllorNone(true);
			return (false);
		}

	}
	public static Intent showHisaab(Context context, int type, int typeId,
			int TripId)
	{int array[]=new int[1];
	array[0]=typeId;
		return(showHisaab(context, type, array,
			TripId));
	}
	public static Intent showHisaab(Context context, int type, int[] array,
			int TripId) {
		Intent intent = new Intent(context, HisaabActivity.class);
		intent.putExtra("TYPE", type);
		intent.putExtra(DBinfo.TRIP_ID, TripId);
		switch (type) {
		case EVENTS:
			intent.putExtra(DBinfo.EVENT_ID + "Array", array);
			break;
		case TRIPS:
			intent.putExtra(DBinfo.TRIP_ID + "Array", array);
			break;
		default:
		}
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
		return (intent);
	}
/**
 * 
 * @param cr Content Resolver
 * @param eventId EventID of the Event to be updated
 * @param name 
 * @param bill 
 * @param EventDate
 * @param EventPlace
 * @param EventDesc
 * 
 * Members hisaab updation in Shares Table
 * @param members The list of all members in the event/Trip
 * @param memberPaid Array containing amounts they paid
 * @param memberShared Array containing amounts they shared
 */
	public static void UpdateEvent(ContentResolver cr, int eventId,
			String name, String bill, String EventDate,String EventPlace, String EventDesc,String EventPic,
			List<ListItemClass> members,float[] memberPaid, float[] memberShared) {
		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_EVENT_NAME, name);
		values.put(DBinfo.CLM_EVENT_BILL, bill);
		values.put(DBinfo.CLM_EVENT_DATE, EventDate);
		values.put(DBinfo.CLM_EVENT_PLACE, EventPlace);
		values.put(DBinfo.CLM_EVENT_DESC, EventDesc);
		values.put(DBinfo.CLM_EVENT_PIC, EventPic);
		
		
		String Selection = DBinfo.EVENT_ID + "=" + Integer.toString(eventId);
		cr.update(DB_CP.EVENT_URI, values, Selection, null);
		// Inserting Paid/shared list in Shares Table
		if (memberPaid != null && memberShared != null) {
			cr.delete(DB_CP.SHARE_URI,
					DBinfo.EVENT_ID + "=" + Integer.toString(eventId), null);
			UpdateShares(cr, eventId, members, memberPaid, memberShared);
		}

	}

	private static void UpdateShares(ContentResolver cr, int eventId,
			List<ListItemClass> members, float[] Paid, float[] Shared) {
		ContentValues values = new ContentValues();
		int membercount = members.size();
		if (Paid != null) {
			for (int i = 0; i < membercount; i++) {
				values.clear();
				values.put(DBinfo.EVENT_ID, eventId);
				values.put(DBinfo.CLM_PAIDBY_MEMBERID, members.get(i).toInt());
				if (Paid.length > i)
					values.put(DBinfo.CLM_SHARE_AMOUNT, Paid[i]);
				else
					values.put(DBinfo.CLM_SHARE_AMOUNT, 0);
				cr.insert(DB_CP.SHARE_URI, values);
			}
		}
		if (Shared != null) {

			for (int i = 0; i < membercount; i++) {
				values.clear();
				values.put(DBinfo.EVENT_ID, eventId);
				values.put(DBinfo.CLM_SHAREDBY_MEMBERID, members.get(i).toInt());
				if (Shared.length > i)
					values.put(DBinfo.CLM_SHARE_AMOUNT, -Shared[i]);
				else
					values.put(DBinfo.CLM_SHARE_AMOUNT, 0);
				cr.insert(DB_CP.SHARE_URI, values);
			}

		}
	}

	private static void UpdateShares(ContentResolver cr, int eventId,
			List<ListItemClass> shares) {
		ContentValues values = new ContentValues();
		float shareAmounts=0;
		if (shares != null) {
			for (int i = 0; i < shares.size(); i++)
			{
				values.clear();
				values.put(DBinfo.EVENT_ID, eventId);
				shareAmounts=shares.get(i).getAmount();
				
				if(shareAmounts>=0)
				{
					//Log.w("Serial No(Paid):"+String.valueOf(i), String.valueOf(shareAmounts)+" From: "+shares.get(i).getIntItemAsked(ListItemClass.PAIDBY_MEMBERID));
					values.put(DBinfo.CLM_SHARE_AMOUNT, shareAmounts);
					values.put(DBinfo.CLM_PAIDBY_MEMBERID, shares.get(i).getIntItemAsked(ListItemClass.PAIDBY_MEMBERID));
					values.put(DBinfo.CLM_SHAREDBY_MEMBERID, 0);
				//	Log.w("Serial No:"+String.valueOf(i), String.valueOf(shareAmounts));
				}
				else
				{
					values.put(DBinfo.CLM_SHARE_AMOUNT, shareAmounts);
					values.put(DBinfo.CLM_PAIDBY_MEMBERID, 0);
					values.put(DBinfo.CLM_SHAREDBY_MEMBERID, shares.get(i).getIntItemAsked(ListItemClass.SHAREDBY_MEMBERID));
					//Log.w("Serial No(SHare):"+String.valueOf(i), String.valueOf(shareAmounts)+" From: "+shares.get(i).getIntItemAsked(ListItemClass.SHAREDBY_MEMBERID));
				}
				cr.insert(DB_CP.SHARE_URI, values);
			}
		}
	}
	public static void UpdateGroup(ContentResolver cr, int GroupId, String name,int Theme,int Type,
			String _desc, int[] selectedMembers) {

		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_GROUP_NAME, name);
		values.put(DBinfo.CLM_GROUP_DESC, _desc);
		values.put(DBinfo.CLM_GROUP_TYPE, Type);
		values.put(DBinfo.CLM_GROUP_THEME, Theme);
		cr.update(DB_CP.GROUP_URI, values,
				DBinfo.GROUP_ID + "=" + String.valueOf(GroupId), null);

	}
	
	public static void movetogroup(ContentResolver cr,int[] selectedTrips, int destGroupId)
	{
		String tripIds="";
		for(int i=0;i<selectedTrips.length;i++)
			tripIds+= i>0?","+selectedTrips[i]:""+selectedTrips[i];

		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_TRIP_GROUPID, destGroupId);
		cr.update(DB_CP.TRIP_URI, values,
				DBinfo.TRIP_ID + " in(" + tripIds+")", null);
		Log.w("Move",DBinfo.TRIP_ID + " in(" + tripIds+")");
	}
	public static void UpdateTrip(ContentResolver cr, int TripId, String name,
			String _date, String place, int[] selectedMembers, int Theme) {

		ContentValues values = new ContentValues();
		values.put(DBinfo.CLM_TRIP_NAME, name);
		values.put(DBinfo.CLM_TRIP_DATE, _date);
		values.put(DBinfo.CLM_TRIP_PLACE, place);
		values.put(DBinfo.CLM_TRIP_THEME, Theme);
		values.put(DBinfo.CLM_TRIP_DESC, "");
		//values.put(DBinfo.CLM_TRIP_GROUPID, groupId);
		cr.update(DB_CP.TRIP_URI, values,
				DBinfo.TRIP_ID + "=" + String.valueOf(TripId), null);

		cr.delete(DB_CP.TRIPMEMBER_URI,
				DBinfo.TRIP_ID + "=" + Integer.toString(TripId), null);
		UpdateTripMember(cr, TripId, selectedMembers);
	}

	private static void UpdateTripMember(ContentResolver cr, int tripid,
			int[] selectedMembers) {
		ContentValues values = new ContentValues();
		for (int j = 0; j < selectedMembers.length; j++) {
			values.clear();
			values.put(DBinfo.TRIP_ID, tripid);
			values.put(DBinfo.MEMBER_ID, selectedMembers[j]);
			cr.insert(DB_CP.TRIPMEMBER_URI, values);
		}
	}
	// TODO PromptDialog Funtions
	/**
	 * @param Title String Title of the box
	 * @param Content Instruction in the Box
	 * @param Type  Box type(1-Text Entry, 2-Date Entry, 3-List Selection )
	 * @param context Context of Activity where shown
	 * @return PromtDialog to show the dialog
	 * 
	 */
	public static void getInputDialog(final Context context, String Title,
			String Content, int type,String defaultVal,View outView,View NextFocus) {
		View view;
		switch (type) {
		case PromptDialog.DATE_DLG:// date
			DatePicker viewDP = new DatePicker(context);
			if(Build.VERSION.SDK_INT>=11)
				viewDP.setCalendarViewShown(false);
			Date Setdate;
			if(!TextUtils.isEmpty(defaultVal))
			{
				Setdate=Helper.getDateIfParsable(defaultVal);
				Calendar cal=Calendar.getInstance();
				cal.setTime(Setdate);
				viewDP.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
				//viewDP.updateDate(2010,4,1);
			}
			view = viewDP;
			break;
		case PromptDialog.EDIT_BOX:// text entry
		default:
			EditText viewET = new EditText(context);
			viewET.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			view = viewET;

		}
		
		new PromptDialog(context, Title, Content, view,
				type,outView,NextFocus) {
			@Override
			public boolean onOkClicked(View inputView,View outputView,View Next) {				
				switch (this.getviewType()) {
/*				case PromptDialog.EDIT_BOX:// Edit Text
					EditText txt = (EditText) inputView;
					String MemberName = txt.getText().toString();
					if (DB_CP.getListItemClass(DB_CP.MEMBER_URI,
							DBinfo.CLM_MEMBER_NAME + "='" + MemberName + "'") != null)
						Toast.makeText(getApplicationContext(),
								MemberName + ":MEMBER Name Already Exists ",
								Toast.LENGTH_SHORT).show();
					else if (MemberName.isEmpty())
						Toast.makeText(getApplicationContext(),
								"No Member Added(Edit box blank)",
								Toast.LENGTH_SHORT).show();
					else {
						String inserted_id = Helper.AddMember(
								getContentResolver(), MemberName);
						int MEMBERId = Helper.getIntIfParsable(inserted_id);
						if (MEMBERId > -1)
							;
						// getLoaderManager().restartLoader(0, null,
						// AddTripActivity.this);
						return true;
					}
					return false;*/
				case PromptDialog.DATE_DLG:
					DatePicker DP = (DatePicker) inputView;
					String day= DP.getDayOfMonth()<10?"0"+Integer.toString(DP.getDayOfMonth()):Integer.toString(DP.getDayOfMonth());
					String Month=DP.getMonth()<9?"0"+Integer.toString(DP.getMonth()+1):Integer.toString(DP.getMonth()+1);
					String DPDate = day+ "/"
							+ Month + "/"
							+ Integer.toString(DP.getYear());
					((EditText)outputView).setText(DPDate);
					//Next.onTouchEvent(null);
					Next.requestFocus();
					showSoftKeyboard(Next,context);
					return true;
					/*
					 * case PromptDialog.LIST_DLG://list boolean flag=false;
					 * for(int i=0;i<lstMembers.getCount();i++)
					 * if(!aa.listFunctions(0, i))
					 * if(Helper.IsMemberwithHisaab(TripId,
					 * aa.getItem(i).toInt())) { flag=true; aa.listFunctions(1,
					 * i); } if(flag==true)
					 * Toast.makeText(getApplicationContext(),
					 * "Some members can't be removed from the trip as they have their hisaab pending"
					 * , Toast.LENGTH_LONG).show(); showmembers(); return true;
					 */
				default:
					return true;
				}
			}
		};
		//return PmtDlg;
	}

	public static void showSoftKeyboard(View view,Context context) 
	{ 
		if (view.requestFocus())
		{
			InputMethodManager imm = (InputMethodManager)  context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	 	}
	 }
	
	
	public static int[] getTripMembers(int tripId) {
		ArrayList<ListItemClass> lic = null;
		String where = DBinfo.TRIP_ID + " = " + tripId;
		lic = DB_CP.getListItemClassArray(DB_CP.TRIPMEMBER_URI, where);
		int[] SelectedContacts = new int[lic.size()];
		for (int i = 0; i < lic.size(); i++) {
			SelectedContacts[i] = lic.get(i).getIntItemAsked(ListItemClass.MEMBERID_FORTRIP);
		}
		// Toast.makeText(getApplicationContext(),SelectedContacts.length+
		// " memebrs Loaded", Toast.LENGTH_SHORT).show();
		return SelectedContacts;
	}



	/**
	 * Base constructor for in case.. :)
	 */
	public Helper() {
		}

	public static Intent SendSMS(String address, String content) {
		// TODO Auto-generated method stub
		Intent smsIntent=new Intent(Intent.ACTION_VIEW);
		smsIntent.putExtra("sms_body",content);
		smsIntent.putExtra("address",address);
		smsIntent.setType("vnd.android-dir/mms-sms");
		
		return smsIntent;
	}

	public static Intent sendEmail(String address, String subject,String content) {
		return(sendEmail(address, subject, content,null));
	}
		
	private static Intent sendEmail(String address, String subject,String content,Uri uri) {
		// TODO Auto-generated method stub
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
	            "mailto",address, null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, content);
		if(uri!=null)
			emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
	return Intent.createChooser(emailIntent, "Send email...");
	}
	public static Intent sendTripViaEmail(String emailaddress, String subject,
			String emailContent, int tripid) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse("file://" + Helper.exportToFile(tripid));
		return sendEmail( emailaddress, subject,emailContent, uri);		
		
	}

	public static Intent sendTripViaEmail(String emailaddress, String subject,
			String emailContent, int[] tripids) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse("file://" + Helper.exportToFile(tripids));
		return sendEmail( emailaddress, subject,emailContent, uri);		
	}


	private static Intent sendHTMLEmail(String address, String subject,String content) {
		// TODO Auto-generated method stub

		Intent emailIntent = new Intent(Intent.ACTION_SEND );
		emailIntent.setType("text/html");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(content).toString());

	return Intent.createChooser(emailIntent, "Send email...");
	
		
	}
	
/*	public static SwipeDismissList.OnDismissCallback getSwipeCallback(final ContentResolver cr,final ListItemAdapter aa,final int type)
	{
	
		SwipeDismissList.OnDismissCallback callback = new SwipeDismissList.OnDismissCallback()
		{
		
			public SwipeDismissList.Undoable onDismiss(AbsListView listView, final int position) {
				// Delete the item from your adapter (sample code):
				final ListItemClass itemToDelete = aa.getItem(position);
				switch(type)
				{
				case MEMBERS:
					if(isMemberInTrip(itemToDelete.toInt()))
					{
						Toast.makeText(listView.getContext(),itemToDelete.toString() + " can't be deleted\n Associated Trip(s) Exist!!!",Toast.LENGTH_SHORT).show();			
						return null;
					}
				default:
					aa.remove(itemToDelete);
					return new SwipeDismissList.Undoable() {
					public void undo() {
						// Return the item at its previous position again
						aa.insert(itemToDelete, position);
					}
					// Return an undo message for that item
					public String getTitle() {
						return itemToDelete.toString() + " deleted";
					}

					// Called when user cannot undo the action anymore
					public void discard() {
						// Use this place to e.g. delete the item from database
						Helper.DeleteSelectedItems (cr,itemToDelete.toInt(),type);
					}
				};
				}
			}
		};
		return callback;
	}
	*/
	public static UndoOptions.OnDismissCallback getUndoCallback(final ContentResolver cr,final List<ListItemClass> lst,final ListItemAdapter aa,final int type)
	{
	//	abstract final void finaljob(List<Integer> pos);
		UndoOptions.OnDismissCallback callback = new UndoOptions.OnDismissCallback()
		{
			public UndoOptions.Undoable onDismiss(List<Integer> tpositions) {
				// the temp removal of item
				//Toast.makeText(aa.getContext(),positions.size()+"items Temporarily hidden",Toast.LENGTH_SHORT ).show();
				final List<ListItemClass> deletedItems=new ArrayList<ListItemClass>();
				for(int i=0;i<tpositions.size();i++)
					{
					if(type==MEMBERS && isMemberInTrip(aa.getItem(tpositions.get(i)).toInt()))
							tpositions.remove(tpositions.get(i));
					}
				
				for(int i=0;i<tpositions.size();i++)
					deletedItems.add(aa.getItem(tpositions.get(i)));
				
				for(int i=0;i<tpositions.size();i++)
					aa.remove(deletedItems.get(i));
				
				final List<Integer> positions=tpositions;
				return new UndoOptions.Undoable() {
					public void undo() {
						// Return the item at its previous position again
						//Toast.makeText(aa.getContext(),"Job undone",Toast.LENGTH_SHORT ).show();
						for(int i=0;i<positions.size();i++)
							aa.insert(deletedItems.get(i),positions.get(i));
						}
					// Return an undo message for that item
					public String getTitle() {
						return positions.size()+" item deleted";
					}

					// Called when user cannot undo the action anymore
					public void discard() {
						// Use this place to e.g. delete the item from database
						//Toast.makeText(aa.getContext(),"Finally deleted:"+lst.get(positions.get(0)).toString(),Toast.LENGTH_SHORT ).show();
						String msg=Helper.DeleteSelectedItems(cr,positions,lst,type);
						if (!msg.isEmpty())
							Toast.makeText(aa.getContext(),msg,Toast.LENGTH_SHORT ).show();
					}
				};
			}
		};
		return callback;
	}
	public static String readfromFile(File inFile)
	{
		 if(!inFile.exists())
			 return "";		 
		InputStream inputStream=null;
		try {
			inputStream = new FileInputStream(inFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    //inputStream.length
	    byte buf[] = new byte[1024];
	    int len;
	    try {
	        while ((len = inputStream.read(buf)) != -1) {
	            outputStream.write(buf, 0, len);
	        }
	        outputStream.close();
	        inputStream.close();
	    } catch (IOException e) {
	        Log.e("IOException readTextFile", e.getMessage());
	    }
	    return outputStream.toString();
	}
	public static String writeToFile(String str,File outFile)
	{
		
		/*File dir=Environment.getExternalStorageDirectory();
		String filename="export.trn";
			 if(!dir.exists()) 
				 dir.mkdir();
			outFile=new File(dir,filename);
			outFile.getParentFile().mkdirs();*/
		try {
	    	FileOutputStream outputStream =new FileOutputStream(outFile);          //Create a stream to access the file
	    	outputStream.write(str.getBytes());     //Write the data
			outputStream.flush();
			outputStream.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		} 
		 return outFile.toString();
	}
	/**  * Check the device to make sure it has the Google Play Services APK. If  
	 *  it doesn't, display a dialog that allows users to download the APK from  
	 *  the Google Play Store or enable it in the device's system settings.  */ 
public static boolean checkPlayServices(Activity activity) 
	{     
		final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000; 
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);     
		if (resultCode != ConnectionResult.SUCCESS) 
		{         
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) 
			{             
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,PLAY_SERVICES_RESOLUTION_REQUEST).show();
				} 
			else 
			{             
				Log.i("Google Support", "This device is not supported.");
				activity.finish();
				}         
			return false;
			}
		return true; 
		}

	public static void AnalyseRecievedMessage(Bundle extras) {
		// TODO extras contains all the data sent from online part of app please act accordingly
	}
	
	public static File getPathfromIntent(Intent data,ContentResolver cr) {
        // just some safety built in 
		Uri uri = data.getData();
		String strFilePath=data.getDataString();
		strFilePath=uri.getPath().toString();
		//Log.w("File",strFilePath);
		File retFile;
         if( uri != null ){
            // now we get the path to the image file
        	Cursor cursor = cr.query(uri, null,
                                            null, null, null);
        	if(cursor!=null)
        	{
	           	cursor.moveToFirst();
	           	strFilePath = cursor.getString(0);
	           	cursor.close();
	       		retFile=(new File(strFilePath));
        	}
        	else
        	{
        		retFile=(new File(strFilePath));
        	}

            if(retFile.exists())
           	 return retFile;
         }
           	 return null;         
     }
	
	public static class importTrip extends AsyncTask<String, Integer, Boolean> {
		private Activity context;

		  ProgressDialog pd;
		  File file;
		  int SuccessProgress=0,totProgress=0;
		  int groupId=1;
		  public importTrip(File file,Activity context,int groupId)
			{
			  this.file=file;
			  this.context=context;
			  this.groupId=groupId;
			  }

		
		  @Override
		  protected void onPreExecute() {
		   super.onPreExecute();
		  // Toast.makeText(ctx, "Starting registration Process", Toast.LENGTH_SHORT).show();
		   pd=new ProgressDialog(context);
		   pd.setTitle("Performing the requested action..");
		   pd.setMessage("Please wait...");
		   //pd.setIndeterminate(true);
		   pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		   pd.setCancelable(true);
		   pd.setOnCancelListener(new OnCancelListener() {
		        @Override
		        public void onCancel(DialogInterface dialog) {
		        	cancel(true);
		        }
		    });
		   pd.show();
		   
		  }
		  
		@Override
        protected Boolean doInBackground(String... params) {
			//Toast.makeText(context, "Importing trips, Please wait...",  Toast.LENGTH_LONG).show();
			JSONObject srcJSON=null;
			try {
				srcJSON = new JSONObject(Helper.readfromFile(file));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if(srcJSON.isNull(JSONTYPE))return false;
				if(srcJSON.getInt(JSONTYPE)==JSONTYPE_SINGLETRIP)
				{
					TRIPCLASS newTrip=(new Helper()).new TRIPCLASS(groupId);
					if(newTrip.setTripFromJSON(srcJSON))
						newTrip.AddTrip(context);
					else
						Toast.makeText(context, "Invalid Trip Format in file", Toast.LENGTH_LONG).show();
				}
				else if(srcJSON.getInt(JSONTYPE)==JSONTYPE_TRIPARRAY)
				{
					JSONArray JSONTrips=srcJSON.getJSONArray("TRIPS");
					totProgress=JSONTrips.length();
					for(int i=0;i<JSONTrips.length();i++)
					{
						TRIPCLASS newTrip=(new Helper()).new TRIPCLASS(groupId);
						if(newTrip.setTripFromJSON((JSONObject) JSONTrips.get(i)))
							{
								publishProgress(i,totProgress);	
								if(newTrip.AddTrip(context,i+1))
									SuccessProgress++;
							}
						else
							Toast.makeText(context, "Invalid Trip Format in file for Trip:"+i, Toast.LENGTH_LONG).show();
						if(isCancelled()) 
							break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Toast.makeText(context, "Error:"+e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
				return false;
			}
			return true;
			
			//Helper.writeToFile(newTrip.getJSONforTrip().toString(), "export2.txt");
		
		
        }
		@Override
		protected void onProgressUpdate(final Integer... values)
		{
			pd.setMax(values[1]);
			pd.setProgress(values[0]);
						//pd.setMessage(curProgress+"/"+totProgress+"... Trips added");
		}
		@Override
		  protected void onCancelled(Boolean result) 
		 {
			((TripActivity) context).ReloadList();
			 pd.dismiss();
			 Toast.makeText(context, "Only "+SuccessProgress +"/"+totProgress+" Trips imported", Toast.LENGTH_LONG).show();
			
		 }
		 @Override
		  protected void onPostExecute(Boolean result) 
		 {
			((TripActivity) context).ReloadList();
			 pd.dismiss();
			 Toast.makeText(context, "Total "+SuccessProgress +"/"+totProgress+" Trips imported", Toast.LENGTH_LONG).show();
			
		 }
	}
        
	
	public static File getDefaultDirectory()
	{
		String path=Environment.getExternalStorageDirectory().getPath()+"/treckon/";
		File file=new File(path);
		file.mkdirs();
		return file;
	}
	public static File exportToFile(int TripId)
	{
		File file=new File(getDefaultDirectory(),getTitleFromId(TripId,TRIPS)+".trn");
		return exportToFile(TripId,file);
	}
	public static File exportToFile(int[] TripIds)
	{
		File file=new File(getDefaultDirectory(),getTitleFromId(TripIds[0],TRIPS)+" and ("+(TripIds.length-1)+" others).trn");
		return exportToFile(TripIds,file);
	}
    public static File exportToFile(int TripId,File file)
    {
    	TRIPCLASS curTrip=(new Helper()).new TRIPCLASS(1);
		curTrip.setfromTripID(TripId);	
		Helper.writeToFile(curTrip.getJSONforTrip().toString(),file);
		
//		TRIPCLASS newTrip=(new Helper()).new TRIPCLASS();
//		if(newTrip.setTripFromJSON(test))
	
		return file;
    }
    
    public static File exportToFile(int[] TripIds,File file) {
		// TODO Auto-generated method stub
    	JSONObject obj = new JSONObject();
    	JSONArray tripsJSON = new JSONArray();
		for(int TripId:TripIds)
		{
			TRIPCLASS curTrip=(new Helper()).new TRIPCLASS(1);
			curTrip.setfromTripID(TripId);
			tripsJSON.put(curTrip.getJSONforTrip());
		}
		
    	try {
			obj.put(JSONTYPE, JSONTYPE_TRIPARRAY);
			obj.put("TRIPS",tripsJSON);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Helper.writeToFile(obj.toString(),file);
		return file;
	}
	/*
	 * public static File getFilePath(int TripId) { return
	 * getFilePath(TripId,false); }
	 * 
	 * public static File getFilePath(int TripId,boolean thumbnail) { String
	 * dir; if(!thumbnail) dir=ThumbnailDetails.MainDirectory; else
	 * dir=ThumbnailDetails.thumbDirectory;
	 * 
	 * File file; File filedir=new File(dir);
	 * 
	 * if(!filedir.exists()) { filedir.mkdir(); } if (TripId>0) file= new
	 * File(dir,Integer.toString(TripId)+".jpg"); else file=new
	 * File(dir,"temp.jpg"); return file; }
	 * 
	 * 
	 * 
	 * public static void Compressto(File src,File dst,int width,int height) {
	 * Bitmap photo=BitmapFactory.decodeFile(src.getPath()); try{
	 * photo=Bitmap.createScaledBitmap(photo , width, height, false);
	 * }catch(NullPointerException e) {
	 * 
	 * } FileOutputStream fo; try { if(!dst.exists()) dst.createNewFile(); fo =
	 * new FileOutputStream(dst); photo.compress(Bitmap.CompressFormat.JPEG, 40,
	 * fo); //fo.write(bytes.toByteArray()); fo.flush(); fo.close(); } catch(
	 * IOException e) { Log.d("While Compressing",e.getMessage()); } }
	 * 
	 * public static void setBitmap(String path,ImageView imgView) { File
	 * file=new File(path); if(!file.exists()) try { file.createNewFile(); }
	 * catch (IOException e) {
	 * 
	 * e.printStackTrace(); } setBitmap(new File(path), imgView); }
	 * 
	 * public static Bitmap getBitmap(File file,int width,int height) { Bitmap
	 * bitmap; Uri outputFileUri = Uri.fromFile(file); BitmapFactory.Options
	 * factoryOptions = new BitmapFactory.Options();
	 * factoryOptions.inJustDecodeBounds = true;
	 * bitmap=BitmapFactory.decodeFile(outputFileUri.getPath(),factoryOptions);
	 * factoryOptions.inJustDecodeBounds = false; factoryOptions.inSampleSize =
	 * scaleFactor(factoryOptions,width,height); factoryOptions.inPurgeable =
	 * true; try { bitmap =
	 * BitmapFactory.decodeFile(outputFileUri.getPath(),factoryOptions); } catch
	 * (OutOfMemoryError e) { Log.d("IMGVIEW", "Error Loading Image: " +
	 * e.getMessage()); } return bitmap; }
	 * 
	 * public static int scaleFactor( BitmapFactory.Options options, int
	 * reqWidth, int reqHeight) { // Raw height and width of image final int
	 * height = options.outHeight; final int width = options.outWidth; int
	 * inSampleSize = 1; if (height > reqHeight || width > reqWidth) { //
	 * Calculate ratios of height and width to requested height and width final
	 * int heightRatio = Math.round((float) height / (float) reqHeight); final
	 * int widthRatio = Math.round((float) width / (float) reqWidth); // Choose
	 * the smallest ratio as inSampleSize value, this will guarantee // a final
	 * image with both dimensions larger than or equal to the // requested
	 * height and width. inSampleSize = heightRatio < widthRatio ? heightRatio :
	 * widthRatio; } return inSampleSize; }
	 * 
	 * public static void setBitmap(File file,ImageView imgView) {
	 * imgView.setImageBitmap(getBitmap(file, imgView.getWidth(),
	 * imgView.getHeight())); }
	 * 
	 * public static void showAllChecked(ArrayList<ListItemClass> arrayList) {
	 * for(int i=0;i<arrayList.size();i++) {
	 * Log.w("Adaptt",(arrayList.get(i).getCheck()) +" At " +
	 * Integer.toString(i)); } }
	 */

	public static String formatSQLdate(String d) {
		// TODO Auto-generated method stub
		d="(CASE SUBSTR("+d+", 3, 1) WHEN '/' THEN  SUBSTR("+d+", 1,2)||'-' || SUBSTR("+d+", 4) ELSE '0'||SUBSTR("+d+", 1,1)||'-' || SUBSTR("+d+", 3) END)";
		String m="(CASE SUBSTR("+d+", 6, 1) WHEN '/' THEN SUBSTR("+d+", 4,2)||'-' || SUBSTR("+d+", 7)  ELSE '0'||SUBSTR("+d+", 4,1)||'-' || SUBSTR("+d+", 6) END)";
		String y="(SUBSTR("+m+",4))";
		m="(SUBSTR("+m+",1,2))";
		d="(SUBSTR("+d+",1,2))";
		String Formatted=y+"||'-'||"+m+"||'-'||"+d;
		//Log.w("Format",Formatted);
		return Formatted;
	}

	
	

}
