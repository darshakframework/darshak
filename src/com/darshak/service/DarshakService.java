package com.darshak.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.darshak.AirplaneModeConfigureActivity;
import com.darshak.Application;
import com.darshak.R;
import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.constants.NetworkType;
import com.darshak.constants.PacketType;
import com.darshak.db.DarshakDBHelper;
import com.darshak.modal.EventDetails;
import com.darshak.modal.Packet;
import com.darshak.modal.PacketAttribute;
import com.darshak.packetreader.LogFileReader;
import com.darshak.packetreader.PacketReader;
import com.darshak.util.Utils;

/**
 * @author Andreas Schildbach
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class DarshakService extends Service {

	private static final String LOG_TAG = "DarshakService";

	// Notification ID to allow for future updates
	private static final int MY_NOTIFICATION_ID = 131313;

	private ServiceHandler mServiceHandler;

	private Messenger mServiceMessenger;

	private DarshakDBHelper sDBHelper = null;

	private PacketReader sPacketReader;

	private LogFileReader sLogFileReader = new LogFileReader();

	/**
	 * Binder for darshakService.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Initialize the Thread handler.
	 */
	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "onCreate of DarshakService is called.");
		// Initialize the thread handler.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		// Looper which keeps tracks of the the messages and the runnable added.
		Looper mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		// Set the intent to be triggered when clicked on notification,
		sNotificationIntent = new Intent(getApplicationContext(),
				AirplaneModeConfigureActivity.class);
		sContentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
				sNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		sDBHelper = ((Application) getApplication()).getDBHelper();

		sPacketReader = new PacketReader(getApplicationContext(), sDBHelper);
	}

	/**
	 * Send message.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "onStartCommand of DarshakService is called.");
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);
		return START_STICKY;
	}

	public void onDestroy() {
		unbindService(mSecPhoneServiceConnection);
	}

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (connectToRilService()) {
				Log.d(LOG_TAG, "Successfully connected to SecPhoneService. :)");
			} else {
				return;
			}
			triggerLogDump(0);
		}
	}

	// Bind to a service m.sec.phone.SecPhoneService.
	private boolean connectToRilService() {
		final Context context = getApplicationContext();

		Intent intent = new Intent();
		intent.setClassName("com.sec.phone", "com.sec.phone.SecPhoneService");
		try {
			// 1 = BIND_AUTO_CREATE
			context.bindService(intent, mSecPhoneServiceConnection, 1);
		} catch (final RuntimeException e) {
			Log.e(LOG_TAG, "Error while binding to SecPhoneService.");
			return false;
		}
		return true;
	}

	// Methods of this service connection are invoked when connection with the
	// service is established.
	private ServiceConnection mSecPhoneServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentname,
				IBinder ibinder) {
			mServiceMessenger = new Messenger(ibinder);
			ramdumpMode(1);
		}

		// Service should not be disconnected whenever it does reconnect it.
		@Override
		public void onServiceDisconnected(ComponentName componentname) {
			if (connectToRilService()) {
				Log.d(LOG_TAG, "Successfull connected to SecPhoneService. :)");
			} else {
				return;
			}
		}
	};

	private void ramdumpMode(int i) {
		Log.d(LOG_TAG, "Setting ram dump mode.");
		try {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			DataOutputStream dataoutputstream = new DataOutputStream(
					bytearrayoutputstream);
			dataoutputstream.writeByte(7);
			dataoutputstream.writeByte(10);
			dataoutputstream.writeShort(5);
			dataoutputstream.writeByte(i);
			dataoutputstream.close();

			invokeOemRilRequestRaw(bytearrayoutputstream.toByteArray(),
					ramdumpHandler.obtainMessage(1007), ramdumpModeMessenger);

		} catch (IOException e) {
			Log.e("sysDump", "ioexception: ", e);
		}
	}

	/*
	 * Returns a new Message from the global message pool. More efficient than
	 * creating and allocating new instances. The retrieved message has its
	 * handler set to this instance (Message.target == this). If you don't want
	 * that facility, just call Message.obtain() instead.
	 */
	private Handler ramdumpHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.getData().getInt("error") == 0) {
				Log.d(LOG_TAG, "Ramdumpmode set");
			} else {
				Log.d(LOG_TAG, "Ramdumpmode error");
			}
		}
	};

	// Service replies through this messenger. Therefore whenever service
	// replies handleMessage of rampdumHandler will be called.
	private Messenger ramdumpModeMessenger = new Messenger(ramdumpHandler);

	private void invokeOemRilRequestRaw(byte abyte0[], Message message,
			Messenger messenger) {
		if (mServiceMessenger != null) {
			try {
				Bundle bundle = message.getData();
				bundle.putByteArray("request", abyte0);
				message.setData(bundle);
				message.replyTo = messenger;

				mServiceMessenger.send(message);

				SystemClock.sleep(200);
			} catch (RemoteException remoteexception) {
				Log.d(LOG_TAG, "RemoteException", remoteexception);
			}
		} else {
			Log.e(LOG_TAG, "Could not connect to phone service.");
		}
	}

	private byte[] startSysDumpData(int i) {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		DataOutputStream dataoutputstream = new DataOutputStream(
				bytearrayoutputstream);
		try {
			dataoutputstream.writeByte(7);
			dataoutputstream.writeByte(i);
			dataoutputstream.writeShort(5);
			dataoutputstream.writeByte(0);
		} catch (IOException ioexception) {
			Log.d(LOG_TAG, "IOException in startSysDumpData!!!");
			return null;
		}
		return bytearrayoutputstream.toByteArray();
	}

	private Handler triggerHandler = new Handler() {

		@Override
		public void handleMessage(Message message) {
			Log.d(LOG_TAG, "DONE: " + message.what);
			if (message.getData().getInt("error") == 0) {
				if (Utils.isGalaxyS2()) {
					Utils.deleteLogFilesWithPrefix(new String[] { Constants.LOG_FILE_S2_PREFIX_AENAES });
				}
				Log.e(LOG_TAG, "Received message from PhoneSecService.");
				// files are not available as soon as message received from
				// service.
				SystemClock.sleep(3000);
				lookForSecurityCodes();
			} else {
				Log.e(LOG_TAG, "Error while fetching modem log.");
			}
		}
	};

	private Messenger triggerModeMessenger = new Messenger(triggerHandler);

	private void triggerLogDump(Integer index) {
		byte[] data = startSysDumpData(18);

		invokeOemRilRequestRaw(data, triggerHandler.obtainMessage(1),
				triggerModeMessenger);
	}

	private void lookForSecurityCodes() {
		File[] matchingLogFile = Utils.searchLogFile();
		if (matchingLogFile == null || matchingLogFile.length == 0) {
			Log.e(LOG_TAG, "Log file not found.");
			return;
		} else {
			EventDetails eventDetails = getEventDetails();
			NetworkType nwType = eventDetails.getNwType();
			String nwOperator = eventDetails.getNwOperator();
			Event event = eventDetails.getEvent();
			long eventReportedAt = eventDetails.getReportedAt();

			Log.e(LOG_TAG, "Current Event " + event.name());

			for (int i = 0; i < matchingLogFile.length; i++) {
				Log.d(LOG_TAG,
						"Source log file : "
								+ matchingLogFile[i].getAbsolutePath());

				long startTime = System.currentTimeMillis();

				byte[] filteredByteSeq = sLogFileReader
						.readFile(matchingLogFile[i]);
				if (filteredByteSeq == null) {
					Utils.deleteLogFile(matchingLogFile[i]);
					continue;
				}
				long endTime = System.currentTimeMillis();
				Log.e(LOG_TAG, "Time taken to read and filter log file "
						+ (endTime - startTime) / 1000 + " Seconds.");
				// Silent SMS does not generate an event, to know the presence
				// of Silent SMS log file needs to be scanned.
				startTime = System.currentTimeMillis();
				List<Packet> silentSMSEntries = sPacketReader.generateResult(
						filteredByteSeq, Constants.SMS);
				Log.e(LOG_TAG, "Number of silent SMS entries : "
						+ silentSMSEntries.size());

				List<Packet> profileParams = sPacketReader.generateResult(
						filteredByteSeq, Constants.PROFILE_PARAMS);
				beginProfileParamComparison(profileParams);

				Log.e(LOG_TAG, "Number of Profile parameters found : "
						+ profileParams.size());

				if (event != Event.NONE) {
					if (NetworkType._3G == nwType) {
						List<Packet> _3gEntries = sPacketReader.generateResult(
								filteredByteSeq, Constants._3G);
						// In case of 3G it has been observed that for cellular events
						// No configured packets matches. then the event is not registered
						// and not displayed. To ovecome that issue. Create empty packet.
						if (_3gEntries == null || _3gEntries.size() == 0) {
							Packet pkt = new Packet(PacketType.NULL, "");
							_3gEntries.add(pkt);
						}
						insertEntriesIntoDB(_3gEntries, NetworkType._3G,
								nwOperator, event, eventReportedAt);
						Log.e(LOG_TAG,
								"Number of 3G entries : " + _3gEntries.size());
						_3gEntries = null;
					} else if (NetworkType.GSM == nwType) {
						List<Packet> gsmEntries = sPacketReader.generateResult(
								filteredByteSeq, Constants.GSM);
						insertEntriesIntoDB(gsmEntries, NetworkType.GSM,
								nwOperator, event, eventReportedAt);
						Log.e(LOG_TAG,
								"Number of GSM entries : " + gsmEntries.size());
						gsmEntries = null;
					}
					// TODO remove it
					Utils.mvLogFile(matchingLogFile[i]);
				} else {
					// TODO remove it
					Utils.deleteLogFile(matchingLogFile[i]);
				}
				if (insertEntriesIfNotDuplicate(silentSMSEntries, nwType,
						nwOperator, Event.INCOMING_SILENT_SMS,
						System.currentTimeMillis())) {
					setSilentSMSNotification();
				}
				insertEntriesIfNotDuplicate(profileParams, nwType, nwOperator,
						Event.PROFILE_PARAMS, System.currentTimeMillis());

				// TODO uncomment it
				//Utils.deleteLogFile(matchingLogFile[i]);
				// TODO comment it
				//Utils.mvLogFile(matchingLogFile[i]);
				
				endTime = System.currentTimeMillis();
				Log.e(LOG_TAG, "Time taken to extract code info "
						+ (endTime - startTime) / 1000 + " Seconds.");

				filteredByteSeq = null;
				silentSMSEntries = null;
				profileParams = null;
			}
			matchingLogFile = null;
		}
	}

	private EventDetails getEventDetails() {
		EventDetails eventDetails = sDBHelper.getOldestUnconsumedEvent();
		if (eventDetails == null) {
			Application application = ((Application) getApplication());
			NetworkType nwType = application.getNwType();
			String nwOperator = application.getNwOperator();
			eventDetails = new EventDetails(Event.NONE, nwType, nwOperator);
		}
		return eventDetails;
	}

	private boolean insertEntriesIfNotDuplicate(List<Packet> packets,
			NetworkType nwType, String nwOperator, Event event,
			long eventReportedAt) {
		boolean atleastOneEntryIsAdded = false;
		if (packets != null && packets.size() > 0) {
			long logEntryUid = sDBHelper.insertLogEntry(nwType, nwOperator,
					event, eventReportedAt);
			for (Packet packet : packets) {
				// Check if the log entry to be added is already present.
				// Silent SMS has only one type of code entry, so if it is
				// already present don't create an log entry.
				boolean isDuplicate = sDBHelper.isPacketAlreadyInserted(
						packet.getPacketTypeId(), packet.getHexCode());
				if (!isDuplicate) {
					long packetUid = sDBHelper.insertPacket(logEntryUid,
							eventReportedAt, packet.getPacketTypeId(),
							packet.getHexCode(), isDuplicate);
					for (PacketAttribute packetAttribute : packet
							.getPacketAttributes()) {
						sDBHelper.insertPacketAttribute(packetUid,
								eventReportedAt,
								packetAttribute.getPacketAttrTypeId(),
								packetAttribute.getHexCode(),
								packetAttribute.getDisplayText());
						atleastOneEntryIsAdded = true;
					}
				} else {
					/*
					 * Log.e(LOG_TAG, "Code entry already exists in database." +
					 * codeEntry.getKind() + ", " + codeEntry.getHexCode());
					 */
				}
			}
			// If no entry is added then delete the log entry.
			if (!atleastOneEntryIsAdded) {
				sDBHelper.deleteLogEntry(logEntryUid);
			}
		}
		return atleastOneEntryIsAdded;
	}

	private void insertEntriesIntoDB(List<Packet> packets, NetworkType nwType,
			String nwOperator, Event event, long eventReportedAt) {
		if (packets != null && packets.size() > 0) {
			long logEntryUid = sDBHelper.insertLogEntry(nwType, nwOperator,
					event, eventReportedAt);
			for (Packet packet : packets) {
				// Check if the log entry to be added is already present.
				boolean isDuplicate = sDBHelper.isPacketAlreadyInserted(
						packet.getPacketTypeId(), packet.getHexCode());
				//if (isDuplicate) {
					/*
					 * Log.e(LOG_TAG, "Code entry already exists in database." +
					 * logEntryUid + ", " + codeEntry.getKind() + ", " +
					 * codeEntry.getHexCode());
					 */
				//}
				long codeEntryUid = sDBHelper.insertPacket(logEntryUid,
						eventReportedAt, packet.getPacketTypeId(),
						packet.getHexCode(), isDuplicate);
				for (PacketAttribute packetAttribute : packet
						.getPacketAttributes()) {
					sDBHelper.insertPacketAttribute(codeEntryUid,
							eventReportedAt,
							packetAttribute.getPacketAttrTypeId(),
							packetAttribute.getHexCode(),
							packetAttribute.getDisplayText());
				}
			}
		}
	}

	private void beginProfileParamComparison(List<Packet> packets) {
		if (packets != null && packets.size() > 0) {
			List<PacketAttribute> packetAttrs = new ArrayList<PacketAttribute>();
			for (Packet packet : packets) {
				packetAttrs.addAll(packet.getPacketAttributes());
			}
			new ProfileParamsComparisonTask(getApplicationContext())
					.execute(packetAttrs
							.toArray(new PacketAttribute[packetAttrs.size()]));
		}
	}

	private static final String tickerText = "Silent SMS has been received.";
	private static final String contentTitle = "Silent SMS Notification";
	private static final String contentText = "Alert- Silent SMS has been received. It seems suspicious activity based on type of SMS.";

	// Notification Action Elements
	private Intent sNotificationIntent;
	private PendingIntent sContentIntent;

	private long[] sVibratePattern = { 0, 200, 200, 300 };

	private void setSilentSMSNotification() {
		Notification.Builder notificationBuilder = new Notification.Builder(
				getApplicationContext()).setTicker(tickerText)
				.setSmallIcon(R.drawable.icon_in_white).setAutoCancel(true)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(sContentIntent).setVibrate(sVibratePattern)
				.setStyle(new Notification.BigTextStyle().bigText(contentText));

		// Pass the Notification to the NotificationManager:
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MY_NOTIFICATION_ID,
				notificationBuilder.build());
	}
}