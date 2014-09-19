package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.TMSI_NUM;
import static com.darshak.constants.PacketType._3G_CM_SERV_REQ;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

public class UMTSServiceRequestCodeFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		String hexCode = formatHexBytes(packetBytes);
		Packet packet = new Packet(_3G_CM_SERV_REQ, hexCode);
		packet.addPacketAttribute(getTMSI(packetBytes));
		return packet;
	}

	private PacketAttribute getTMSI(byte[] packetBytes) {
		byte[] randNumBytes = extract(packetBytes, 9, 13);
		String hexCode = formatHexBytes(randNumBytes);
		String displayText = TMSI_NUM.getInfo() + " : " + hexCode;
		return new PacketAttribute(TMSI_NUM, hexCode, displayText);
	}
}