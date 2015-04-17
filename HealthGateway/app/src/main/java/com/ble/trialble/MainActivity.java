package com.ble.trialble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {


    private static final int REQUEST_ENABLE_BT = 2;
    private static final int GID = 1;
    private static final String macad = "84:51:BF:2C:8C:05";  // batman

    private TextView log;

    Hashtable<String,Hashtable<String,String>> sensors = new Hashtable<String,Hashtable<String,String>>();
    Hashtable<String,ArrayList<String>> devices = new Hashtable<>();
    private GatewayRunner grn;
    private PingResponder prn;
    private FilterComm fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Initializer().execute();

        Button startButton = (Button)this.findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grn = new GatewayRunner();
                prn = new PingResponder();
                fc = new FilterComm();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    grn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    prn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    fc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else {
                    grn.execute();
                    prn.execute();
                    fc.execute();

                }
                Log.d("here","asynctask");
            }
        });

        Button stopButton = (Button)this.findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    if(grn!=null)
                        grn.mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(grn!=null)
                    grn.cancel(true);
                if(prn!=null)
                    prn.cancel(true);
                if(fc!=null)
                    fc.cancel(true);

            }
        });

        log  = (TextView)this.findViewById(R.id.tw1);
        log.setMovementMethod(new ScrollingMovementMethod());
        log.setText("Ready!!\n");
    }


     @Override
    public void onStart(){
        super.onStart();
         BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (adapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }

         /*if (adapter.getScanMode() !=
                 BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
             Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
             discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
             startActivity(discoverableIntent);
         }*/
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

    private class Initializer extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            ArrayList<String> senlist=null;
            Hashtable<String,String> ht = new Hashtable<>();

            String messageStr="84:51:BF:2C:8C:05";   // batman
            int server_port = 30001;
            DatagramSocket sClient = null;
            try {
                sClient = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            InetAddress local = null;
            try {
                local = InetAddress.getByName("10.42.0.19");   // batman
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            int msg_length=messageStr.length();
            byte[] message = messageStr.getBytes();
            DatagramPacket pClient = new DatagramPacket(message, msg_length,local,server_port);
            Log.e("before sent","before sent");
            try {
                sClient.send(pClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("sent","Successfully sent");
            byte[] message2 = new byte[15000];
            DatagramPacket p2 = new DatagramPacket(message2, message2.length);
            Log.e("before receive","before receive");
            try {
                sClient.receive(p2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String txt = new String(message2, 0, p2.getLength());
            Log.e("received",txt);
            if(txt.length()>10)
                publishProgress("Gateway Initialised from Repository!");
            else{
                publishProgress("Error in initialization!");
            }

            publishProgress("Sensors on this Gateway!");
            try {
                JSONArray repo = new JSONArray(txt);
                JSONObject obj=null;
                for(int jb=0;jb<repo.length();jb++){
                    obj = (JSONObject) repo.get(jb);
                    ht = new Hashtable<>();
                    ht.put("sensorid",(String) obj.get("sensorId"));
                    Log.d("ids ",(String) obj.get("sensorId"));
                    ht.put("type",(String) obj.get("type"));
                    ht.put("sensordevice",(String) obj.get("deviceName"));
                    if(devices.containsKey((String) obj.get("deviceName"))){
                        senlist = devices.get((String) obj.get("deviceName"));
                        senlist.add((String) obj.get("sensorId"));
                        devices.put((String) obj.get("deviceName"),senlist);
                    }
                    else{
                        senlist = new ArrayList<>();
                        senlist.add((String) obj.get("sensorId"));
                        devices.put((String) obj.get("deviceName"), senlist);
                    }
                    ht.put("unit",(String) obj.get("unit"));
                    ht.put("location",(String) obj.get("location"));
                   /* JSONArray tmparr = (JSONArray) obj.get("geolocation");

                    ht.put("longitude",((Double)((JSONObject) tmparr.get(1)).get("longitude")).toString());
                    ht.put("latitude",((Double)((JSONObject) tmparr.get(0)).get("latitude")).toString());*/
                    JSONArray tmparr = (JSONArray) obj.get("geo");
                    ht.put("longitude",((Double)tmparr.get(0)).toString());
                    ht.put("latitude",((Double)tmparr.get(1)).toString());
                    ht.put("data","-1");
                    ht.put("status","1");
                    sensors.put(ht.get("sensorid"),ht);
                    publishProgress("Sensor id: "+ht.get("sensorid"));
                    publishProgress("Sensor device: "+ht.get("sensordevice"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            /*
            ht.put("sensorid","1");
            ht.put("type","Temperature");
            ht.put("sensordevice","helloworld");
            if(devices.containsKey("helloworld")){
                senlist = devices.get("helloworld");
                senlist.add("1");
                devices.put("helloworld",senlist);
            }
            else{
                senlist = new ArrayList<>();
                senlist.add("1");
                devices.put("helloworld",senlist);
            }
            ht.put("unit","Celsius");
            ht.put("location","hogwarts");
            ht.put("longitude","45.43");
            ht.put("latitude","34.43");
            ht.put("data","-1");
            ht.put("status","1");
            sensors.put(ht.get("sensorid"),ht);

            ht = new Hashtable<>();
            ht.put("sensorid","2");
            ht.put("type","Temperature");
            ht.put("sensordevice","helloworld");
            if(devices.containsKey("helloworld")){
                senlist = devices.get("helloworld");
                senlist.add("2");
                devices.put("helloworld",senlist);
            }
            else{
                senlist = new ArrayList<>();
                senlist.add("2");
                devices.put("helloworld",senlist);
            }
            ht.put("unit","Celsius");
            ht.put("status","1");
            ht.put("location","Godric's Hollow");
            ht.put("longitude","35.43");
            ht.put("latitude","32.43");
            ht.put("data","-1");
            sensors.put(ht.get("sensorid"),ht);*/

            return null;
        }

        protected void onProgressUpdate(String... text) {
            log.append(text[0]+"\n");
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }

    }

    private class PingResponder extends AsyncTask<String, String, String>{


        public static final int SERVERPORT = 33333;
        @Override
        protected String doInBackground(String... params) {
            Socket socket = null;
            DatagramSocket datagramSocket=null;
            try {
                //         serverSocket = new ServerSocket(SERVERPORT);
                datagramSocket=new DatagramSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject object;
            while (true) {

                try {
                    object = new JSONObject();
                    object.put("gid",""+GID);
                    object.put("mac",""+macad);
                    Set<String> keyset = sensors.keySet();
                    for(String key : keyset){
                        object.put(key,sensors.get(key).get("status"));
                    }
                    //     Log.e("hell","hi123");
                    byte[] message = new byte[2048];

                    String messageStr = object.toString();
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    datagramSocket.receive(p);
                    String txt = new String(message, 0, p.getLength());
                    //     socket = serverSocket.accept();

                    byte[] message2 = messageStr.getBytes();
                    DatagramPacket p2 = new DatagramPacket(message2, message2.length, p.getAddress(), p.getPort());
                    datagramSocket.send(p2);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        protected void onProgressUpdate(String... text) {
            log.append(text[0]+"\n");
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }

    }

    private class FilterComm extends AsyncTask<String, String, String>{


        public static final int SERVERPORT = 33334;
        public static final String SERVERIP = "10.42.0.19";  // batman


        @Override
        protected String doInBackground(String... params) {
            Socket socket = null;
            DatagramSocket datagramSocket=null;
            try {
                //         serverSocket = new ServerSocket(SERVERPORT);
                datagramSocket=new DatagramSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject object;
            while (true) {

                try {

                    //     Log.e("hell","hi123");
                    byte[] message = new byte[2048];


                    DatagramPacket p = new DatagramPacket(message, message.length);
                    datagramSocket.receive(p);
                    String txt = new String(message, 0, p.getLength());
                    //     socket = serverSocket.accept();
                    object = new JSONObject();
                    object.put("gid",""+GID);
                    object.put("mac",""+macad);
                    Set<String> keyset = sensors.keySet();
                    JSONArray datas = new JSONArray();
                    JSONObject temp=null;
                    for(String key : keyset){
                        Hashtable<String,String> ht  = sensors.get(key);
                        temp=new JSONObject();
                        temp.put("sensorId",ht.get("sensorid"));
                        temp.put("type", ht.get("type"));
                        temp.put("unit", ht.get("unit"));
                        temp.put("location", ht.get("location"));
                        JSONArray jsr = new JSONArray();
                        jsr.put(Double.parseDouble(ht.get("longitude")));
                        jsr.put(Double.parseDouble(ht.get("latitude")));
                        //JSONObject geotemp = new JSONObject();
                        //geotemp.put("longitude",ht.get("longitude"));
                        //geotemp.put("latitude",ht.get("latitude"));
                        temp.put("geo",jsr);
                        temp.put("data",ht.get("data"));

                        datas.put(temp);
                    }
                    object.put("data",datas);
                    String messageStr = object.toString();

                   /* object.put("sensor-1",sensors.get("1").get("data"));
                    object.put("sensor-2",sensors.get("2").get("data"));
                    String messageStr = object.toString();*/
                    byte[] message2 = messageStr.getBytes();
                    DatagramPacket p2 = new DatagramPacket(message2, message2.length, p.getAddress(), p.getPort());
                    datagramSocket.send(p2);

                    Log.e("responce to filter",messageStr);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        protected void onProgressUpdate(String... text) {
            log.append(text[0]+"\n");
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }

    }

    private class GatewayRunner extends AsyncTask<String, String, String>{

       public BluetoothSocket mmSocket = null;

        @Override
        protected String doInBackground(String... params) {
            Log.d("here","doinbckgrnd");
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            ByteArrayOutputStream out;
            byte[] buffer = new byte[4096];

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            List<String> s = new ArrayList<String>();
            BluetoothDevice temp = null;
            int turn = 0;
            while(true) {

               publishProgress("Polling Sequence: "+turn);
                Set<String> keyset = devices.keySet();
                turn++;
                for (String devc : keyset){

                    for (BluetoothDevice bt : pairedDevices) {
                        if (bt.getName().trim().equals(devc)) {
                            temp = bt;
                            break;
                        }
                    }

                    Log.d("here",devc);

                    if (temp != null) {
                        try {
                            mmSocket = temp.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mmSocket.connect();
                        } catch (IOException e) {
                            try {
                                mmSocket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            return null;
                        }
                        InputStream mmInStream = null;
                        OutputStream mmOutStream = null;
                        try {
                            mmInStream = mmSocket.getInputStream();
                            mmOutStream = mmSocket.getOutputStream();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ArrayList<String> senlist = devices.get(devc);
                        for (String senid : senlist){
                            try {
                                String msg = senid;

                                mmOutStream.write(msg.getBytes());
                                out = new ByteArrayOutputStream();
                                int len = mmInStream.read(buffer);
                                out.write(buffer, 0, len);

                                String result = out.toString("UTF-8");
                                Log.d("Sensor id" + senid, result);
                                publishProgress("Sensor ID: " + senid + " data: " + result + "\n");

                                Hashtable<String,String> htb = sensors.get(senid);
                                if(result.equals("NaN")){
                                    htb.put("status", "0");
                                }
                                else {
                                    htb.put("data", result);
                                }
                                sensors.put(senid,htb);
                                //publishProgress("------>>>>> "+senid);
                                //publishProgress(sensors.get(senid).get("data"));
                                // mHandler.obtainMessage(AbstractActivity.MESSAGE_READ,
                                // bytes, -1, buffer).sendToTarget();
                            } catch (Exception e) {
                                publishProgress(e.getMessage());

                                //break;
                            }

                        }
                        String msg = "finish";
                        try {
                            mmOutStream.write(msg.getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            mmInStream.close();
                            mmOutStream.close();
                            mmSocket.close();
                        } catch (Exception e) {

                        }
                    }
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


        @Override
        protected void onPreExecute() {
            log.append("Polling Sequence Initiated!!!\n");
        }

        @Override
        protected void onProgressUpdate(String... text) {
            log.append(text[0]+"\n");
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}
