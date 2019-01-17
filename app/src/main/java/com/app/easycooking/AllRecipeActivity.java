package com.app.easycooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.database.DatabaseActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AllRecipeActivity extends Activity{
	private String selectedImagePath;
	private static final int SELECT_PICTURE = 1;
	private DatabaseActivity myDB = new DatabaseActivity(this);
	private ArrayList<HashMap<String, String>> recipeList;
	private ArrayList<HashMap<String, String>> specificList;
	private EditText eTxtName;
	private Button btAddRecipe;
	private Button btSearch;
	private ListView listViewRecipe;
	private DecimalFormat df = new DecimalFormat("#,###,###.##");
	private ImageView e_imgViewRecipe;
	private ImageView a_imgViewRecipe;
	private String e_recipe_id;
/*	private String specific_add_id = "";
	private String specific_edit_id = "";*/
	private ListView a_ListView;
	private ArrayList<HashMap<String, String>> a_refrigeratorList;
	private ArrayList<HashMap<String, String>> refrigeratorList;
	private ListView listViewItem;
	private CheckBox ckNormal;
	private CheckBox ckVegetarian;
	private CheckBox ckLessFat;

	private String strSpecific = "";
	private String[] arraySpecificId;
	private String[] arraySpecific;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_recipe);
		eTxtName = (EditText)findViewById(R.id.editTextName);
		btAddRecipe = (Button)findViewById(R.id.btnAddRecipe);
		btSearch = (Button)findViewById(R.id.btnSearch);
		listViewRecipe = (ListView)findViewById(R.id.listViewRecipes);

		OnLoadDataListView();

		btAddRecipe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogAddNew();
			}
		});
		btSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OnLoadDataListView();
			}
		});


		listViewRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String recipe_id = recipeList.get(position).get("id");
				String recipe_name = recipeList.get(position).get("name");
				String recipe_specific = recipeList.get(position).get("specific_name");
				String recipe_calories = recipeList.get(position).get("calories");
				String recipe_how_to_cook = recipeList.get(position).get("how_to_cook");
				String specific_id = recipeList.get(position).get("specific_id");
				DialogEditRecipe(recipe_id, recipe_name, recipe_specific, recipe_calories, recipe_how_to_cook, specific_id);
			}
		});
		listViewRecipe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
										   View view, int position, long id) {
				String recipe_id = recipeList.get(position).get("id");
				String recipe_name = recipeList.get(position).get("name");
				DialogAddItems(recipe_id, recipe_name);
				return false;
			}
		});
	}

	private void OnLoadDataListView() {
		//refrigeratorList.clear();
		recipeList = myDB.SearchRecipes(eTxtName.getText().toString().trim());

			SimpleAdapter sAdapRecipes;
			sAdapRecipes = new SimpleAdapter(AllRecipeActivity.this,
					recipeList, R.layout.activity_column_recipe,
					new String[] { "name", "calories", "specific_name" },
					new int[] { R.id.ColName, R.id.ColCalories, R.id.ColSpecific });
			listViewRecipe.setAdapter(sAdapRecipes);
			registerForContextMenu(listViewRecipe);

	}

	private void DialogAddItems(final String recipe_id, String recipe_name) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View checkBoxView = View.inflate(this, R.layout.dialog_recipe_add_items, null);
		final TextView a_TxtRecipe = (TextView)checkBoxView.findViewById(R.id.a_txtRecipe);
		a_imgViewRecipe = (ImageView)checkBoxView.findViewById(R.id.a_imageView);
		a_ListView = (ListView)checkBoxView.findViewById(R.id.a_listView);
		Button a_BtnAddItem = (Button)checkBoxView.findViewById(R.id.a_btnAddItem);
		Button a_BtnAddNew = (Button)checkBoxView.findViewById(R.id.a_btnAddNew);

		a_TxtRecipe.setText(recipe_name);
		LoadItems(recipe_id);
		readImgFromDB(recipe_id, "a");

		a_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String item_id = a_refrigeratorList.get(position).get("id");
				String item_name = a_refrigeratorList.get(position).get("name");
				String item_amount = a_refrigeratorList.get(position).get("amount");
				String item_unit = a_refrigeratorList.get(position).get("unit");
				DialogAddCut(recipe_id, item_id, item_name, item_amount, item_unit);
			}
		});
		a_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
										   View view, int position, long id) {
				final String item_id = a_refrigeratorList.get(position).get("id");
				final String item_name = a_refrigeratorList.get(position).get("name");
				builder.setTitle("Do you want to delete?");
				builder.setMessage("Name : " + item_name)
						.setCancelable(false)
						.setPositiveButton("Confirm",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int id) {
										myDB.DeleteItemInRecipe(recipe_id, item_id);
										dialog.dismiss();
										LoadItems(recipe_id);
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
		});

		a_BtnAddItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				YouRefrigerator(recipe_id);
			}
		});
		a_BtnAddNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddNewItem(recipe_id);
			}
		});

		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Add ingredients in refrigerator");
		builderInOut.setMessage("")
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

	private void AddNewItem(final String recipe_id) {
		View checkBoxView = View.inflate(this, R.layout.dialog_add_item, null);
		final EditText eItemAmount = (EditText)checkBoxView.findViewById(R.id.editItemAmount);
		final EditText eItemName = (EditText)checkBoxView.findViewById(R.id.editRecipeName);
		final EditText eItemUnit = (EditText)checkBoxView.findViewById(R.id.editItemUnit);
		Button btAddNewItem = (Button)checkBoxView.findViewById(R.id.btnAddItemToRefriger);

		btAddNewItem.setOnClickListener(new View.OnClickListener() {
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
				String message = myDB.AddNewItem(strName, "0", strUnit);
				if("Add ingredients completed".equals(message)){
					String idOfIngredientsName = myDB.IdOfIngredientsName(strName);
					message = myDB.InsertRecipeAddItems(recipe_id, idOfIngredientsName, strAmount);

					eItemName.setText("");
					eItemAmount.setText("");
					eItemUnit.setText("");

					LoadItems(recipe_id);
				}
				MessageStatus(message);
			}
		});
		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Add Item to refrigerator");
		builderInOut.setMessage("Please input value")
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

	private void DialogAddCut(final String recipe_id, final String item_id, String item_name, String item_amount, String item_unit) {
		View checkBoxView = View.inflate(this, R.layout.dialog_add_cut, null);
		final EditText edittxtBalance = (EditText)checkBoxView.findViewById(R.id.editTextBalance);
		final EditText edittxtAmount = (EditText)checkBoxView.findViewById(R.id.editTextAmount);
		final EditText edittxtUnit = (EditText)checkBoxView.findViewById(R.id.editTextUnit);
		Button btAdd = (Button)checkBoxView.findViewById(R.id.btnAddAmount);
		Button btCut = (Button)checkBoxView.findViewById(R.id.btnCutAmount);
		edittxtUnit.setEnabled(false);
		edittxtBalance.setText(item_amount);
		edittxtAmount.setText("0");
		edittxtUnit.setText(item_unit);
		btAdd.setOnClickListener(new View.OnClickListener() {
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
		btCut.setOnClickListener(new View.OnClickListener() {
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
		builderInOut.setTitle("Items Management");
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
						//amount = Double.parseDouble(strAmount);
						myDB.UpdateItemInRecipe(recipe_id, item_id, strAmount);
						Double totalAmount = Double.parseDouble(strBalance.trim()) + Double.parseDouble(strAmount.trim());
						String message = myDB.UpdateItem(item_id, totalAmount.toString(), strUnit);
						MessageStatus(message);
						if ("Completed".equals(message)) {
							edittxtBalance.setText(df.format(totalAmount).toString());
							edittxtAmount.setText("0");
						}
						LoadItems(recipe_id);
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();
	}

	private void YouRefrigerator(final String recipe_id) {
		View checkBoxView = View.inflate(this, R.layout.activity_refrigerator, null);
		Button btAddItem = (Button)checkBoxView.findViewById(R.id.btnAddRecipe);
		listViewItem = (ListView)checkBoxView.findViewById(R.id.listViewItems);
		final EditText a_txtName = (EditText)checkBoxView.findViewById(R.id.editTextName);
		Button a_BtnSearch = (Button)checkBoxView.findViewById(R.id.btnSearch);
		btAddItem.setVisibility(View.INVISIBLE);

		SearchRefrigerator(a_txtName.getText().toString().trim());

		listViewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item_id = refrigeratorList.get(position).get("id");
				String message = myDB.InsertRecipeAddItems(recipe_id, item_id, "0");
				MessageStatus(message);
				LoadItems(recipe_id);
			}
		});

		a_BtnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchRefrigerator(a_txtName.getText().toString().trim());
			}
		});

		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Add ingredients in refrigerator");
		builderInOut.setMessage("")
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

	private void SearchRefrigerator(String name) {
		refrigeratorList = myDB.SearchItems(name);
		SimpleAdapter sAdapAddItems;
		sAdapAddItems = new SimpleAdapter(AllRecipeActivity.this,
				refrigeratorList, R.layout.activity_column_items,
				new String[] { "name", "amount", "unit" },
				new int[] { R.id.ColName, R.id.ColAmount, R.id.ColUnit });
		listViewItem.setAdapter(sAdapAddItems);
		registerForContextMenu(listViewItem);
	}

	private void LoadItems(String recipe_id) {
		a_refrigeratorList = myDB.SearchItemsInRecipe(recipe_id);
			SimpleAdapter adapItems = new SimpleAdapter(AllRecipeActivity.this,
					a_refrigeratorList, R.layout.activity_column_items,
					new String[] { "name", "amount", "unit" },
					new int[] { R.id.ColName, R.id.ColAmount, R.id.ColUnit });
			a_ListView.setAdapter(adapItems);
			registerForContextMenu(a_ListView);
	}

	private void DialogEditRecipe(final String recipe_id, final String recipe_name, String recipe_specific, String recipe_calories, String recipe_how_to_cook, String specific_id) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//String[] specific_array;
		View checkBoxView = View.inflate(this, R.layout.dialog_edit_recipe, null);
		e_imgViewRecipe = (ImageView)checkBoxView.findViewById(R.id.e_imageViewRecipe);
		//final Spinner e_spSpecific = (Spinner)checkBoxView.findViewById(R.id.e_spinnerSpecific);
		final EditText e_recipeName = (EditText)checkBoxView.findViewById(R.id.e_editRecipeName);
		final EditText e_calories = (EditText)checkBoxView.findViewById(R.id.e_editTextCalories);
		final EditText e_howToCook = (EditText)checkBoxView.findViewById(R.id.e_editTextHowToCook);
		final Button e_btnEdit = (Button)checkBoxView.findViewById(R.id.e_btnEditRecipe);
		final Button e_btnDelete = (Button)checkBoxView.findViewById(R.id.e_btnDeleteRecipe);
		ckNormal = (CheckBox) checkBoxView.findViewById(R.id.ckbNormal);
		ckVegetarian = (CheckBox) checkBoxView.findViewById(R.id.ckbVegetarian);
		ckLessFat = (CheckBox) checkBoxView.findViewById(R.id.ckbLessFat);


		SetSpecificFormID(specific_id);
		//Get Specific to specificList
