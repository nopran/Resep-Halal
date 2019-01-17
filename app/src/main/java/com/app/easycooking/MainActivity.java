package com.app.easycooking;

import com.database.DatabaseActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	private DatabaseActivity myDB = new DatabaseActivity(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myDB.openDatabase();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(MainActivity.this,
						SignInActivity.class);
				startActivity(intent);
				finish();
			}
		}, 3000);
	}

}
