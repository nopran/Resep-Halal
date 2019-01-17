package com.app.easycooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LogoutActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logout);
		
		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Are you Sure ?");
		builderInOut.setMessage("Kembali ke login")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(LogoutActivity.this, SignInActivity.class); 
						startActivity(i);
					}
				})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent i = new Intent(LogoutActivity.this, TabMain.class); 
								startActivity(i);
							}
						}).show();
	}
}
