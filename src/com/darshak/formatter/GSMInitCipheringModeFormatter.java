package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.NW_NT_OP_REQ_START_ENC;
import static com.darshak.constants.PacketAttributeType.NW_OP_NT_REQ_IMEI;
import static com.darshak.constants.PacketAttributeType.NW_OP_REQ_IMEI;
import static com.darshak.constants.PacketAttributeType.NW_OP_REQ_START_ENC;
import static com.darshak.constants.PacketAttributeType.NW_OP_USING_A51;
import static com.darshak.constants.PacketAttributeType.NW_OP_USING_A52;
import static com.darshak.constants.PacketAttributeType.NW_OP_USING_A53;
import static com.darshak.constants.PacketType.GSM_INIT_CIPHER_MODE;
import static com.darshak.util.Utils.formatHexBytes;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class GSMInitCipheringModeFormatter extends PacketFormatter {

	private static final byte ENC_MASK = (byte) 0x01;
	// 0000 1110 -> 0xe
	private static final byte ENC_ALGO_MASK = (byte) 0x0e;
	private static final byte IMEI_MASK = (byte) 0x10;

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		String hexCode = formatHexBytes(packetBytes);
		Packet packet = new Packet(GSM_INIT_CIPHER_MODE, hexCode);
		packet.addPacketAttribute(getEncryptionStatus(packetBytes));
		packet.addPacketAttribute(getEncryptionAlgorithm(packetBytes));
		packet.addPacketAttribute(getIMEIRequestedStatus(packetBytes));
		return packet;
	}

	private PacketAttribute getEncryptionStatus(byte[] packetBytes) {
		byte[] encBytes = extract(packetBytes, 5, 6);
		String hexCode = formatHexBytes(encBytes);
		byte tmp = encBytes[0];
		// 1st bit from left is set to one then NW operator requests to start
		// ciphering.
		if ((ENC_MASK & tmp) == (byte) 0x00) {
			return new PacketAttribute(NW_NT_OP_REQ_START_ENC, hexCode,
					NW_NT_OP_REQ_START_ENC.getInfo());
		}
		return new PacketAttribute(NW_OP_REQ_START_ENC, hexCode,
				NW_OP_REQ_START_ENC.getInfo());

	}

	private PacketAttribute getEncryptionAlgorithm(byte[] packetBytes) {
		byte[] encBytes = extract(packetBytes, 5, 6);
		String hexCode = formatHexBytes(encBytes);
		byte tmp = encBytes[0];
		// xxxx 000x -> A51 algorithm supported.
		if ((ENC_ALGO_MASK & tmp) == (byte) 0x00) {
			return new PacketAttribute(NW_OP_USING_A51, hexCode,
					NW_OP_USING_A51.getInfo());
		}
		// xxxx 001x -> A52 algorithm supported.
		if ((ENC_ALGO_MASK & tmp) == (byte) 0x02) {
			return new PacketAttribute(NW_OP_USING_A52, hexCode,
					NW_OP_USING_A52.getInfo());
		}
		// xxxx 010x -> A53 algorithm supported.
		if ((ENC_ALGO_MASK & tmp) == (byte) 0x04) {
			return new PacketAttribute(NW_OP_USING_A53, hexCode,
					NW_OP_USING_A53.getInfo());
		}
		// Ideally control should never reach here
		return null;
	}

	private PacketAttribute getIMEIRequestedStatus(byte[] packetBytes) {
		byte[] imeiBytes = extract(packetBytes, 5, 6);
		String hexCode = formatHexBytes(imeiBytes);
		byte tmp = imeiBytes[0];
		// 1 bit from left is set to one then NW operator requests IMEI
		if ((IMEI_MASK & tmp) == (byte) 0x00) {
			return new PacketAttribute(NW_OP_NT_REQ_IMEI, hexCode,
					NW_OP_NT_REQ_IMEI.getInfo());
		}
		return new PacketAttribute(NW_OP_REQ_IMEI, hexCode,
				NW_OP_REQ_IMEI.getInfo());
	}
}