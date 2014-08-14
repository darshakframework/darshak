package com.darshak.formatter;

import java.math.BigInteger;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class NumberFormatter extends PacketFormatter {

	@Override
	public String formatBytes(byte[] extractedBytes) {
		BigInteger bigInt = new BigInteger(extractedBytes);
		return bigInt.toString(10);
	}
}
