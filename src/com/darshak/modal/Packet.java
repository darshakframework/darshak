package com.darshak.modal;

import java.util.ArrayList;
import java.util.List;

import com.darshak.constants.PacketType;
import com.darshak.constants.Constants;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class Packet {

	private long sUid;

	private long sTime = 0;

	private long sLogEntryUid;

	private PacketType sPacketTypeId;

	private String sHexCode;

	private List<PacketAttribute> sPacketAttributes = new ArrayList<PacketAttribute>();

	public Packet(long uid, long time, long logEntryUid,
			PacketType packetTypeId, String hexCode) {
		super();
		this.sUid = uid;
		this.sTime = time;
		this.sLogEntryUid = logEntryUid;
		this.sPacketTypeId = packetTypeId;
		this.sHexCode = hexCode.intern();
	}

	public Packet(PacketType kind, String hexCode) {
		this(Constants.UNKNOWN_UID, Constants.UNKNOWN_UID,
				Constants.UNKNOWN_UID, kind, hexCode);
	}

	public long getUid() {
		return sUid;
	}

	public long getTime() {
		return sTime;
	}

	public long getLogEntryUid() {
		return sLogEntryUid;
	}

	public int getPacketTypeId() {
		return sPacketTypeId.getPacketTypeId();
	}

	public PacketType getPacketType() {
		return sPacketTypeId;
	}

	public String getHexCode() {
		return sHexCode;
	}

	public void addPacketAttributes(List<PacketAttribute> packetAttributes) {
		sPacketAttributes.addAll(packetAttributes);
	}

	public void addPacketAttribute(PacketAttribute packetAttribute) {
		sPacketAttributes.add(packetAttribute);
	}

	public List<PacketAttribute> getPacketAttributes() {
		return sPacketAttributes;
	}

	@Override
	public String toString() {
		return "Packet [sUid=" + sUid + ", sTime=" + sTime + ", sLogEntryUid="
				+ sLogEntryUid + ", sPacketTypeId=" + sPacketTypeId
				+ ", sHexCode=" + sHexCode + ", sPackets=" + sPacketAttributes
				+ "]";
	}
}