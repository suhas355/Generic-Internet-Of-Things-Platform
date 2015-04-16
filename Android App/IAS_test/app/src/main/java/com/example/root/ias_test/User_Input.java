package com.example.root.ias_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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


public class User_Input extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__input);
        final Button button1 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            Notify grn = new Notify();

            grn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        }
          }



        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user__input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    private class Notify extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params)
        {
            final EditText mEdit_long, mEdit_lat, mEdit_rad;

            mEdit_long   = (EditText)findViewById(R.id.editText2);
            mEdit_lat   = (EditText)findViewById(R.id.editText);
            mEdit_rad   = (EditText)findViewById(R.id.editText3);


            final String lat = mEdit_lat.getText().toString();
            final String lon = mEdit_long.getText().toString();
            final String rad = mEdit_rad.getText().toString();
            final String output;
            try {

                   /* String urlGET = "http://192.168.217.108:6010?param1=2&lat=" +mEdit_lat.getText()+"&long="+mEdit_long.getText()+"&rad="+mEdit_rad.getText();
                    HttpGet  getMethod = new HttpGet(urlGET);
                    HttpResponse response = null;
                    HttpClient httpClient = new DefaultHttpClient();*/
                JSONObject jsonobj; // declared locally so that it destroys after serving its purpose
                // String type_arr[] = {"temperature"};
                JSONArray type_arr = new JSONArray();
                type_arr.put("temperature");
                jsonobj = new JSONObject();
                jsonobj.put("latitude", Double.parseDouble(lat));
                jsonobj.put("longitude", Double.parseDouble(lon));

                jsonobj.put("type",type_arr);
                jsonobj.put("radius", Double.parseDouble(rad));

                StringEntity se = new StringEntity(jsonobj.toString());

                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

                HttpClient httpclient = new DefaultHttpClient();
                URL url = new URL("http://192.168.217.108:6010/getdata/geolocation");
                HttpPost httppostreq = new HttpPost(url.toString());

                httppostreq.setEntity(se);
                HttpResponse response;
                Log.e("About to execute","");

                response = httpclient.execute(httppostreq);

                String result = EntityUtils.toString(response.getEntity());
                Log.e("Result is: ",result);


                // CONVERT RESPONSE STRING TO JSON ARRAY
                JSONArray ja = new JSONArray(result);

                // ITERATE THROUGH AND RETRIEVE CLUB FIELDS
                int n = ja.length();
                String out="Number of Sensors Found: "+n+"\n";

                for (int i = 0; i < n; i++) {
                    // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
                    JSONObject jo = ja.getJSONObject(i);

                    // RETRIEVE EACH JSON OBJECT'S FIELDS
                    String sid = jo.getString("sensorId");
                    String data = jo.getString("data");
                    String loc = jo.getString("location");
                    String geo = jo.getString("geo").toString();
                    out+="\nSensor ID: "+sid+"\n Value: "+data+"\nLocation: "+loc+"\nGeo Location: "+geo;
                    Log.e("output is: "+out,"");
                }

                publishProgress(out);


                Log.e("Retrieved Results", "Retrieved");

            } catch(Exception e)
            {
                publishProgress("No Data Found !!");

                Log.e("Error in setting up","JSON");
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
                           // Log.e("In funciton","");
            AlertDialog alertDialog = new AlertDialog.Builder(User_Input.this).create();

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
