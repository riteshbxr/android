
package com.projects.utils;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.projects.em.Helper;
import com.projects.em.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
public class SyncActivity extends Activity implements OnClickListener {
	
  
 private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
 
  private static final String TAG = "GCMRelated";
  private static EditText txtUID,txtmsg,txttoId ;
  private static Button regbutton;
  private static Button updatebutton;
  private static TextView lblMessages;
  GoogleCloudMessaging gcm;
 AtomicInteger msgId = new AtomicInteger();
 String regid,uname,pass;
 
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_sync);
  regbutton = (Button) findViewById(R.id.btnregister);
  updatebutton = (Button) findViewById(R.id.btnupdateUID);
  txtUID=(EditText)findViewById(R.id.txtUid);
  txtmsg=(EditText)findViewById(R.id.txtMessage);
  txttoId=(EditText)findViewById(R.id.txttoId);
  lblMessages=(TextView)findViewById(R.id.lblMessage);
  regbutton.setOnClickListener(this);
  updatebutton.setOnClickListener(this);
  ((Button) findViewById(R.id.btnsendMessage)).setOnClickListener(this);
  if (checkPlayServices()) {
      gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regid = Helper.MyPreferences.getRegistrationId(SyncActivity.this);
            if(!regid.isEmpty()){
             regbutton.setEnabled(false);
             regbutton.setText("Already registered");
             uname = Helper.MyPreferences.getuname(SyncActivity.this);
             txtUID.setText(uname);
            }else{
             regbutton.setEnabled(true);
            }
  }
 } 

 /**
  * Check the device to make sure it has the Google Play Services APK. If
  * it doesn't, display a dialog that allows users to download the APK from
  * the Google Play Store or enable it in the device's system settings.
  */
  
 private boolean checkPlayServices() {
     int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
     if (resultCode != ConnectionResult.SUCCESS) {
         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
             GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                     PLAY_SERVICES_RESOLUTION_REQUEST).show();
         } else {
             Log.i(TAG, "This device is not supported.");
             finish();
         }
         return false;
     }
     return true;
 }


 @Override
 public void onClick(View view) {
  // Check device for Play Services APK.
     switch(view.getId())
     {
     case R.id.btnregister :
     if (checkPlayServices()) 
     {
    	 gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
         regid = Helper.MyPreferences.getRegistrationId(SyncActivity.this);
         if (regid.isEmpty()) 
         {
            view.setEnabled(false);
            regbutton.setText("Registered");
          	uname=txtUID.getText().toString();
            new RegisterApp(SyncActivity.this, gcm, Helper.MyPreferences.getAppVersion(getApplicationContext()),uname,pass).execute();
         }else
         {
            Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
         }
     } else 
       Log.i(TAG, "No valid Google Play Services APK found.");
     
     break;
     case R.id.btnupdateUID:
    	 if (checkPlayServices()) 
    	 {
    		 gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
	         regid = Helper.MyPreferences.getRegistrationId(SyncActivity.this);
	         if (regid.isEmpty()) {
	           Toast.makeText(getApplicationContext(), "Please click Register First", Toast.LENGTH_SHORT).show();
	         }
	         else
	         {
	        	uname=txtUID.getText().toString();
	        	new Helper.ExcutelinkOnline("http://treckon.recknowledge.com/register.php?regId=" + regid+"&uname="+uname+"&pass=1234&senderid="+Helper.SENDER_ID).execute();
	        	final SharedPreferences prefs = Helper.MyPreferences.getAppPreferences(this);
	             SharedPreferences.Editor editor = prefs.edit();
	             editor.putString(Helper.MyPreferences.PROPERTY_UNAME, uname);
	             editor.putString(Helper.MyPreferences.PROPERTY_PASS, pass);
	             editor.commit();
		        	Toast.makeText(getApplicationContext(), "UID Updated", Toast.LENGTH_SHORT).show();
		        	//new RegisterApp(SyncActivity.this, gcm, Helper.MyPreferences.getAppVersion(getApplicationContext()),uname,pass).execute();
		     }
		 } 
    	 else {
		         Log.i(TAG, "No valid Google Play Services APK found.");
	     }
    	 break;
     case R.id.btnsendMessage:
    	 String msg=txtmsg.getText().toString();
    	 msg=msg.replace(" ","%20");
    	 String link="http://treckon.recknowledge.com/communicateTreckon.php?regId=" + regid +"&uname="+txttoId.getText().toString()+"&msg="+msg;
    	 new Helper.ExcutelinkOnline(link).execute();
    	 lblMessages.setText(link);
    	 Toast.makeText(getApplicationContext(), "selected Msg Sent", Toast.LENGTH_SHORT).show();
     }
}  
 
 @Override 
 protected void onNewIntent (Intent intent)
 {
	 super.onNewIntent(intent);
	 lblMessages.setText(Helper.MyPreferences.getPreferences(this, "tempmessage"));
 }
}