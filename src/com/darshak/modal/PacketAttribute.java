package com.darshak.modal;

import com.darshak.constants.Constants;
import com.darshak.constants.PacketAttributeType;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class PacketAttribute {

	private long sUid;

	private long sTime = 0;

	private long sPacketUid;

	private PacketAttributeType sPacketAttrType;

	private String sHexCode;

	private String sDisplayText;

	public PacketAttribute(long uid, long time, long packetUid,
			PacketAttributeType packetAttrType, String hexCode, String displayText) {
		super();
		this.sUid = uid;
		this.sTime = time;
		this.sPacketUid = packetUid;
		this.sHexCode = hexCode;
		this.sDisplayText = displayText;
		this.sPacketAttrType = packetAttrType;
	}

	public PacketAttribute(PacketAttributeType packetAttrType, String hexCode, String displayText) {
		this(Constants.UNKNOWN_UID, System.currentTimeMillis(),
				Constants.UNKNOWN_UID, packetAttrType, hexCode, displayText);
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
		return sPacketAttrType.getTypeId();
	}

	public PacketAttributeType getPacketAttrType() {
		return sPacketAttrType;
	}

	@Override
	public String toString() {
		return "PacketAttribute [sUid=" + sUid + ", sTime=" + sTime
				+ ", sPacketUid=" + sPacketUid + ", sPacketAttrType="
				+ sPacketAttrType + ", sHexCode=" + sHexCode
				+ ", sDisplayText=" + sDisplayText + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sDisplayText == null) ? 0 : sDisplayText.hashCode());
		result = prime * result
				+ ((sHexCode == null) ? 0 : sHexCode.hashCode());
		result = prime * result
				+ ((sPacketAttrType == null) ? 0 : sPacketAttrType.hashCode());
		result = prime * result + (int) (sPacketUid ^ (sPacketUid >>> 32));
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
		PacketAttribute other = (PacketAttribute) obj;
		if (sDisplayText == null) {
			if (other.sDisplayText != null)
				return false;
		} else if (!sDisplayText.equals(other.sDisplayText))
			return false;
		if (sHexCode == null) {
			if (other.sHexCode != null)
				return false;
		} else if (!sHexCode.equals(other.sHexCode))
			return false;
		if (sPacketAttrType != other.sPacketAttrType)
			return false;
		if (sPacketUid != other.sPacketUid)
			return false;
		if (sTime != other.sTime)
			return false;
		if (sUid != other.sUid)
			return false;
		return true;
	}	
}