package com.darshak.formatter;

import com.darshak.constants.PacketType;
import com.darshak.modal.Packet;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class NullFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		return new Packet(PacketType.START_SCAN, "");
	}

}
