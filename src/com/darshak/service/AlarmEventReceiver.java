package com.darshak.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class AlarmEventReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = AlarmEventReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		// Check if the call is in progress then invoke the DarshakService
		if (isCallActive(context)) {
			// Do nothing.
			return;
		}
		Log.e(LOG_TAG, "Alarm receiver invoked.");
		Intent serviceIntent = new Intent(context, DarshakService.class);
		context.startService(serviceIntent);
	}

	private boolean isCallActive(Context context) {
		AudioManager manager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (manager.getMode() == AudioManager.MODE_IN_CALL) {
			return true;
		} else {
			return false;
		}
	}
}