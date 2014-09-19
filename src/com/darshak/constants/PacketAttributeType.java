package com.darshak.constants;


/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public enum PacketAttributeType {
	TMSI_NUM(0, "TMSI Number"),
	RND_NUM(1, "RANDOM Number"),
	
	A51_AVLBLE_ON_MOBILE(2, "Encryption algorithm A5/1 is available on mobile device."),
	A51_NT_AVLBLE_ON_MOBILE(3, "Encryption algorithm A5/1 is not available on mobile device."),	
	A52_AVLBLE_ON_MOBILE(4, "Encryption algorithm A5/2 is available on mobile device."),
	A52_NT_AVLBLE_ON_MOBILE(5, "Encryption algorithm A5/2 is not available on mobile device."),	
	A53_AVLBLE_ON_MOBILE(6, "Encryption algorithm A5/3 is available on mobile device."),
	A53_NT_AVLBLE_ON_MOBILE(7, "Encryption algorithm A5/3 is not available on mobile device."),
	
	NW_OP_REQ_START_ENC(8, "Network operator is requesting to start ciphering."),
	NW_NT_OP_REQ_START_ENC(9, "Network operator is not requesting to start ciphering. Communication is insecure."),
	
	NW_OP_USING_A51(10, "Network operator is using A5/1 encryption algorithm for mobile communication."),
	NW_OP_USING_A52(11, "Network operator is using A5/2 encryption algorithm for mobile communication."),
	NW_OP_USING_A53(12, "Network operator is using A5/3 encryption algorithm for mobile communication."),
	
	NW_OP_REQ_IMEI(13, "Network operator is requesting IMEI."),
	NW_OP_NT_REQ_IMEI(14, "Network operator is not requesting IMEI."),	
	NW_OP_AUTHENTICATES(15, "Network operator does authentication."),
	
	SS_SENDER(16, "Silent SMS Sender number"),
	SS_TSMP(17, "Time at which silent SMS received"),
	
	NW_OP_USING_UEA1(18, "Network operator uses uea1 algorithm for encryption."),
	AUTN_NUM(19, "AUTN Number"),
	
	CELL_IDENTITY(20, "Cell Identity"),
	MOB_CNTRY_CODE(21, "Mobile Country Code"), 
	MOB_NW_CODE(22, "Mobile Network Code"),
	LOC_AREA_CODE(23, "Location Area Code"),
	SINGLE_CHNL_ARFCN(24, "Single channel ARFCN"),	
	MSCR(26, "MSCR"),
	ASSIGNED_TMSI(27, "Assigned TMSI"), 
	CELL_OPTIONS(28, "Cell options"), 
	CELL_SELECTION_PARAMS(29, "Cell selection parameters"),
	NW_OP_USING_UEA0(30, "Network operator uses uea0 algorithm for encryption"),
	TMSI_ASSIGNED(31, "TMSI assigne"),
	SRES_VALUE(32, "SRES value"),
	XRES_VALUE(33, "XRES value");

	private int sTypeId;
	private String sInfo;
	
	private PacketAttributeType(int type, String info) {
		this.sTypeId = type;
		this.sInfo = info;	
	}
	
	public int getTypeId() {
		return sTypeId;
	}
	
	public String getInfo() {
		return sInfo;
	}
	
	public static PacketAttributeType getPacketAttributeType(int typeId) {		
		for (PacketAttributeType codeDisTxt : values()) {
			if (codeDisTxt.getTypeId() == typeId) {
				return codeDisTxt;
			}
		}
		throw new IllegalArgumentException("Incorrect packet attribute type id");
	}
}
