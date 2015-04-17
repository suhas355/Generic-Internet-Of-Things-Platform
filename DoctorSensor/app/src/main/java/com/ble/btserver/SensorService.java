package com.ble.btserver;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SensorService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    public static final String BT_DEVICE = "btdevice";
    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    // device
    private AcceptThread mAcceptThread;
    private static ConnectedThread mConnectedThread;
    private int mState;
    public SensorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private synchronized void setState(int state) {
       Log.d("SensorServie", "setState() " + mState);
        mState = state;

    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SensorService", "Onstart Command");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
        return START_STICKY;
    }

    public synchronized void start() {
        Log.d("SensorService", "start");
        // Cancel any thread attempting to make a connection

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void stop() {
        Log.d("SensorService", "stop");

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }

    private synchronized void connected(BluetoothSocket mmSocket) {


        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        // Message msg =
        // mHandler.obtainMessage(AbstractActivity.MESSAGE_DEVICE_NAME);
        // Bundle bundle = new Bundle();
        // bundle.putString(AbstractActivity.DEVICE_NAME, "p25");
        // msg.setData(bundle);
        // mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);

    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("sensor", UUID.fromString(SPP_UUID) );
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                Log.d("SensorService", "Polling for device");
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    Log.d("SensorService", "Got one device!! :D");
                    // Do work to manage the connection (in a separate thread)
                    connected(socket);
                    /*try {
                        mmServerSocket.close();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        break;
                    }*/

                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { };
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[4096];  // buffer store for the stream
            int bytes; // bytes returned from read()
            ByteArrayOutputStream out;

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStrea

                    out = new ByteArrayOutputStream();
                    int len = mmInStream.read(buffer);
                    out.write(buffer, 0, len);
                    String result = out.toString("UTF-8");
                    String s;
                    Log.d("SensorService", "from gateway "+result);
                    if(result.equals("1")){
                        s = "Data for the first sensor!";
                        write(s.getBytes());
                    }
                    else if(result.equals("2")){
                        s = "Data for the second sensor!";
                        write(s.getBytes());
                    }
                    else if(result.equals("finish")){
                        mmInStream.close();
                        mmOutStream.close();
                        cancel();
                        break;
                    }



                } catch (Exception e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
