package com.darshak.db;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public final class DatabaseSchema {
	public static final String DB_NAME = "DarshakDB";
	public static final int DB_VERSION = 1;

	public static final class LogEntrySchema {
		public static final String TABLE_NAME = "LOG";
		public static final String UID = "UID";
		public static final String TIME = "TIME";
		public static final String NW_TYPE = "NW_TYPE";
		public static final String EVENT = "EVENT";
		public static final String NW_OPERATOR = "NW_OPERATOR";
		public static final String IS_DELETED = "IS_DELETED";

		public static final String[] COLUMNS = { UID, TIME, NW_TYPE, EVENT,
				NW_OPERATOR, IS_DELETED };
		
		public static String CREATE_TABLE_LOG = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME 
				+ " ( " 
				+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ TIME + " INTEGER , "
				+ NW_TYPE + " INTEGER, " 
				+ EVENT + " INTEGER, " 
				+ NW_OPERATOR + " TEXT, "				
				+ IS_DELETED + " INTEGER " 
				+ " ) ";
	}

	public static final class PacketSchema {
		public static final String TABLE_NAME = "PACKET";
		public static final String UID = "UID";
		public static final String LOG_UID = "LOG_UID";
		public static final String TYPE = "TYPE";
		public static final String HEX_CODE = "HEX_CODE";		
		public static final String IS_DUPLICATE = "IS_DUPLICATE";
		public static final String TIME = "TIME";
		
		public static final String[] COLUMNS = { UID, LOG_UID, TYPE, HEX_CODE, IS_DUPLICATE, TIME};
		
		public static String CREATE_TABLE_PACKET = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ( "
				+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ LOG_UID + " INTEGER, "
				+ TIME + " INTEGER, "
				+ TYPE + " INTEGER, "
				+ HEX_CODE + " TEXT, "
				+ IS_DUPLICATE + " INTEGER, "
				+ "FOREIGN KEY(" + LOG_UID 	+ ") REFERENCES " + LogEntrySchema.TABLE_NAME + "(" + LogEntrySchema.UID + ")" 
				+ ")";
	}

	public static final class PacketAttributeSchema {
		public static final String TABLE_NAME = "PACKET_ATTR";
		public static final String UID = "UID";
		public static final String PACKET_UID = "PACKET_UID";
		public static final String TYPE = "TYPE";
		public static final String HEX_CODE = "HEX_CODE";
		public static final String DISPLAY_TXT = "DISPLAY_TXT";
		public static final String TIME = "TIME";

		public static final String[] COLUMNS = { UID, PACKET_UID, TYPE,
				HEX_CODE, DISPLAY_TXT, TIME };
		
		public static final String CREATE_TABLE_PACKET_ATTR = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME 
				+ " ( " 
				+ UID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ PACKET_UID + " INTEGER, "
				+ TIME + " INTEGER, "
				+ TYPE + " INTEGER, "
				+ HEX_CODE + " TEXT, "
				+ DISPLAY_TXT + " TEXT, " 
				+ "FOREIGN KEY(" + PACKET_UID + ") REFERENCES "	+ PacketSchema.TABLE_NAME + "("+ PacketSchema.UID	+ ")"
				+ " ) ";
	}
	
	public static final class CellularEvent {
		public static final String TABLE_NAME = "CELLULAR_EVENT";
		public static final String EVENT_UID = "UID";
		public static final String EVENT_CODE = "EVENT_CODE";
		public static final String EVENT_TIME = "EVENT_TIME";
		public static final String EVENT_NW_TYPE = "EVENT_NW_TYPE";
		public static final String EVENT_NW_OP = "EVENT_NW_OP";
		public static final String EVENT_CONSUMED = "EVENT_CONSUMED";

		public static final String[] COLUMNS = {EVENT_UID, EVENT_CODE, EVENT_TIME, EVENT_NW_TYPE, EVENT_NW_OP, EVENT_CONSUMED};

		public static final String CREATE_TABLE_CELLULAR_EVENT = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ( "
				+ EVENT_UID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
				+ EVENT_CODE + " INTEGER, "
				+ EVENT_TIME + " INTEGER, "
				+ EVENT_NW_TYPE + " INTEGER, "
				+ EVENT_NW_OP + " TEXT, "
				+ EVENT_CONSUMED + " INTEGER "
				+ " ) ";
	}
	
	public static final class ProfileParams {
		public static final String TABLE_NAME = "PROFILE_PARAMS";
		public static final String TYPE = "TYPE";
		public static final String HEX_CODE = "HEX_CODE";
		public static final String DISPLAY_TXT = "DISPLAY_TXT";

		public static final String[] COLUMNS = { TYPE, HEX_CODE, DISPLAY_TXT };

		public static final String CREATE_TABLE_PROFILE_PARAMS = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ( "
				+ TYPE + " INTEGER, "
				+ HEX_CODE + " TEXT, " 
				+ DISPLAY_TXT + " TEXT, " 
				+ "PRIMARY KEY (" + TYPE + ", " + HEX_CODE + ")"
				+ " ) ";
	}
	
	public static final class SentinelPacketScehama {
		public static final String TABLE_NAME = "SENTINEL_PACKET";
		public static final String SCAN_TYPE = "SCAN_TYPE";
		public static final String BYTE_SEQ = "BYTE_SEQ";

		public static final String[] COLUMNS = { SCAN_TYPE, BYTE_SEQ };

		public static final String CREATE_TABLE_SENTINEL_PACKET = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ( "
				+ SCAN_TYPE + " INTEGER PRIMARY KEY UNIQUE, "
				+ BYTE_SEQ + " BLOB " 
				+ " ) ";
	}
}