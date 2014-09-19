package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.AUTN_NUM;
import static com.darshak.constants.PacketAttributeType.RND_NUM;
import static com.darshak.constants.PacketType._3G_INIT_AUTH_REQ;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

public class UMTSInitAuthReqFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		String hexCode = formatHexBytes(packetBytes);
		Packet packet = new Packet(_3G_INIT_AUTH_REQ, hexCode);
		packet.addPacketAttribute(getRandomNum(packetBytes));
		packet.addPacketAttribute(getAUTN(packetBytes));
		return packet;
	}

	private PacketAttribute getRandomNum(byte[] packetBytes) {
		byte[] randNumBytes = extract(packetBytes, 3, 19);
		String hexCode = formatHexBytes(randNumBytes);
		String displayText = RND_NUM.getInfo() + " : " + hexCode;
		return new PacketAttribute(RND_NUM, hexCode, displayText);
	}

	private PacketAttribute getAUTN(byte[] packetBytes) {
		byte[] autnBytes = extract(packetBytes, 21, 37);
		String hexCode = formatHexBytes(autnBytes);
		String displayText = AUTN_NUM.getInfo() + " : " + hexCode;
		return new PacketAttribute(AUTN_NUM, hexCode, displayText);
	}
}