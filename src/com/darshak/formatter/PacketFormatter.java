package com.darshak.formatter;

import java.util.ArrayList;
import java.util.List;

import com.darshak.modal.PacketAttribute;
import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class PacketFormatter {

	public List<PacketAttribute> format(byte[] extractedBytes, int kind,
			String infoText) {
		List<PacketAttribute> indCodeEntryList = new ArrayList<PacketAttribute>();
		String hexCode = Utils.formatHexBytes(extractedBytes);
		String displayText = infoText.concat(": ").concat(
				formatBytes(extractedBytes));
		PacketAttribute indCodeEntry = new PacketAttribute(kind, hexCode,
				displayText);
		indCodeEntryList.add(indCodeEntry);
		return indCodeEntryList;
	}

	protected String formatBytes(byte[] extractedBytes) {
		return "";
	}
}