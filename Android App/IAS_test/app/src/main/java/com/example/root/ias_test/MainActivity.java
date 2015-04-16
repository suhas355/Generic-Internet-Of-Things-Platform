package com.example.root.ias_test;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {

    private static final long REPEAT_TIME = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       /* final Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v)
                                       {
          Thread thread = new Thread(new Runnable(){
                                               @Override
                                               public void run() {
          try
          {
              String param1 = "1";
              String urlGET = "http://192.168.217.106?param1=" + param1;

              HttpGet getMethod = new HttpGet(urlGET);
              HttpResponse response = null;
              HttpClient httpClient = new DefaultHttpClient();
              try {
                  response = httpClient.execute(getMethod);
              } catch (Exception e)
              {
                  Log.e("message","message");
                  AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                  alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                  alertDialog.setTitle("Alert");
                  e.printStackTrace();
                  alertDialog.setMessage("Exception: " + e.getLocalizedMessage() + e);
                  alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                  new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        }
                  });
                alertDialog.show();

               }
           } catch (Exception e)
            {
                e.printStackTrace();
            }
          }
                    });

        thread.start();

        }
                }
    );

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Perform action on click

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                JSONObject jsonobj; // declared locally so that it destroys after serving its purpose
                jsonobj = new JSONObject();
                try {
                    // adding some keys
                    jsonobj.put("name", "Ruchir");



                    HttpClient httpclient = new DefaultHttpClient();
                    URL url = new URL("http://192.168.217.106:6010");
                    HttpPost httppostreq = new HttpPost(url.toString());
                    StringEntity se = new StringEntity(jsonobj.toString());



                    httppostreq.setHeader("Accept", "application/json");
                    httppostreq.setHeader("Content-type", "application/json");



                    //System.out.println(se);
                    se.setContentType("application/json;charset=UTF-8");
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                    httppostreq.setEntity(se);

                    alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("set content");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                    HttpResponse response;

                    response = httpclient.execute(httppostreq);



                } catch (Exception e) {
                    Log.d("*******************", "In Exception");
                    alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    e.printStackTrace();
                    alertDialog.setMessage(e.getLocalizedMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }


            }
        });*/
        final Button button4 = (Button) findViewById(R.id.button4);
        final Context context = this;
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                try
                {
                    Intent intent = new Intent(context, User_Input.class);
                    startActivity(intent);

                }catch(Exception e)
                {

                }

            }
        }
        );

        final Button button5 = (Button) findViewById(R.id.button5);
        final Context context1 = this;
        button5.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v)
                                       {

                                           try
                                           {
                                               Intent intent1 = new Intent(context1, Input_bySensorID.class);
                                               startActivity(intent1);

                                           }catch(Exception e)
                                           {

                                           }

                                       }
                                   }
        );

        // Register Callback
        final Button button7 = (Button) findViewById(R.id.button7);
        final Context context8 = this;
        button7.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v)
                                       {

                                           try
                                           {
                                               Intent intent = new Intent(context8, Register_Callback.class);
                                               startActivity(intent);

                                           }catch(Exception e)
                                           {

                                           }

                                       }
                                   }
        );

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
}
