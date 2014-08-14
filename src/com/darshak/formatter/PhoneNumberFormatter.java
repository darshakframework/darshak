package com.darshak.formatter;

import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class PhoneNumberFormatter extends PacketFormatter {

	@Override
	public String formatBytes(byte[] extractedBytes) {
		StringBuilder phoneNumber = new StringBuilder();
		for (int i = 2; i < extractedBytes.length; i++) {
			phoneNumber.append(String.format("%02X ",
					Utils.swipeNibble(extractedBytes[i])));
		}
		return phoneNumber.toString();
	}
}