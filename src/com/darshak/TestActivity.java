package com.darshak;

import java.lang.reflect.Method;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This activity is created to test the app without user intervention.
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */

public class TestActivity extends Activity {

	private static final String TAG = TestActivity.class.getSimpleName();

	private static final String PREF_NAME = "PREF_TEST_ACTIVITY";
	private static final String TEST_IS_RUNNING_KEY = "TEST_IS_RUNNING";

	private TextView sErrorTextView;
	private TextView sIntervalErrorTextView;

	private EditText sPhoneNumberEditText;
	private EditText sCountEditText;
	private EditText sTimeGapInMinuteEditText;

	// private Button sSendSMSButton;
	private Button sMakeCallButton;
	private Button sClearButton;
	private Button sIntervalMakeCallButton;
	private Button sIntervalClearButton;
	private Button sStopTest;

	private Spinner sIntervalSpinner;

	// private ImageView sWaitImageView;

	private String sPhoneNumber;
	private int sCount;

	private int sTimeGapInMinutes;
	private int sIntervalInHours;
	private long sContinueTill;

	private boolean sTestRunning;

	/*
	 * private MakeCallsTask sMakeCallsTask; private IntervalMakeCallsTasks
	 * sIntervalMakeCalls;
	 */

	private SharedPreferences sSharedPreferences;

	private boolean sIsTaskCancelled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		initializeViewElements();
		// initializeAsyncTasks();

		// initialize shared preference
		sSharedPreferences = getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(TEST_IS_RUNNING_KEY, sTestRunning);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		sTestRunning = savedInstanceState.getBoolean(TEST_IS_RUNNING_KEY);
		if (sTestRunning) {
			disableButtons();
		} else {
			enableButtons();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		restoreTestState();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Editor editor = sSharedPreferences.edit();
		editor.putBoolean(TEST_IS_RUNNING_KEY, sTestRunning);
		editor.commit();
		editor.apply();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		restoreTestState();
	}

	private void restoreTestState() {
		sTestRunning = sSharedPreferences
				.getBoolean(TEST_IS_RUNNING_KEY, false);
		if (sTestRunning) {
			disableButtons();
			sErrorTextView.setText("Test is running");
			sIntervalErrorTextView.setText("Test is running");
		} else {
			enableButtons();
		}
	}

	/*
	 * private void initializeAsyncTasks() { sMakeCallsTask = new
	 * MakeCallsTask(); sIntervalMakeCalls = new IntervalMakeCallsTasks(); }
	 */

	private void initializeViewElements() {
		sErrorTextView = (TextView) findViewById(R.id.errorTextView);
		sIntervalErrorTextView = (TextView) findViewById(R.id.intervalErrorTextView);

		sPhoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
		sCountEditText = (EditText) findViewById(R.id.callOrSMSCountEditText);
		sTimeGapInMinuteEditText = (EditText) findViewById(R.id.timeGapEditText);

		// sSendSMSButton = (Button) findViewById(R.id.sendSMSButton);
		sMakeCallButton = (Button) findViewById(R.id.makeCallButton);
		sClearButton = (Button) findViewById(R.id.clearButton);
		sIntervalMakeCallButton = (Button) findViewById(R.id.intervalMakeCallButton);
		sIntervalClearButton = (Button) findViewById(R.id.intervalClearButton);
		sStopTest = (Button) findViewById(R.id.stopTestButton);

		sIntervalSpinner = (Spinner) findViewById(R.id.intervalSpinner);
		initializeIntervalSpinner();

		// sWaitImageView = (ImageView) findViewById(R.id.waitImageView);
	}

	private void initializeIntervalSpinner() {
		String[] intervalOptions = new String[] { "1 hour", "2 hours",
				"3 hours", "4 hours", "5 hours", "6 hours" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, intervalOptions);
		sIntervalSpinner.setAdapter(adapter);
	}

