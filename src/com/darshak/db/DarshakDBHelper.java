package com.darshak.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.darshak.constants.PacketType;
import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.constants.PacketAttributeType;
import com.darshak.constants.NetworkType;
import com.darshak.modal.Packet;
import com.darshak.modal.EventDetails;
import com.darshak.modal.PacketAttribute;
import com.darshak.modal.LogEntry;

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
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public long insertLogEntry(NetworkType nwType, String nwOperator, Event event,
			long eventReportedAt) {
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
		
		Log.e(LOG_TAG, "Inserted log entry to database." + nwType + ", " + event
				+ " , " + nwOperator);
		 
		return rowId;
	}

	public long insertPacket(long logEntryUid, long eventReportedAt, int packetType,
			String hexCode, boolean isDuplicate) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.PacketSchema.LOG_UID, logEntryUid);
		dbValues.put(DatabaseSchema.PacketSchema.TYPE, packetType);
		dbValues.put(DatabaseSchema.PacketSchema.HEX_CODE, hexCode);
		dbValues.put(DatabaseSchema.PacketSchema.IS_DUPLICATE,
				isDuplicate ? 1 : 0);
		dbValues.put(DatabaseSchema.PacketSchema.TIME, eventReportedAt);

		long rowId = db.insert(DatabaseSchema.PacketSchema.TABLE_NAME, null,
				dbValues);
		
		Log.e(LOG_TAG, "Inserted Packet entry to database." + logEntryUid + ", "
				+ packetType + ", " + hexCode);
		 
		return rowId;
	}

	public boolean isPacketAlreadyInserted(int typeId, String hexCode) {
		SQLiteDatabase db = getReadableDatabase();
		String[] projection = { DatabaseSchema.PacketSchema.UID };

		String selection = DatabaseSchema.PacketSchema.TYPE + " = "
				+ String.valueOf(typeId) + " AND "
				+ DatabaseSchema.PacketSchema.HEX_CODE + " = '" + hexCode
				+ "'";
		selection = selection.intern();

		Cursor result = db.query(DatabaseSchema.PacketSchema.TABLE_NAME,
				projection, selection, null, null, null, null);

		if (result == null || result.getCount() == 0) {
			return false;
		}
		
		result.close();
		return true;
	}

	public long insertPacketAttribute(long packetUid, long eventReportedAt,
			int packetAttributeId, String hexCode, String displayText) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();
		dbValues.put(DatabaseSchema.PacketAttributeSchema.PACKET_UID,
				packetUid);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.TYPE, packetAttributeId);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.HEX_CODE, hexCode);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.DISPLAY_TXT, displayText);
		dbValues.put(DatabaseSchema.PacketAttributeSchema.TIME, eventReportedAt);

		long rowId = db.insert(DatabaseSchema.PacketAttributeSchema.TABLE_NAME,
				null, dbValues);
		
		Log.e(LOG_TAG, "Inserted packet attribute to database." + packetUid + ", "
				+ packetAttributeId + ", " + hexCode + ", " + displayText);
		 
		return rowId;
	}

	public long insertEventDetails(EventDetails eventDetails) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues dbValues = new ContentValues();		
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_CODE, eventDetails.getEvent().getEventCode());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_TIME, eventDetails.getReportedAt());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_NW_TYPE, eventDetails.getNwType().getNwTypeCode());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_NW_OP, eventDetails.getNwOperator());
		dbValues.put(DatabaseSchema.CellularEvent.EVENT_CONSUMED, 0);

		long rowId = db.insert(DatabaseSchema.CellularEvent.TABLE_NAME, null,
				dbValues);
		
		Log.e(LOG_TAG, "Added event details into database." + eventDetails);		
		return rowId;
	}
	
	public List<LogEntry> getLogEntries(String uidCondition, String orderBy, int numOfRecords,
			String filterQuery) {		
		String whereClause = DatabaseSchema.LogEntrySchema.IS_DELETED 
				+ " = "
				+ String.valueOf(0) 
				+ " AND " 
				+ uidCondition;
		whereClause = whereClause.intern();

		List<LogEntry> logEntries = new ArrayList<LogEntry>();

		SQLiteDatabase db = getReadableDatabase();

		Cursor result = db.query(DatabaseSchema.LogEntrySchema.TABLE_NAME,
				DatabaseSchema.LogEntrySchema.COLUMNS, whereClause, null, null,
				null, orderBy);

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
		result.close();
		return logEntries;
	}

	public LogEntry getLogEntry(long uid, String filterSelectionQuery) {
		String orderBy = DatabaseSchema.LogEntrySchema.TIME + " ASC ";
		orderBy = orderBy.intern();

		String whereClause = DatabaseSchema.LogEntrySchema.IS_DELETED + " = "
				+ String.valueOf(0) + " AND "
				+ DatabaseSchema.LogEntrySchema.UID + " = "
				+ String.valueOf(uid);
		whereClause = whereClause.intern();

		List<LogEntry> logEntries = new ArrayList<LogEntry>();

		SQLiteDatabase db = getReadableDatabase();

		Cursor result = db.query(DatabaseSchema.LogEntrySchema.TABLE_NAME,
				DatabaseSchema.LogEntrySchema.COLUMNS, whereClause, null, null,
				null, orderBy);

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
			logEntries.add(logEntry);
		}
		result.close();
		return logEntry;
	}

	public List<Packet> getPackets(long logEntryUid) {
		String selection = DatabaseSchema.PacketSchema.LOG_UID + " = "
				+ String.valueOf(logEntryUid);
		selection = selection.intern();
		return getPackets(selection, null);
	}

	public List<Packet> getPackets(long logEntryUid,
			String filterSelectionQuery) {
		String selection = DatabaseSchema.PacketSchema.LOG_UID + " = "
				+ String.valueOf(logEntryUid) 
				+ " AND ( "
				+ filterSelectionQuery
				+ " ) ";
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

	public List<PacketAttribute> getPacketAttributes(
			long packetUid, PacketAttributeType type) {
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
		String orderBy = DatabaseSchema.CellularEvent.EVENT_TIME + " ASC ";
		orderBy.intern();

		String whereClause = DatabaseSchema.CellularEvent.EVENT_CONSUMED
				+ " = 0 ";
		whereClause = whereClause.intern();

		SQLiteDatabase db = getReadableDatabase();

		Cursor result = db.query(DatabaseSchema.CellularEvent.TABLE_NAME,
				DatabaseSchema.CellularEvent.COLUMNS, whereClause, null, null,
				null, orderBy);

		if (result == null || result.getCount() == 0) {
			return null;
		}
		result.moveToFirst();

		long uid = result.getLong(result
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
		consumeEventDetails(eventDetails);
		
		result.close();
		return eventDetails;
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

		Log.d(LOG_TAG,
				"Deleted Log entry from database : " + uid);
	}

	public void consumeEventDetails(EventDetails eventDetails) {
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
		String whereClause = DatabaseSchema.CellularEvent.EVENT_CONSUMED
				+ " = 0 "
				+ " AND "
				+ DatabaseSchema.CellularEvent.EVENT_CODE
				+ " IN ( "
				+ Event.INCOMING_CALL.getEventCode()
				+ ", " + Event.OUTGOING_CALL.getEventCode()
				+ ", " + Event.INCOMING_SMS.getEventCode()
				+ ", " + Event.OUTGOING_SMS.getEventCode()
				+ " ) "
				+ " AND "
				+ DatabaseSchema.CellularEvent.EVENT_NW_TYPE
				+ " = " + Constants.GSM;

		whereClause = whereClause.intern();

		SQLiteDatabase db = getReadableDatabase();

		Cursor result = db.query(DatabaseSchema.CellularEvent.TABLE_NAME,
				DatabaseSchema.CellularEvent.COLUMNS, whereClause, null, null,
				null, null);

		if (result == null) {
			return 0;
		}
		int count = result.getCount();

		result.close();
		return count;
	}

	private List<Packet> getPackets(String whereClause, String orderBy) {
		List<Packet> packets = new ArrayList<Packet>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor result = db.query(DatabaseSchema.PacketSchema.TABLE_NAME,
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
			packet.addPacketAttributes(getPacketAttributes(packet
					.getUid()));
			packets.add(packet);
			result.moveToNext();
		}
		
		result.close();
		return packets;
	}

	private List<PacketAttribute> getPacketAttributes(
			String whereClause, String orderBy) {
		List<PacketAttribute> packetAttributes = new ArrayList<PacketAttribute>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor result = db.query(DatabaseSchema.PacketAttributeSchema.TABLE_NAME,
				DatabaseSchema.PacketAttributeSchema.COLUMNS, whereClause, null,
				null, null, orderBy);

		if (result == null || result.getCount() == 0) {
			return packetAttributes;
		}
		result.moveToFirst();

		Log.d(LOG_TAG,
				"Number of packet attributes matching where clause:"
						+ whereClause + " are: " + result.getCount());
		while (!result.isAfterLast()) {
			packetAttributes.add(getPacketAttribute(result));
			result.moveToNext();
		}

		result.close();
		return packetAttributes;
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
		String hexCode = result
				.getString(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketSchema.HEX_CODE));
		hexCode = hexCode.intern();

		return new Packet(uid, time, logEntryUid,
				PacketType.getPacketTypeById(type), hexCode);
	}

	private PacketAttribute getPacketAttribute(Cursor result) {
		long uid = result.getLong(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.UID));
		long packetUid = result
				.getLong(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.PACKET_UID));
		int type = result.getInt(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.TYPE));
		long time = result.getLong(result
				.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.TIME));
		String hexCode = result
				.getString(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.HEX_CODE));
		hexCode = hexCode.intern();
		String displayText = result
				.getString(result
						.getColumnIndexOrThrow(DatabaseSchema.PacketAttributeSchema.DISPLAY_TXT));
		displayText = displayText.intern();
		return new PacketAttribute(uid, time, packetUid, type, hexCode,
				displayText);
	}
}