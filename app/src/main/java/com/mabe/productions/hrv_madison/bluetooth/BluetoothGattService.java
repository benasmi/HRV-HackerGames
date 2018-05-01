package com.mabe.productions.hrv_madison.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mabe.productions.hrv_madison.Utils;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothGattService extends Service {

    private static final long CONNECTION_TIMEOUT = 15000;

    private BluetoothGatt gatt;
    private BluetoothDevice device;
    public static final String ACTION_CONNECTED = "CONNECTED_TO_GAAT";
    public static final String ACTION_DISCONNECTED = "DISCONNECTED_FROM_GAAT";
    public static final String ACTION_RECEIVING_DATA = "RECEIVING_DATA";

    final ArrayList<Integer> rrList = new ArrayList<Integer>();
    final ArrayList<Integer> rrListSimple = new ArrayList<Integer>();
    private static UUID HEART_RATE_SERVICE_UUID = Utils.convertFromInteger(0x180D);
    private static UUID HEART_RATE_MEASUREMENT_CHAR_UUID = Utils.convertFromInteger(0x2A37);
    private static UUID HEART_RATE_CONTROL_POINT_CHAR_UUID = Utils.convertFromInteger(0x2A39);
    private static UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = Utils.convertFromInteger(0x2902);
    private static String TAG = "TEST";

    public static boolean isGattDeviceConnected = false;
    @Override
    public IBinder onBind(Intent intent) {



        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        device = intent.getParcelableExtra("device");

        //In a situation, where user is already connected to a device,
        //and decides to (for some reason) connect to another device,
        //the gatt callbacks are still fired from the old device.
        //closing the gatt service if it's already running solves this problem.
        if(gatt != null){
            gatt.close();
            isGattDeviceConnected = false;
        }

        gatt = device.connectGatt(this, true, gattCallback);

        //Timeout the connection after some time if not connected
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isGattDeviceConnected){
                    gatt.close();
                    LocalBroadcastManager.getInstance(BluetoothGattService.this).sendBroadcast(new Intent(ACTION_DISCONNECTED));
                }
            }
        }, CONNECTION_TIMEOUT);


        return START_NOT_STICKY;
    }



    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if(newState == BluetoothProfile.STATE_CONNECTED){
                gatt.discoverServices();
            }else{
                LocalBroadcastManager.getInstance(BluetoothGattService.this).sendBroadcast(new Intent(ACTION_DISCONNECTED));
                isGattDeviceConnected = false;
            }

        }

        
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            isGattDeviceConnected = true;
            LocalBroadcastManager.getInstance(BluetoothGattService.this).sendBroadcast(new Intent(ACTION_CONNECTED).putExtra("BT_DEVICE",device));



            BluetoothGattCharacteristic characteristic =
                    gatt.getService(HEART_RATE_SERVICE_UUID)
                            .getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID);
            gatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor =
                    characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);

            descriptor.setValue(
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);


            super.onServicesDiscovered(gatt, status);
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            BluetoothGattCharacteristic characteristic =
                    gatt.getService(HEART_RATE_SERVICE_UUID)
                            .getCharacteristic(HEART_RATE_CONTROL_POINT_CHAR_UUID);
            characteristic.setValue(new byte[]{1, 1});
            gatt.writeCharacteristic(characteristic);

            super.onDescriptorWrite(gatt, descriptor, status);
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            // This is special handling for the Heart Rate Measurement profile. Data
            // parsing is carried out as per profile specifications.
            int heartRate = extractHeartRate(characteristic);
            double contact = extractContact(characteristic);
            double energy = extractEnergyExpended(characteristic);
            int intervals[] = extractBeatToBeatInterval(characteristic);


            LocalBroadcastManager.getInstance(BluetoothGattService.this).sendBroadcast(new Intent(ACTION_RECEIVING_DATA).putExtra("RR_intervals",intervals).putExtra("BPM",heartRate));


            super.onCharacteristicChanged(gatt, characteristic);
        }
    };


    private static int extractHeartRate(
            BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
//        Log.d(TAG, "Heart rate flag: " + flag);
        int format = -1;
        // Heart rate bit number format
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
//            Log.d(TAG, "Heart rate format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
//            Log.d(TAG, "Heart rate format UINT8.");
        }
        final int heartRate = characteristic.getIntValue(format, 1);
//        Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        return heartRate;
    }

    private static double extractContact(
            BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Sensor contact status
        if ((flag & 0x02) != 0) {
//            Log.d(TAG, "Heart rate sensor contact info exists");
            if ((flag & 0x04) != 0) {
//                Log.d(TAG, "Heart rate sensor contact is ON");
            } else {
//                Log.d(TAG, "Heart rate sensor contact is OFF");
            }
        } else  {
//            Log.d(TAG, "Heart rate sensor contact info doesn't exists");
        }
        //final int heartRate = characteristic.getIntValue(format, 1);
        //Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        return 0.0d;
    }

    private static double extractEnergyExpended(
            BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Energy calculation status
        if ((flag & 0x08) != 0) {
//            Log.d(TAG, "Heart rate energy calculation exists.");
        } else {
//            Log.d(TAG, "Heart rate energy calculation doesn't exists.");
        }
        //final int heartRate = characteristic.getIntValue(format, 1);
        //Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        return 0.0d;
    }

    private static int[] extractBeatToBeatInterval(
            BluetoothGattCharacteristic characteristic) {

        int emptyArray[]={};
        int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        int format = -1;
        int energy = -1;
        int offset = 1; // This depends on hear rate value format and if there is energy data
        int rr_count = 0;

        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
//            Log.d(TAG, "Heart rate format UINT16.");
            offset = 3;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
//            Log.d(TAG, "Heart rate format UINT8.");
            offset = 2;
        }
        if ((flag & 0x08) != 0) {
            // calories present
            energy = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            offset += 2;
//            Log.d(TAG, "Received energy: {}"+ energy);
        }
        if ((flag & 0x16) != 0){
            // RR stuff.
//            Log.d(TAG, "RR stuff found at offset: "+ offset);
//            Log.d(TAG, "RR length: "+ (characteristic.getValue()).length);
            rr_count = ((characteristic.getValue()).length - offset) / 2;
//            Log.d(TAG, "RR length: "+ (characteristic.getValue()).length);
//            Log.d(TAG, "rr_count: "+ rr_count);
            if (rr_count > 0) {
                int[] mRr_values = new int[rr_count];
                for (int i = 0; i < rr_count; i++) {
                    mRr_values[i] = characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                    offset += 2;
//                    Log.d(TAG, "Received RR: " + mRr_values[i]);
                }
                return mRr_values;
            }
        }
//        Log.d(TAG, "No RR data on this update: ");
        return emptyArray;
    }






}
