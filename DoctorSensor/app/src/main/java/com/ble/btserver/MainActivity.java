package com.ble.btserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    Hashtable<String,String> sensors = new Hashtable<String,String>();
    private static final int REQUEST_ENABLE_BT = 2;
    TextView log;
    private SensorSimulator smSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = (Button)this.findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(new Intent(getApplicationContext(), SensorService.class));
                smSensor =  new SensorSimulator();
                smSensor.execute("sensorss");
            }
        });

        final EditText ed1 = (EditText) this.findViewById(R.id.sensor1);

        Button sb1 = (Button)this.findViewById(R.id.button1);
        sb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(new Intent(getApplicationContext(), SensorService.class));
                String s = ed1.getText().toString();
                Toast.makeText(getApplicationContext(),"Set value " + s ,Toast.LENGTH_LONG).show();

                sensors.put("m1", s);
            }
        });

        Button stopbutton = (Button)this.findViewById(R.id.stopsensor);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(smSensor!=null){
                    if(smSensor.mmServerSocket!=null){
                        try {
                            smSensor.mmServerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    smSensor.cancel(true);
                }

            }
        });


        final EditText ed2 = (EditText) this.findViewById(R.id.sensor2);

        Button sb2 = (Button)this.findViewById(R.id.button2);
        sb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(new Intent(getApplicationContext(), SensorService.class));
                String s = ed2.getText().toString();
                Toast.makeText(getApplicationContext(),"Set value " + s ,Toast.LENGTH_LONG).show();

                sensors.put("m2", s);
            }
        });

        final EditText ed3 = (EditText) this.findViewById(R.id.sensor3);

        Button sb3 = (Button)this.findViewById(R.id.button3);
        sb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(new Intent(getApplicationContext(), SensorService.class));
                String s = ed3.getText().toString();
                sensors.put("m3", s);
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

        if (adapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }

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


    private class SensorSimulator extends AsyncTask<String, String, Void> {
        public BluetoothServerSocket mmServerSocket = null;



        public SensorSimulator(){

            sensors.put("m1", "80");

            sensors.put("m2","120");

            sensors.put("m3","70");

        }
        @Override
        protected Void doInBackground(String... params) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothSocket mmSocket = null;
            String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            byte[] buffer = new byte[4096];  // buffer store for the stream
            int bytes; // bytes returned from read()
            ByteArrayOutputStream out;

            try {
                // MY_UUID is the app's UUID string, also used by the client code
                mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("sensor", UUID.fromString(SPP_UUID));
            } catch (IOException e) {
            }
            Set<String> keyset = sensors.keySet();
            Random random = new Random();
            while (true) {

                /*for(String key : keyset){

                    double rand = random.nextDouble();
                    double scaled = rand * 55;
                    scaled = scaled - 10;

                    sensors.put(key,""+scaled);
                }*/


                try {
                    mmSocket = mmServerSocket.accept();
                } catch (IOException e) {
                    try {
                        mmSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    return null;
                }
                publishProgress("device connected");
                InputStream mmInStream = null;
                OutputStream mmOutStream = null;

                if (mmSocket != null) {
                    try {
                        mmInStream = mmSocket.getInputStream();
                        mmOutStream = mmSocket.getOutputStream();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    while (true) {
                        try {
                            out = new ByteArrayOutputStream();
                            int len = mmInStream.read(buffer);
                            out.write(buffer, 0, len);
                            String result = out.toString("UTF-8");
                            String s;

                            Log.d("SensorService", "from gateway " + result);
                            if (result.equals("finish")) {
                                break;
                            } else {
                                s = sensors.get(result);
                                mmOutStream.write(s.getBytes());
                                publishProgress("Sent to gateway "+s);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                            //break;
                        }

                    }

                    try {
                        mmInStream.close();
                        mmOutStream.close();
                        mmSocket.close();
                    } catch (Exception e) {

                    }
                }

            }
        }


        @Override
        protected void onPreExecute() {
            log.append("Sensors Started!!!\n");
        }

        @Override
        protected void onProgressUpdate(String... text) {
            log.append("iteration"+text[0]+"\n");
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

}
