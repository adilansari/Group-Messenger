package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class myProvider extends ContentProvider {

	private myHelper myDb;
	private SQLiteDatabase db;
	private static final String AUTHORITY = "edu.buffalo.cse.cse486586.groupmessenger.provider";
	private static final String BASE_PATH = myHelper.TABLE_NAME;
	public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/" + BASE_PATH);
	public static final String TAG= "adil";
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		db = myDb.getWritableDatabase();
		ContentValues values= new ContentValues(arg1);
		long rowId= db.insert(myHelper.TABLE_NAME, myHelper.VALUE_FIELD, values);
		
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(newUri, null);
			Log.e(TAG, "Insertion success!!");
			return newUri;
		}
		else {
			Log.e(TAG, "Insert to db failed");
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		Log.v(TAG, "provider created");
		myDb = new myHelper(getContext());
		myDb.getWritableDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,String arg4) {
		db= myDb.getWritableDatabase();
		SQLiteQueryBuilder qb= new SQLiteQueryBuilder();
		qb.setTables(myHelper.TABLE_NAME);
		
		qb.appendWhereEscapeString(myHelper.KEY_FIELD + " = " + arg2);
		Cursor c= qb.query(db, null, null, null, null, null, null);
		//Cursor c= db.query(myHelper.TABLE_NAME, null, arg2, null, null, null, null);
		c.setNotificationUri(getContext().getContentResolver(), arg0);
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
