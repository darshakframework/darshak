package com.darshak.formatter;

import com.darshak.constants.PacketType;
import com.darshak.modal.Packet;

public class NullFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		return new Packet(PacketType.START_SCAN, "");
	}

}
