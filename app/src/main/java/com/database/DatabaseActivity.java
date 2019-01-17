package com.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

@SuppressLint("Instantiatable")
public class DatabaseActivity extends SQLiteOpenHelper {
	private static String DB_NAME = "resep_halal.sqlite";
	private static String TABLE_MEMBER = "tb_member";
	private static String TABLE_REFRIGERATOR = "tb_refrigerator";
	private static String TABLE_RECIPE = "tb_resep";
	private static String TABLE_RECIPE_ADD_ITEMS = "tb_recipe_add_items";
	private static String TABLE_SPECIFIC = "tb_specific";
	private static String TABLE_HISTORY = "ta_history";
	private static String TABLE_RECIPE_SPECIFIC = "tb_recipe_specific";
	private static Integer BUFFER_SIZE = 128;
	private SQLiteDatabase myDataBase;
	private final Context context;
	private DecimalFormat df = new DecimalFormat("#,###,###.##");
	private String[] arraySpecificId;
	private String[] arraySpecificName;

	public DatabaseActivity(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	public void openDatabase() {
		File dbFolder = context.getDatabasePath(DB_NAME).getParentFile();
		if (!dbFolder.exists()) {
			dbFolder.mkdir();
		}

		File dbFile = context.getDatabasePath(DB_NAME);

		if (!dbFile.exists()) {
			try {
				copyDatabase(dbFile);
			} catch (IOException e) {
				throw new RuntimeException("Error creating source database", e);
			}
		}
	}

	private void copyDatabase(File targetDbFile) throws IOException {
		// ***********************************************************************************
		// Open your local db as the input stream
		InputStream myInput = context.getAssets().open(DB_NAME);

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(targetDbFile);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[BUFFER_SIZE];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();

		// ***********************************************************************************
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public boolean SignIn(String user, String pass) {
		SQLiteDatabase db;
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_MEMBER + " WHERE user = '"
					+ user + "' AND pass = '" + pass + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			db.close();
			db = this.getWritableDatabase(); // Update or Insert to database
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.print(e.toString());
			return false;
		}
	}

	public Boolean SignUp(String user, String pass) {
		SQLiteDatabase db;
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_MEMBER + " WHERE user = '"
					+ user + "' AND pass = '" + pass + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			db = this.getWritableDatabase(); // Update or Insert to database
			if (count > 0) {
				db.close();
				return false;
			} else {
				db = this.getWritableDatabase(); // Read Data
				String strSQLInsert = "INSERT INTO " + TABLE_MEMBER
						+ "(user, pass) VALUES('" + user + "', '" + pass
						+ "') ";
				db.execSQL(strSQLInsert);
				db.close();
				return true;
			}
		} catch (Exception e) {
			System.out.print(e.toString());
			return false;
		}
	}

