package com.app.backgroundnotifications.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


//This is the SyncService which execute when app is running to display notifcation
public class SyncService extends IntentService  {
	private static final String TAG             = SyncService.class.getName();
	public static final  String ACTION          = SyncService.class.getPackage().getName();
	private static final int    NOTIFICATION_ID = 1;
	private static final String ACTION_START    = "ACTION_START";
	private static final String ACTION_DELETE   = "ACTION_DELETE";
	public static final String RESULT_CODE                 = "resultCode";
	public static final String RESULT_VALUE                = "resultValue";
	String jsonResponse = "";
	
	public SyncService() {
		
		super("");
	}
	
	public SyncService(String name) {
		
		super(name);
	}
	
	/**
	 * This method is invoked on the worker thread with a request to process.
	 * Only one Intent is processed at a time, but the processing happens on a
	 * worker thread that runs independently from other application logic.
	 * So, if this code takes a long time, it will hold up other requests to
	 * the same IntentService, but it will not hold up anything else.
	 * When all requests have been handled, the IntentService stops itself,
	 * so you should not call {@link #stopSelf}.
	 *
	 * @param intent The value passed to {@link
	 *               Context#startService(Intent)}.
	 *               This may be null if the service is being restarted after
	 *               its process has gone away; see
	 *               {@link Service#onStartCommand}
	 *               for details.
	 */
	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		
		try {
			if ( intent != null ) {
				localBroadCast(intent);
			}
		}
		catch (Exception e) {
			Log.e(TAG , "Error In SyncService" , e);
		}
	}
	//This is the localBroadCast method and here we get device data 
	private void localBroadCast(Intent intent) {
		// Fetch data passed into the intent on start
		Bundle bundle       = intent.getExtras();
		Long   deviceID     = 1L;
		String deviceSerial = "";
		if ( bundle != null ) {
			Toast.makeText(getApplicationContext() , "playerId" + bundle.getString("playerId") , Toast.LENGTH_SHORT).show();
			//get data from server and hand over to broadcast device data function
			broadcastDeviceData("This message from sync service");

		}
		
	}
	//Here we send the received sensor data froms server from BroadCast Receiver
	private void broadcastDeviceData(String jsonResponse) {
		
		if ( !jsonResponse.isEmpty() ) {
			Intent in = new Intent(ACTION);
			// Put extras into the intent as usual
			in.putExtra(RESULT_CODE , Activity.RESULT_OK);
			in.putExtra(RESULT_VALUE , jsonResponse);
			in.putExtra("ButtonState" , false);
			// Fire the broadcast with intent packaged
			LocalBroadcastManager.getInstance(this).sendBroadcast(in);
		}
	}
}
