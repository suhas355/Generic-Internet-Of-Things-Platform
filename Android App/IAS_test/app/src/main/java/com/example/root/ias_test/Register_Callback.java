package com.example.root.ias_test;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;


public class Register_Callback extends ActionBarActivity
{
    int callback_min=1, callback_max=-1;
    public String callback_sid, callback_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__callback);

        final int[] flag = {0};

        final Button button1 = (Button) findViewById(R.id.button8);
        button1.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Notify grn = new Notify();

                                           grn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                                       }
                                   }
        );
    }



                        @Override
                        public boolean onCreateOptionsMenu (Menu menu){
                            // Inflate the menu; this adds items to the action bar if it is present.
                            getMenuInflater().inflate(R.menu.menu_register__callback, menu);
                            return true;
                        }

                        @Override
                        public boolean onOptionsItemSelected (MenuItem item){
                            // Handle action bar item clicks here. The action bar will
                            // automatically handle clicks on the Home/Up button, so long
                            // as you specify a parent activity in AndroidManifest.xml.
                            int id = item.getItemId();

                            //noinspection SimplifiableIfStatement
                            if (id == R.id.action_settings) {
                                return true;
                            }

                            return super.onOptionsItemSelected(item);
                        }

                    private void setRecurringAlarm(Context context) {

                        Calendar updateTime = Calendar.getInstance();
                        updateTime.setTimeZone(TimeZone.getDefault());
                        updateTime.set(Calendar.HOUR_OF_DAY, 12);
                        updateTime.set(Calendar.MINUTE, 30);
                        Intent downloader = new Intent(context, MyStartServiceReceiver.class);
                        downloader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES / 300, pendingIntent);

                        Log.e("MyActivity", "Set alarmManager.setRepeating to: " + updateTime.getTime().toLocaleString());

                    }


                    private class Notify extends AsyncTask<String,String,String> {

                        @Override
                        protected String doInBackground(String... params) {
                            try
                            {
                                Log.e("In doinback","In doinback");
                                final EditText mEdit_sid, mEdit_max, mEdit_min;

                                mEdit_sid = (EditText) findViewById(R.id.editText5);
                                mEdit_max = (EditText) findViewById(R.id.editText6);
                                mEdit_min = (EditText) findViewById(R.id.editText7);

                                int max, min;
                                max = Integer.parseInt(mEdit_max.getText().toString());
                                min = Integer.parseInt(mEdit_min.getText().toString());

                                if (min <= max)
                                {
                                    Log.e("In if","In if");
                                    //Query is valid, register callback..!!
                                    callback_sid = mEdit_sid.getText().toString();
                                    callback_max = max;
                                    callback_min = min;


                                    JSONObject jsonobj; // declared locally so that it destroys after serving its purpose
                                    jsonobj = new JSONObject();
                                    jsonobj.put("sensorId", callback_sid);
                                    jsonobj.put("minValue", callback_min);

                                    jsonobj.put("maxValue", callback_max);

                                    StringEntity se = new StringEntity(jsonobj.toString());

                                    se.setContentType("application/json;charset=UTF-8");
                                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

                                    HttpClient httpclient = new DefaultHttpClient();
                                    URL url = new URL("http://192.168.217.108:6010/getdata");
                                    HttpPost httppostreq = new HttpPost(url.toString());
                                    try {
                                        httppostreq.setEntity(se);
                                        HttpResponse response;

                                        response = httpclient.execute(httppostreq);
                                        String resp_text = EntityUtils.toString(response.getEntity());
                                        // Convert String to json object
                                        JSONObject json = new JSONObject(resp_text);

                                        String message = json.getString("message");
                                        String id = json.getString("id");
                                        callback_id = id;
                                        Log.e("Callback id", "Id");


                                        if (message.equals("Callback registered"))
                                        {
                                            publishProgress("Callback Registered");
                                            Log.e("Here", "Succesfully registered");
                                            // alertDialog.setMessage("Callback Successfully Registered");
                                            String urlGET = "http://192.168.217.108:6010/getdata/getresults?id="+callback_id;
                                            while(true)
                                            {
                                                Log.e("In while","here"+callback_id);
                                                Thread.sleep(10000);
                                                HttpGet getMethod = new HttpGet(urlGET);
                                                HttpResponse response1 = null;
                                                HttpClient httpClient = new DefaultHttpClient();
                                                try {
                                                    response1 = httpClient.execute(getMethod);
                                                    String resp_text1 = EntityUtils.toString(response1.getEntity());
                                                    // Convert String to json object
                                                    JSONObject json1 = new JSONObject(resp_text1);

                                                    String message1 = json1.getString("Message");
                                                    if (message1.equals("Pending")) {
                                                        //Do Nothing as message is pending
                                                        Log.e("Pending", "Pending");
                                                    }
                                                    else
                                                    {
                                                        Log.e("Got Results", "Got Results");
                                                        Log.e("Data: ",""+json1.toString());

                                                        JSONArray ja = new JSONArray(json1.get(callback_id).toString());

                                                        String output= "";
                                                        // ITERATE THROUGH AND RETRIEVE CLUB FIELDS
                                                        int n = ja.length();
                                                        String to_print="";
                                                        for (int i = 0; i < n; i++) {
                                                            // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
                                                            JSONObject jo = ja.getJSONObject(i);

                                                            // RETRIEVE EACH JSON OBJECT'S FIELDS
                                                            to_print = "\nGeo-Location: "+jo.getString("geo")+";";
                                                            to_print = to_print + "\nLocation: "+jo.getString("location")+";"
                                                                  +"\nTime: "+jo.getString("timestamp");

                                                        }




                                                        publishProgress(to_print);
                                                        break;
                                                    }


                                                } catch (Exception e) {
                                                    Log.e("Exception in getting results for id", ""+e);


                                                }
                                            }




                                        } else {
                                            //alertDialog.setMessage("Error in Registering Callback. Try Again");
                                            Log.e("In else", "Error in Registering callback");

                                        }



                                    } catch (Exception e) {
                                        Log.e("error in sending json to server", " error message" + e);


                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        return null;
                        }

                        @Override
                        protected void onPreExecute()
                        {

                        }

                        @Override
                        protected void onPostExecute(String pram)
                        {


                        }
                        @Override

                        protected void onProgressUpdate(String...dharak)
                        {
                            //Call Publish Progress
                            AlertDialog alertDialog = new AlertDialog.Builder(Register_Callback.this).create();

                            alertDialog.setTitle("Alert: NOTIFICATION RECEIVED");

                            alertDialog.setMessage(dharak[0]);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                            /*
                            int duration = Toast.LENGTH_LONG;
                            Context context = getApplicationContext();

                            Toast toast = Toast.makeText(context, dharak[0], duration);
                            toast.show();

                            */




                        }

                    }
                }
