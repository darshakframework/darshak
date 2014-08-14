package com.darshak.constants;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public enum PacketType {
	GSM_INIT_SERV_REQ(0, "GSM init service request."), 
	GSM_INIT_CIPHER_MODE(1, "GSM init cipher mode."), 
	GSM_INIT_AUTH_REQ(2, "GSM init authentication mode"),
	
	_3G_INIT_SERV_REQ(3, "3G init service request."),	
	_3G_INIT_CIPHER_MODE(4, "3G init cipher mode."), 
	_3G_INIT_AUTH_REQ(5, "3G init authentication mode"),

	SILENT_SMS(6, "Silent SMS");

	private String sInfo;
	
	private int sTypeId;

	private PacketType(int type, String info) {
		this.sInfo = info;
		this.sTypeId = type;
	}

	public String getInfo() {
		return sInfo;
	}

	public int getPacketTypeId() {
		return sTypeId;
	}

	public static PacketType getPacketTypeById(int typeId) {
		for (PacketType packetType : values()) {
			if (packetType.getPacketTypeId() == typeId) {
				return packetType;
			}
		}
		throw new IllegalArgumentException("Invalid ordinal for PacketType");
	}
}