	public void gotoHome(MenuItem menu) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
	}

	public void sendSMS(View view) {
		clearErrorMessage();
		if (fetchUserInput()) {
			new SendSMSsTask().execute(sCount);
		}
	}

	public void clear(View view) {
		clearErrorMessage();
		sPhoneNumberEditText.setText("");
		sCountEditText.setText("");
	}

	public void makeCall(View view) {
		clearErrorMessage();
		if (fetchUserInput()) {
			sIsTaskCancelled = false;
			new MakeCallsTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, sCount);
		}
	}

	public void intervalMakeCall(View view) {
		clearErrorMessage();
		if (fetchIntervalUserInput()) {
			sIsTaskCancelled = false;
			new IntervalMakeCallsTasks().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, 0);
		}
	}

	public void intervalClear(View view) {
		clearErrorMessage();
		sTimeGapInMinuteEditText.setText("");
		sIntervalSpinner.setSelected(false);
	}

	public void stopTest(View view) {
		// sMakeCallsTask.cancel(false);
		// sIntervalMakeCalls.cancel(false);
		sStopTest.setEnabled(false);
		sIsTaskCancelled = true;
	}

	/*
	 * private void setWaitImageVisibility(boolean visible) {
	 * 
	 * if (visible) { sWaitImageView.setVisibility(View.VISIBLE); } else {
	 * sWaitImageView.setVisibility(View.INVISIBLE); }
	 * 
	 * }
	 */

	private void clearErrorMessage() {
		sErrorTextView.setText("");
		sIntervalErrorTextView.setText("");
	}

	private void disableButtons() {
		// sSendSMSButton.setEnabled(false);
		sMakeCallButton.setEnabled(false);
		sClearButton.setEnabled(false);

		sIntervalMakeCallButton.setEnabled(false);
		sIntervalClearButton.setEnabled(false);
	}

	private void enableButtons() {
		// sSendSMSButton.setEnabled(true);
		sMakeCallButton.setEnabled(true);
		sClearButton.setEnabled(true);

		sIntervalMakeCallButton.setEnabled(true);
		sIntervalClearButton.setEnabled(true);

		sStopTest.setEnabled(true);
	}

	private boolean fetchUserInput() {
		sPhoneNumber = sPhoneNumberEditText.getText().toString();
		if (sPhoneNumber == null || sPhoneNumber.length() == 0) {
			sErrorTextView.setText("Phone number should not be empty.");
			return false;
		}
		try {
			sCount = Integer.parseInt(sCountEditText.getText().toString());
			if (sCount <= 0 || sCount > 10) {
				sErrorTextView
						.setText("Number of SMSs and Calls to be made should be between 0 and 10.");
				return false;
			}
		} catch (NumberFormatException nfe) {
			sErrorTextView
					.setText("Number of SMSs and Calls to be made should be between 0 and 10.");
			return false;
		}

		Log.e(TAG, "Phone number :: " + sPhoneNumber);
		Log.e(TAG, "Number of SMSs and Calls to be made is :: " + sCount);
		return true;
	}

	private boolean fetchIntervalUserInput() {
		sPhoneNumber = sPhoneNumberEditText.getText().toString();
		if (sPhoneNumber == null || sPhoneNumber.length() == 0) {
			sIntervalErrorTextView.setText("Phone number should not be empty.");
			return false;
		}

		try {
			sTimeGapInMinutes = Integer.parseInt(sTimeGapInMinuteEditText
					.getText().toString());
			if (sTimeGapInMinutes <= 0 || sTimeGapInMinutes > 60) {
				sIntervalErrorTextView
						.setText("Time gap between calls should be between 1 and 60 minutes.");
				return false;
			}
		} catch (NumberFormatException nfe) {
			sIntervalErrorTextView
					.setText("Time gap between calls should be between 1 and 60 minutes.");
			return false;
		}

		try {
			sIntervalInHours = sIntervalSpinner.getSelectedItemPosition();
			if (AdapterView.INVALID_POSITION == sIntervalInHours) {
				sIntervalErrorTextView
						.setText("Please select the time interval.");
				return false;
			}
		} catch (NumberFormatException nfe) {
			sIntervalErrorTextView.setText("Please select the time interval.");
			return false;
		}
		sIntervalInHours = sIntervalInHours + 1;
		sContinueTill = System.currentTimeMillis()
				+ (sIntervalInHours * 60 * 60 * 1000);

		Log.e(TAG, "Time gap between calls :: " + sTimeGapInMinutes
				+ " minutes");
		Log.e(TAG, "Time interval of the test  :: " + sIntervalInHours
				+ " hours");
		Log.e(TAG, "Continue test till " + new Date(sContinueTill));
		return true;
	}

	private static final int WAIT_BETWEEN_RADIO_EVENT = 15 * 1000;

	private void waitForAWhile() {
		try {
			Thread.sleep(WAIT_BETWEEN_RADIO_EVENT);
		} catch (InterruptedException ie) {
			// ignore
		}
	}

	private void waitForAWhile(int minutes) {
		try {
			Thread.sleep(minutes * 60 * 1000);
		} catch (InterruptedException ie) {
			// ignore
		}
	}

	private abstract class AbstractTask extends
			AsyncTask<Integer, Integer, Integer> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			disableButtons();
			// setWaitImageVisibility(true);
			sTestRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Log.e(TAG, "OnPostExecute is called ::");
			enableButtons();
			sTestRunning = false;
		}

		/*
		 * @Override protected void onProgressUpdate(Integer... result) {
		 * super.onProgressUpdate(result); // setWaitImageVisibility(false);
		 * enableButtons(); sTestRunning = false; }
		 */

		/*
		 * @Override protected void onCancelled() { super.onCancelled();
		 * clearErrorMessage(); enableButtons(); sTestRunning = false; }
		 */
	}

	private abstract class AbstractMakeCallTask extends AbstractTask {

		protected void makeCall() {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + sPhoneNumber));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(intent);
		}

		// http://stackoverflow.com/questions/15481524/how-to-programatically-answer-end-a-call-in-android-4-1
		protected void disconnectCall() {
			try {

				String serviceManagerName = "android.os.ServiceManager";
				String serviceManagerNativeName = "android.os.ServiceManagerNative";
				String telephonyName = "com.android.internal.telephony.ITelephony";

				Class<?> telephonyClass;
				Class<?> telephonyStubClass;
				Class<?> serviceManagerClass;
				Class<?> serviceManagerNativeClass;

				Method telephonyEndCall;

				Object telephonyObject;
				Object serviceManagerObject;

				telephonyClass = Class.forName(telephonyName);
				telephonyStubClass = telephonyClass.getClasses()[0];
				serviceManagerClass = Class.forName(serviceManagerName);
				serviceManagerNativeClass = Class
						.forName(serviceManagerNativeName);

				Method getService = serviceManagerClass.getMethod("getService",
						String.class);

				Method tempInterfaceMethod = serviceManagerNativeClass
						.getMethod("asInterface", IBinder.class);

				Binder tmpBinder = new Binder();
				tmpBinder.attachInterface(null, "fake");

				serviceManagerObject = tempInterfaceMethod.invoke(null,
						tmpBinder);
				IBinder retbinder = (IBinder) getService.invoke(
						serviceManagerObject, "phone");
				Method serviceMethod = telephonyStubClass.getMethod(
						"asInterface", IBinder.class);

				telephonyObject = serviceMethod.invoke(null, retbinder);
				telephonyEndCall = telephonyClass.getMethod("endCall");

				telephonyEndCall.invoke(telephonyObject);

			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG,
						"FATAL ERROR: could not connect to telephony subsystem");
				Log.e(TAG, "Exception object: " + e);
			}
		}
	}

	private class MakeCallsTask extends AbstractMakeCallTask {

		@Override
		protected Integer doInBackground(Integer... params) {
			for (int i = 0; i < sCount; i++) {
				if (/* isCancelled() || */sIsTaskCancelled) {
					Log.e(TAG, "Task is being cancelled");
					break;
				}
				makeCall();
				waitForAWhile();
				disconnectCall();
				waitForAWhile();
			}
			// publishProgress(0);
			return 0;
		}
	}

	public class IntervalMakeCallsTasks extends AbstractMakeCallTask {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			sIntervalErrorTextView.setText("Test will run till "
					+ new Date(sContinueTill));
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			clearErrorMessage();
		}

		protected Integer doInBackground(Integer... params) {
			while (System.currentTimeMillis() < sContinueTill) {
				if (/* isCancelled() || */sIsTaskCancelled) {
					Log.e(TAG, "Task is being cancelled");
					break;
				}
				waitForAWhile(sTimeGapInMinutes);
				makeCall();
				waitForAWhile();
				disconnectCall();
			}
			// publishProgress(0);
			return 0;
		}
	}

	private class SendSMSsTask extends AbstractTask {

		@Override
		protected Integer doInBackground(Integer... params) {
			for (int i = 0; i < sCount; i++) {
				sendSMS(i);
				waitForAWhile();
			}
			publishProgress(0);
			return null;
		}

		/*
		 * private void sendSMS(int index) { SmsManager sms =
		 * SmsManager.getDefault(); sms.sendTextMessage(sPhoneNumber, null,
		 * "Test" + index, null, null); }
		 */

		private void sendSMS(int index) {
			Uri uri = Uri.parse("smsto:" + sPhoneNumber);
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra("sms_body", "Test" + index);
			startActivity(intent);
		}
	}
}