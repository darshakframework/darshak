package com.darshak;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.constants.PacketAttributeType;
import com.darshak.db.DarshakDBHelper;
import com.darshak.modal.FilterSelectionStatus;
import com.darshak.modal.LogEntry;
import com.darshak.modal.PacketAttribute;
import com.darshak.modal.PaginationDetails;
import com.darshak.service.AlarmEventReceiver;
import com.darshak.service.OutgoingSMSContentObserver;
import com.darshak.util.Utils;

/**
 * @author Andreas Schildbach
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class MainActivity extends Activity {

	private static final String LOG_TAG = "MainActivity";

	private DarshakDBHelper sDBHelper = null;
	
	private FilterSelectionStatus sFilterSelectionStatus = null;
	
	private PaginationDetails sPaginationDetails = null;
	
	private TableRow sTableHeaderRow = null;
	
	private static final String PREF_NAME = "PREF_SENT_SMS_ID";

	private static final String HIGHEST_SMS_ID_KEY = "HIGHEST_SMS_ID";
	
	private static final String SMS_CONTENT_OBSERVER_INITIALIZED_KEY = "SMS_CONTENT_OBSERVER_INITIALIZED";

	// Create Sent box URI
	private static final Uri SENT_URI = Uri.parse("content://sms/sent");

	// List required columns
	private static final String[] REQ_COLS = new String[] { "_id", "date" };
	
	private static final String SMS_SORT_ORDER = " date DESC ";

	private SharedPreferences sSharedpreferences;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);

		final TextView errorText = (TextView) findViewById(R.id.errorText);
		if (!Utils.isSupportedVersion() && !Utils.isSupportedModel()) {
			errorText.setText(R.string.wrong_model_and_version);
		} else if (!Utils.isSupportedModel()) {
			errorText.setText(R.string.wrong_model);
		} else if (!Utils.isSupportedVersion()) {
			errorText.setText(R.string.wrong_version);
		}
		
		sDBHelper = ((Application) getApplication()).getDBHelper();
		
		// By default don't show next and prev buttons.
		sPaginationDetails = new PaginationDetails(false, false);
		
		sTableHeaderRow = (TableRow) View.inflate(getApplicationContext(),
				R.layout.log_table_header_row, null);

		sFilterSelectionStatus = new FilterSelectionStatus(true);

		// initialize shared preference
		sSharedpreferences = getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		// Initialize SMS sent count in preference
		if (getHighestSMSIdInPref() == -1) {
			setHighestSMSIdInPref(getHighestSMSId());
		}

		initializeNextPrevButtonsState();

		loadLogEntries();		
		
		initializeAlarmManager();		
		initializeSMSContentObserver();
	}
	
	private void initializeAlarmManager() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent broadcast_intent = new Intent(getApplicationContext(),
				AlarmEventReceiver.class);

		// Check alarm is already initialized
		boolean alarmNotRegistered = PendingIntent.getBroadcast(
				getApplicationContext(), 0, broadcast_intent,
				PendingIntent.FLAG_NO_CREATE) == null;

		if (alarmNotRegistered) {
			Log.d(LOG_TAG, "Intializing Alarm");

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, broadcast_intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			Calendar now = Calendar.getInstance();
			// Start in 1 seconds
			long triggerAtTime = now.getTimeInMillis();
			// Repeat after 30 seconds : worked fall back to it
			long repeatAlarmEvery = (Constants.DARSHAK_SERV_EXE_INTERVAL_SEC * 1000);
			// TODO: which type is most suitable.
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						triggerAtTime, repeatAlarmEvery, pendingIntent);
			
			Log.d(LOG_TAG, "Intializing Alarm completed");
		} else {
			Log.d(LOG_TAG, "Alarm already registered.");
		}
	}
	
	private void initializeSMSContentObserver() {
		if (!isSMSContentObserverInitialized()) {
			// Register observer for SMS sent
			ContentResolver contentResolver = getApplicationContext()
					.getContentResolver();
			contentResolver.registerContentObserver(Uri.parse("content://sms"),
					true, new OutgoingSMSContentObserver(this));
			smsContentObserverInitialized();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_options, menu);
		return true;
	}

	@Override
	protected void onRestart() {
		super.onRestart();		
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("PaginationDetails",
				sPaginationDetails);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		sPaginationDetails = (PaginationDetails) savedInstanceState
				.getSerializable("PaginationDetails");
	}

	private void restart() {
		finish();
		startActivity(getIntent());
	}

	private void updateView(LogEntry... logEntries) {
		if (logEntries == null || logEntries.length == 0) {
			Log.d(LOG_TAG, "Log files not present.");
			Button clearButton = (Button) findViewById(R.id.clear_log);
			// clearButton.setVisibility(View.GONE);
			clearButton.setEnabled(false);
			return;
		}
		addRowsForLogEntries(logEntries);
		initializeNextPrevButtonsState();
		logEntries = null;
	}
	
	private void addRowsForLogEntries(LogEntry... logEntries) {
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		for (final LogEntry logEntry : logEntries) {
			final TableRow tableRow = (TableRow) View.inflate(
					getApplicationContext(), R.layout.history_log_table_row,
					null);

			OnClickListener onClickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(LOG_TAG, "On item click position : ");
					Intent showHistIntent = new Intent(getApplicationContext(),
							HistoryLogActivity.class);
					showHistIntent.putExtra(
							Constants.LOG_ENTRY_TO_BE_DISPLAYED,
							logEntry.getUid());
					showHistIntent.putExtra(
							Constants.FILTER_SELECTION_QUERY,
							sFilterSelectionStatus.getQuery());
					startActivity(showHistIntent);
				}
			};

			OnClickListener removeEntryListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(LOG_TAG, "Clicked on remove entry image : ");
					sDBHelper.deleteLogEntry(logEntry.getUid());
					restart();
				}
			};

			OnTouchListener onTouchListner = new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					switch (event.getAction()) {

					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_HOVER_ENTER:
					case MotionEvent.ACTION_HOVER_MOVE:
						tableRow.setBackgroundColor(getResources().getColor(
								R.color.default_color));
						break;
					default:
						tableRow.setBackgroundColor(Color.TRANSPARENT);
						break;
					}
					return false;
				}
			};

			OnTouchListener removeImageTouchListner = new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					switch (event.getAction()) {

					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_HOVER_ENTER:
					case MotionEvent.ACTION_HOVER_MOVE:
						view.setBackgroundColor(getResources().getColor(
								R.color.default_color));
						break;
					default:
						view.setBackgroundColor(Color.TRANSPARENT);
						break;
					}
					return false;
				}
			};

			TextView dateTextView = (TextView) tableRow
					.findViewById(R.id.date_textView);
			dateTextView.setText(Utils.formatDate(logEntry));
			dateTextView.setOnClickListener(onClickListener);
			dateTextView.setOnTouchListener(onTouchListner);

			TextView typeTextView = (TextView) tableRow
					.findViewById(R.id.type_textView);
			typeTextView.setText(Utils.getNetworkType(logEntry));
			typeTextView.setOnClickListener(onClickListener);
			typeTextView.setOnTouchListener(onTouchListner);

			ImageView eventImageView = (ImageView) tableRow
					.findViewById(R.id.event_imageView);
			eventImageView.setImageResource(getEvent(logEntry));
			eventImageView.setOnClickListener(onClickListener);
			eventImageView.setOnTouchListener(onTouchListner);

			ImageView authImageView = (ImageView) tableRow
					.findViewById(R.id.auth_imageView);
			authImageView
					.setImageResource(getAuthenticationImageSource(logEntry));
			authImageView.setOnClickListener(onClickListener);
			authImageView.setOnTouchListener(onTouchListner);

			ImageView encryImageView = (ImageView) tableRow
					.findViewById(R.id.encry_imageView);
			encryImageView.setImageResource(getEncryptionImageSource(logEntry));
			encryImageView.setOnClickListener(onClickListener);
			encryImageView.setOnTouchListener(onTouchListner);

			ImageView removeEntryImageView = (ImageView) tableRow
					.findViewById(R.id.remove_imageView);
			removeEntryImageView.setOnClickListener(removeEntryListener);
			removeEntryImageView.setOnTouchListener(removeImageTouchListner);

			tableLayout.addView(tableRow);
		}
	}

	private int getAuthenticationImageSource(LogEntry logEntry) {
		if (logEntry.getNwType() == Constants._3G) {
			List<PacketAttribute> packetAttributes = logEntry
					.getAllPacketAttributes();
			for (PacketAttribute packetAttribute : packetAttributes) {
				if (packetAttribute.getPacketAttrTypeId() == PacketAttributeType.RND_NUM
						.getTypeId()) {
					return R.drawable.correct;
				}
			}
			return R.drawable.wrong;
		}
		
		List<PacketAttribute> packetAttributes = logEntry
				.getAllPacketAttributes();
		for (PacketAttribute packetAttribute : packetAttributes) {
			if (packetAttribute.getPacketAttrTypeId() == PacketAttributeType.NW_OP_AUTHENTICATES
					.getTypeId()) {
				return R.drawable.correct;
			}
		}
		return R.drawable.wrong;
	}

	private int getEncryptionImageSource(LogEntry logEntry) {
		if(logEntry.getNwType() == Constants._3G) {
			List<PacketAttribute> packetAttributes = logEntry
					.getAllPacketAttributes();
			for (PacketAttribute packetAttribute : packetAttributes) {
				if (packetAttribute.getPacketAttrTypeId() == PacketAttributeType.NW_OP_USING_UEA1
						.getTypeId()) {
					return R.drawable.correct;
				}				
			}
			return R.drawable.wrong;
		}
		
		List<PacketAttribute> packetAttributes = logEntry
				.getAllPacketAttributes();
		for (PacketAttribute packetAttribute : packetAttributes) {
			if (packetAttribute.getPacketAttrTypeId() == PacketAttributeType.NW_OP_USING_A53
					.getTypeId()) {
				return R.drawable.correct;
			}
			if (packetAttribute.getPacketAttrTypeId() == PacketAttributeType.NW_OP_USING_A51
					.getTypeId()) {
				return R.drawable.yellow;
			}
		}
		return R.drawable.wrong;
	}

	private int getEvent(LogEntry logEntry) {
		Event event = Event.getMatchingEvent(logEntry.getEvent());
		return event.getImageSource();
	}

	/**
	 * Method is invoked when clear logs button is clicked on the view.
	 * 
	 * @param view
	 */
	public void clearLogs(View view) {
		sDBHelper.deleteAllLogEntries();
		restart();	
	}

	/**
	 * Method is invoked when refresh logs button is clicked on the view.
	 * 
	 * @param view
	 */
	public void refreshLogFiles(View view) {
		restart();
	}
	
	public void openAboutUsPage(MenuItem menu) {
		Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
		startActivity(intent);
	}

	public void openTestPage(MenuItem menu) {
		Intent intent = new Intent(getApplicationContext(), TestActivity.class);
		startActivity(intent);
	}

	private void loadLogEntries() {		
		disableNextPrevButtonsState();
		new RenderLogEntryTable().execute((String) null);
	}
	
	public void next(View view) {
		sPaginationDetails.setPrev(true);
		sPaginationDetails.setQueryOnNext();
		sPaginationDetails.setSortOrderOnNext();
		sPaginationDetails.setMovingFwd(true);
		loadLogEntries();
	}

	public void prev(View view) {
		sPaginationDetails.setNext(true);
		sPaginationDetails.setQueryOnPrev();
		sPaginationDetails.setSortOrderOnPrev();
		sPaginationDetails.setMovingFwd(false);
		loadLogEntries();
	}
	
	private void disableNextPrevButtonsState() {
		Button next = (Button) findViewById(R.id.next);
		next.setEnabled(false);

		Button prev = (Button) findViewById(R.id.prev);
		prev.setEnabled(false);
	}
	
	private void initializeNextPrevButtonsState() {
		Button next = (Button) findViewById(R.id.next);
		next.setEnabled(sPaginationDetails.isNext());

		Button prev = (Button) findViewById(R.id.prev);
		prev.setEnabled(sPaginationDetails.isPrev());
	}

	private void resetTableView() {
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		tableLayout.removeAllViews();
		tableLayout.invalidate();
		tableLayout.refreshDrawableState();
		tableLayout.addView(sTableHeaderRow);
	}

	private class RenderLogEntryTable extends
			AsyncTask<String, LogEntry, Integer> {

		@Override
		protected Integer doInBackground(String... params) {			
			List<LogEntry> listOfLogEntry = getLogEntries();

			publishProgress(listOfLogEntry.toArray(new LogEntry[listOfLogEntry
					.size()]));
			return null;
		}

		@Override
		protected void onProgressUpdate(LogEntry... logEntries) {
			resetTableView();
			updateView(logEntries);			
		}

		private List<LogEntry> getLogEntries() {
			List<LogEntry> listOfLogEntry = sDBHelper.getLogEntries(
					sPaginationDetails.getWhereClause(), sPaginationDetails.getSortOrder(),
					PaginationDetails.NUM_OF_RECORDS + 1,
					sFilterSelectionStatus.getQuery());
			
			// reversing entries is required because to get the previous entries
			// sort order ASC is used however on the screen entries are shown in
			// descending order.
			if (!sPaginationDetails.isMovingFwd()) {
				Collections.reverse(listOfLogEntry);
			}
			// If number of records returned, means can be more records.
			if (listOfLogEntry.size() > PaginationDetails.NUM_OF_RECORDS) {
				if (sPaginationDetails.isMovingFwd()) {
					sPaginationDetails.setNext(true);
				} else {
					sPaginationDetails.setPrev(true);
				}				
				
			} else {
				if (sPaginationDetails.isMovingFwd()) {
					sPaginationDetails.setNext(false);
				} else {
					sPaginationDetails.setPrev(false);
				}				
			}
			if (listOfLogEntry.size() > 0) {
				// Set the start uid.
				sPaginationDetails.setStartUid(listOfLogEntry.get(0)
						.getUid());
				if (listOfLogEntry.size() == 1) {
					// Set the end ID last entry.
					sPaginationDetails.setEndUid(listOfLogEntry.get(
							listOfLogEntry.size() - 1).getUid());
				} else {					
					// Set the end ID second last entry.
					sPaginationDetails.setEndUid(listOfLogEntry.get(
							listOfLogEntry.size() - 2).getUid());
				}
			}
			return listOfLogEntry;
		}		
	}
	
	public int getHighestSMSId() {
		// Get Content Resolver object, which will deal with Content Provider
		ContentResolver contentResolver = getContentResolver();

		// Fetch Sent SMS Message from Built-in Content Provider
		Cursor cursor = contentResolver.query(SENT_URI, REQ_COLS, null, null,
				SMS_SORT_ORDER);

		// SMS are sorted as per date in descending order, first id is the latest ID		
		if (cursor!= null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			int highestSMSID = cursor.getInt(0);
			Log.e(LOG_TAG, "HIGHEST SMS ID " + highestSMSID);
			return highestSMSID;
		}
		return -1;
	}

	public void setHighestSMSIdInPref(int highestSMSID) {
		Editor editor = sSharedpreferences.edit();
		editor.putInt(HIGHEST_SMS_ID_KEY, highestSMSID);
		editor.commit();
		editor.apply();
	}

	public int getHighestSMSIdInPref() {
		return sSharedpreferences.getInt(HIGHEST_SMS_ID_KEY, -1);
	}
	
	public void smsContentObserverInitialized() {
		Editor editor = sSharedpreferences.edit();
		editor.putBoolean(SMS_CONTENT_OBSERVER_INITIALIZED_KEY, true);
		editor.commit();
		editor.apply();
	}
	
	public boolean isSMSContentObserverInitialized() {
		return sSharedpreferences.getBoolean(
				SMS_CONTENT_OBSERVER_INITIALIZED_KEY, false);
	}
}