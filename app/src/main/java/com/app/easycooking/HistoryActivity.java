package com.app.easycooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.database.DatabaseActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HistoryActivity extends Activity{
	private DatabaseActivity myDB = new DatabaseActivity(this);
	private ArrayList<HashMap<String, String>> recipeList;
	private DecimalFormat df = new DecimalFormat("#,###,###.##");
	private SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
	private static String dateFrom = "";
	private static String dateTo = "";
	private Button btDateFrom;
	private Button btDateTo;
	private EditText editDateFrom;
	private EditText editDateTo;
	private int mYear, mMonth, mDay;
	private Button btSearch;
	private ListView listViewHistory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		btDateFrom = (Button) findViewById(R.id.btnDateFrom);
		btDateTo = (Button) findViewById(R.id.btnDateTo);
		editDateFrom = (EditText) findViewById(R.id.editTextDateFrom);
		editDateTo = (EditText) findViewById(R.id.editTextDateTo);
		btSearch = (Button)findViewById(R.id.btnSearch);
		listViewHistory = (ListView)findViewById(R.id.listViewHistory);

		// Perform action on click
		btDateFrom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dateClick("DateFrom");
			}
		});
		btDateTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dateClick("DateTo");
			}
		});
		btSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("".equals(dateFrom) || "".equals(dateTo)) {
					MessageStatus("Please specify start date to end date");
				} else {
					SearchData(dateFrom, dateTo);
				}
			}
		});
	}

	private void SearchData(String dateFrom, String dateTo) {
		//refrigeratorList.clear();
		recipeList = myDB.SearchHistory(dateFrom + " 01:00:00", dateTo + " 23:59:59");

		SimpleAdapter sAdapRecipes;
		sAdapRecipes = new SimpleAdapter(HistoryActivity.this,
				recipeList, R.layout.activity_column_history,
				new String[] { "name", "calories", "date_time" },
				new int[] { R.id.ColName, R.id.ColCalories, R.id.ColDate });
		listViewHistory.setAdapter(sAdapRecipes);
		registerForContextMenu(listViewHistory);

	}

	public void dateClick(String v) {
		if ("DateFrom".equals(v)) {
			// Process to get Current Date
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);

			// Launch Date Picker Dialog
			DatePickerDialog dpd = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
											  int monthOfYear, int dayOfMonth) {

							monthOfYear = monthOfYear + 1;
							String strMonthOfYear = String.valueOf(monthOfYear);
							String strDayOfMonth = String.valueOf(dayOfMonth);
							if (monthOfYear < 10) {
								strMonthOfYear = "0" + strMonthOfYear;
							}
							if (dayOfMonth < 10) {
								strDayOfMonth = "0" + strDayOfMonth;
							}
							// Display Selected date in textbox
							editDateFrom.setText(strDayOfMonth + "/"
									+ strMonthOfYear + "/" + year);
							dateFrom = year + "-" + strMonthOfYear + "-"
									+ strDayOfMonth;

						}
					}, mYear, mMonth, mDay);
			dpd.show();
		} else if ("DateTo".equals(v)) {

			// Process to get Current Date
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);

			// Launch Date Picker Dialog
			DatePickerDialog dpd = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
											  int monthOfYear, int dayOfMonth) {

							monthOfYear = monthOfYear + 1;
							String strMonthOfYear = String.valueOf(monthOfYear);
							String strDayOfMonth = String.valueOf(dayOfMonth);
							if (monthOfYear < 10) {
								strMonthOfYear = "0" + strMonthOfYear;
							}
							if (dayOfMonth < 10) {
								strDayOfMonth = "0" + strDayOfMonth;
							}
							// Display Selected date in textbox
							editDateTo.setText(strDayOfMonth + "/"
									+ strMonthOfYear + "/" + year);
							dateTo = year + "-" + strMonthOfYear + "-"
									+ strDayOfMonth;

						}
					}, mYear, mMonth, mDay);
			dpd.show();
		}
	}

	private void MessageStatus(String message){
		final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
		viewDialog.setTitle("Status");
		viewDialog
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int id) {
								dialog.dismiss();
							}
						});
		AlertDialog alert = viewDialog.create();
		alert.show();
	}

}
