package com.darshak.modal;

import android.util.Log;

import com.darshak.constants.PacketType;
import com.darshak.db.DatabaseSchema;

/**
 * On Main page filters can be introduced. This class will help to achive that.
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class FilterSelectionStatus {

	private static final String LOG_TAG = FilterSelectionStatus.class
			.getSimpleName();

	private static final PacketType[] TRACK_SILENT_SMS_AND_CELLULAR_EVENTS = new PacketType[] {
			PacketType.GSM_INIT_SERV_REQ, PacketType.GSM_INIT_CIPHER_MODE,
			PacketType.GSM_INIT_AUTH_REQ, PacketType._3G_INIT_SERV_REQ,
			PacketType._3G_INIT_CIPHER_MODE, PacketType._3G_INIT_AUTH_REQ,
			PacketType.SILENT_SMS };

	private boolean sSilentSMSCellularEvents;

	public FilterSelectionStatus(boolean silentSMSCellularEvents) {
		super();
		this.sSilentSMSCellularEvents = silentSMSCellularEvents;
	}

	private String createQueryForCodeEnums(PacketType[] packetTypes) {
		StringBuilder strBuilder = new StringBuilder();

		for (PacketType packetType : packetTypes) {
			if (strBuilder.length() > 0)
				strBuilder.append(" OR ");
			strBuilder.append(DatabaseSchema.PacketSchema.TYPE);
			strBuilder.append(" = ");
			strBuilder.append(packetType.getPacketTypeId());
		}
		return strBuilder.toString().intern();
	}

	public String getQuery() {
		StringBuilder strBuilder = new StringBuilder();
		if (sSilentSMSCellularEvents) {
			strBuilder
					.append(createQueryForCodeEnums(TRACK_SILENT_SMS_AND_CELLULAR_EVENTS));
		}

		// Dummy condition to match no condition.
		if (strBuilder.length() == 0)
			strBuilder.append(" 0 = 1 ");

		Log.e(LOG_TAG,
				"Final query to get main table entries "
						+ strBuilder.toString());
		return strBuilder.toString().intern();
	}
}