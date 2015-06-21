package com.example.mars;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
	DBAdapter db = new DBAdapter(this);
	@Override
	public IBinder onBind(Intent arg0){
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		toast("Service Started");
		db.open();
		showPerm();
		return START_STICKY;
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		toast("Service Ended!");
	}
	private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
     }
	//---get all perms---
	   private void showPerm() {
	      Cursor c = db.getPerm(33);
	      if (c.moveToFirst()) {
	         do {
	            displayDatabaseContent(c);
	         } while (c.moveToNext());
	      }
	   }
	 //---display content---
	   public void displayDatabaseContent(Cursor c) {
	         toast(" \n" + "DataBase ==>" + c.getString(0) + "Mid: " + c.getString(1) + "Code: " + c.getString(2) + "Mach1: " + c.getString(3) + "Mach2: " + c.getString(4));
	      }
	
}
