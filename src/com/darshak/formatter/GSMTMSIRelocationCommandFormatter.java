package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.TMSI_ASSIGNED;
import static com.darshak.constants.PacketType.TMSI_RELOCATION;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class GSMTMSIRelocationCommandFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		String hexCode = formatHexBytes(packetBytes);
		Packet packet = new Packet(TMSI_RELOCATION, hexCode);
		packet.addPacketAttribute(getAssignedTMSI(packetBytes));
		return packet;
	}

	private PacketAttribute getAssignedTMSI(byte[] packetBytes) {
		byte[] tmsiBytes = extract(packetBytes, 12, 16);
		String hexCode = formatHexBytes(tmsiBytes);
		String displayText = TMSI_ASSIGNED.getInfo() + " : " + hexCode;
		return new PacketAttribute(TMSI_ASSIGNED, hexCode, displayText);
	}
}
