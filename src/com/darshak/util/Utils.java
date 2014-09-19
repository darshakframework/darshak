package com.darshak.util;

/**
 * @author Andreas Schildbach
 * @author Swapnil Udar & Ravishankar Borgaonkar
 */
import static com.darshak.constants.Constants.LOG_DIR_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.darshak.constants.Constants;
import com.darshak.constants.NetworkType;
import com.darshak.modal.LogEntry;

import eu.chainfire.libsuperuser.Shell;

public class Utils {

	private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat(
			"dd-MMM-yy HH:mm", Locale.getDefault());;

	private Utils() {
		// Private constructor for utils class
	}

	private static final String LOG_TAG = Utils.class.getSimpleName();

	private static final byte ZERO = (byte) 0x0F;

	public static NetworkType networkToConnectionType(final int networkType) {
		if (networkType == 0)
			return null;
		else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS
				|| networkType == TelephonyManager.NETWORK_TYPE_HSDPA
				|| networkType == TelephonyManager.NETWORK_TYPE_HSPA
				|| networkType == TelephonyManager.NETWORK_TYPE_HSPAP
				|| networkType == TelephonyManager.NETWORK_TYPE_HSUPA)
			return NetworkType._3G;
		else if (networkType == TelephonyManager.NETWORK_TYPE_GPRS
				|| networkType == TelephonyManager.NETWORK_TYPE_EDGE
				|| networkType == TelephonyManager.NETWORK_TYPE_CDMA)
			return NetworkType.GSM;
		else
			throw new IllegalStateException(Integer.toString(networkType));
	}

	public static String formatDate(LogEntry logEntry) {
		Date inputDate = new Date(logEntry.getTime());
		return OUTPUT_DATE_FORMAT.format(inputDate).intern();
	}

	public static String getNetworkType(LogEntry logEntry) {
		NetworkType nwType = NetworkType.getMatchingNetworkType(logEntry
				.getNwType());
		return nwType.getNwTypeDesc();
	}

	public static String formatHexBytes(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte byteData : bytes) {
			result.append(String.format("%02X ", byteData));
		}
		return result.toString().intern();
	}

	public static void deleteLogFile(final File logFileToBeDeleted) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Shell.SU.run("rm " + logFileToBeDeleted.getAbsolutePath());
				Log.d(LOG_TAG,
						"Deleted log file "
								+ logFileToBeDeleted.getAbsolutePath());
			}
		}).start();
	}

	public static File[] searchLogFile() {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (Build.MODEL.equals(Constants.MODEL_S3)) {
					if (filename.startsWith(Constants.LOG_FILE_S3_PREFIX)) {
						Log.d(LOG_TAG, "match: " + Constants.LOG_FILE_S3_PREFIX
								+ ": " + filename);
						return true;
					}
				}
				if (Build.MODEL.equals(Constants.MODEL_S2)) {
					if (filename.startsWith(Constants.LOG_FILE_S2_PREFIX)) {
						Log.d(LOG_TAG, "match: " + Constants.LOG_FILE_S2_PREFIX
								+ ": " + filename);
						return true;
					}
				}
				return false;
			}
		};
		File[] files = Constants.LOG_DIR_FILE.listFiles(filter);

		if (files.length == 0) {
			Log.d(LOG_TAG, "Log file not found.");
			return null;
		}
		return files;
	}

	public static byte swipeNibble(byte byt) {
		byte tmp1 = (byte) (byt >> 4);
		tmp1 = (byte) (tmp1 & ZERO);

		byte tmp2 = (byte) (byt & ZERO);
		tmp2 = (byte) (tmp2 << 4);

		byte result = (byte) (tmp2 ^ tmp1);
		return result;
	}

	public static boolean isRootNeededForRead() {
		return LOG_DIR_FILE.canRead();
	}

	public static boolean isRootNeededForDelete() {
		return LOG_DIR_FILE.canWrite();
	}

	public static boolean isGalaxyS3() {
		return Build.MODEL.equals(Constants.MODEL_S3);
	}

	public static boolean isGalaxyS2() {
		return Build.MODEL.equals(Constants.MODEL_S2);
	}

	public static boolean isSupportedModel() {
		if (Build.MODEL.equals(Constants.MODEL_S2)) {
			return true;
		}
		if (Build.MODEL.equals(Constants.MODEL_S3)) {
			return true;
		}
		Log.i(LOG_TAG, "Model: " + Build.MODEL + " not supported");
		return false;
	}

	public static boolean isSupportedVersion() {
		if (Build.VERSION.RELEASE.equals("4.1.2")) {
			return true;
		}
		Log.i(LOG_TAG, "Version: " + Build.VERSION.RELEASE + " not supported");
		return false;
	}

	public static void deleteLogFilesWithPrefix(final String[] filePrefix) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				for (String prefix : filePrefix) {

					Shell.SU.run("rm " + LOG_DIR_FILE.getAbsolutePath() + "/"
							+ prefix + "*");

				}
			}
		}).start();
	}

	public static void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ie) {
			// ignore
		}
	}

	private static final Random randomGenerator = new Random();

	public static void mvLogFile(final File file) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String destDir = "/data/data/com.darshak/databases/";				
				String destFile = destDir + file.getName();
				Shell.SU.run("mv " + file.getAbsolutePath() + " " + destFile);
				Log.d(LOG_TAG, "Deleted log file " + file.getAbsolutePath());
			}
		}).start();
	}
}
