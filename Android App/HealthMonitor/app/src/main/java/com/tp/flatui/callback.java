package com.tp.flatui;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import com.cengalabs.flatui.views.FlatRadioButton;
import com.cengalabs.flatui.views.FlatTextView;

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


public class callback extends ActionBarActivity {
    FlatButton button;
    FlatEditText mEdit_max,mEdit_min;
    FlatRadioButton mEdit_sid,temp1,temp2,temp3;
    //FlatButton temp1,temp2,temp3;

    int callback_min=1, callback_max=-1;
    public String callback_sid, callback_id="";
    int min,max;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);
        button = (FlatButton)findViewById(R.id.notify);
        mEdit_max = (FlatEditText)findViewById(R.id.edittext_box5);
        mEdit_min = (FlatEditText)findViewById(R.id.edittext_box6);
        temp1 = (FlatRadioButton)findViewById(R.id.radio1);
        temp2 = (FlatRadioButton)findViewById(R.id.radio2);
        temp3 = (FlatRadioButton)findViewById(R.id.radio3);
        button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Notify grn = new Notify();
                  //Log.d("Temp1",temp1.getText().toString());
                  //Log.d("Temp2",temp2.getText().toString());
                  //Log.d("Temp3",temp3.getText().toString());
                  if(temp1.isChecked()){
                      Log.d("Temp1",temp1.getText().toString());
                      mEdit_sid = temp1;

                  }
                  if(temp2.isChecked()){
                      Log.d("Temp2",temp2.getText().toString());
                      mEdit_sid = temp2;
                  }
                  if(temp3.isChecked()){
                      Log.d("Temp3",temp3.getText().toString());
                      mEdit_sid = temp3;
                  }
                  grn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


              }
          }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_callback, menu);
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
            try
            {
                Log.e("In doinback", "In doinback");
                //final EditText mEdit_sid, mEdit_max, mEdit_min;

                //mEdit_sid = (EditText) findViewById(R.id.editText5);
                //mEdit_max = (EditText) findViewById(R.id.editText6);
                //mEdit_min = (EditText) findViewById(R.id.editText7);


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
                    jsonobj.put("sensorId", Register.key.get(callback_sid));
                    jsonobj.put("minValue", callback_min);

                    jsonobj.put("maxValue", callback_max);

                    StringEntity se = new StringEntity(jsonobj.toString());

                    se.setContentType("application/json;charset=UTF-8");
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

                    HttpClient httpclient = new DefaultHttpClient();
                    URL url = new URL("http://10.42.0.19:6300/monitor/register_callback");
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
                            //mEdit_max.setText("");
                            //mEdit_min.setText("");

                            Log.e("Here", "Succesfully registered");
                            // alertDialog.setMessage("Callback Successfully Registered");
                            String urlGET = "http://10.42.0.19:6300/monitor/getresults?id="+callback_id;
                            while(true){
                                Log.d("In while","here"+callback_id);
                                Thread.sleep(2000);
                                HttpGet getMethod = new HttpGet(urlGET);
                                HttpResponse response1 = null;
                                HttpClient httpClient = new DefaultHttpClient();
                                try {
                                    response1 = httpClient.execute(getMethod);
                                    String resp_text1 = EntityUtils.toString(response1.getEntity());
                                    // Convert String to json object
                                    Log.e("Chintu patel",resp_text1);
                                    JSONObject json1 = new JSONObject(resp_text1);

                                    String message1 = json1.getString("Message");
                                    if (message1.equalsIgnoreCase("Pending")) {
                                        //Do Nothing as message is pending
                                        Log.e("Pending", "Pending");
                                    }
                                    else
                                    {
                                        Log.e("Got Results", "Got Results");
                                        Log.e("Data: ",""+json1.toString());

                                        //JSONArray ja = new JSONArray(json1.get(callback_id).toString());

                                        String output= "";
                                        // ITERATE THROUGH AND RETRIEVE CLUB FIELDS
                                        //int n = ja.length();
                                       // String to_print="";

                                        publishProgress("Alert");
                                        Log.e("Test","************************************************Chintu");
                                        break;
                                    }


                                } catch (Exception e) {
                                    Log.e("Exception for id", ""+e);


                                }
                            }




                        } else {
                            //alertDialog.setMessage("Error in Registering Callback. Try Again");
                            Log.e("In else", "Error in Registering callback");

                        }



                    } catch (Exception e) {
                        Log.e("error", " error message" + e);


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

        protected void onProgressUpdate(String...text){
            Toast.makeText(getApplicationContext(), text[0], Toast.LENGTH_LONG).show();
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
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
