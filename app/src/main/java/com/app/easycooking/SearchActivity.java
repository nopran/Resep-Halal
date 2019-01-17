package com.app.easycooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.database.DatabaseActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class SearchActivity extends Activity{
	private EditText eTextName;
	private CheckBox ckNormal;
	private CheckBox ckVegetarian;
	private CheckBox ckLessFat;
	private CheckBox ckLessSugar;
	private CheckBox ckLessCarbohydrate;
	private CheckBox ckLessSalt;
	private Button btSearch;
	private ListView listVRecipes;
	private String searchSpecific = "";
	private DatabaseActivity myDB = new DatabaseActivity(this);
	private ArrayList<HashMap<String, String>> recipeList;
	private DecimalFormat df = new DecimalFormat("#,###,###.##");
	private ImageView imgViewRecipe;
	private ListView listView;
	private ArrayList<HashMap<String, String>> refrigeratorList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		eTextName = (EditText)findViewById(R.id.editTextName);
		ckNormal = (CheckBox) findViewById(R.id.ckbNormal);
		ckVegetarian = (CheckBox) findViewById(R.id.ckbVegetarian);
		ckLessFat = (CheckBox) findViewById(R.id.ckbLessFat);
		ckLessSugar = (CheckBox) findViewById(R.id.ckbLessSugar);
		ckLessCarbohydrate = (CheckBox) findViewById(R.id.ckbVegetarian);
		ckLessSalt = (CheckBox) findViewById(R.id.ckbLessSalt);
		btSearch = (Button) findViewById(R.id.btnSearch);
		listVRecipes = (ListView) findViewById(R.id.listViewRecipes);

		btSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchSpecific = "";
				if (ckVegetarian.isChecked()) {
					searchSpecific = " AND ( rs.specific_id = 1";
				}
				if (ckLessFat.isChecked()) {
					if (!"".equals(searchSpecific)) {
						searchSpecific += " OR rs.specific_id = 2";
					} else {
						searchSpecific = " AND ( rs.specific_id = 2";
					}
				}
				if (ckLessSugar.isChecked()) {
					if (!"".equals(searchSpecific)) {
						searchSpecific += " OR rs.specific_id = 3";
					} else {
						searchSpecific = " AND ( rs.specific_id = 3";
					}
				}
				if (ckLessCarbohydrate.isChecked()) {
					if (!"".equals(searchSpecific)) {
						searchSpecific += " OR rs.specific_id = 4";
					} else {
						searchSpecific = " AND ( rs.specific_id = 4";
					}
				}
				if (ckLessSalt.isChecked()) {
					if (!"".equals(searchSpecific)) {
						searchSpecific += " OR rs.specific_id = 5";
					} else {
						searchSpecific = " AND ( rs.specific_id = 5";
					}
				}
				if (!"".equals(searchSpecific)) {
					searchSpecific += " ) ";
				}
				OnSearchRecipe(searchSpecific);
			}
		});

		listVRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String recipe_id = recipeList.get(position).get("id");
				String recipe_name = recipeList.get(position).get("name");
				String recipe_specific = recipeList.get(position).get("specific_name");
				String recipe_calories = recipeList.get(position).get("calories");
				String recipe_how_to_cook = recipeList.get(position).get("how_to_cook");
				DialogRecipe(recipe_id, recipe_name, recipe_specific, recipe_calories, recipe_how_to_cook);
			}
		});
		listVRecipes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				return false;
			}
		});
	}

	private void DialogRecipe(final String recipe_id, final String recipe_name, String recipe_specific, String recipe_calories, final String recipe_how_to_cook) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View checkBoxView = View.inflate(this, R.layout.dialog_makeadish, null);
		imgViewRecipe = (ImageView)checkBoxView.findViewById(R.id.imageView);
		listView = (ListView)checkBoxView.findViewById(R.id.listView);
		TextView txtRecipeName = (TextView)checkBoxView.findViewById(R.id.txtRecipe);
		final Button btMakeADish = (Button)checkBoxView.findViewById(R.id.btnMakeADish);
		final Button btHowToCook = (Button)checkBoxView.findViewById(R.id.btnHowToCook);

		txtRecipeName.setText(recipe_name);
		LoadItems(recipe_id);
		readImgFromDB(recipe_id);

		btMakeADish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = myDB.MakeADish(recipe_id, refrigeratorList);
				MessageStatus(message);
				btMakeADish.setEnabled(false);
				LoadItems(recipe_id);
			}
		});
		btHowToCook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogHowToCook(recipe_name, recipe_how_to_cook);
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String item_id = refrigeratorList.get(position).get("id");
				String item_name = refrigeratorList.get(position).get("name");
				String item_amount = refrigeratorList.get(position).get("amount");
				String item_unit = refrigeratorList.get(position).get("unit");
				DialogAddCut(recipe_id, item_id, item_name, item_amount, item_unit);
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("Make a dish");
		builderInOut.setMessage("")
				.setView(checkBoxView)
				.setCancelable(false)
				.setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
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

	private void DialogHowToCook(String recipe_name, String recipe_how_to_cook) {
		View checkBoxView = View.inflate(this, R.layout.dialog_how_to_cook, null);
		final EditText txtHowToCook = (EditText)checkBoxView.findViewById(R.id.txtHowToCook);

		txtHowToCook.setText(recipe_how_to_cook);
		txtHowToCook.setEnabled(false);
		AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
		builderInOut.setTitle("How to cook : " + recipe_name);
		builderInOut.setMessage("")
				.setView(checkBoxView)
				.setCancelable(false)
				.setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
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

	private void OnSearchRecipe(String searchSpecificId) {
		recipeList = myDB.SearchRecipesBySpecificId(searchSpecificId, eTextName.getText().toString().trim());
		if(recipeList.size() < 1){
			MessageStatus("No recipe");
		}
		SimpleAdapter sAdapRecipes;
		sAdapRecipes = new SimpleAdapter(SearchActivity.this,
				recipeList, R.layout.activity_column_recipe,
				new String[] { "name", "calories", "specific_name" },
				new int[] { R.id.ColName, R.id.ColCalories, R.id.ColSpecific });
		listVRecipes.setAdapter(sAdapRecipes);
		registerForContextMenu(listVRecipes);
	}


/*	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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


/*	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}*/

	void readImgFromDB(String recipe_id) {
		byte[] byteImageFood = null;
		byteImageFood = myDB.ReadImageFromDB(recipe_id);
		if (byteImageFood != null) {
			setImage(byteImageFood);
		}
	}

	void setImage(byte[] byteImageFood) {
		if(byteImageFood != null) {
			imgViewRecipe.setImageBitmap(BitmapFactory.decodeByteArray(byteImageFood, 0, byteImageFood.length));
		}
	}

	private void LoadItems(String recipe_id) {
		refrigeratorList = myDB.SearchItemsInRecipe(recipe_id);
		SimpleAdapter adapItems = new SimpleAdapter(SearchActivity.this,
				refrigeratorList, R.layout.activity_column_items,
				new String[] { "name", "amount", "unit" },
				new int[] { R.id.ColName, R.id.ColAmount, R.id.ColUnit });
		listView.setAdapter(adapItems);
		registerForContextMenu(listView);
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
						Double totalAmount = Double.parseDouble(strBalance.trim()) + Double.parseDouble(strAmount.trim());
						//amount = Double.parseDouble(strAmount);
						String message = myDB.UpdateItemInRecipe(recipe_id, item_id, totalAmount.toString());
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
