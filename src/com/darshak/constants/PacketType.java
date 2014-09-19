package com.darshak.constants;

import com.darshak.formatter.UMTSAuthenticationResponseFormatter;
import com.darshak.formatter.GSMInitAuthReqFormatter;
import com.darshak.formatter.GSMInitCipheringModeFormatter;
import com.darshak.formatter.GSMInitServiceRequestFormatter;
import com.darshak.formatter.GSMSysInfoTypeThreeFormatter;
import com.darshak.formatter.GSMTMSIRelocationCommandFormatter;
import com.darshak.formatter.NullFormatter;
import com.darshak.formatter.PacketFormatter;
import com.darshak.formatter.UMTSServiceRequestCodeFormatter;
import com.darshak.formatter.SilentSMSFormatter;
import com.darshak.formatter.UMTSInitAuthReqFormatter;
import com.darshak.formatter.UMTSSecurityModeCommandFormatter;
import com.darshak.modal.Packet;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public enum PacketType {
	GSM_INIT_SERV_REQ(0, "GSM init service request.", new GSMInitServiceRequestFormatter()), 
	GSM_INIT_CIPHER_MODE(1, "GSM init cipher mode.", new GSMInitCipheringModeFormatter()), 
	GSM_INIT_AUTH_REQ(2, "GSM init authentication mode", new GSMInitAuthReqFormatter()),
	
	_3G_SEC_MODE_CMD(3, "3G Security mode command.", new UMTSSecurityModeCommandFormatter()),	 
	_3G_INIT_AUTH_REQ(5, "3G init authentication mode", new UMTSInitAuthReqFormatter()),

	SILENT_SMS(6, "Silent SMS", new SilentSMSFormatter()),	
	
	SYS_INFO_3(8, "System Information Type 3", new GSMSysInfoTypeThreeFormatter()), 
	TMSI_RELOCATION(9, "TMSI Relocation Command", new GSMTMSIRelocationCommandFormatter()),
	
	START_SCAN(10, "Log scan start packet", new NullFormatter()),
	
	_3G_CM_SERV_REQ(11, "3G CM service request", new UMTSServiceRequestCodeFormatter()),
	_3G_AUTH_RES(11, "3G Authentication response", new UMTSAuthenticationResponseFormatter()),
	
	NULL(12, "Dummy packet", new NullFormatter());

	private String sInfo;
	
	private int sTypeId;
	
	private PacketFormatter sFormatter;

	private PacketType(int type, String info, PacketFormatter formatter) {
		this.sInfo = info;
		this.sTypeId = type;
		this.sFormatter = formatter;
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
	
	public Packet format(byte[] packetBytes) {
		return sFormatter.formatPacket(packetBytes);
	}
}