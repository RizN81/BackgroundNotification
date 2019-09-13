package com.app.backgroundnotifications.listener;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.app.backgroundnotifications.service.NotificationIntentService;


/**
 * This is WakefulBroadcastReceiver used to receive intents fired from the AlarmManager for showing notifications
 * and from the notification itself if it is deleted.
 */
public class NotificationEventReceiver extends WakefulBroadcastReceiver {
	//Create key for setting Alram
	private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
	private static final String ACTION_DELETE_NOTIFICATION        = "ACTION_DELETE_NOTIFICATION";
	
	
	//This method will setup a background alarm which works in background to fetch the sensors //data from server and display in the notification bar of Android
	
	public static void setupAlarm(Context context) {
		
		AlarmManager  alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent alarmIntent  = getStartPendingIntent(context);//120000
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP ,
		                          getTriggerAt(new Date()) ,
		                          1000,
		                          alarmIntent);
	}
	//This method will cancel a background alarm which is running
	public static void cancelAlarm(Context context) {
		
		AlarmManager  alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent alarmIntent  = getStartPendingIntent(context);
		alarmManager.cancel(alarmIntent);
	}
	
	//This method will return current date time stamp in Mill Seconds which required to setup //background alarm
	public static long getTriggerAt(Date now) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		//calendar.add(Calendar.HOUR, NOTIFICATIONS_INTERVAL_IN_HOURS);
		return calendar.getTimeInMillis();
	}
	
	//This method will return Pending intent in which we call NotificationEventReceiver
	//to call it from background Alarm
	private static PendingIntent getStartPendingIntent(Context context) {
		
		Intent intent = new Intent(context , NotificationEventReceiver.class);
		intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
		return PendingIntent.getBroadcast(context , 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT);
	}
	//This method will return Delete Pending intent in which we call NotificationEventReceiver
	//to call it from background Alarm
	public static PendingIntent getDeleteIntent(Context context) {
		
		Intent intent = new Intent(context , NotificationEventReceiver.class);
		intent.setAction(ACTION_DELETE_NOTIFICATION);
		return PendingIntent.getBroadcast(context , 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	//This is the default method of BroadCast Receiver which receive calls when background alarm trigger it.
	@Override
	public void onReceive(Context context , Intent intent) {
		
		String action        = intent.getAction();
		//Here we check the action if its Start then we start background alarm again 
		Intent serviceIntent = null;
		if ( ACTION_START_NOTIFICATION_SERVICE.equals(action) ) {
			Log.i(getClass().getSimpleName() , "onReceive from alarm, starting notification service");
			serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
		}
		//We check if user clear the notification from notification if yes then we start background alarm
		else if ( ACTION_DELETE_NOTIFICATION.equals(action) ) {
			Log.i(getClass().getSimpleName() , "onReceive delete notification action, starting notification service to handle delete");
			serviceIntent = NotificationIntentService.createIntentDeleteNotification(context);
		}
		
		//We get serviceIntent not null if deivce is starting
		if ( serviceIntent != null ) {
		NotificationIntentService.enqueueWork(context,serviceIntent);
//			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//				context.startService(serviceIntent);
//			} else {
//				//context.startService(serviceIntent);
//				startWakefulService(context , serviceIntent);
//			}
			// Start the service, keeping the device awake while it is launching.
			//startWakefulService(context , serviceIntent);
		}
	}
}
