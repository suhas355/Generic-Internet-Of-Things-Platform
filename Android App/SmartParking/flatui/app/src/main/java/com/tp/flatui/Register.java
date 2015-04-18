package com.tp.flatui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;


public class Register extends ActionBarActivity {
    FlatButton button;
    FlatEditText ambulance;
    FlatEditText freq;
    static String ecg, bp ,pulse;
    static String amId;
    static String frequecy;
    static HashMap<String,String> key  = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        button=(FlatButton)findViewById(R.id.register);
        ambulance=(FlatEditText)findViewById(R.id.edittext_box1);
        freq=(FlatEditText)findViewById(R.id.edittext_box3);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                frequecy = freq.getText().toString();
                amId = ambulance.getText().toString();
                Notify grn = new Notify();
                grn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                //GatewayRunner grn = new GatewayRunner();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    class Notify extends AsyncTask<String,String,String> {
        //public String callback_sid, callback_id="";
        //int callback_min=1, callback_max=-1;
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonobj;
            jsonobj = new JSONObject();
            try {

                    /*Random r = new Random();
                    ecg = Integer.toString(r.nextInt(100));
                    bp = Integer.toString(r.nextInt(100));
                    pulse = Integer.toString(r.nextInt(100));*/
                JSONArray arr = new JSONArray();
                arr.put("ecg");
                arr.put("bp");
                arr.put("pulse");
                jsonobj.put("type",  arr);
                StringEntity se = new StringEntity(jsonobj.toString());
                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                HttpClient httpclient = new DefaultHttpClient();
                URL url = new URL("http://10.42.0.58:6300/monitor/getsensors");
                HttpPost httppostreq = new HttpPost(url.toString());
                httppostreq.setEntity(se);
                HttpResponse response;
                response = httpclient.execute(httppostreq);
                String resp_text = EntityUtils.toString(response.getEntity());
                // Convert String to json object
                JSONObject json = new JSONObject(resp_text);
                ecg = json.getString("ecg");
                bp = json.getString("bp");
                pulse = json.getString("pulse");
                key.put("ECG",ecg);
                key.put("BP",bp);
                key.put("Pulse Rate",pulse);
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            } catch (Exception e) {
                publishProgress("Unable to connect to server");
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

        protected void onProgressUpdate(String...text){

            Toast.makeText(getApplicationContext(), text[0], Toast.LENGTH_LONG).show();
            //Call Publish Progress
        /*AlertDialog alertDialog = new AlertDialog.Builder(Register_Callback.this).create();

        alertDialog.setTitle("Alert: NOTIFICATION RECEIVED");

        alertDialog.setMessage(dharak[0]);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();*/

                            /*
                            int duration = Toast.LENGTH_LONG;
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, dharak[0], duration);
                            toast.show();
                            */




        }

    }

}
