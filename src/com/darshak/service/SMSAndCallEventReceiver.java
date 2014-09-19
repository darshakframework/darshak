package com.darshak.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.darshak.Application;
import com.darshak.constants.Event;
import com.darshak.constants.NetworkType;
import com.darshak.modal.EventDetails;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class SMSAndCallEventReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = SMSAndCallEventReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Application application = ((Application) context.getApplicationContext());
		
		NetworkType nwType = application.getNwType();
		String nwOperator = application.getNwOperator();

		if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
			Log.e(LOG_TAG, intent.getAction());
			Bundle extras = intent.getExtras();			
			if (extras != null) {
				String phoneState = extras
						.getString(TelephonyManager.EXTRA_STATE);
				if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneState)) {
					Log.e(LOG_TAG, TelephonyManager.EXTRA_STATE_RINGING);
					setCallOrSMSStatus(application, Event.INCOMING_CALL, nwType, nwOperator);
				}
			}
		}
		if ("android.intent.action.NEW_OUTGOING_CALL"
				.equals(intent.getAction())) {
			Log.e(LOG_TAG, intent.getAction());
			setCallOrSMSStatus(application, Event.OUTGOING_CALL, nwType, nwOperator);
		}
		if ("android.provider.Telephony.SMS_RECEIVED"
				.equals(intent.getAction())) {
			Log.e(LOG_TAG, "SMS received " + intent.getAction());
			setCallOrSMSStatus(application, Event.INCOMING_SMS, nwType, nwOperator);			
			/*application.setSMSReceivedReceiverInvoked(true);*/
		}
		if ("android.provider.Telephony.SMS_DELIVER".equals(intent.getAction())) {
			Log.e(LOG_TAG, "SMS delivered " + intent.getAction());
			setCallOrSMSStatus(application, Event.OUTGOING_SMS, nwType, nwOperator);
		}
	}

	private void setCallOrSMSStatus(Application application, Event event, NetworkType nwType,
			String nwOperator) {
		EventDetails eventDetails = new EventDetails(event, nwType, nwOperator);
		application.getDBHelper().insertEventDetails(eventDetails);		
	}
}