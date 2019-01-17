package com.app.easycooking;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.database.DatabaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class RefrigeratorActivity extends Activity{
	private DatabaseActivity myDB = new DatabaseActivity(this);
	private ArrayList<HashMap<String, String>> refrigeratorList;
	private EditText eTxtName;
	private Button btSearch;
	private Button btAddItem;
	private ListView listViewItem;
	private DecimalFormat df = new DecimalFormat("#,###,###.##");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refrigerator);

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		eTxtName = (EditText)findViewById(R.id.editTextName);
		btSearch  = (Button)findViewById(R.id.btnSearch);
		btAddItem = (Button)findViewById(R.id.btnAddRecipe);
		listViewItem = (ListView)findViewById(R.id.listViewItems);
		
		OnLoadDataListView();

		btSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OnLoadDataListView();
			}
		});
		btAddItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogAddNew();
			}
		});

		listViewItem.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String item_id = refrigeratorList.get(position).get("id");
				String item_name = refrigeratorList.get(position).get("name");
				String item_amount = refrigeratorList.get(position).get("amount");
				String item_unit = refrigeratorList.get(position).get("unit");
				DialogAddCut(item_id, item_name, item_amount, item_unit);
			}
		});
/*		listViewItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						final String item_id = refrigeratorList.get(position).get("id");
						final String item_name = refrigeratorList.get(position).get("name");
						builder.setTitle("Do you want to delete?");
						builder.setMessage("Name : " + item_name)
								.setCancelable(false)
								.setPositiveButton("Confirm",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												myDB.DeleteItem(item_id);
												dialog.dismiss();
												OnLoadDataListView();
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
						return false;
					}
				});*/
	}
	
	protected void OnLoadDataListView() {
		//refrigeratorList.clear();
		refrigeratorList = myDB.SearchItems(eTxtName.getText().toString().trim());
		if(refrigeratorList.size() > 0){
			SimpleAdapter sAdapIems;
			sAdapIems = new SimpleAdapter(RefrigeratorActivity.this,
					refrigeratorList, R.layout.activity_column_items,
					new String[] { "name", "amount", "unit" },
					new int[] { R.id.ColName, R.id.ColAmount, R.id.ColUnit });
			listViewItem.setAdapter(sAdapIems);
			registerForContextMenu(listViewItem);
		}
	}
	
	private void DialogAddCut(final String item_id, String item_name, String item_amount, String item_unit) {
		View checkBoxView = View.inflate(this, R.layout.dialog_add_cut, null);
		final EditText edittxtBalance = (EditText)checkBoxView.findViewById(R.id.editTextBalance);
		final EditText edittxtAmount = (EditText)checkBoxView.findViewById(R.id.editTextAmount);
		final EditText edittxtUnit = (EditText)checkBoxView.findViewById(R.id.editTextUnit);
		Button btAdd = (Button)checkBoxView.findViewById(R.id.btnAddAmount);
		Button btCut = (Button)checkBoxView.findViewById(R.id.btnCutAmount);
		edittxtBalance.setText(item_amount);
		edittxtAmount.setText("0");
		edittxtUnit.setText(item_unit);
		btAdd.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Double amount = 0.00;
				String strAmount = edittxtAmount.getText().toString().trim().replace(",", "");
				if ("".equals(strAmount)) {
					strAmount = "0";
				}
				amount = Double.parseDouble(strAmount);
				amount += 1;
				edittxtAmount.setText(df.format(amount).toString());
			}
		});
		btCut.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Double amount = 0.00;
				String strAmount = edittxtAmount.getText().toString().trim().replace(",", "");
				if ("".equals(strAmount)) {
					strAmount = "0"; 				
				}
				amount = Double.parseDouble(strAmount);
				amount -= 1;
				edittxtAmount.setText(df.format(amount).toString());
			}
		});
		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Bahan Dapur");
		builderInOut.setMessage(item_name)
				.setView(checkBoxView)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//Double amount = 0.00;
						String strBalance = edittxtBalance.getText().toString().trim().replace(",", "");
						String strAmount = edittxtAmount.getText().toString().trim().replace(",", "");
						String strUnit = edittxtUnit.getText()
								.toString().trim();
						if ("".equals(strBalance)) {
							strBalance = "0.00";
						}
						if ("".equals(strAmount)) {
							strAmount = "0.00";
						}
						if ("".equals(strUnit)) {
							strUnit = "Unit";
						}
						Double totalAmount = Double.parseDouble(strBalance.trim()) + Double.parseDouble(strAmount.trim());
						//amount = Double.parseDouble(strAmount);
						String message = myDB.UpdateItem(item_id, totalAmount.toString(), strUnit);
						MessageStatus(message);
						if ("Berhasil".equals(message)) {
							edittxtBalance.setText(df.format(totalAmount).toString());
							edittxtAmount.setText("0");
						}
						OnLoadDataListView();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();
	}
	
	private void DialogAddNew() {
		View checkBoxView = View.inflate(this, R.layout.dialog_add_item, null);
		final EditText eItemAmount = (EditText)checkBoxView.findViewById(R.id.editItemAmount);
		final EditText eItemName = (EditText)checkBoxView.findViewById(R.id.editRecipeName);
		final EditText eItemUnit = (EditText)checkBoxView.findViewById(R.id.editItemUnit);
		Button btAddNewItem = (Button)checkBoxView.findViewById(R.id.btnAddItemToRefriger);
		
		btAddNewItem.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String strName = eItemName.getText().toString().trim();
				String strAmount = eItemAmount.getText().toString().trim();
				String strUnit = eItemUnit.getText().toString().trim();
				if ("".equals(strName)) {
					strName = "No name"; 				
				}
				if ("".equals(strAmount)) {
					strAmount = "0.00"; 
				}
				if ("".equals(strUnit)) {
					strUnit = "Unit"; 
				}		
				String message =  myDB.AddNewItem(strName, strAmount, strUnit);
				MessageStatus(message);
				if("Bahan Berhasil Ditambah".equals(message)){
					eItemName.setText("");
					eItemAmount.setText("");
					eItemUnit.setText("");
				}

			}
		});
		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Tambah Bahan Di dapur");
		builderInOut.setMessage("Isi Data dengan lengkap !")
				.setView(checkBoxView)
				.setCancelable(false)
				.setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						OnLoadDataListView();
						dialog.cancel();
					}
				})
				/*.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})*/
				.show();
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
