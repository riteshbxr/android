package com.projects.utils;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.projects.em.Helper;
import com.projects.em.R;

import android.app.Activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;
 
 
public class RegisterApp extends AsyncTask<Void, Void, String> {
 
 private static final String TAG = "GCMRelated";
  Activity ctx;
 GoogleCloudMessaging gcm;
 
 String regid = null; 
 String uname = null;
 String pass = null;
 private int appVersion;
 public RegisterApp(Activity ctx, GoogleCloudMessaging gcm, int appVersion,String uname,String pass){
  this.ctx = ctx;
  this.gcm = gcm;
  this.appVersion = appVersion;
  this.uname=uname;
  this.pass=pass;
  Helper.SENDER_ID=ctx.getResources().getString(R.string.GCMSenderid);
 }
  
  
 @Override
 protected void onPreExecute() {
  super.onPreExecute();
  Toast.makeText(ctx, "Starting registration Process", Toast.LENGTH_SHORT).show();
 }
 
 
 @Override
 protected String doInBackground(Void... arg0) {
  String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);
            }
            regid = gcm.register(Helper.SENDER_ID);
            msg = "Device registered, registration ID=" + regid;
 
            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();
 
            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.
 
            // Persist the regID - no need to register again.
            storeRegistrationId(ctx, regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
 }
 
 private void storeRegistrationId(Activity ctx, String regid) {
  final SharedPreferences prefs = Helper.MyPreferences.getAppPreferences(ctx);
     Log.i(TAG, "Saving regId on app version " + appVersion);
     SharedPreferences.Editor editor = prefs.edit();
     editor.putString(Helper.MyPreferences.PROPERTY_REG_ID, regid);
     editor.putInt(Helper.MyPreferences.PROPERTY_APP_VERSION, appVersion);
     editor.putString(Helper.MyPreferences.PROPERTY_UNAME, uname);
     editor.putString(Helper.MyPreferences.PROPERTY_PASS, pass);
     editor.commit();
 }
 
 
 private void sendRegistrationIdToBackend() {
	 Helper.SendMessageOnline("http://treckon.recknowledge.com/register.php?regId=" + regid+"&uname="+uname+"&pass="+pass+"&senderid="+Helper.SENDER_ID);
 }
 
 
 @Override
 protected void onPostExecute(String result) {
  super.onPostExecute(result);
  Toast.makeText(ctx, "Registration Completed. Now you can send trips to friends", Toast.LENGTH_SHORT).show();
  Log.v(TAG, result);
 }
}