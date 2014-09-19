package com.darshak.packetreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class LogFileReader {
	
	private static final String LOG_TAG = LogFileReader.class.getSimpleName();
	
	public byte[] readFile(File logFile) {
		Log.d(LOG_TAG,
				"Log file to read for identifying security parameters : "
						+ logFile.getAbsolutePath());
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(logFile, "r");
			byte[] fileByteArray = new byte[(int) file.length()];
			file.read(fileByteArray);
			return fileByteArray;
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "Log file not found  : " + logFile.getAbsolutePath()
					+ " " + e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG,
					"IO Error while opening log file : "
							+ logFile.getAbsolutePath() + " " + e.getMessage());
		} finally {
			if (file != null) {
				try {
					file.close();
					file = null;
				} catch (IOException e) {
					Log.e(LOG_TAG,
							"Errow while closing file : " + e.getMessage());
				}
			}			
		}
		return new byte[0];		
	}
}