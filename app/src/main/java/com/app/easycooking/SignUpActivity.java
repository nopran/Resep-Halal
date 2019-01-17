package com.app.easycooking;

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

public class SignUpActivity extends Activity{
	private DatabaseActivity myDB = new DatabaseActivity(this);
	private EditText txtUser;
	private EditText txtPass;
	private EditText txtRePass;
	private Button btBack;
	private Button btSubmit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
		txtUser = (EditText)findViewById(R.id.editTextUser);
		txtPass = (EditText)findViewById(R.id.editTextPass);
		txtRePass = (EditText)findViewById(R.id.editTextRePass);
		btBack = (Button)findViewById(R.id.btnBack);
		btSubmit = (Button)findViewById(R.id.btnSubmit);
		
		btBack.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
				startActivity(i);
			}
		});
		btSubmit.setOnClickListener(new OnClickListener() {	
			String message = "";
			@Override
			public void onClick(View v) {
				String user = txtUser.getText().toString().trim();
				String pass = txtPass.getText().toString().trim();
				String rePass = txtRePass.getText().toString().trim();
				if("".equals(user)||"".equals(pass)||"".equals(rePass)){
					message = "Please fill in complete data!";
				}
				else if (!pass.equals(rePass)) {
					message = "Password not equals Re-Password!";
				}else {
					Boolean status = myDB.SignUp(user, pass);
					if(status){
						message = "Register is completed";
						txtUser.setText("");
						txtPass.setText("");
						txtRePass.setText("");
					}else {
						message = "Have you ever registered!";
					}					
				}
				viewDetail.setTitle("Status");
				viewDetail
						.setMessage(message)
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int id) {
										if ("Register is completed".equals(message)) {
											Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
											startActivity(i);
										}
										dialog.dismiss();
									}
								});
				AlertDialog alert = viewDetail.create();
				alert.show();
			}
		});
	}

}
