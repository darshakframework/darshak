package com.darshak.constants;

import java.io.File;

/**
 * @author Andreas Schildbach
 * @author Swapnil Udar & Ravishankar Borgaonkar
 */
public interface Constants {

	File LOG_DIR_FILE = new File("/data/log/err/");

	String MODEL_S2 = "GT-I9100";
	String MODEL_S3 = "GT-I9300";

	String LOG_FILE_S3_PREFIX = "CPLOG_ISTP_TRACE";
	String LOG_FILE_S2_PREFIX = "MA_TRACE_";
	String LOG_FILE_S2_PREFIX_AENAES = "AENEAS_TRACE_";
	String LOG_FILE_XGS_PREFIX = "xgs.";

	String PREFS_KEY_OWN_NUMBER = "own_number";

	int GSM = 1;
	int _3G = 2;
	int SMS = 3;
	int PROFILE_PARAMS = 4;

	String RAW_FILE_PREFIX = "darshak_raw";

	String EVENT = "event";

	String LOG_ENTRY_TO_BE_DISPLAYED = "logEntryToBeDisplayed";
	
	String FILTER_SELECTION_QUERY = "filterSelectionQuery";

	int DARSHAK_SERV_EXE_INTERVAL_SEC = 15;

	long UNKNOWN_UID = -1;
	
	int NUM_OF_TERMINATING_BYTES = 200;
	
	boolean prodMode = false;
}