	public void DeleteItem(String item_id) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "DELETE FROM " + TABLE_REFRIGERATOR
					+ " WHERE id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public ArrayList<HashMap<String, String>> SearchItems(String name) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT id, name, amount, unit FROM "
					+ TABLE_REFRIGERATOR + " WHERE name LIKE '%" + name	+ "%' ORDER BY id DESC ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						String strAmount = cursor.getString(2);
						if (strAmount != "") {
							strAmount = df
									.format(Double.parseDouble(strAmount))
									.toString().trim();
						}
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						map.put("amount", strAmount);
						map.put("unit", cursor.getString(3));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String UpdateItem(String item_id, String strAmount, String strUnit) {
		SQLiteDatabase db;
		String message = "";
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "UPDATE " + TABLE_REFRIGERATOR
					+ " SET amount = " + strAmount + ", unit = '" + strUnit
					+ "' " + "WHERE id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
			message = "Completed";
		} catch (Exception e) {
			System.out.print(e.toString());
			message = e.getMessage();
		}
		return message;
	}

	public String AddNewItem(String strName, String strAmount, String strUnit) {
		SQLiteDatabase db;
		String message = "";
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_REFRIGERATOR
					+ " WHERE name = '" + strName + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			db = this.getWritableDatabase(); // Update or Insert to database
			if (count > 0) {
				db.close();
				message = "Bahan sudah ada !";
			} else {
				db = this.getWritableDatabase(); // Read Data
				String strSQLInsert = "INSERT INTO " + TABLE_REFRIGERATOR
						+ "(name, amount, unit) VALUES('" + strName + "', "
						+ strAmount + ", '" + strUnit + "') ";
				db.execSQL(strSQLInsert);
				db.close();
				message = "Bahan Berhasil ditambahkan";
			}
		} catch (Exception e) {
			System.out.print(e.toString());
			message = e.getMessage();
		}
		return message;
	}

	public ArrayList<HashMap<String, String>> SearchRecipes(String v_name) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			/*String strSQL = "SELECT r.id, r.name, r.calories, r.how_to_cook FROM "
					+ TABLE_RECIPE + " r LEFT OUTER JOIN " + TABLE_RECIPE_SPECIFIC + " rs ON r.id = rs.recipe_id WHERE r.name LIKE '%" + v_name + "%' "
					+ " ORDER BY r.calories, rs.specific_id "; //rs.specific_id,*/
			String strSQL = "SELECT id, name, calories, how_to_cook FROM "
					+ TABLE_RECIPE + "  WHERE name LIKE '%" + v_name + "%' "
					+ " ORDER BY specific_id, calories ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						map.put("calories", "Kalori = " + (df.format(Double.parseDouble(cursor.getString(2)))).toString());

						String strSQLSpecific = "SELECT rs.specific_id, s.name FROM "+TABLE_RECIPE_SPECIFIC+" rs LEFT OUTER JOIN "+TABLE_SPECIFIC+" s "
								+ " ON rs.specific_id = s.id WHERE rs.recipe_id = "+cursor.getString(0)+" ";
						Cursor cursorSpecific = db.rawQuery(strSQLSpecific, null);
						if (cursorSpecific != null) {
							arraySpecificId = new String[cursorSpecific.getCount()];
							arraySpecificName = new String[cursorSpecific.getCount()];
							int i = 0;
							if (cursorSpecific.moveToFirst()) {
								do {
										arraySpecificId[i] = cursorSpecific.getString(0);
										arraySpecificName[i] = cursorSpecific.getString(1);
									i++;
								} while (cursorSpecific.moveToNext());
							}
						}
						cursorSpecific.close();
						String strSpecificId = "";
						String strSpecificName = "";
						for (int i = 0; i <arraySpecificId.length ; i++) {
							if((arraySpecificId.length - i) == 1){
								strSpecificId += arraySpecificId[i].toString();
								strSpecificName += arraySpecificName[i].toString();
							}else {
								strSpecificId += arraySpecificId[i].toString() + ",";
								strSpecificName += arraySpecificName[i].toString() + ", ";
							}
						}
						map.put("specific_id", strSpecificId);
						map.put("specific_name", strSpecificName);
						map.put("how_to_cook", cursor.getString(3));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	public String AddNewRecipe(String strName, String strCalories, String[] arraySpecific) {
		SQLiteDatabase db;
		String statusMsg = "";
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_RECIPE
					+ " WHERE name = '" + strName + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			if (count > 0) {
				db.close();
				statusMsg = "Resep Sudah Ada!";
			} else {
				String specificOrder = "";
				for (int i = 0; i < arraySpecific.length; i++) {
					specificOrder += arraySpecific[i].toString().trim();
				}
				db = this.getWritableDatabase(); // Update or Insert to database
				String strSQLInsert = "INSERT INTO " + TABLE_RECIPE
						+ "(name, calories, specific_id) VALUES('" + strName + "', "
						+ strCalories + ", "+specificOrder+") ";
				db.execSQL(strSQLInsert);
				//Get Id Recipe
				String recipeId = "";
				db = this.getReadableDatabase();
				String strSQLGetIDRecipe = "SELECT id FROM " + TABLE_RECIPE
							+ " WHERE name = '" + strName + "' LIMIT 1 ";
				cursor = db.rawQuery(strSQLGetIDRecipe, null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							recipeId = cursor.getString(0);
						} while (cursor.moveToNext());
					}
				}
				cursor.close();

				db = this.getWritableDatabase(); // Update or Insert to database
				for (int i = 0; i < arraySpecific.length; i++) {
					String strSQLInsertSpecific = "INSERT INTO " + TABLE_RECIPE_SPECIFIC
							+ "(recipe_id, specific_id) VALUES('" + recipeId + "', "
							+ arraySpecific[i].toString().trim() + ") ";
					db.execSQL(strSQLInsertSpecific);
				}
				db.close();
				statusMsg = "Resep Berhasil Ditambahkan";
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			statusMsg = "Error : " + e.getMessage();
		}
		return statusMsg;
	}

	public ArrayList<HashMap<String, String>> GetSpecific() {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT id, name FROM "
					+ TABLE_SPECIFIC + " ORDER BY id ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String EditRecipe(String e_recipe_id, String strName, String strCalories, String[] arraySpecific, String strHowToCook) {
		SQLiteDatabase db;
		String statusMsg = "";
		try {
			db = this.getWritableDatabase(); // Update or Insert to database

			String strSQLDeleteSpecific = "DELETE FROM "+TABLE_RECIPE_SPECIFIC+" WHERE recipe_id = "+e_recipe_id+" ";
			db.execSQL(strSQLDeleteSpecific);
			String specificOrder = "";
			for (int i = 0; i < arraySpecific.length; i++) {
				String strSQLInsertSpecific = "INSERT INTO " + TABLE_RECIPE_SPECIFIC
						+ "(recipe_id, specific_id) VALUES(" + e_recipe_id + ", "
						+ arraySpecific[i].toString().trim() + ") ";
				db.execSQL(strSQLInsertSpecific);
				specificOrder += arraySpecific[i].toString().trim();
			}

			String strSQLUpdate = "UPDATE " + TABLE_RECIPE + " SET "
					+ " name = '" + strName + "', calories = " + strCalories + " , specific_id = "+specificOrder.trim()+", how_to_cook = '" + strHowToCook + "' WHERE id = " + e_recipe_id + " ";
			db.execSQL(strSQLUpdate);

			db.close();
			statusMsg = "Perubahan Berhasil Disimpan";
		} catch (Exception e) {
			System.out.print(e.getMessage());
			statusMsg = "Error : " + e.getMessage();
		}
		return statusMsg;
	}

	public void SaveInDB(String selectedImagePath, String e_recipe_id) {
		byte[] byteImage = null;
		SQLiteDatabase db;
		db = this.getWritableDatabase();
		ContentValues Val = new ContentValues();
		try {
			FileInputStream instream = new FileInputStream(selectedImagePath);
			BufferedInputStream bif = new BufferedInputStream(instream);
			byteImage = new byte[bif.available()];
			bif.read(byteImage);
			Val.put("image", byteImage);
			long ret = db.update(TABLE_RECIPE, Val, " id = ?",
					new String[]{String.valueOf(e_recipe_id)});
			if (ret < 0) {
				System.out.print("Error");
			} else {
				System.out.print("Success");
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		db.close();
	}

	public byte[] ReadImageFromDB(String e_recipe_id) {
		byte[] byteImage = null;
		SQLiteDatabase db;
		db = this.getReadableDatabase();
		try {
			String strSQL = "SELECT * FROM " + TABLE_RECIPE
					+ " WHERE id = '" + e_recipe_id + "' LIMIT 1 ";
			Cursor cursor = db.rawQuery(strSQL, null);

			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {
				System.out.println("\n Reading Details \n Name : "
						+ cursor.getString(1));
				cursor.moveToNext();
			}
			// /////Read data from blob field////////////////////
			cursor.moveToFirst();
			byteImage = cursor.getBlob(cursor.getColumnIndex("image"));
			cursor.close();
			db.close();
			return byteImage;
		} catch (Exception e) {
			System.out.print(e.toString());
			db.close();
			return null;
		}
	}

	public void DeleteRecipe(String recipe_id) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLRe = "DELETE FROM " + TABLE_RECIPE
					+ " WHERE id = " + recipe_id + " ";
			db.execSQL(strSQLRe);
			String strSQLReAddItems = "DELETE FROM " + TABLE_RECIPE_ADD_ITEMS
					+ " WHERE recipe_id = " + recipe_id + " ";
			db.execSQL(strSQLReAddItems);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public ArrayList<HashMap<String, String>> SearchItemsInRecipe(String recipe_id) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT a.refrigerator_id, r.name, a.amount, r.unit FROM " + TABLE_RECIPE_ADD_ITEMS + " a LEFT OUTER JOIN "
					+ TABLE_REFRIGERATOR + " r ON a.refrigerator_id = r.id WHERE a.recipe_id = " + recipe_id + " ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						String strAmount = cursor.getString(2);
						if (strAmount != "") {
							strAmount = df
									.format(Double.parseDouble(strAmount))
									.toString().trim();
						}
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						map.put("amount", strAmount);
						map.put("unit", cursor.getString(3));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String InsertRecipeAddItems(String recipe_id, String item_id, String amount) {
		SQLiteDatabase db;
		String statusMsg = "";
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_RECIPE_ADD_ITEMS
					+ " WHERE recipe_id = " + recipe_id + " AND refrigerator_id = " + item_id + " ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			if (count > 0) {
				db.close();
				statusMsg = "This ingredients already exist!";
			} else {
				db = this.getWritableDatabase(); // Update or Insert to database
				String strSQLInsert = "INSERT INTO " + TABLE_RECIPE_ADD_ITEMS
						+ "(recipe_id, refrigerator_id, amount) VALUES('" + recipe_id + "', "
						+ item_id + ", " + amount + ") ";
				db.execSQL(strSQLInsert);
				db.close();
				statusMsg = "Bahan Berhasil Ditambah";
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			statusMsg = "Error : " + e.getMessage();
		}
		return statusMsg;
	}

	public void DeleteItemInRecipe(String recipe_id, String item_id) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "DELETE FROM " + TABLE_RECIPE_ADD_ITEMS
					+ " WHERE recipe_id = " + recipe_id + " AND refrigerator_id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public String UpdateItemInRecipe(String recipe_id, String item_id, String strAmount) {
		SQLiteDatabase db;
		String message = "";
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "UPDATE " + TABLE_RECIPE_ADD_ITEMS
					+ " SET amount = " + strAmount
					+ " WHERE recipe_id = " + recipe_id + " AND refrigerator_id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
			message = "Completed";
		} catch (Exception e) {
			System.out.print(e.toString());
			message = e.getMessage();
		}
		return message;
	}

	public ArrayList<HashMap<String, String>> SearchRecipesBySpecificId(String searchSpecificId, String name) {
		//String[] spSpecificId = searchSpecificId.split(",");
		try {
			ArrayList<HashMap<String, String>> RecipeAllList = new ArrayList<HashMap<String, String>>();
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data

			String strSQLRecipeAll = "SELECT rai.recipe_id, COUNT(rai.recipe_id) AS count FROM " + TABLE_RECIPE_ADD_ITEMS + " rai "
					+ " LEFT OUTER JOIN " + TABLE_RECIPE + " r ON rai.recipe_id = r.id"
					+ " WHERE r.name LIKE '%" + name + "%' "+searchSpecificId+ " "
					+ " GROUP BY rai.recipe_id"
					+ " ORDER BY r.specific_id, r.calories ";
			Cursor cursorRecipeAll = db.rawQuery(strSQLRecipeAll, null);
			if (cursorRecipeAll != null) {
				if (cursorRecipeAll.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("recipe_id", cursorRecipeAll.getString(0));
						map.put("count", cursorRecipeAll.getString(1));
						RecipeAllList.add(map);
					} while (cursorRecipeAll.moveToNext());
				}
			}

			for (int i = 0; i < RecipeAllList.size(); i++) {
				String strSQLInRefrigerator = "SELECT rai.recipe_id FROM " + TABLE_RECIPE_ADD_ITEMS + " rai "
						+ " LEFT OUTER JOIN " + TABLE_REFRIGERATOR + " ref ON rai.refrigerator_id = ref.id"
						+ " WHERE recipe_id = " + RecipeAllList.get(i).get("recipe_id")
						+ " AND  ref.amount >= rai.amount";
				Cursor cursorInRefrigerator = db.rawQuery(strSQLInRefrigerator, null);
				Integer countInRefrigerator = cursorInRefrigerator.getCount();
				cursorInRefrigerator.close();
				if (Integer.parseInt(RecipeAllList.get(i).get("count")) == countInRefrigerator) {
					String strSQL = "SELECT r.id, r.name, r.calories, s.name, r.how_to_cook FROM "
							+ TABLE_RECIPE + " r LEFT OUTER JOIN " + TABLE_SPECIFIC + " s ON r.specific_id = s.id WHERE r.id = " + RecipeAllList.get(i).get("recipe_id")
							+ " ORDER BY r.calories LIMIT 1";
					Cursor cursor = db.rawQuery(strSQL, null);
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							do {
								map = new HashMap<String, String>();
								map.put("id", cursor.getString(0));
								map.put("name", cursor.getString(1));
								map.put("calories", "Kalori = " + (df.format(Double.parseDouble(cursor.getString(2)))).toString());

								String strSQLSpecific = "SELECT rs.specific_id, s.name FROM "+TABLE_RECIPE_SPECIFIC+" rs LEFT OUTER JOIN "+TABLE_SPECIFIC+" s "
										+ " ON rs.specific_id = s.id WHERE rs.recipe_id = "+cursor.getString(0)+" ";
								Cursor cursorSpecific = db.rawQuery(strSQLSpecific, null);
								if (cursorSpecific != null) {
									arraySpecificId = new String[cursorSpecific.getCount()];
									arraySpecificName = new String[cursorSpecific.getCount()];
									int j = 0;
									if (cursorSpecific.moveToFirst()) {
										do {
											arraySpecificId[j] = cursorSpecific.getString(0);
											arraySpecificName[j] = cursorSpecific.getString(1);
											j++;
										} while (cursorSpecific.moveToNext());
									}
								}
								cursorSpecific.close();
								String strSpecificId = "";
								String strSpecificName = "";
								for (int j = 0; j <arraySpecificId.length ; j++) {
									if((arraySpecificId.length - j) == 1){
										strSpecificId += arraySpecificId[j].toString();
										strSpecificName += arraySpecificName[j].toString();
									}else {
										strSpecificId += arraySpecificId[j].toString() + ",";
										strSpecificName += arraySpecificName[j].toString() + ", ";
									}
								}
								map.put("specific_id", strSpecificId);
								map.put("specific_name", strSpecificName);
								map.put("how_to_cook", cursor.getString(4));
								MyArrList.add(map);
							} while (cursor.moveToNext());
						}
					}
					cursor.close();
				}
			}
			db.close();
			return MyArrList;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	public String MakeADish(String recipe_id, ArrayList<HashMap<String, String>> refrigeratorList) {
		SQLiteDatabase db = null;
		Double dAmount = 0.00;
		String message = "";
		try {
			if(refrigeratorList != null){
				for (int i = 0; i < refrigeratorList.size(); i++) {
					dAmount = 0.00;
					db = this.getReadableDatabase(); // Read Data
					String strSQL = "SELECT id, name, amount, unit FROM "
							+ TABLE_REFRIGERATOR + " WHERE id = " + refrigeratorList.get(i).get("id") + " LIMIT 1";
					Cursor cursor = db.rawQuery(strSQL, null);
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							do {
							if(cursor.getString(2) != null && refrigeratorList.get(i).get("amount") != null){
								dAmount = Double.parseDouble(cursor.getString(2).toString().trim()) - Double.parseDouble(refrigeratorList.get(i).get("amount").toString().trim());
							}
							db = this.getWritableDatabase(); // Write Data
							String strSQLUpdate = "UPDATE " + TABLE_REFRIGERATOR
									+ " SET amount = " + dAmount
									+ " WHERE id = " + refrigeratorList.get(i).get("id") + " ";
							db.execSQL(strSQLUpdate);
							} while (cursor.moveToNext());
						}
					}
					cursor.close();
				}
				db = this.getWritableDatabase(); // Write Data
				String strSQLUpdate = "INSERT INTO " + TABLE_HISTORY
						+ "(recipe_id) VALUES(" + recipe_id + ") ";
				db.execSQL(strSQLUpdate);

				message = "Make a dish completed";
			}
			else{
				message = "Can not make a dish";
			}
		}
		catch (Exception e) {
			System.out.print(e.toString());
			message = e.getMessage().toString();
		}
		db.close();
		return message;
	}

	public ArrayList<HashMap<String, String>> SearchHistory(String dateFrom, String dateTo) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT r.name, r.calories, h.date_time FROM "
					+ TABLE_HISTORY + " h LEFT OUTER JOIN " + TABLE_RECIPE + " r ON h.recipe_id = r.id "
					+ " WHERE (h.date_time BETWEEN '"+dateFrom+"' AND '"+dateTo+"') "
					+ " ORDER BY h.date_time DESC";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("name", cursor.getString(0));
						map.put("calories", "Kalori = " + (df.format(Double.parseDouble(cursor.getString(1)))).toString());
						map.put("date_time", cursor.getString(2));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public String IdOfIngredientsName(String strName) {
		String str_id = "";
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT id FROM "+TABLE_REFRIGERATOR+" WHERE name = '"+strName+"' LIMIT 1 ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			str_id = MyArrList.get(0).get("id");
			return str_id;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
