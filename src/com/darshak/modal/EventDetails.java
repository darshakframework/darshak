package com.darshak.modal;

import java.io.Serializable;

import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.constants.NetworkType;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class EventDetails implements Serializable {
	
	private static final long serialVersionUID = 8121117510726611701L;
	
	private long sUid;
	private Event sEvent;
	private long sReportedAt;
	private NetworkType sNwType;
	private String sNwOperator;

	public EventDetails(long uid, Event event, long reportedAt, NetworkType nwType, String nwOperator) {
		super();
		this.sUid = uid;
		this.sEvent = event;
		this.sReportedAt = reportedAt;
		this.sNwType = nwType;
		this.sNwOperator = nwOperator;
	}
	
	public EventDetails(Event event, NetworkType nwType, String nwOperator) {
		this(Constants.UNKNOWN_UID, event, System.currentTimeMillis(), nwType,
				nwOperator);
	}

	public long getUid() {
		return sUid;
	}

	public Event getEvent() {
		return sEvent;
	}

	public long getReportedAt() {
		return sReportedAt;
	}

	public NetworkType getNwType() {
		return sNwType;
	}

	public String getNwOperator() {
		return sNwOperator;
	}

	@Override
	public String toString() {
		return "EventDetails [sEvent=" + sEvent + ", sReportedAt="
				+ sReportedAt + ", sNwType=" + sNwType + ", sNwOperator="
				+ sNwOperator + "]";
	}
}