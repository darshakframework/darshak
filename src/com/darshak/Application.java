package com.darshak;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.darshak.constants.Constants;
import com.darshak.constants.NetworkType;
import com.darshak.db.DarshakDBHelper;
import com.darshak.util.Utils;

/**
 * @author Andreas Schildbach
 * @author Swapnil Udar & Ravishankar Borgaonkar
 */
public class Application extends android.app.Application {

	private static final String LOG_TAG = Application.class.getSimpleName();

	private DarshakDBHelper sDBHelper = null;

	private TelephonyManager telephonyManager = null;

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
	}

	@Override
	public void onTerminate() {		
		super.onTerminate();		
	}

	public DarshakDBHelper getDBHelper() {
		return sDBHelper;
	}

	public NetworkType getNwType() {
		return Utils.networkToConnectionType(telephonyManager.getNetworkType());
	}

	public String getNwOperator() {
		return telephonyManager.getNetworkOperatorName().intern();
	}
}