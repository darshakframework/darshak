package com.darshak.constants;

import com.darshak.R;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public enum Event {
	INCOMING_SMS(1, "Incoming SMS", R.drawable.in_sms), 
	OUTGOING_SMS(2,	"Outgoing SMS", R.drawable.out_sms), 
	INCOMING_CALL(3, "Incoming Call", R.drawable.in_call), 
	OUTGOING_CALL(4, "Outgoing Call", R.drawable.out_call), 
	INCOMING_SILENT_SMS(5,	"Incoming Silent SMS", R.drawable.silent_sms),
	PROFILE_PARAMS(6,	"Profile parameters", R.drawable.nothing),	
	NONE(-1, "Null event", R.drawable.nothing);

	private int sEventCode;
	private String sEventDesc;
	private int sImageResource;

	private Event(int eventCode, String eventDesc, int imageResource) {
		this.sEventCode = eventCode;
		this.sEventDesc = eventDesc;
		this.sImageResource = imageResource;
	}

	public int getEventCode() {
		return this.sEventCode;
	}

	public String getEventDesc() {
		return this.sEventDesc;
	}

	public int getImageSource() {
		return this.sImageResource;
	}

	public static Event getMatchingEvent(int eventCode) {
		for (Event event : values()) {
			if (event.getEventCode() == eventCode) {
				return event;
			}
		}
		throw new IllegalArgumentException("Incorrect event code");
	}
}
