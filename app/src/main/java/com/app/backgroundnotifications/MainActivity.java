package com.app.backgroundnotifications;

import static com.app.backgroundnotifications.service.SyncService.RESULT_CODE;
import static com.app.backgroundnotifications.service.SyncService.RESULT_VALUE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.app.backgroundnotifications.listener.BackgroundTask;
import com.app.backgroundnotifications.listener.NotificationEventReceiver;
import com.app.backgroundnotifications.service.SyncService;

public class MainActivity extends AppCompatActivity {
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		//Initialzie Notification Delegater
//		NotificationDelegater.initialize(context , NotificationDelegater.GLOBAL);
//		NotificationGlobal global = NotificationDelegater.getInstance().global();
//		global.setViewEnabled(true);
		
		final Intent intent = new Intent(getApplicationContext() , BackgroundTask.class);
		Bundle       bundle = new Bundle();
		
		bundle.putString("playerId" , "1");
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

	@Override
	protected void onResume() {
		
		// Register for the particular broadcast based on ACTION string
		IntentFilter filter = new IntentFilter(SyncService.ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver , filter);
		super.onResume();

	}
	
	@Override
	protected void onStart() {
		//Check if user is turn off the notification then cancel it
		
		NotificationEventReceiver.cancelAlarm(getApplicationContext());
		
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		//Check if user is turn on the notification then start it
		
		NotificationEventReceiver.setupAlarm(getApplicationContext());
		
		super.onStop();
	}
	
	//----------------------BACKGROUND TASK--------------------
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context , Intent intent) {
			//Get the result code that we received from background thread for updating data
			int resultCode = intent.getIntExtra(RESULT_CODE , RESULT_CANCELED);
			
			//if result code is OK then get the data and update it
			if ( resultCode == RESULT_OK ) {
				String  resultValue   = intent.getStringExtra(RESULT_VALUE);
				boolean isDeviceState = intent.getBooleanExtra("ButtonState" , false);
				
				
				try {
					Toast.makeText(getApplicationContext() , "From Activity " + resultValue , Toast.LENGTH_LONG).show();
				}
				catch (Exception e) {
					Log.e(MainActivity.class.getName() , "Error In Broadcast" , e);
				}
				
			}
		}
	};
	
	@Override
	protected void onPause() {
		//Cancel background broadcast listener
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		//cancelAlarm();
		super.onPause();
	}
	
	//This method will be used to cancel the alarm manager
	public void cancelAlarm() {
		
		Intent intent = new Intent(getApplicationContext() , BackgroundTask.class);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this , BackgroundTask.REQUEST_CODE ,
		                                                         intent , PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pIntent);
	}
}
