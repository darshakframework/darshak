package com.darshak.util;

/**
 * @author Andreas Schildbach
 * @author Swapnil Udar & Ravishankar Borgaonkar
 */
import static com.darshak.constants.Constants.LOG_DIR_FILE;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Build;
import android.util.Log;

import com.darshak.constants.Constants;

import eu.chainfire.libsuperuser.Shell;

public class Utils {

	private Utils() {
		// private constructor for utility class
	}

	private static final String LOG_TAG = Utils.class.getSimpleName();

	private static final byte ZERO = (byte) 0x0F;

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
}
