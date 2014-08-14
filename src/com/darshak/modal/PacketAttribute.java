package com.darshak.modal;

import com.darshak.constants.Constants;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class PacketAttribute {

	private long sUid;

	private long sTime = 0;

	private long sPacketUid;

	private int sPacketAttrTypeId;

	private String sHexCode;

	private String sDisplayText;

	public PacketAttribute(long uid, long time, long packetUid,
			int packetAttrTypeId, String hexCode, String displayText) {
		super();
		this.sUid = uid;
		this.sTime = time;
		this.sPacketUid = packetUid;
		this.sHexCode = hexCode;
		this.sDisplayText = displayText;
		this.sPacketAttrTypeId = packetAttrTypeId;
	}

	public PacketAttribute(int kind, String hexCode, String displayText) {
		this(Constants.UNKNOWN_UID, System.currentTimeMillis(),
				Constants.UNKNOWN_UID, kind, hexCode, displayText);
	}

	public long getUid() {
		return sUid;
	}

	public long getTime() {
		return sTime;
	}

	public long getPacketUid() {
		return sPacketUid;
	}

	public String getHexCode() {
		return sHexCode;
	}

	public String getDisplayText() {
		return sDisplayText;
	}

	public int getPacketAttrTypeId() {
		return sPacketAttrTypeId;
	}

	@Override
	public String toString() {
		return "PacketAttribute [sUid=" + sUid + ", sTime=" + sTime
				+ ", sPacketUid=" + sPacketUid + ", sPacketAttrTypeId="
				+ sPacketAttrTypeId + ", sHexCode=" + sHexCode
				+ ", sDisplayText=" + sDisplayText + "]";
	}
}