package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.SS_SENDER;
import static com.darshak.constants.PacketAttributeType.SS_TSMP;
import static com.darshak.constants.PacketType.SILENT_SMS;
import static com.darshak.util.Utils.formatHexBytes;

import java.util.ArrayList;
import java.util.List;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class SilentSMSFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {

		Packet packet = null;
		List<PacketAttribute> packetAttributes = new ArrayList<PacketAttribute>();

		// byte at index 1, denotes the length of senders number
		if (packetBytes[1] == (byte) 0x0d) {
			String hexCode = formatHexBytes(packetBytes);
			packet = new Packet(SILENT_SMS, hexCode);
			// Byte sequence from 1 to 10 = Senders Number
			packetAttributes.add(getSenders(packetBytes, 3, 10, packetBytes[1]));
			// Byte sequence from 12 to 19 = Received time
			packetAttributes.add(getReceivedTime(packetBytes, 12, 19));
			packet.addPacketAttributes(packetAttributes);
		} else if (packetBytes[1] == (byte) 0x0c) {
			String hexCode = formatHexBytes(extract(packetBytes, 0,
					packetBytes.length - 1));
			packet = new Packet(SILENT_SMS, hexCode);

			// Byte sequence from 1 to 9 = Senders Number
			packetAttributes.add(getSenders(packetBytes, 3, 9, packetBytes[1]));
			// Byte sequence from 11 to 18 = Received time
			packetAttributes.add(getReceivedTime(packetBytes, 11, 18));
			packet.addPacketAttributes(packetAttributes);
		} else if (packetBytes[1] == (byte) 0x0b) {
			String hexCode = formatHexBytes(extract(packetBytes, 0,
					packetBytes.length - 1));
			packet = new Packet(SILENT_SMS, hexCode);

			// Byte sequence from 1 to 9 = Senders Number
			packetAttributes.add(getSenders(packetBytes, 3, 9, packetBytes[1]));
			// Byte sequence from 11 to 19 = Received time
			packetAttributes.add(getReceivedTime(packetBytes, 11, 19));
			packet.addPacketAttributes(packetAttributes);
		} else if (packetBytes[1] == (byte) 0x0a) {
			String hexCode = formatHexBytes(packetBytes);
			packet = new Packet(SILENT_SMS, hexCode);

			// Byte sequence from 1 to 9 = Senders Number
			packetAttributes.add(getSenders(packetBytes, 3, 8, packetBytes[1]));
			// Byte sequence from 11 to 19 = Received time
			packetAttributes.add(getReceivedTime(packetBytes, 10, 18));
			packet.addPacketAttributes(packetAttributes);
		}
		return packet;

	}

	protected PacketAttribute getSenders(byte[] packetBytes, int startByteIndx,
			int endByteIndx, int phoneNumLength) {
		byte[] sendersByte = extract(packetBytes, startByteIndx, endByteIndx);
		String hexCode = formatHexBytes(sendersByte);
		String displayText = SS_SENDER.getInfo() + " : "
				+ formatPhoneNumber(sendersByte, phoneNumLength);
		return new PacketAttribute(SS_SENDER, hexCode, displayText);
	}

	protected PacketAttribute getReceivedTime(byte[] packetBytes,
			int startByteIndx, int endByteIndx) {
		byte[] sendersByte = extract(packetBytes, startByteIndx, endByteIndx);
		String hexCode = formatHexBytes(sendersByte);
		String displayText = SS_TSMP.getInfo() + " : "
				+ formatTimestamp(sendersByte);
		return new PacketAttribute(SS_TSMP, hexCode, displayText);
	}
}