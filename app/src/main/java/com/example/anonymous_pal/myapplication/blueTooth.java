package com.example.anonymous_pal.myapplication;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothHealthCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;


/**
 * Created by anonymous_pal on 21.4.2017.
 */

public class blueTooth extends Service {

    public static final int RESULT_OK = 0;
    public static final int RESULT_FAIL = -1;


    private static final String TAG = "BluetoothHDPService";
    private BluetoothHealthAppConfiguration mHealthAppConfig;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHealth mBluetoothHealth;
    private BluetoothDevice mDevice;
    private Messenger mClient;
    private int mChannelId;


    // Status codes sent back to the UI client.
    // Application registration complete.
    public static final int STATUS_HEALTH_APP_REG = 100;
    // Application unregistration complete.
    public static final int STATUS_HEALTH_APP_UNREG = 101;
    // Channel creation complete.
    public static final int STATUS_CREATE_CHANNEL = 102;
    // Channel destroy complete.
    public static final int STATUS_DESTROY_CHANNEL = 103;
    // Reading data from Bluetooth HDP device.
    public static final int STATUS_READ_DATA = 104;
    // Done with reading data.
    public static final int STATUS_READ_DATA_DONE = 105;
    // Message codes received from the UI client.
    // Register client with this service.
    public static final int MSG_REG_CLIENT = 200;
    // Unregister client from this service.
    public static final int MSG_UNREG_CLIENT = 201;
    // Register health application.
    public static final int MSG_REG_HEALTH_APP = 300;
    // Unregister health application.
    public static final int MSG_UNREG_HEALTH_APP = 301;
    // Connect channel.
    public static final int MSG_CONNECT_CHANNEL = 400;
    // Disconnect channel.
    public static final int MSG_DISCONNECT_CHANNEL = 401;

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (mBluetoothAdapter == null || (!mBluetoothAdapter.isEnabled())) {
            Toast.makeText(getApplicationContext(), "No bluetooth Detected", Toast.LENGTH_LONG).show();
            stopSelf();
        }

        //Get a reference to bluetooth Proxy object...Service listener is implemented below

        if (!mBluetoothAdapter.getProfileProxy(this, mBluetoothServiceListener, BluetoothProfile.HEALTH)) {
            Toast.makeText(this, "bluetooth_health_profile_not_available", Toast.LENGTH_LONG);
            stopSelf();
            return;
        }


    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Sends an update message to registered UI client.
    private void sendMessage(int what, int value) {
        if (mClient == null) {
            Log.d(TAG, "No clients registered.");
            return;
        }
        try {
            mClient.send(Message.obtain(null, what, value, 0));
        } catch (RemoteException e) {
            // Unable to reach client.
            e.printStackTrace();
        }
    }




    // Callbacks to handle connection set up and disconnection clean up.
    private final BluetoothProfile.ServiceListener mBluetoothServiceListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEALTH) {
                mBluetoothHealth = (BluetoothHealth) proxy;
                if (Log.isLoggable(TAG, Log.DEBUG))
                    Log.d(TAG, "onServiceConnected to profile: " + profile);
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEALTH) {
                mBluetoothHealth = null;
            }
        }
    };


    private final BluetoothHealthCallback mHealthCallback = new BluetoothHealthCallback() {
        // Callback to handle application registration and unregistration events.  The service
        // passes the status back to the UI client.
        public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration config,
                                                         int status) {
            if (status == BluetoothHealth.APP_CONFIG_REGISTRATION_FAILURE) {
                mHealthAppConfig = null;
                sendMessage(STATUS_HEALTH_APP_REG, RESULT_FAIL);
            } else if (status == BluetoothHealth.APP_CONFIG_REGISTRATION_SUCCESS) {
                mHealthAppConfig = config;
                sendMessage(STATUS_HEALTH_APP_REG, RESULT_OK);
            } else if (status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_FAILURE ||
                    status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_SUCCESS) {
                sendMessage(STATUS_HEALTH_APP_UNREG,
                        status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_SUCCESS ?
                                RESULT_OK : RESULT_FAIL);
            }
        }


    };

    // Thread to read incoming data received from the HDP device.  This sample application merely
    // reads the raw byte from the incoming file descriptor.  The data should be interpreted using
    // a health manager which implements the IEEE 11073-xxxxx specifications.
    private class ReadThread extends Thread {
        private ParcelFileDescriptor mFd;
        public ReadThread(ParcelFileDescriptor fd) {
            super();
            mFd = fd;
        }
        @Override
        public void run() {
            FileInputStream fis = new FileInputStream(mFd.getFileDescriptor());
            final byte data[] = new byte[8192];
            try {
                while(fis.read(data) > -1) {
                    // At this point, the application can pass the raw data to a parser that
                    // has implemented the IEEE 11073-xxxxx specifications.  Instead, this sample
                    // simply indicates that some data has been received.
                    sendMessage(STATUS_READ_DATA, 0);
                }
            } catch(IOException ioe) {}
            if (mFd != null) {
                try {
                    mFd.close();
                } catch (IOException e) { /* Do nothing. */ }
            }
            sendMessage(STATUS_READ_DATA_DONE, 0);
        }
    }

}
