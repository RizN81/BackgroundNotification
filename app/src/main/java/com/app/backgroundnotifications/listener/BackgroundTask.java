package com.app.backgroundnotifications.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.app.backgroundnotifications.service.SyncService;


//This is the BroadcastReceiver which execute at the interval of 5000 to update the data on GUI
//This BroadcastReceiver will start SyncService which get the data from Server and return it to //the caller
public class BackgroundTask extends BroadcastReceiver {
	public static final  int    REQUEST_CODE = 100;
	private static final String TAG          = BackgroundTask.class.getName();
	
	//This is the default method of BroadcastReceiver
	@Override
	public void onReceive(Context context , Intent intent) {
		
		try {
			//Create a bundle object and intent object for calling SyncService
			Bundle bundle        = intent.getExtras();
			Intent serviceIntent = new Intent(context , SyncService.class);
			serviceIntent.putExtras(bundle);
			context.startService(serviceIntent);
			
		}
		catch (IllegalStateException e) {
			Log.e(TAG , "Error in Background Task" , e);
		}
	}
	
}
