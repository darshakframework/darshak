package com.darshak.service;

import java.util.HashSet;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.darshak.AirplaneModeConfigureActivity;
import com.darshak.Application;
import com.darshak.R;
import com.darshak.db.DarshakDBHelper;
import com.darshak.modal.PacketAttribute;

public class ProfileParamsComparisonTask extends
		AsyncTask<PacketAttribute, String, Integer> {

	private static final String LOG_TAG = ProfileParamsComparisonTask.class
			.getSimpleName();

	private static final String tickerText = "Profile parameter changed";
	private static final String contentTitle = "Profile parameter changed Notification";
	private static final String contentText = "Alert- Profile parameter changed. If you are travelling then ignore this notification.";

	private long[] sVibratePattern = { 0, 200, 200, 300 };

	// Notification Action Elements
	private PendingIntent sContentIntent;
	private Context sContext;
	DarshakDBHelper sDBHelper;

	public ProfileParamsComparisonTask(Context context) {
		sContext = context;
		// Set the intent to be triggered when clicked on notification,
		Intent sNotificationIntent = new Intent(sContext,
				AirplaneModeConfigureActivity.class);
		sContentIntent = PendingIntent.getActivity(sContext, 0,
				sNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		sDBHelper = ((Application) sContext.getApplicationContext())
				.getDBHelper();
	}

	@Override
	protected Integer doInBackground(PacketAttribute... params) {
		Set<String> changedPacketsAttrTypeIds = new HashSet<String>();
		if (params != null && params.length > 0) {			
			for (PacketAttribute packetAttr : params) {
				if (!sDBHelper.isProfileParamPresent(packetAttr)) {
					changedPacketsAttrTypeIds.add(packetAttr
							.getPacketAttrType().name());
					sDBHelper.insertProfileParams(packetAttr);
				}
			}
		}
		if (changedPacketsAttrTypeIds.size() > 0) {
			publishProgress(changedPacketsAttrTypeIds.toString());
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(String... paramsChanged) {		
		setNotification(paramsChanged);		
	}

	private void setNotification(String... paramsChanged) {
		Log.e(LOG_TAG, "Notification is set to due to change.");
		Notification.Builder notificationBuilder = new Notification.Builder(
				sContext).setTicker(tickerText)
				.setSmallIcon(R.drawable.icon_in_white)
				.setAutoCancel(true)
				.setContentTitle(contentTitle)
				.setContentText(contentText)
				.setContentIntent(sContentIntent)
				.setVibrate(sVibratePattern)
				.setStyle(new Notification.BigTextStyle()
				.bigText(contentText + paramsChanged[0]));

		// Pass the Notification to the NotificationManager:
		NotificationManager mNotificationManager = (NotificationManager) sContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1313, notificationBuilder.build());
	}
}
