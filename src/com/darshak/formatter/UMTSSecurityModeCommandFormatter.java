package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.NW_OP_USING_UEA0;
import static com.darshak.constants.PacketAttributeType.NW_OP_USING_UEA1;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.constants.PacketType;
import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

public class UMTSSecurityModeCommandFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		// 0th byte of the packet should be 0x8C or 0x0c
		byte tmp = packetBytes[0];

		if (zeroByteCondition(packetBytes) && seventhByteCondition(packetBytes)
				&& twentythByteCondition(packetBytes)) {

			String hexCode = formatHexBytes(packetBytes);
			Packet packet = new Packet(PacketType._3G_SEC_MODE_CMD, hexCode);

			byte[] encAlgoBytes = extract(packetBytes, 6, 7);
			// 6th byte should be 0x4a or 0x28
			tmp = encAlgoBytes[0];
			if ((tmp == (byte) 0x4a) || (tmp == (byte) 0x28)) {
				packet.addPacketAttribute(new PacketAttribute(NW_OP_USING_UEA1,
						hexCode, NW_OP_USING_UEA1.getInfo()));
			} else {
				packet.addPacketAttribute(new PacketAttribute(NW_OP_USING_UEA0,
						hexCode, NW_OP_USING_UEA0.getInfo()));
			}
			return packet;
		}
		return null;
	}

	private boolean zeroByteCondition(byte[] packetBytes) {
		// 0th byte of the packet should be 0x8C or 0x0c
		byte tmp = packetBytes[0];
		return ((byte) 0x0C == tmp) || ((byte) 0x8C == tmp);
	}

	private boolean seventhByteCondition(byte[] packetBytes) {
		// 0th byte of the packet should be 0x8C or 0x0c
		byte tmp = packetBytes[7];
		return ((byte) 0xC0 == tmp) || ((byte) 0x30 == tmp);
	}

	private boolean twentythByteCondition(byte[] packetBytes) {
		// 20th byte of the packet should be 0x00 or 0x80
		byte tmp = packetBytes[20];
		return ((byte) 0x80 == tmp) || ((byte) 0x00 == tmp);
	}

}