/*		specificList = myDB.GetSpecific();
		Integer num = specificList.size();
		specific_array = new String[num];
		for (int i = 0; i < specificList.size(); i++) {
			specific_array[i] = specificList.get(i).get("name");
		}*/
		/*ArrayAdapter<String> dataAdapterActivity = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, specific_array);
		dataAdapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		e_spSpecific.setAdapter(dataAdapterActivity);
		int spinnerInt = dataAdapterActivity.getPosition(recipe_specific);
		e_spSpecific.setSelection(spinnerInt);
		e_spSpecific.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				e_spSpecific.setSelection(position);
				Integer p_id = e_spSpecific.getSelectedItemPosition();
				specific_edit_id = p_id.toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});*/

		Double e_caloriesDouble = 0.00;
		if(!"".equals(recipe_calories)) {
			recipe_calories = recipe_calories.replace("Kalori", "");
			recipe_calories = recipe_calories.replace("=", "");
			recipe_calories = recipe_calories.replace(",", "");
			e_caloriesDouble = Double.parseDouble(recipe_calories.trim());
		}
		e_recipeName.setText(recipe_name);
		e_calories.setText(df.format(e_caloriesDouble).toString());
		e_howToCook.setText(recipe_how_to_cook);
		e_recipe_id = recipe_id;
		readImgFromDB(e_recipe_id, "e");
		e_btnEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strName = e_recipeName.getText().toString().trim();
				String strCalories = e_calories.getText().toString().trim();
				String strHowToCook = e_howToCook.getText().toString();
				String statusMsg;

				GetSpecificToArray();

				if (!"".equals(strName) && !"".equals(strCalories) && arraySpecific.length > 0) {
					statusMsg = myDB.EditRecipe(e_recipe_id, strName.trim(), strCalories.replace(",", "").trim(), arraySpecific, strHowToCook);
				} else {
					statusMsg = "Please complete the following information!";
				}
				MessageStatus(statusMsg);
			}
		});
		e_btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.setTitle("Do you want to delete?");
				builder.setMessage("Recipe name : " + recipe_name)
						.setCancelable(false)
						.setPositiveButton("Confirm",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int id) {
										myDB.DeleteRecipe(recipe_id);
										e_recipeName.setText("");
										e_calories.setText("");
										e_howToCook.setText("");
										e_btnEdit.setEnabled(false);
										e_btnDelete.setEnabled(false);
										e_imgViewRecipe.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
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
			}
		});
		e_imgViewRecipe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						SELECT_PICTURE);
			}
		});
		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Edit Resep");
		builderInOut.setMessage("")
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

	private void DialogAddNew() {
		//String[] specific_array;
		View checkBoxView = View.inflate(this, R.layout.dialog_add_recipe, null);
		final EditText eRecipeName = (EditText)checkBoxView.findViewById(R.id.editRecipeName);
		final EditText eCalories = (EditText)checkBoxView.findViewById(R.id.editTextCalories);
		Button btAddNewRecipe = (Button)checkBoxView.findViewById(R.id.btnNewAddRecipe);
		ckNormal = (CheckBox) checkBoxView.findViewById(R.id.ckbNormal);
		ckVegetarian = (CheckBox) checkBoxView.findViewById(R.id.ckbVegetarian);
		ckLessFat = (CheckBox) checkBoxView.findViewById(R.id.ckbLessFat);



/*		final Spinner spSpecific = (Spinner)checkBoxView.findViewById(R.id.spinnerSpecific);*/
		//Get Specific to specificList
		/*specificList = myDB.GetSpecific();
		Integer num = specificList.size();
		specific_array = new String[num];
		for (int i = 0; i < specificList.size(); i++) {
			specific_array[i] = specificList.get(i).get("name");
		}*/
		/*ArrayAdapter<String> dataAdapterActivity = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, specific_array);
		dataAdapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSpecific.setAdapter(dataAdapterActivity);
		spSpecific.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				spSpecific.setSelection(position);
				Integer p_id = spSpecific.getSelectedItemPosition();
				specific_add_id = p_id.toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});*/
		//---------------------------------------------

		btAddNewRecipe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strName = eRecipeName.getText().toString().trim();
				String strCalories  = eCalories.getText().toString().trim();
				String statusMsg;

				GetSpecificToArray();

				if (!"".equals(strName) && !"".equals(strCalories) && arraySpecific.length > 0) {
					statusMsg = myDB.AddNewRecipe(strName.trim(), strCalories.trim(), arraySpecific);
					if("Resep Berhasil Ditambahkan".equals(statusMsg)){
						eRecipeName.setText("");
						eCalories.setText("");
					}
				}else{
					statusMsg = "Silahkan isi data dengan Lengkap !";
				}
				MessageStatus(statusMsg);
			}
		});
		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Add Resep");
		builderInOut.setMessage("Silahkan isi data dengan Lengkap !")
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

	private void SetSpecificFormID(String specific_id) {
		arraySpecificId = specific_id.trim().split(",");
		for (int i = 0; i < arraySpecificId.length; i++) {
			if("1".equals(arraySpecificId[i].toString())){
				ckVegetarian.setChecked(true);
			}else if("2".equals(arraySpecificId[i].toString())){
				ckLessFat.setChecked(true);
			}
		}
	}
	private void GetSpecificToArray() {
		strSpecific = "0";
		if (ckVegetarian.isChecked()) {
			strSpecific += ",1";
		}
		if (ckLessFat.isChecked()) {
			strSpecific += ",2";
		}
		arraySpecific = strSpecific.trim().split(",");
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) if (requestCode == SELECT_PICTURE) {
			Uri selectedImageUri = data.getData();
			selectedImagePath = getPath(selectedImageUri);
			System.out.println("Image Path : " + selectedImagePath);
			e_imgViewRecipe.setVisibility(View.VISIBLE);
			e_imgViewRecipe.setImageURI(selectedImageUri);

			saveInDB();
		}
	}

	private void saveInDB() {
		myDB.SaveInDB(selectedImagePath, e_recipe_id);
	}

	void readImgFromDB(String recipe_id, String fromDialog) {
		byte[] byteImageFood = null;
		byteImageFood = myDB.ReadImageFromDB(recipe_id);
		if (byteImageFood != null) {
			setImage(byteImageFood, fromDialog);
		}
	}

	void setImage(byte[] byteImageFood, String fromDialog) {
		if(byteImageFood != null) {
			if("e".equals(fromDialog)){
				e_imgViewRecipe.setImageBitmap(BitmapFactory.decodeByteArray(byteImageFood, 0, byteImageFood.length));
			}else if("a".equals(fromDialog)){
				a_imgViewRecipe.setImageBitmap(BitmapFactory.decodeByteArray(byteImageFood, 0, byteImageFood.length));
			}

		}
	}


	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
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
