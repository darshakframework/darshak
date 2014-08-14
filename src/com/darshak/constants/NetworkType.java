package com.darshak.constants;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public enum NetworkType {
	GSM(1, "GSM"), _3G(2, "3G");

	private int sNwTypeCode;
	private String sNwTypeDesc;

	private NetworkType(int nwTypeCode, String nwTypeDesc) {
		this.sNwTypeCode = nwTypeCode;
		this.sNwTypeDesc = nwTypeDesc;
	}

	public int getNwTypeCode() {
		return this.sNwTypeCode;
	}

	public String getNwTypeDesc() {
		return this.sNwTypeDesc;
	}

	public static NetworkType getMatchingNetworkType(int nwTypeCode) {
		for (NetworkType nwType : values()) {
			if (nwType.getNwTypeCode() == nwTypeCode) {
				return nwType;
			}
		}
		throw new IllegalArgumentException("Incorrect Network type code");
	}
}