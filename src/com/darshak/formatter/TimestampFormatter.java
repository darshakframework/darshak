package com.darshak.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class TimestampFormatter extends PacketFormatter {

	SimpleDateFormat sInputDateFormat;
	SimpleDateFormat sOutputDateFormat;

	public TimestampFormatter() {
		sInputDateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.ROOT);
		sInputDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		sOutputDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss",
				Locale.ROOT);
		sOutputDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
	}

	@Override
	public String formatBytes(byte[] extractedBytes) {
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
}