package com.darshak.service;

import android.database.ContentObserver;
import android.net.Uri;
import android.util.Log;

import com.darshak.Application;
import com.darshak.constants.Event;
import com.darshak.modal.EventDetails;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class SMSSentAndReceivedObserver extends ContentObserver {

	private static final String LOG_TAG = SMSSentAndReceivedObserver.class
			.getSimpleName();

	private Application sApplication;

	public SMSSentAndReceivedObserver(Application application) {
		super(null);
		this.sApplication = application;
	}

	@Override
	public void onChange(boolean selfChange) {
		onChange(selfChange, null);
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		Log.e(LOG_TAG, "ON CHANGE: EITHER SMS HAS BEEN RECEIVED OR SENT");
		setCallOrSMSStatus(Event.OUTGOING_SMS);
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