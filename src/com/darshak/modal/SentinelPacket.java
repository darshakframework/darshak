package com.darshak.modal;

public class SentinelPacket {

	private int sScanType;
	private byte[] sByteSequence;

	public SentinelPacket(int scanType, byte[] byteSequence) {
		super();
		this.sScanType = scanType;
		this.sByteSequence = byteSequence;
	}

	public int getScanType() {
		return sScanType;
	}

	public byte[] getByteSequence() {
		return sByteSequence;
	}
}
