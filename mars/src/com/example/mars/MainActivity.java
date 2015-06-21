package com.example.mars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
   EditText logView;
   String currentTime;
   static final int update_interval = 10000;
   private Timer timer = new Timer();
   DBAdapter db = new DBAdapter(this);
   int rows;
   
   public String readJSONFeed(String URL) {
      StringBuilder stringBuilder = new StringBuilder();
      HttpClient client = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(URL);
      try {
         HttpResponse response = client.execute(httpGet);
         StatusLine statusLine = response.getStatusLine();
         int statusCode = statusLine.getStatusCode();
         if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
               stringBuilder.append(line);
            }
         }
         else {
            Log.e("JSON", "Failed to download file");
            logView.setText(logView.getText() + " \n" + "Sync Faild @" + currentTime);
         }
      }
      catch (ClientProtocolException e) {
         e.printStackTrace();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      return stringBuilder.toString();
   }
   private class ReadJSONFeedTask extends AsyncTask < String, Void, String > {
      protected String doInBackground(String...urls) {
         return readJSONFeed(urls[0]);
      }
      protected void onPostExecute(String result) {
         try {
            JSONArray jsonArray = new JSONArray(result);
            db.truncate();
            //fetching json array
            for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               long id = db.insertContact(jsonObject.getString("id"), jsonObject.getString("code"), jsonObject.getInt("mach1"), jsonObject.getInt("mach2"));
            }
            currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            //---Json state---
            logView.setText(logView.getText() + " \n" + "Sync: " + jsonArray.length() + " rows Compelete @" + currentTime);
            //---SQL state---
            numberOfRows();
            logView.setText(logView.getText() + " \n" + "Database==> " + rows + " rows stored @" + currentTime);
         }
         catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
   
   //---OnCreate---
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      logView = (EditText) findViewById(R.id.editText1);
      //Run background service
      startService(new Intent(getBaseContext(), MyService.class));
      // ---add a contact---
      db.open();
      downloadData();
      showAllPerms();
   }
   
   //---Download data---
   private void downloadData() {
         timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
               new ReadJSONFeedTask()
                  .execute("http://www.hus-art.com/zulucoding/data.json");
            }
         }, 0, update_interval);
      }
   
   //---display content---
   public void displayDatabaseContent(Cursor c) {
         logView.setText(logView.getText() + " \n" + "DataBase ==>" + c.getString(0) + "Mid: " + c.getString(1) + "Code: " + c.getString(2) + "Mach1: " + c.getString(3) + "Mach2: " + c.getString(4));
      }
   
   //---toast message---
   private void toast(String message) {
         Toast.makeText(this, message, Toast.LENGTH_LONG).show();
      }
   
   //---get all perms---
   private void showAllPerms() {
      Cursor c = db.getAllPerms();
      if (c.moveToFirst()) {
         do {
            displayDatabaseContent(c);
         } while (c.moveToNext());
      }
   }
   
   // ---number of rows---
   private void numberOfRows() {
      Cursor c = db.getAllPerms();
      if (c.moveToFirst()) {
         do {
            rows = c.getCount();
         } while (c.moveToNext());
      }
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater()
         .inflate(R.menu.main, menu);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      if (id == R.id.action_settings) {
         return true;
      }
      return super.onOptionsItemSelected(item);
   }
}