package com.example.root.ias_test;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by root on 9/4/15.
 */


public class MyService extends IntentService
{

    public MyService() {

        super("MyServiceName");

    }

    @Override

    protected void onHandleIntent(Intent intent) {

        Log.e("MyService", "About to execute MyTask");

        new MyTask().execute();

        this.sendNotification(this);

    }

    private class MyTask extends AsyncTask<String, Void, Boolean> {

        @Override

        protected Boolean doInBackground(String... strings)
        {

            Log.e("MyService - MyTask", "Calling doInBackground within MyTask");
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {

                    try
                    {
                        //GET REQUEST


                        String urlGET = "192.168.217.106:6010/getdata/getresults?id=";

                        HttpGet getMethod = new HttpGet(urlGET);
                        HttpResponse response = null;
                        HttpClient httpClient = new DefaultHttpClient();
                        try {
                            response = httpClient.execute(getMethod);
                            String resp_text =  EntityUtils.toString(response.getEntity());
                            // Convert String to json object
                            JSONObject json = new JSONObject(resp_text);

                            String message = json.getString("Message");
                            if(message.equals("pending"))
                            {
                                //Do Nothing as message is pending
                            }
                            else
                            {
                                //Display results in alertbox
                                AlertDialog alertDialog = new AlertDialog.Builder(MyService.this).create();
                                alertDialog = new AlertDialog.Builder(MyService.this).create();
                                alertDialog.setTitle("Alert");

                                alertDialog.setMessage("ALERT:  LIMITS BROKEN !!!");
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
                            Log.e("message","message");


                        }


                      /*  JSONObject jsonobj; // declared locally so that it destroys after serving its purpose
                        jsonobj = new JSONObject();
                        jsonobj.put("sensorId", "102");
                        jsonobj.put("minValue", "30");

                        jsonobj.put("maxValue", "55");
                        StringEntity se = new StringEntity(jsonobj.toString());

                        se.setContentType("application/json;charset=UTF-8");
                        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

                        HttpClient httpclient = new DefaultHttpClient();
                        URL url = new URL("http://192.168.217.108:6010/getdata");
                        HttpPost httppostreq = new HttpPost(url.toString());
                        try
                        {
                            httppostreq.setEntity(se);
                            HttpResponse response;

                            response = httpclient.execute(httppostreq);


                        } catch (Exception e)
                        {
                            Log.e("error message"," error message");


                        }*/
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();




            return false;

        }

    }

    private void sendNotification(Context context) {

        Intent notificationIntent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationManager notificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification =  new Notification(android.R.drawable.star_on, "Refresh", System.currentTimeMillis());

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.setLatestEventInfo(context, "Title","Content", contentIntent);

        notificationMgr.notify(0, notification);

    }

}


