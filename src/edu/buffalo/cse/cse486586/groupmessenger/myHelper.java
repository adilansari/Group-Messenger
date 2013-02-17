package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class myHelper extends SQLiteOpenHelper {

	
	public static final String DB_NAME = "KeyValueStore.db";
	public static final int DB_VERSION= 1;
	public static final String TABLE_NAME= "Store";
	public static final String KEY_FIELD= "key";
	public static final String VALUE_FIELD = "value";
	public static final String CREATE_TABLE= "CREATE TABLE " + TABLE_NAME + "(" + KEY_FIELD + " TEXT PRIMARY KEY NOT NULL, " + VALUE_FIELD + " TEXT);";
	public static final String TAG= "adil";
	
	public myHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		Log.v(TAG, "onCreate call");			
		arg0.execSQL(CREATE_TABLE);
		Log.v(TAG, "Table created");
		}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		 arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		 onCreate(arg0);
	}

	public void insertPair(String key, String value) {
		ContentValues keyValuesToInsert= new ContentValues();
		keyValuesToInsert.put(myHelper.KEY_FIELD, key);
		keyValuesToInsert.put(myHelper.VALUE_FIELD, value);
	}
}
