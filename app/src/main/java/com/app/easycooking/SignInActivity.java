package com.app.easycooking;

import org.w3c.dom.Text;

import com.database.DatabaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class SignInActivity extends Activity{
	private DatabaseActivity myDB = new DatabaseActivity(this);
	private EditText txtUser;
	private EditText txtPass;
	private TextView txtRegister;
	private Button btSignIn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		
		final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
		txtUser = (EditText)findViewById(R.id.editTextUser);
		txtPass = (EditText)findViewById(R.id.editTextPass);
		txtRegister = (TextView)findViewById(R.id.txtRegister);
		btSignIn = (Button)findViewById(R.id.btnSignin);
		
		txtRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
				startActivity(i);
			}
		});
		btSignIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String user = txtUser.getText().toString().trim();
				String pass = txtPass.getText().toString().trim();
				Boolean isSignIn = myDB.SignIn(user, pass);
				if(isSignIn){
					Intent i = new Intent(SignInActivity.this, TabMain.class);
					startActivity(i);
				}else {
					viewDetail.setTitle("Status");
					viewDetail
							.setMessage("Incorrect Username or Password!")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int id) {
											dialog.dismiss();
										}
									});
					AlertDialog alert = viewDetail.create();
					alert.show();
				}
			}
		});
	}

}
