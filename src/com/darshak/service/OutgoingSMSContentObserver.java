package com.darshak.service;

import android.database.ContentObserver;
import android.net.Uri;
import android.util.Log;

import com.darshak.Application;
import com.darshak.MainActivity;
import com.darshak.constants.Event;
import com.darshak.modal.EventDetails;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class OutgoingSMSContentObserver extends ContentObserver {

	private static final String LOG_TAG = OutgoingSMSContentObserver.class
			.getSimpleName();

	private MainActivity sMainActivity;

	private Application sApplication;

	public OutgoingSMSContentObserver(MainActivity mainActivity) {
		super(null);
		this.sMainActivity = mainActivity;
		this.sApplication = (Application) mainActivity.getApplication();
	}

	@Override
	public void onChange(boolean selfChange) {
		onChange(selfChange, null);
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		Log.e(LOG_TAG, "Either SMS has been sent or received");
		Log.e(LOG_TAG, "On SMS content change URI becomes" + uri);
		int highestSMSIDInPref = sMainActivity.getHighestSMSIdInPref();
		int highestSMDID = sMainActivity.getHighestSMSId();
		Log.e(LOG_TAG, "Highest SMS ID in pref : " + highestSMSIDInPref
				+ ", & in content " + highestSMDID);
		if (highestSMSIDInPref < highestSMDID) {
			Log.e(LOG_TAG, "New SMS being sent");
			// set new SMS count in pref
			sMainActivity.setHighestSMSIdInPref(highestSMDID);
			setCallOrSMSStatus(Event.OUTGOING_SMS);
		}
	}

	public boolean deliverSelfNotifications() {
		return false;
	}

	private void setCallOrSMSStatus(Event event) {
		EventDetails eventDetails = new EventDetails(event,
				sApplication.getNwType(), sApplication.getNwOperator());
		sApplication.getDBHelper().insertEventDetails(eventDetails);
	}
}