package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.SRES_VALUE;
import static com.darshak.constants.PacketAttributeType.XRES_VALUE;
import static com.darshak.constants.PacketType._3G_AUTH_RES;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

public class UMTSAuthenticationResponseFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		// 1st byte of the packet should be 0x*4
		/*byte tmp = packetBytes[1];
		tmp = (byte) (tmp << 4);
		if ((byte) 0x40 == tmp) {*/
			String hexCode = formatHexBytes(packetBytes);
			Packet packet = new Packet(_3G_AUTH_RES, hexCode);
			packet.addPacketAttribute(getSRESValue(packetBytes));
			packet.addPacketAttribute(getXRESValue(packetBytes));
			return packet;
		/*}
		return null;*/
	}

	private PacketAttribute getSRESValue(byte[] packetBytes) {
		byte[] randNumBytes = extract(packetBytes, 2, 6);
		String hexCode = formatHexBytes(randNumBytes);
		String displayText = SRES_VALUE.getInfo() + " : " + hexCode;
		return new PacketAttribute(SRES_VALUE, hexCode, displayText);
	}

	private PacketAttribute getXRESValue(byte[] packetBytes) {
		byte[] randNumBytes = extract(packetBytes, 8, 12);
		String hexCode = formatHexBytes(randNumBytes);
		String displayText = XRES_VALUE.getInfo() + " : " + hexCode;
		return new PacketAttribute(XRES_VALUE, hexCode, displayText);
	}
}