package com.darshak.formatter;

import static com.darshak.constants.PacketAttributeType.CELL_IDENTITY;
import static com.darshak.constants.PacketAttributeType.CELL_OPTIONS;
import static com.darshak.constants.PacketAttributeType.CELL_SELECTION_PARAMS;
import static com.darshak.constants.PacketAttributeType.LOC_AREA_CODE;
import static com.darshak.constants.PacketAttributeType.MOB_CNTRY_CODE;
import static com.darshak.constants.PacketAttributeType.MOB_NW_CODE;
import static com.darshak.constants.PacketAttributeType.MSCR;
import static com.darshak.constants.PacketType.SYS_INFO_3;
import static com.darshak.util.Utils.formatHexBytes;
import static com.darshak.util.Utils.swipeNibble;
import static java.lang.String.format;

import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;
import com.darshak.util.Utils;

public class GSMSysInfoTypeThreeFormatter extends PacketFormatter {

	@Override
	public Packet formatPacket(byte[] packetBytes) {
		String hexCode = formatHexBytes(packetBytes);
		Packet packet = new Packet(SYS_INFO_3, hexCode);
		packet.addPacketAttribute(getCellIdentity(packetBytes));
		packet.addPacketAttribute(getMobCountryCode(packetBytes));
		packet.addPacketAttribute(getMobNwCode(packetBytes));
		packet.addPacketAttribute(getLocationAreaCode(packetBytes));
		packet.addPacketAttribute(getMSCR(packetBytes));
		packet.addPacketAttribute(getPWRCValue(packetBytes));
		packet.addPacketAttribute(getRadioLinkTimeout(packetBytes));
		packet.addPacketAttribute(getCellSelectionParameter(packetBytes));
		return packet;
	}

	private PacketAttribute getCellIdentity(byte[] packetBytes) {
		byte[] cellIdBytes = extract(packetBytes, 3, 5);
		String hexCode = formatHexBytes(cellIdBytes);
		String displayText = CELL_IDENTITY.getInfo() + " : "
				+ formatNumber(cellIdBytes);
		return new PacketAttribute(CELL_IDENTITY, hexCode, displayText);
	}

	private PacketAttribute getMobCountryCode(byte[] packetBytes) {
		byte[] mobCntryBytes = extract(packetBytes, 5, 7);
		String hexCode = formatHexBytes(mobCntryBytes);
		StringBuilder mobCountryCode = new StringBuilder();
		for (int i = 0; i < mobCntryBytes.length; i++) {
			mobCountryCode
					.append(format("%02X ", swipeNibble(mobCntryBytes[i])));
		}
		String result = mobCountryCode.toString();
		result = result.replace(" ", "");
		mobCountryCode.setLength(0);
		// Ignore last character
		String displayText = MOB_CNTRY_CODE.getInfo() + " : "
				+ result.substring(0, result.length() - 1);
		return new PacketAttribute(MOB_CNTRY_CODE, hexCode, displayText);
	}

	private PacketAttribute getMobNwCode(byte[] packetBytes) {
		byte[] mobNetCodeBytes = extract(packetBytes, 7, 8);
		String hexCode = formatHexBytes(mobNetCodeBytes);
		StringBuilder mobileNetworkCode = new StringBuilder();
		for (int i = 0; i < mobNetCodeBytes.length; i++) {
			mobileNetworkCode.append(format("%02X ",
					swipeNibble(mobNetCodeBytes[i])));
		}
		String displayText = MOB_NW_CODE.getInfo() + " : "
				+ mobileNetworkCode.toString();
		mobileNetworkCode.setLength(0);
		return new PacketAttribute(MOB_NW_CODE, hexCode, displayText);
	}

	private PacketAttribute getLocationAreaCode(byte[] packetBytes) {
		byte[] locAreaCodeBytes = extract(packetBytes, 8, 10);
		String hexCode = formatHexBytes(locAreaCodeBytes);
		String displayText = LOC_AREA_CODE.getInfo() + " : "
				+ formatNumber(locAreaCodeBytes);
		return new PacketAttribute(LOC_AREA_CODE, hexCode, displayText);
	}

	private PacketAttribute getMSCR(byte[] packetBytes) {
		byte[] mscrBytes = extract(packetBytes, 10, 11);
		String hexCode = formatHexBytes(mscrBytes);
		byte byt = mscrBytes[0];
		String displayText = null;
		if (((byt & (byte) 0x80)) == (byte) 0x80) {
			displayText = "MSC is Release '99 onwards";
		} else {
			displayText = "MSC is Release '98 onwards";
		}
		return new PacketAttribute(MSCR, hexCode, displayText);
	}

	private PacketAttribute getPWRCValue(byte[] packetBytes) {
		byte[] pwrcBytes = extract(packetBytes, 13, 14);
		String hexCode = formatHexBytes(pwrcBytes);
		String displayText;
		byte tmp = pwrcBytes[0];
		if (((tmp & (byte) 0x40)) == (byte) 0x40) {
			displayText = "PWRC : True";
		} else {
			displayText = "PWRC : False";
		}
		return new PacketAttribute(CELL_OPTIONS, hexCode, displayText);
	}

	private PacketAttribute getRadioLinkTimeout(byte[] packetBytes) {
		byte[] pwrcBytes = extract(packetBytes, 13, 14);
		String hexCode = formatHexBytes(pwrcBytes);
		String displayText;
		byte tmp = pwrcBytes[0];
		tmp = (byte) (tmp & 0x0F);
		displayText = "Radio Link Timeout : " + (int) tmp;
		return new PacketAttribute(CELL_OPTIONS, hexCode, displayText);
	}

	private PacketAttribute getCellSelectionParameter(byte[] packetBytes) {
		byte[] cellSelParam = extract(packetBytes, 15, 16);
		String hexCode = formatHexBytes(cellSelParam);
		byte tmp = cellSelParam[0];
		// On 6 LS Bits are required.
		tmp = (byte) (0x3F & tmp);
		String displayText = "RXLEV-ACCESS-MIN: "
				+ Utils.formatHexBytes(new byte[] { tmp });
		return new PacketAttribute(CELL_SELECTION_PARAMS, hexCode, displayText);
	}
}