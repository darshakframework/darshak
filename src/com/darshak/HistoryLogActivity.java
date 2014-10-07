package com.darshak;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.db.DarshakDBHelper;
import com.darshak.modal.LogEntry;
import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;
import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class HistoryLogActivity extends Activity {

	private static final String LOG_TAG = "HistoryLogActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_log);

		Bundle extraBundle = getIntent().getExtras();

		long logEntryUid = (Long) extraBundle
				.get(Constants.LOG_ENTRY_TO_BE_DISPLAYED);

		String filterSelectionQuery = extraBundle
				.getString(Constants.FILTER_SELECTION_QUERY);

		Log.d(LOG_TAG, "Log file whose content to be displayed : "
				+ logEntryUid);

		DarshakDBHelper dbHelper = ((Application) getApplication())
				.getDBHelper();
		LogEntry logEntry = dbHelper.getLogEntry(logEntryUid,
				filterSelectionQuery);

		TextView logDateValueTextView = (TextView) findViewById(R.id.logDateValue);
		logDateValueTextView.setText(Utils.formatDate(logEntry));

		TextView nwTypeValueTextView = (TextView) findViewById(R.id.nwTypeValue);
		nwTypeValueTextView.setText(Utils.getNetworkType(logEntry));

		TextView nwOperatorValueTextView = (TextView) findViewById(R.id.nwOperatorValue);
		nwOperatorValueTextView.setText(logEntry.getNwOperator());

		TextView eventValueTextView = (TextView) findViewById(R.id.eventValue);
		eventValueTextView.setText(getEvent(logEntry));

		LinearLayout packetAttributesView = (LinearLayout) findViewById(R.id.historyInnerLinearLayout);

		for (Packet packet : logEntry.getPackets()) {
			TextView textView = new TextView(this);
			textView.setText(packet.getPacketType().name());
			textView.setTextColor(getResources()
					.getColor(R.color.default_color));
			packetAttributesView.addView(textView);

			if (!Constants.prodMode) {
				TextView codesTextView = new TextView(this);
				codesTextView.setText(packet.getHexCode());
				codesTextView.setTextColor(getResources().getColor(
						R.color.default_color));
				packetAttributesView.addView(codesTextView);
			}
			
			for (PacketAttribute packetAttribute : packet.getPacketAttributes()) {
				TextView indTextView = new TextView(this);
				indTextView.setText(packetAttribute.getDisplayText());
				packetAttributesView.addView(indTextView);
			}
		}
		logEntry = null;
	}

	private String getEvent(LogEntry logEntry) {
		Event event = Event.getMatchingEvent(logEntry.getEvent());
		return event.name();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.history_log, menu);
		return true;
	}
}