package com.darshak.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.constants.NetworkType;
import com.darshak.constants.PacketAttributeType;
import com.darshak.constants.PacketType;
import com.darshak.modal.EventDetails;
import com.darshak.modal.LogEntry;
import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;
import com.darshak.modal.SentinelPacket;
import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class DarshakDBHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = DarshakDBHelper.class.getSimpleName();

	public DarshakDBHelper(Context context) {
		super(context, DatabaseSchema.DB_NAME, null, DatabaseSchema.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DatabaseSchema.LogEntrySchema.CREATE_TABLE_LOG);
		db.execSQL(DatabaseSchema.PacketSchema.CREATE_TABLE_PACKET);
		db.execSQL(DatabaseSchema.PacketAttributeSchema.CREATE_TABLE_PACKET_ATTR);
		db.execSQL(DatabaseSchema.CellularEvent.CREATE_TABLE_CELLULAR_EVENT);
		db.execSQL(DatabaseSchema.ProfileParams.CREATE_TABLE_PROFILE_PARAMS);
		db.execSQL(DatabaseSchema.SentinelPacketScehama.CREATE_TABLE_SENTINEL_PACKET);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public long insertLogEntry(NetworkType nwType, String nwOperator,
			Event event, long eventReportedAt) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.LogEntrySchema.NW_TYPE,
				nwType.getNwTypeCode());
		dbValues.put(DatabaseSchema.LogEntrySchema.EVENT, event.getEventCode());
		dbValues.put(DatabaseSchema.LogEntrySchema.NW_OPERATOR, nwOperator);
		dbValues.put(DatabaseSchema.LogEntrySchema.IS_DELETED, 0);
		dbValues.put(DatabaseSchema.LogEntrySchema.TIME, eventReportedAt);

		long rowId = db.insert(DatabaseSchema.LogEntrySchema.TABLE_NAME, null,
				dbValues);

		Log.e(LOG_TAG, "Inserted log entry to database." + nwType + ", "
				+ event + " , " + nwOperator);

		return rowId;
	}

	public long insertPacket(long logEntryUid, long eventReportedAt,
			int packetType, String hexCode, boolean isDuplicate) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.PacketSchema.LOG_UID, logEntryUid);
		dbValues.put(DatabaseSchema.PacketSchema.TYPE, packetType);
		dbValues.put(DatabaseSchema.PacketSchema.HEX_CODE, hexCode);
		dbValues.put(DatabaseSchema.PacketSchema.IS_DUPLICATE, isDuplicate ? 1
				: 0);
		dbValues.put(DatabaseSchema.PacketSchema.TIME, eventReportedAt);

		long rowId = db.insert(DatabaseSchema.PacketSchema.TABLE_NAME, null,
				dbValues);

		Log.e(LOG_TAG, "Inserted Packet entry to database." + logEntryUid
				+ ", " + packetType + ", " + hexCode);

		return rowId;
	}

	public boolean isPacketAlreadyInserted(int typeId, String hexCode) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor result = null;
		try {
			String[] projection = { DatabaseSchema.PacketSchema.UID };

			String selection = DatabaseSchema.PacketSchema.TYPE + " = "
					+ String.valueOf(typeId) + " AND "
					+ DatabaseSchema.PacketSchema.HEX_CODE + " = '" + hexCode
					+ "'";
			selection = selection.intern();

			result = db.query(DatabaseSchema.PacketSchema.TABLE_NAME,
					projection, selection, null, null, null, null);

			if (result == null || result.getCount() == 0) {
				return false;
			}
			return true;
		} finally {
			if (result != null)
				result.close();
		}
	}

	public long insertPacketAttribute(long packetUid, long eventReportedAt,
			int packetAttributeId, String hexCode, String displayText) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.PacketAttributeSchema.PACKET_UID, packetUid);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.TYPE,
				packetAttributeId);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.HEX_CODE, hexCode);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.DISPLAY_TXT,
				displayText);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.TIME, eventReportedAt);

		long rowId = db.insert(DatabaseSchema.PacketAttributeSchema.TABLE_NAME,
				null, dbValues);

		Log.e(LOG_TAG, "Inserted packet attribute to database." + packetUid
				+ ", " + packetAttributeId + ", " + hexCode + ", "
				+ displayText);
		return rowId;
	}

	public long insertEventDetails(EventDetails eventDetails) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_CODE, eventDetails
				.getEvent().getEventCode());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_TIME,
				eventDetails.getReportedAt());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_NW_TYPE, eventDetails
				.getNwType().getNwTypeCode());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_NW_OP,
				eventDetails.getNwOperator());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_CONSUMED, 0);

		long rowId = db.insert(DatabaseSchema.CellularEvent.TABLE_NAME, null,
				dbValues);
		Log.e(LOG_TAG, "Added event details into database." + eventDetails);
		return rowId;

	}

	public long insertProfileParams(PacketAttribute packetAttribute) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.ProfileParams.TYPE,
				packetAttribute.getPacketAttrTypeId());
		dbValues.put(DatabaseSchema.ProfileParams.HEX_CODE,
				packetAttribute.getHexCode());
		dbValues.put(DatabaseSchema.ProfileParams.DISPLAY_TXT,
				packetAttribute.getDisplayText());

		long rowId = db.insert(DatabaseSchema.ProfileParams.TABLE_NAME, null,
				dbValues);

		Log.e(LOG_TAG, "Added Profile param into database." + packetAttribute);
		return rowId;
	}

	public long insertSentinelPacket(int scanType, byte[] byteSeq) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.SentinelPacketScehama.SCAN_TYPE, scanType);
		dbValues.put(DatabaseSchema.SentinelPacketScehama.BYTE_SEQ, byteSeq);

		long rowId = db.insert(DatabaseSchema.SentinelPacketScehama.TABLE_NAME,
				null, dbValues);

		Log.e(LOG_TAG, "Added Sentinel packet." + Utils.formatHexBytes(byteSeq));
		return rowId;
	}

	public List<LogEntry> getLogEntries(String uidCondition, String orderBy,
			int numOfRecords, String filterQuery) {
		SQLiteDatabase db = null;
		Cursor result = null;

		String whereClause = DatabaseSchema.LogEntrySchema.IS_DELETED + " = "
				+ String.valueOf(0) + " AND " + uidCondition;
		whereClause = whereClause.intern();

		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		try {
			db = getReadableDatabase();
			result = db.query(DatabaseSchema.LogEntrySchema.TABLE_NAME,
					DatabaseSchema.LogEntrySchema.COLUMNS, whereClause, null,
					null, null, orderBy);

			if (result == null || result.getCount() == 0) {
				return logEntries;
			}
			result.moveToFirst();
			Log.d(LOG_TAG,
					"Number of Log entries found in DB : " + result.getCount());

			int index = 0;
			while (!result.isAfterLast() && index < numOfRecords) {
				LogEntry logEntry = getLogEntry(result);
				List<Packet> packets = getPackets(logEntry.getUid(),
						filterQuery);
				if (packets != null && packets.size() > 0) {
					logEntry.addPackets(packets);
					logEntries.add(logEntry);
					index = index + 1;
				}
				result.moveToNext();
			}
			return logEntries;
		} finally {
			if (result != null)
				result.close();
		}
	}

	public LogEntry getLogEntry(long uid, String filterSelectionQuery) {
		SQLiteDatabase db = null;
		Cursor result = null;

		String orderBy = DatabaseSchema.LogEntrySchema.TIME + " ASC ";
		orderBy = orderBy.intern();

		String whereClause = DatabaseSchema.LogEntrySchema.IS_DELETED + " = "
				+ String.valueOf(0) + " AND "
				+ DatabaseSchema.LogEntrySchema.UID + " = "
				+ String.valueOf(uid);
		whereClause = whereClause.intern();

		try {
			db = getReadableDatabase();

			result = db.query(DatabaseSchema.LogEntrySchema.TABLE_NAME,
					DatabaseSchema.LogEntrySchema.COLUMNS, whereClause, null,
					null, null, orderBy);

			if (result == null || result.getCount() == 0) {
				return null;
			}
			result.moveToFirst();
			Log.d(LOG_TAG,
					"Number of Log entries found in DB : " + result.getCount());

			LogEntry logEntry = getLogEntry(result);
			List<Packet> packets = getPackets(logEntry.getUid(),
					filterSelectionQuery);
			if (packets != null && packets.size() > 0) {
				logEntry.addPackets(packets);
			}
			return logEntry;
		} finally {
			if (result != null)
				result.close();
		}
	}

	public List<Packet> getPackets(long logEntryUid) {
		String selection = DatabaseSchema.PacketSchema.LOG_UID + " = "
				+ String.valueOf(logEntryUid);
		selection = selection.intern();
		return getPackets(selection, null);
	}

	public List<Packet> getPackets(long logEntryUid, String filterSelectionQuery) {
		String selection = DatabaseSchema.PacketSchema.LOG_UID + " = "
				+ String.valueOf(logEntryUid) + " AND ( "
				+ filterSelectionQuery + " ) ";
		selection = selection.intern();
		return getPackets(selection, null);
	}

	public List<PacketAttribute> getPacketAttributes(long packetUid) {
		String whereClause = DatabaseSchema.PacketAttributeSchema.PACKET_UID
				+ " = " + String.valueOf(packetUid);
		whereClause = whereClause.intern();

		return getPacketAttributes(whereClause, null);
	}

	public Packet getPacket(PacketType type) {
		String orderBy = DatabaseSchema.PacketSchema.TIME + " DESC ";
		orderBy = orderBy.intern();

		String whereClause = DatabaseSchema.PacketSchema.TYPE + " = "
				+ String.valueOf(type.getPacketTypeId());
		whereClause = whereClause.intern();

		List<Packet> packets = getPackets(whereClause, orderBy);
		return packets.get(0);
	}

	public List<PacketAttribute> getPacketAttributes(long packetUid,
			PacketAttributeType type) {
		String orderBy = DatabaseSchema.PacketAttributeSchema.TIME + " DESC ";
		orderBy = orderBy.intern();

		String whereClause = DatabaseSchema.PacketAttributeSchema.TYPE + " = "
				+ String.valueOf(type.getTypeId()) + " AND "
				+ DatabaseSchema.PacketAttributeSchema.PACKET_UID + " = "
				+ String.valueOf(packetUid);
		whereClause = whereClause.intern();

		return getPacketAttributes(whereClause, orderBy);
	}

	public EventDetails getOldestUnconsumedEvent() {
		SQLiteDatabase db = null;
		Cursor result = null;

		String orderBy = DatabaseSchema.CellularEvent.EVENT_TIME + " ASC ";
		orderBy.intern();

		String whereClause = DatabaseSchema.CellularEvent.EVENT_CONSUMED
				+ " = 0 ";
		whereClause = whereClause.intern();

		try {
			db = getReadableDatabase();
			db.beginTransactionNonExclusive();

			result = db.query(DatabaseSchema.CellularEvent.TABLE_NAME,
					DatabaseSchema.CellularEvent.COLUMNS, whereClause, null,
					null, null, orderBy);

			if (result == null || result.getCount() == 0) {
				return null;
			}
			result.moveToFirst();

			long uid = result
					.getLong(result
							.getColumnIndexOrThrow(DatabaseSchema.CellularEvent.EVENT_UID));

			int eventCode = result
					.getInt(result
							.getColumnIndexOrThrow(DatabaseSchema.CellularEvent.EVENT_CODE));
			Event event = Event.getMatchingEvent(eventCode);

			long eventReportedAt = result
					.getLong(result
							.getColumnIndexOrThrow(DatabaseSchema.CellularEvent.EVENT_TIME));

			int nwTypeCode = result
					.getInt(result
							.getColumnIndexOrThrow(DatabaseSchema.CellularEvent.EVENT_NW_TYPE));
			NetworkType nwType = NetworkType.getMatchingNetworkType(nwTypeCode);

			String nwOperator = result
					.getString(result
							.getColumnIndexOrThrow(DatabaseSchema.CellularEvent.EVENT_NW_OP));

			EventDetails eventDetails = new EventDetails(uid, event,
					eventReportedAt, nwType, nwOperator);

			// Mark event consumed.
			consumeEventDetails(eventDetails);
			return eventDetails;
		} finally {
			if (result != null)
				result.close();
			if (db != null) {
				db.setTransactionSuccessful();
				db.endTransaction();
			}
		}
	}

	public SentinelPacket getSentinelPacket(int scanType) {
		SQLiteDatabase db = null;
		Cursor result = null;
		try {
			String whereClause = DatabaseSchema.SentinelPacketScehama.SCAN_TYPE
					+ " = " + String.valueOf(scanType);
			whereClause = whereClause.intern();

			db = getReadableDatabase();
			result = db.query(DatabaseSchema.SentinelPacketScehama.TABLE_NAME,
					DatabaseSchema.SentinelPacketScehama.COLUMNS, whereClause,
					null, null, null, null);

			if (result == null || result.getCount() == 0) {
				return null;
			}

			result.moveToFirst();
			byte[] byteSeq = result
					.getBlob(result
							.getColumnIndexOrThrow(DatabaseSchema.SentinelPacketScehama.BYTE_SEQ));
			Log.d(LOG_TAG,
					"Number of sentinel packets found are: "
							+ result.getCount());
			return new SentinelPacket(scanType, byteSeq);
		} finally {
			if (result != null)
				result.close();
		}
	}

	public int updateSentinelPacket(SentinelPacket sentinelPacket) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.SentinelPacketScehama.BYTE_SEQ,
				sentinelPacket.getByteSequence());
		String whereClause = DatabaseSchema.SentinelPacketScehama.SCAN_TYPE
				+ " = " + String.valueOf(sentinelPacket.getScanType());
		whereClause = whereClause.intern();

		return db.update(DatabaseSchema.SentinelPacketScehama.TABLE_NAME,
				dbValues, whereClause, null);
	}

	public void deleteAllLogEntries() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(DatabaseSchema.LogEntrySchema.TABLE_NAME, null, null);
		Log.d(LOG_TAG, "Deleted all Log entries from database.");
	}

	public void deleteLogEntry(long uid) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.LogEntrySchema.IS_DELETED, 1);
		String whereClause = DatabaseSchema.LogEntrySchema.UID + " = "
				+ String.valueOf(uid);
		whereClause = whereClause.intern();
		db.update(DatabaseSchema.LogEntrySchema.TABLE_NAME, dbValues,
				whereClause, null);

		Log.d(LOG_TAG, "Deleted Log entry from database : " + uid);
	}

	private void consumeEventDetails(EventDetails eventDetails) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_CONSUMED, 1);
		String whereClause = DatabaseSchema.CellularEvent.EVENT_UID + " = "
				+ String.valueOf(eventDetails.getUid());
		whereClause = whereClause.intern();

		whereClause = whereClause.intern();
		db.update(DatabaseSchema.CellularEvent.TABLE_NAME, dbValues,
				whereClause, null);

		Log.e(LOG_TAG, "Marked event as consumed : " + eventDetails);
	}

	public int numberOfUnconsumedGSMEvents() {
		SQLiteDatabase db = null;
		Cursor result = null;
		try {
			String whereClause = DatabaseSchema.CellularEvent.EVENT_CONSUMED
					+ " = 0 " + " AND "
					+ DatabaseSchema.CellularEvent.EVENT_CODE + " IN ( "
					+ Event.INCOMING_CALL.getEventCode() + ", "
					+ Event.OUTGOING_CALL.getEventCode() + ", "
					+ Event.INCOMING_SMS.getEventCode() + ", "
					+ Event.OUTGOING_SMS.getEventCode() + " ) " + " AND "
					+ DatabaseSchema.CellularEvent.EVENT_NW_TYPE + " = "
					+ Constants.GSM;

			whereClause = whereClause.intern();

			db = getReadableDatabase();

			result = db.query(DatabaseSchema.CellularEvent.TABLE_NAME,
					DatabaseSchema.CellularEvent.COLUMNS, whereClause, null,
					null, null, null);

			if (result == null) {
				return 0;
			}
			return result.getCount();
		} finally {
			if (result != null)
				result.close();
		}
	}

	public boolean isProfileParamPresent(PacketAttribute packetAttribute) {
		SQLiteDatabase db = null;
		Cursor result = null;
		try {
			String whereClause = DatabaseSchema.ProfileParams.TYPE + " = "
					+ String.valueOf(packetAttribute.getPacketAttrTypeId())
					+ " AND " + DatabaseSchema.ProfileParams.HEX_CODE + " = '"
					+ packetAttribute.getHexCode() + "'";

			whereClause = whereClause.intern();

			db = getReadableDatabase();

			result = db.query(DatabaseSchema.ProfileParams.TABLE_NAME,
					DatabaseSchema.ProfileParams.COLUMNS, whereClause, null,
					null, null, null);

			if (result == null || result.getCount() == 0) {
				return false;
			}
			return true;
		} finally {
			if (result != null)
				result.close();
		}
	}

	private List<Packet> getPackets(String whereClause, String orderBy) {
		SQLiteDatabase db = null;
		Cursor result = null;
		List<Packet> packets = new ArrayList<Packet>();
		try {
			db = getReadableDatabase();
			result = db.query(DatabaseSchema.PacketSchema.TABLE_NAME,
					DatabaseSchema.PacketSchema.COLUMNS, whereClause, null,
					null, null, orderBy);

			if (result == null || result.getCount() == 0) {
				return packets;
			}
			result.moveToFirst();

			Log.d(LOG_TAG, "Number of packets matching where clause:"
					+ whereClause + " are: " + result.getCount());
			while (!result.isAfterLast()) {
				Packet packet = getPacket(result);
				packet.addPacketAttributes(getPacketAttributes(packet.getUid()));
				packets.add(packet);
				result.moveToNext();
			}
			return packets;
		} finally {
			if (result != null)
				result.close();
		}
	}

	private List<PacketAttribute> getPacketAttributes(String whereClause,
			String orderBy) {
		SQLiteDatabase db = null;
		Cursor result = null;
		List<PacketAttribute> packetAttributes = new ArrayList<PacketAttribute>();
		try {
			db = getReadableDatabase();
			result = db.query(DatabaseSchema.PacketAttributeSchema.TABLE_NAME,
					DatabaseSchema.PacketAttributeSchema.COLUMNS, whereClause,
					null, null, null, orderBy);

			if (result == null || result.getCount() == 0) {
				return packetAttributes;
			}
			result.moveToFirst();

			Log.d(LOG_TAG, "Number of packet attributes matching where clause:"
					+ whereClause + " are: " + result.getCount());
			while (!result.isAfterLast()) {
				packetAttributes.add(getPacketAttribute(result));
				result.moveToNext();
			}
			return packetAttributes;
		} finally {
			if (result != null)
				result.close();			
		}
	}

	private LogEntry getLogEntry(Cursor result) {
		long uid = result.getLong(result
				.getColumnIndexOrThrow(DatabaseSchema.LogEntrySchema.UID));
		long time = result.getLong(result
				.getColumnIndexOrThrow(DatabaseSchema.LogEntrySchema.TIME));
		int nwType = result.getInt(result
				.getColumnIndexOrThrow(DatabaseSchema.LogEntrySchema.NW_TYPE));
		int event = result.getInt(result
				.getColumnIndexOrThrow(DatabaseSchema.LogEntrySchema.EVENT));
		String nwOperator = result
				.getString(result
						.getColumnIndexOrThrow(DatabaseSchema.LogEntrySchema.NW_OPERATOR));
		nwOperator = nwOperator.intern();
		boolean isDeleted = result
				.getInt(result
						.getColumnIndexOrThrow(DatabaseSchema.LogEntrySchema.IS_DELETED)) == 1 ? true
				: false;
		return new LogEntry(uid, time, nwType, event, nwOperator, isDeleted);
	}

	private Packet getPacket(Cursor result) {
		long uid = result.getLong(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketSchema.UID));
		long logEntryUid = result.getLong(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketSchema.LOG_UID));
		long time = result.getLong(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketSchema.TIME));
		int type = result.getInt(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketSchema.TYPE));
		String hexCode = result.getString(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketSchema.HEX_CODE));
		hexCode = hexCode.intern();

		return new Packet(uid, time, logEntryUid,
				PacketType.getPacketTypeById(type), hexCode);
	}

	private PacketAttribute getPacketAttribute(Cursor result) {
		long uid = result
				.getLong(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.UID));
		long packetUid = result
				.getLong(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.PACKET_UID));
		int typeId = result
				.getInt(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.TYPE));
		long time = result
				.getLong(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.TIME));
		String hexCode = result
				.getString(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.HEX_CODE));
		hexCode = hexCode.intern();
		String displayText = result
				.getString(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.DISPLAY_TXT));
		displayText = displayText.intern();
		PacketAttributeType packetAttrType = PacketAttributeType
				.getPacketAttributeType(typeId);
		return new PacketAttribute(uid, time, packetUid, packetAttrType,
				hexCode, displayText);
	}
}