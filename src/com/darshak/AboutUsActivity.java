package com.darshak;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class AboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		getMenuInflater().inflate(R.menu.about_us, menu);
		return true;
	}
	
	public void gotoHome(MenuItem menu) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
	}
}