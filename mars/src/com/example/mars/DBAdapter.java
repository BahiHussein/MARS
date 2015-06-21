package com.example.mars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBAdapter {
  public static final String KEY_ROWID = "_id";
  public static final String KEY_MID = "mid";
  public static final String KEY_CODE = "code";
  public static final String KEY_MACH1 = "mach1";
  public static final String KEY_MACH2 = "mach2";
  private static final String TAG = "DBAdapter";

  private static final String DATABASE_mid = "mars";
  private static final String DATABASE_TABLE = "mars_perms";
  private static final int DATABASE_VERSION = 3;

  private static final String DATABASE_CREATE = "create table mars_perms (_id integer primary key autoincrement, "
      + "mid text not null, code text not null, mach1 integer not null, mach2 integer not null);";

  private final Context context;

  private DatabaseHelper DBHelper;
  private SQLiteDatabase db;

  public DBAdapter(Context ctx) {
    this.context = ctx;
    DBHelper = new DatabaseHelper(context);
  }

  private static class DatabaseHelper extends SQLiteOpenHelper {
    DatabaseHelper(Context context) {
      super(context, DATABASE_mid, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      try {
        db.execSQL(DATABASE_CREATE);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, oldVersion + " to " + newVersion
          + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS mars_perms");
      onCreate(db);
    }
    
  }
  
  public void truncate(){
	  try {
		  db.execSQL("DROP TABLE IF EXISTS mars_perms");
	  	  db.execSQL(DATABASE_CREATE);
	  	  Log.i("Database", "Truncating succeeded");
	      } catch (SQLException e) {
	        e.printStackTrace();
	        Log.i("Database", "Truncating failed");
	      }
    }
  
  public DBAdapter open() throws SQLException {
    db = DBHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    DBHelper.close();
  }

  public long insertContact(String mid, String code, int mach1, int mach2 ) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_MID, mid);
    initialValues.put(KEY_CODE, code);
    initialValues.put(KEY_MACH1, mach1);
    initialValues.put(KEY_MACH2, mach2);
    return db.insert(DATABASE_TABLE, null, initialValues);
  }

  public boolean deleteContact(long rowId) {
    return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
  }

  public Cursor getAllPerms() {
    return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_MID,
        KEY_CODE, KEY_MACH1, KEY_MACH2 }, null, null, null, null, null);
  }

  public Cursor getPerm(long code) throws SQLException {
    Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
        KEY_ROWID, KEY_MID, KEY_CODE, KEY_MACH1, KEY_MACH2}, KEY_CODE + "=" + code,
        null, null, null, null, null);
    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;
  }

  public boolean updateContact(long rowId, String mid, String code, int mach1, int mach2) {
    ContentValues args = new ContentValues();
    args.put(KEY_MID, mid);
    args.put(KEY_CODE, code);
    args.put(KEY_MACH1, mach1);
    args.put(KEY_MACH2, mach2);
    return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
  }
}