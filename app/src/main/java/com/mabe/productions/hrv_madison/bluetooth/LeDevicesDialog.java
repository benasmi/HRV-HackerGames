package com.mabe.productions.hrv_madison.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mabe.productions.hrv_madison.R;

import java.util.ArrayList;

public class LeDevicesDialog {

    private final long SCAN_PERIOD = 10000;

    private final Context context;

    Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    boolean isScanning = false;
    private Dialog dialog;
    private Button button_scan;
    private ListView listview_devices;
    public DevicesAdapter adapter;
    private SharedPreferences devicePreference;

    private boolean isReceiverRegistered = false;

    public LeDevicesDialog(Context context) {

        this.context = context;
        registerReceiver();

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        //Initial bluetooth stuff
        mHandler = new Handler();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
        devicePreference = context.getSharedPreferences("SavedDevice", Context.MODE_PRIVATE);

        setupDialog();
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        dialog.setOnCancelListener(onCancelListener);
    }

    private BroadcastReceiver mGattServerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothGattService.ACTION_CONNECTED:
                    BluetoothDevice device = intent.getParcelableExtra("BT_DEVICE");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        device.createBond();
                    }
                    String MAC_adress = device.getAddress();
                    String device_name = device.getName();
                    devicePreference.edit().putString("MAC_adress", MAC_adress).apply();
                    devicePreference.edit().putString("device_name", device_name).apply();

                    dialog.cancel();
                    break;

                case BluetoothGattService.ACTION_DISCONNECTED:
                    devicePreference.edit().clear().apply();
                    break;
            }

            for (int i = 0; i < adapter.devices.size(); i++) {
                adapter.devices.get(i).setInProgress(false);
            }


            for (int i = 0; i < adapter.devices.size(); i++) {
                adapter.devices.get(i).setInProgress(false);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void setupDialog() {

        //Setting up dialog and it's properties
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.device_selection_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //Initializing views
        button_scan = dialog.findViewById(R.id.action_scan);
        listview_devices = dialog.findViewById(R.id.list_devices);

        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScanning(true);
                button_scan.setText(R.string.already_scanning);
                button_scan.setEnabled(false);
            }
        });

        adapter = new DevicesAdapter();
        listview_devices.setAdapter(adapter);

        dialog.show();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {

                    //If device is already present, adding connectivity icon
                    boolean isDevicePresent = false;
                    for (int i = 0; i < adapter.devices.size(); i++) {
                        if (adapter.devices.get(i).getDevice().equals(device)) {
                            adapter.devices.get(i).setNearby(true);
                            isDevicePresent = true;
                        }
                    }

                    if (!isDevicePresent && device.getName() != null) {
                        Log.i("Devices", "" + device.getName() + "?" + device.getAddress());
                        adapter.devices.add(new DeviceViewInfo(true, false, false, device));
                    }
                    adapter.notifyDataSetChanged();
                }
            };

    private void toggleScanning(boolean isScanning) {

        this.isScanning = isScanning;

        if (isScanning) {

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mBluetoothScannerReceiver, filter);
            mBluetoothAdapter.startLeScan(mLeScanCallback);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    button_scan.setEnabled(true);
                    button_scan.setText(R.string.scan);
                }
            }, SCAN_PERIOD);

        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }


    private final BroadcastReceiver mBluetoothScannerReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                //If device is already present, adding connectivity icon
                boolean isDevicePresent = false;
                for (int i = 0; i < adapter.devices.size(); i++) {
                    if (adapter.devices.get(i).getDevice().equals(device)) {
                        adapter.devices.get(i).setNearby(true);
                        isDevicePresent = true;
                    }
                }

                if (!isDevicePresent) {
                    adapter.devices.add(new DeviceViewInfo(true, false, false, device));
                }
                adapter.notifyDataSetChanged();
            }

        }
    };

    private class DevicesAdapter extends BaseAdapter {

        ArrayList<DeviceViewInfo> devices = new ArrayList<DeviceViewInfo>();

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public DeviceViewInfo getItem(int position) {
            return devices.get(position);
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final DeviceViewInfo deviceInfo = devices.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.device_list_item, parent, false);

            TextView txt_device_name = row.findViewById(R.id.device_name);
            TextView txt_device_id = row.findViewById(R.id.device_id);
            ImageView img_connectivity_indicator = row.findViewById(R.id.connectivity_indicator);
            ImageView img_bond_indicator = row.findViewById(R.id.bond_indicator);
            ProgressBar img_progress_indicator = row.findViewById(R.id.device_list_item_progress);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startService(new Intent(context, BluetoothGattService.class).putExtra("device", deviceInfo.getDevice()));
                    deviceInfo.setInProgress(true);
                    notifyDataSetChanged();
                }
            });

            txt_device_name.setText(deviceInfo.getDevice().getName());
            txt_device_id.setText(deviceInfo.getDevice().getAddress());

            if (deviceInfo.isBonded()) {
                img_bond_indicator.setVisibility(View.VISIBLE);
            }
            if (deviceInfo.isNearby()) {
                img_connectivity_indicator.setVisibility(View.VISIBLE);
            }
            if (deviceInfo.isInProgress()) {
                img_progress_indicator.setVisibility(View.VISIBLE);
            }


            return row;
        }

    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothGattService.ACTION_CONNECTED);
            filter.addAction(BluetoothGattService.ACTION_DISCONNECTED);
            LocalBroadcastManager.getInstance(context).registerReceiver(mGattServerReceiver, filter);

            isReceiverRegistered = true;
        }
    }

}