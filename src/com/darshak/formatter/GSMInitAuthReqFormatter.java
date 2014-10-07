package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.NW_OP_AUTHENTICATES;
import static com.darshak.constants.PacketAttributeType.RND_NUM;
import static com.darshak.constants.PacketType.GSM_INIT_AUTH_REQ;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class GSMInitAuthReqFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		String hexCode = formatHexBytes(packetBytes);
		Packet packet = new Packet(GSM_INIT_AUTH_REQ, hexCode);

		byte[] ranNumBytes = extract(packetBytes, 3, 19);
		String randNumHex = formatHexBytes(ranNumBytes);

		PacketAttribute authPacketAttr = new PacketAttribute(
				NW_OP_AUTHENTICATES, randNumHex, NW_OP_AUTHENTICATES.getInfo());

		String randNum = RND_NUM.getInfo() + " : " + randNumHex;
		PacketAttribute randNumAttr = new PacketAttribute(RND_NUM, randNumHex,
				randNum);

		packet.addPacketAttribute(authPacketAttr);
		packet.addPacketAttribute(randNumAttr);
		return packet;
	}
}