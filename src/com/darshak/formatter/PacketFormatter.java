package com.darshak.formatter;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.darshak.modal.Packet;
import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public abstract class PacketFormatter {

	SimpleDateFormat sInputDateFormat;
	SimpleDateFormat sOutputDateFormat;

	public PacketFormatter() {
		sInputDateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.ROOT);
		sInputDateFormat.setTimeZone(TimeZone.getDefault());

		sOutputDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss",
				Locale.ROOT);
		sOutputDateFormat.setTimeZone(TimeZone.getDefault());
	}

	public abstract Packet formatPacket(byte[] packetBytes);

	protected byte[] extract(byte[] packetBytes, int startByteIndx,
			int endByteIndx) {
		byte[] extractedCodes = new byte[endByteIndx - startByteIndx];
		for (int i = startByteIndx; i < endByteIndx; i++) {
			extractedCodes[i - startByteIndx] = packetBytes[i];
		}
		return extractedCodes;
	}

	protected String formatPhoneNumber(byte[] extractedBytes, int phoneNumLen) {
		StringBuilder phoneNumber = new StringBuilder();
		for (int i = 0; i < extractedBytes.length; i++) {
			phoneNumber.append(String.format("%02X ",
					Utils.swipeNibble(extractedBytes[i])));
		}
		return "+"
				+ phoneNumber.toString().replace(" ", "")
						.substring(0, phoneNumLen);
	}

	protected String formatTimestamp(byte[] extractedBytes) {
		StringBuilder timestamp = new StringBuilder();
		// Last byte denotes milliseconds. ignore it.
		for (int i = 0; i < extractedBytes.length - 1; i++) {
			timestamp.append(String.format("%02X",
					Utils.swipeNibble(extractedBytes[i])));
		}
		try {
			Date inputDate = sInputDateFormat.parse(timestamp.toString());
			return sOutputDateFormat.format(inputDate);
		} catch (ParseException e) {
			return timestamp.toString();
		}
	}
	
	public String formatNumber(byte[] extractedBytes) {
		BigInteger bigInt = new BigInteger(1, extractedBytes);
		return bigInt.toString(10);
	}
}