package com.darshak.formatter;

import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class HexFormatter extends PacketFormatter {

	@Override
	public String formatBytes(byte[] extractedBytes) {
		return Utils.formatHexBytes(extractedBytes);
	}
}