package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.A51_AVLBLE_ON_MOBILE;
import static com.darshak.constants.PacketAttributeType.A51_NT_AVLBLE_ON_MOBILE;
import static com.darshak.constants.PacketAttributeType.A52_AVLBLE_ON_MOBILE;
import static com.darshak.constants.PacketAttributeType.A52_NT_AVLBLE_ON_MOBILE;
import static com.darshak.constants.PacketAttributeType.A53_AVLBLE_ON_MOBILE;
import static com.darshak.constants.PacketAttributeType.A53_NT_AVLBLE_ON_MOBILE;
import static com.darshak.constants.PacketType.GSM_INIT_SERV_REQ;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.constants.PacketAttributeType;
import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

public class GSMInitServiceRequestFormatter extends PacketFormatter {

	private static final byte A51_MASK = 0x08;
	private static final byte A53_MASK = 0x02;
	private static final byte A52_MASK = 0x01;

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		String hexCode = formatHexBytes(packetBytes);
		Packet packet = new Packet(GSM_INIT_SERV_REQ, hexCode);
		packet.addPacketAttribute(getA51Support(packetBytes));
		packet.addPacketAttribute(getA52Support(packetBytes));
		packet.addPacketAttribute(getA53Support(packetBytes));
		packet.addPacketAttribute(getTMSINumber(packetBytes));
		return packet;
	}

	private PacketAttribute getA51Support(byte[] packetBytes) {
		byte[] a51Bytes = extract(packetBytes, 7, 8);
		String hexCode = formatHexBytes(a51Bytes);
		byte tmp = a51Bytes[0];
		// 4 bit from left is set to zero then A5/1 algorithm supported.
		if ((A51_MASK & tmp) == (byte) 0x00) {
			return new PacketAttribute(A51_AVLBLE_ON_MOBILE, hexCode,
					A51_AVLBLE_ON_MOBILE.getInfo());
		}
		return new PacketAttribute(A51_NT_AVLBLE_ON_MOBILE, hexCode,
				A51_NT_AVLBLE_ON_MOBILE.getInfo());

	}

	private PacketAttribute getA53Support(byte[] packetBytes) {
		byte[] a53Bytes = extract(packetBytes, 9, 10);
		String hexCode = formatHexBytes(a53Bytes);
		byte tmp = a53Bytes[0];
		// 2nd bit from left is set to one then A5/3 algorithm supported.
		if ((A53_MASK & tmp) == (byte) 0x00) {
			return new PacketAttribute(A53_NT_AVLBLE_ON_MOBILE, hexCode,
					A53_NT_AVLBLE_ON_MOBILE.getInfo());
		}
		return new PacketAttribute(A53_AVLBLE_ON_MOBILE, hexCode,
				A53_AVLBLE_ON_MOBILE.getInfo());
	}

	private PacketAttribute getA52Support(byte[] packetBytes) {
		byte[] a53Bytes = extract(packetBytes, 9, 10);
		String hexCode = formatHexBytes(a53Bytes);
		byte tmp = a53Bytes[0];
		// 1st bit from left is set to one then A5/2 algorithm supported.
		if ((A52_MASK & tmp) == (byte) 0x00) {
			return new PacketAttribute(A52_NT_AVLBLE_ON_MOBILE, hexCode,
					A52_NT_AVLBLE_ON_MOBILE.getInfo());
		}
		return new PacketAttribute(A52_AVLBLE_ON_MOBILE, hexCode,
				A52_AVLBLE_ON_MOBILE.getInfo());
	}

	private PacketAttribute getTMSINumber(byte[] packetBytes) {
		byte[] tmsiBytes = extract(packetBytes, 12, 16);
		String hexCode = formatHexBytes(tmsiBytes);
		String displayText = PacketAttributeType.TMSI_NUM.getInfo() + " : "
				+ hexCode;
		return new PacketAttribute(PacketAttributeType.TMSI_NUM, hexCode,
				displayText);
	}
}