package com.tp.flatui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;
import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.cengalabs.flatui.views.FlatSeekBar;
import com.cengalabs.flatui.views.FlatTextView;

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
import java.util.Random;


public class MainActivity extends ActionBarActivity {
    FlatSeekBar flat1, flat2, flat3;
    FlatButton but,call;
    FlatTextView editText1, editText2,editText3;
    String bp = Register.bp;
    String ecg = Register.ecg;
    String pulse = Register.pulse;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        but = (FlatButton)findViewById(R.id.notify);
        call = (FlatButton)findViewById(R.id.call);
        flat1 = (FlatSeekBar) findViewById(R.id.seekbar1);
        flat2 = (FlatSeekBar) findViewById(R.id.seekbar2);
        flat3 = (FlatSeekBar) findViewById(R.id.seekbar3);
        editText1 = (FlatTextView) findViewById(R.id.title_seekbar1);
        editText2 = (FlatTextView) findViewById(R.id.title_seekbar2);
        editText3 = (FlatTextView) findViewById(R.id.title_seekbar3);
        GatewayRunner grn = new GatewayRunner();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            grn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            grn.execute();
        }

        but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),callback.class);
                startActivity(in);

                //GatewayRunner grn = new GatewayRunner();

            }
        });
        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String number = Register.phn.get(Register.amId);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" +number));
                startActivity(callIntent);

                //GatewayRunner grn = new GatewayRunner();

            }
        });
        Log.d("here", "asynctask");

        //FlatUI.setDefaultTheme(FlatUI.);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class GatewayRunner extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            JSONObject jsonobj;
            jsonobj = new JSONObject();
            try {
                long val = Long.parseLong(Register.frequecy)*60 + System.currentTimeMillis()/1000;
                JSONArray arr = new JSONArray();
                arr.put(bp);arr.put(ecg);arr.put(pulse);
                jsonobj.put("sensorId",  arr);
                jsonobj.put("frequency" , 5);
                jsonobj.put("tillWhen" , val);
                StringEntity se = new StringEntity(jsonobj.toString());
                se.setContentType("application/json;charset=UTF-8");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                HttpClient httpclient = new DefaultHttpClient();
                URL url = new URL("http://10.42.0.19:6300/monitor/register_frequency");
                HttpPost httppostreq = new HttpPost(url.toString());
                httppostreq.setEntity(se);
                HttpResponse response;
                response = httpclient.execute(httppostreq);
                String resp_text = EntityUtils.toString(response.getEntity());
                // Convert String to json object
                JSONObject json = new JSONObject(resp_text);
                id = json.getString("id");
                while(true){
                    Thread.sleep(2000);
                    String urlGET = "http://10.42.0.19:6300/monitor/getresults?id="+id;
                    HttpGet getMethod = new HttpGet(urlGET);
                    HttpResponse response1 = null;
                    HttpClient httpClient = new DefaultHttpClient();
                    response1 = httpClient.execute(getMethod);
                    String resp_text1 = EntityUtils.toString(response1.getEntity());
                    // Convert String to json object
                    JSONObject json1 = new JSONObject(resp_text1);
                    String message = json1.getString("Message");
                    if(message.equalsIgnoreCase("pending")){
                        //publishProgress("Session ended");
                        //break;
                        continue;

                    }
                    Log.d("message",resp_text1);
                    String bpMsg = json1.getString("bp");
                    String ecgMsg = json1.getString("ecg");
                    String pulseMsg = json1.getString("pulse");
                    String msg = json1.getString("Message");
                    if(msg.equalsIgnoreCase("Last data packet")){
                        publishProgress("Session ended");
                        break;

                    }

                    /*String bpMsg = Register.bp;
                    String ecgMsg = Register.ecg;
                    String pulseMsg = Register.pulse;
                    publishProgress(bpMsg+","+ecgMsg+","+pulseMsg);
                    Random r = new Random();
                    ecg = Integer.toString(r.nextInt(100));
                    bp = Integer.toString(r.nextInt(100));
                    pulse = Integer.toString(r.nextInt(100));*/
                    publishProgress(bpMsg+","+ecgMsg+","+pulseMsg);
                }
                //bp = json.getString("bp");
                //pulse = json.getString("pulse");
                //Intent i = new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {
            if(text[0].equalsIgnoreCase("Session ended")){
                Toast.makeText(getApplicationContext(), text[0], Toast.LENGTH_LONG).show();
                flat1.setProgress(0);
                flat2.setProgress(0);
                flat3.setProgress(0);
                flat1.setBackgroundColor(Color.BLUE);
                flat2.setBackgroundColor(Color.BLUE);
                flat3.setBackgroundColor(Color.BLUE);
            }
            else {
                String str[] = text[0].split(",");

                int x1 = Integer.parseInt(str[0]);
                int x2 = Integer.parseInt(str[1]);
                int x3 = Integer.parseInt(str[2]);
                //Toast.makeText(getApplicationContext(), text[0], Toast.LENGTH_LONG).show();
                if (x1 > 140 || x1 < 60) {
                    flat1.setProgressDrawable(new ColorDrawable(Color.rgb(1, 0, 0)));
                    flat1.setProgress(x1);
                    flat1.setBackgroundColor(Color.RED);
                    editText1.setText("BP: " + x1);

                } else {
                    flat1.setProgressDrawable(new ColorDrawable(Color.rgb(0, 1, 0)));
                    flat1.setProgress(x1);
                    flat1.setBackgroundColor(Color.GREEN);
                    editText1.setText("BP: " + x1);
                }
                if (x2 > 90 || x2 < 60) {
                    flat2.setProgressDrawable(new ColorDrawable(Color.rgb(1, 0, 0)));
                    flat2.setProgress(x2);
                    flat2.setBackgroundColor(Color.RED);
                    editText2.setText("ECG: " + x2);
                } else {
                    flat2.setProgressDrawable(new ColorDrawable(Color.rgb(0, 1, 0)));
                    flat2.setProgress(x2);
                    flat2.setBackgroundColor(Color.GREEN);
                    editText2.setText("ECG: " + x2);
                }
                if (x3 > 100 || x3 < 60) {
                    flat3.setProgressDrawable(new ColorDrawable(Color.rgb(1, 0, 0)));
                    flat3.setProgress(x3);
                    flat3.setBackgroundColor(Color.RED);
                    editText3.setText("Pulse Rate: " + x3);
                } else {
                    flat3.setProgressDrawable(new ColorDrawable(Color.rgb(0, 1, 0)));
                    flat3.setProgress(x3);
                    flat3.setBackgroundColor(Color.GREEN);
                    editText3.setText("Pulse Rate: " + x3);
                }
            }

        }
    }
}
