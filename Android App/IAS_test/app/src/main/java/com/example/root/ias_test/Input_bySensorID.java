package com.example.root.ias_test;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Input_bySensorID extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_by_sensor_id);
        final Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v)
                                       {
        final EditText mEdit_sid;
        mEdit_sid   = (EditText)findViewById(R.id.editText4);
        mEdit_sid.getText().toString();
                                           final TextView log = (TextView)findViewById(R.id.tw1); log.setMovementMethod(new ScrollingMovementMethod());

                                           Thread thread = new Thread(new Runnable(){
                                               @Override

                                               public void run() {
        try {
           String urlGET = "http://192.168.217.108:6010/getdata/sensorid?sensorId=" +mEdit_sid.getText();
           HttpGet getMethod = new HttpGet(urlGET);
           HttpResponse response = null;
           HttpClient httpClient = new DefaultHttpClient();
           try {
               response = httpClient.execute(getMethod);
               // receive response as inputStream
              String result="";
               String resp_text = EntityUtils.toString(response.getEntity());
               // Convert String to json object
               // CONVERT RESPONSE STRING TO JSON ARRAY
               Log.e(resp_text,"");
               JSONArray ja = new JSONArray(resp_text);

               String output= "";
               // ITERATE THROUGH AND RETRIEVE CLUB FIELDS
               int n = ja.length();
               for (int i = 0; i < n; i++) {
                   // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
                   JSONObject jo = ja.getJSONObject(i);

                   // RETRIEVE EACH JSON OBJECT'S FIELDS
                   String id = jo.getString("sensorId");
                   String data = jo.getString(("data"));
                   String geo = jo.getString("geo");
                   String location = jo.getString("location");
                   String unit = jo.getString("unit");
                   String type = jo.getString("type");
                   result = "SensorID: "+id + "\n" + "Value: "+data + "\nGeoLocation: " + geo + "\nLocation: " +location + "\nUnit: " +unit + "\nType: " +type;

               }

               Log.e("Received",resp_text);
               log.append(result);
               Log.e(result,"");
           }
          catch (Exception e)
          {

              Log.e("Exception in innter catch",e+"");

           }
         } catch (Exception e) {
            Log.e("Exception in outer catch",e+"");
          }
           }
                     });

        thread.start();
         }
                     }
       );
    }

    private static String convertInputStreamToString(InputStream inputStream) throws Exception{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
        {
            if(line.contains("author")||line.contains("text"))
            result += line;
        }

        inputStream.close();
        return result;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_by_sensor_id, menu);
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
}
