/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.projects.utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.projects.em.Helper;
import com.projects.em.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
 
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
 private static final String TAG = "GcmIntentService";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
 
    public GcmIntentService() {
        super("GcmIntentService");
    }
 
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(),extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(),extras);
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<2; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/2 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                Helper.MyPreferences.setPreferences( this.getApplicationContext(),"tempmessage",extras.getString("Notice"));
                sendNotification(extras.getString("Notice"),extras);
                //Helper.AnalyseRecievedMessage(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
 
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    
	private void sendNotification(String msg,Bundle extras) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent thisintent= new Intent(this, SyncActivity.class);
        thisintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
       // .setSmallIcon(R.drawable.ic_stat_gcm)
        .setContentTitle("Treckon")
        .setSmallIcon(R.drawable.ic_launcher)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg) 
        .setAutoCancel(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
        {
        	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                thisintent, PendingIntent.FLAG_UPDATE_CURRENT,extras);
        	mBuilder.setContentIntent(contentIntent);
        }
        
        Notification nf=mBuilder.build();
        nf.defaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(NOTIFICATION_ID, nf);
        
    }
}