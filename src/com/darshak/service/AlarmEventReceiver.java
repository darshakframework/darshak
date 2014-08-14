package com.darshak.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.darshak.Application;
import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.constants.NetworkType;
import com.darshak.modal.EventDetails;

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

		Application application = ((Application) context.getApplicationContext());
		
		EventDetails eventDetails = application.getDBHelper().getOldestUnconsumedEvent();
		if (eventDetails == null) {
			NetworkType nwType = application.getNwType();
			String nwOperator = application.getNwOperator();
			eventDetails = new EventDetails(Event.NONE, nwType, nwOperator);
		}
		Log.d(LOG_TAG,
				"Alarm Broadcast received, service is invoked for event "
						+ eventDetails);

		Intent serviceIntent = new Intent(context, DarshakService.class);
		serviceIntent.putExtra(Constants.EVENT, eventDetails);
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