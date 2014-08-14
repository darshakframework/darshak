package com.darshak.modal;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class LogEntry {

	private long sUid;

	private int sNwType = 0;

	private int sEvent = 0;

	private long sTime = 0;

	private String sNwOperator = null;

	private boolean sIsDeleted;

	List<Packet> sPackets = new ArrayList<Packet>();
	
	List<PacketAttribute> sPacketAttributes = new ArrayList<PacketAttribute>();

	public LogEntry(long uid, long time, int typeOfLog, int event,
			String nwOperator, boolean isDeleted) {
		super();
		this.sUid = uid;
		this.sNwType = typeOfLog;
		this.sTime = time;
		this.sEvent = event;
		this.sNwOperator = nwOperator;
		this.sIsDeleted = isDeleted;
	}

	public long getUid() {
		return sUid;
	}

	public int getNwType() {
		return sNwType;
	}

	public int getEvent() {
		return this.sEvent;
	}

	public long getTime() {
		return sTime;
	}

	public String getNwOperator() {
		return sNwOperator;
	}

	public boolean isDeleted() {
		return sIsDeleted;
	}

	public void addPackets(List<Packet> packets) {
		sPackets.addAll(packets);
		for (Packet packet : packets) {
			sPacketAttributes.addAll(packet.getPacketAttributes());
		}
	}

	public List<Packet> getPackets() {
		return sPackets;
	}
	
	public List<PacketAttribute> getAllPacketAttributes() {
		return sPacketAttributes; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sPackets == null) ? 0 : sPackets.hashCode());
		result = prime * result + sEvent;
		result = prime * result + (sIsDeleted ? 1231 : 1237);
		result = prime * result
				+ ((sNwOperator == null) ? 0 : sNwOperator.hashCode());
		result = prime * result + sNwType;
		result = prime
				* result
				+ ((sPacketAttributes == null) ? 0 : sPacketAttributes
						.hashCode());
		result = prime * result + (int) (sTime ^ (sTime >>> 32));
		result = prime * result + (int) (sUid ^ (sUid >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogEntry other = (LogEntry) obj;
		if (sPackets == null) {
			if (other.sPackets != null)
				return false;
		} else if (!sPackets.equals(other.sPackets))
			return false;
		if (sEvent != other.sEvent)
			return false;
		if (sIsDeleted != other.sIsDeleted)
			return false;
		if (sNwOperator == null) {
			if (other.sNwOperator != null)
				return false;
		} else if (!sNwOperator.equals(other.sNwOperator))
			return false;
		if (sNwType != other.sNwType)
			return false;
		if (sPacketAttributes == null) {
			if (other.sPacketAttributes != null)
				return false;
		} else if (!sPacketAttributes.equals(other.sPacketAttributes))
			return false;
		if (sTime != other.sTime)
			return false;
		if (sUid != other.sUid)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LogEntry [sUid=" + sUid + ", sNwType=" + sNwType + ", sEvent="
				+ sEvent + ", sTime=" + sTime + ", sNwOperator=" + sNwOperator
				+ ", sIsDeleted=" + sIsDeleted + ", sPackets="
				+ sPackets + ", sPacketAttributes=" + sPacketAttributes
				+ "]";
	}	
}