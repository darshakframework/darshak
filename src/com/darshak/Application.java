package com.darshak;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.darshak.constants.Constants;
import com.darshak.constants.NetworkType;
import com.darshak.db.DarshakDBHelper;
import com.darshak.modal.LogEntry;
import com.darshak.service.AlarmEventReceiver;
import com.darshak.service.SMSSentAndReceivedObserver;
import com.darshak.util.Utils;

/**
 * @author Andreas Schildbach
 * @author Swapnil Udar & Ravishankar Borgaonkar
 */
public class Application extends android.app.Application {

	private static final String LOG_TAG = Application.class.getSimpleName();
	
	private DarshakDBHelper sDBHelper = null;
	
	private TelephonyManager telephonyManager = null;
	
	private SimpleDateFormat sOutputDateFormat;
	
	@Override
	public void onCreate() {

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			private final UncaughtExceptionHandler previousHandler = Thread
					.getDefaultUncaughtExceptionHandler();

			@Override
			public void uncaughtException(final Thread thread, final Throwable x) {
				Log.e(LOG_TAG, "exception in " + thread.getName()
						+ " thread", x);
				previousHandler.uncaughtException(thread, x);
			}
		});

		super.onCreate();

		Log.d(LOG_TAG, "Deleting old log files...");

		Utils.deleteLogFilesWithPrefix(new String[] {
				Constants.LOG_FILE_S2_PREFIX,
				Constants.LOG_FILE_S2_PREFIX_AENAES,
				Constants.LOG_FILE_S3_PREFIX, Constants.LOG_FILE_XGS_PREFIX });

		Log.i(LOG_TAG, "application created");
		
		sDBHelper = new DarshakDBHelper(getApplicationContext());
		
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		sOutputDateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm",
				Locale.getDefault());
		
		initializeAlarmManager();		
		initializeSMSContentObserver();
	}

	@Override
	public void onTerminate() {		
		super.onTerminate();
		// TODO : Remove set Alarm
	}

	private void initializeAlarmManager() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent broadcast_intent = new Intent(getApplicationContext(),
				AlarmEventReceiver.class);

		// Check alarm is already initialized
		boolean alarmNotRegistered = PendingIntent.getBroadcast(
				getApplicationContext(), 0, broadcast_intent,
				PendingIntent.FLAG_NO_CREATE) == null;

		if (alarmNotRegistered) {
			Log.d(LOG_TAG, "Intializing Alarm");

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, broadcast_intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			Calendar now = Calendar.getInstance();
			// Start in 1 seconds
			long triggerAtTime = now.getTimeInMillis();
			// Repeat after 30 seconds : worked fall back to it
			long repeatAlarmEvery = (Constants.DARSHAK_SERV_EXE_INTERVAL_SEC * 1000);
			// TODO: which type is most suitable.
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						triggerAtTime, repeatAlarmEvery, pendingIntent);
			
			Log.d(LOG_TAG, "Intializing Alarm completed");
		} else {
			Log.d(LOG_TAG, "Alarm already registered.");
		}
	}
	
	private void initializeSMSContentObserver() {
		/* Register observer for SMS sent */
		ContentResolver contentResolver = getApplicationContext()
				.getContentResolver();
		contentResolver.registerContentObserver(
				Uri.parse("content://sms/sent"), true,
				new SMSSentAndReceivedObserver(this));
	}
	
	public DarshakDBHelper getDBHelper() {
		return sDBHelper;
	}
	
	public NetworkType getNwType() {
		return networkToConnectionType(telephonyManager.getNetworkType());
	}
	
	public String getNwOperator() {
		return telephonyManager.getNetworkOperatorName().intern();
	}

	private NetworkType networkToConnectionType(final int networkType) {
		if (networkType == 0)
			return null;
		else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS
				|| networkType == TelephonyManager.NETWORK_TYPE_HSDPA
				|| networkType == TelephonyManager.NETWORK_TYPE_HSPA
				|| networkType == TelephonyManager.NETWORK_TYPE_HSPAP
				|| networkType == TelephonyManager.NETWORK_TYPE_HSUPA)
			return NetworkType._3G;
		else if (networkType == TelephonyManager.NETWORK_TYPE_GPRS
				|| networkType == TelephonyManager.NETWORK_TYPE_EDGE
				|| networkType == TelephonyManager.NETWORK_TYPE_CDMA)
			return NetworkType.GSM;
		else
			throw new IllegalStateException(Integer.toString(networkType));
	}
	
	public String formatDate(LogEntry logEntry) {		
		Date inputDate = new Date(logEntry.getTime());		
		return sOutputDateFormat.format(inputDate).intern();
	}
	
	public String getNetworkType(LogEntry logEntry) {
		NetworkType nwType = NetworkType.getMatchingNetworkType(logEntry
				.getNwType());
		return nwType.getNwTypeDesc();
	}	
}