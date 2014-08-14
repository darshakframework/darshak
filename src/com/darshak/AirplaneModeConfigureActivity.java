package com.darshak;

import static android.provider.Settings.System.putInt;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class AirplaneModeConfigureActivity extends Activity {

	private Button activateButton;
	private Button deActivateButton;
	private TextView resultTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_airplane_mode_configure);

		activateButton = (Button) findViewById(R.id.activatePlaneModeButton);
		deActivateButton = (Button) findViewById(R.id.deActivatePlaneModeButton);
		resultTextView = (TextView) findViewById(R.id.planeModeActivationResultTextView);

		if (isAirplaneModeOn()) {
			activateButton.setEnabled(false);
			deActivateButton.setEnabled(true);
		} else {
			activateButton.setEnabled(true);
			deActivateButton.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.airplane_mode_configure, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gotoHome:
			gotoHome();
			break;
		default:
			break;
		}
		return true;
	}

	public void activate(View view) {
		setAirplaneMode(1);

		activateButton.setEnabled(false);
		deActivateButton.setEnabled(true);
		resultTextView.setText("Airplane mode activation successfull.");
	}

	public void deActivate(View view) {
		setAirplaneMode(0);
		activateButton.setEnabled(true);
		deActivateButton.setEnabled(false);
		resultTextView.setText("Airplane mode deactivation successfull.");
	}

	public void gotoHome() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
	}

	private boolean isAirplaneModeOn() {
		return Settings.System.getInt(getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	}

	private void setAirplaneMode(int mode) {
		putInt(getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, mode);

		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		sendBroadcast(intent);
	}
}