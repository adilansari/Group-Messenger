package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class myHelper extends SQLiteOpenHelper {

	
	public static final String DB_NAME = "DataBase";
	public static final int DB_VERSION= 1;
	public static final String TABLE_NAME= "table";
	public static final String key= "key";
	public static final String value = "value";
	public static final String CREATE_TABLE= "create table " + TABLE_NAME + "(" + key + " integer primary key autoincrement, " + value + " text not null);";
	
	
	public myHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL(CREATE_TABLE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		 arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		 onCreate(arg0);
	}

	
}
