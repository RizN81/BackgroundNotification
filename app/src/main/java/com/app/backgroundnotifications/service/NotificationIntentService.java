package com.app.backgroundnotifications.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;
import com.app.backgroundnotifications.R;
import com.app.backgroundnotifications.listener.NotificationEventReceiver;


/**
 * This is Intent Service which perform getting of sensor data from Server(Cloud)
 * // and creating and showing Notification in Android Device Notification bar
 */
public class NotificationIntentService extends JobIntentService {
	
	//This are the tags required for this Service class to check for received actions
	private static final String TAG             = NotificationIntentService.class.getName();
	private static final int    NOTIFICATION_ID = 1;
	private static final String ACTION_START    = "ACTION_START";
	
	@Override
	public int onStartCommand(@Nullable Intent intent , int flags , int startId) {
		
		return START_STICKY;
	}
	
	/**
	 * This is called if the service is currently running and the user has
	 * removed a task that comes from the service's application.  If you have
	 * set {@link ServiceInfo#FLAG_STOP_WITH_TASK ServiceInfo.FLAG_STOP_WITH_TASK}
	 * then you will not receive this callback; instead, the service will simply
	 * be stopped.
	 *
	 * @param rootIntent The original root Intent that was used to launch
	 *                   the task that is being removed.
	 */
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		
		NotificationEventReceiver.setupAlarm(getApplicationContext());
		super.onTaskRemoved(rootIntent);
	}
	
	private static final String ACTION_DELETE = "ACTION_DELETE";
	
	// Service unique ID
	static final int SERVICE_JOB_ID = 50;
	
	// Enqueuing work in to this service.
	public static void enqueueWork(Context context , Intent work) {
		
		enqueueWork(context , NotificationIntentService.class , SERVICE_JOB_ID , work);
	}
	
	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		
		onHandleIntent(intent);
	}
	
	//This method will create NotificationIntentService and set start Action and return it
	//For strat notifcation
	public static Intent createIntentStartNotificationService(Context context) {
		
		Intent intent = new Intent(context , NotificationIntentService.class);
		intent.setAction(ACTION_START);
		return intent;
	}
	
	//This method will create NotificationIntentService and set start Action and return it
	//For delete notification
	public static Intent createIntentDeleteNotification(Context context) {
		
		Intent intent = new Intent(context , NotificationIntentService.class);
		intent.setAction(ACTION_DELETE);
		return intent;
	}
	
	
	
	private void onHandleIntent(Intent intent) {
		
		try {
			//if action is start then we get the latest sensor data from cloud and show it in //notifcation
			String action = intent.getAction();
			if ( ACTION_START.equals(action) ) {
				//Get the data from server
				//createNotificationUI("This is a user notification message from the background service");
				notificationDialog();
			}
		}
		finally {
			
			//Here we receives a device wakeup event and then passes the work off to a Service, while ensuring that the device does not go back to sleep during the transition.
			WakefulBroadcastReceiver.completeWakefulIntent(intent);
		}
	}
	
	private void notificationDialog() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String NOTIFICATION_CHANNEL_ID = "Battlesapce";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			@SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
			// Configure the notification channel.
			notificationChannel.setDescription("Sample Channel description");
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
			notificationChannel.enableVibration(true);
			notificationManager.createNotificationChannel(notificationChannel);
		}
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
		notificationBuilder.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.mipmap.ic_launcher)
				.setTicker("Battlespace")
				//.setPriority(Notification.PRIORITY_MAX)
				.setContentTitle("This is a test notifcation Title")
				.setContentText("This is sample notification")
				.setContentInfo("Information");
		notificationManager.notify(1, notificationBuilder.build());
	}
	
	//This method will create a GUI for showing Notification on Android device
	private void createNotificationUI(String content) {
		//Create the notification builder to create the notification object
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentTitle(getString(R.string.app_name)) //Set the title
				.setAutoCancel(true) //Set auto cancel true
				.setColor(getResources().getColor(R.color.colorAccent)) //Set Background color
				.setStyle(new NotificationCompat.BigTextStyle().bigText(content).setSummaryText("Test Data")) //Set Data
				.setShowWhen(true) //Set Show now
				.setSmallIcon(R.drawable.diploma); //Set icon
		
		//Following pending intent are required for above notification for execution
		PendingIntent pendingIntent = PendingIntent.getActivity(this ,
		                                                        NOTIFICATION_ID ,
		                                                        new Intent() ,
		                                                        PendingIntent.FLAG_UPDATE_CURRENT);
		
		//Set Pending intent
		builder.setContentIntent(pendingIntent);
		
		//Set Delete intent which execute when user clear the notification from notification panel
		builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));
		
		//Create Notification Manager object to show above created notification
//		final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//		manager.notify(1 , builder.build());
		
		//Play notification sound
		Handler handler = new Handler(getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				//Create Notification Manager object to show above created notification
				final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
					createChannel(manager);
				}
				if ( manager != null ) {
					manager.notify(1 , builder.build());
				}
				Toast.makeText(getApplicationContext() , "Test" , Toast.LENGTH_LONG).show();
				//playNotificationSound();
				
			}
		});
	}
	
	//This method play the default messge tone for our Notifcation
	private void playNotificationSound() {
		
		try {
			Uri      notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone ringtone     = RingtoneManager.getRingtone(getApplicationContext() , notification);
			ringtone.play();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@TargetApi(26)
	private void createChannel(NotificationManager notificationManager) {
		
		String name        = "Battlespace";
		String description = "Test";
		int    importance  = NotificationManager.IMPORTANCE_DEFAULT;
		
		NotificationChannel mChannel = new NotificationChannel(name , name , importance);
		mChannel.setDescription(description);
		mChannel.enableLights(true);
		mChannel.setLightColor(Color.BLUE);
		notificationManager.createNotificationChannel(mChannel);
	}
}
