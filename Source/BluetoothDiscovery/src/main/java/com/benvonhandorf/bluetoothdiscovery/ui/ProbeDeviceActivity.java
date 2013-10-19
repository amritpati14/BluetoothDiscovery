package com.benvonhandorf.bluetoothdiscovery.ui;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.*;
import com.benvonhandorf.bluetoothdiscovery.R;

import java.util.ArrayList;

/**
 * Created by benvh on 9/28/13.
 */
public class ProbeDeviceActivity extends ListActivity implements Device.OnDeviceStateChangedListener {
    private static final String TAG = ProbeDeviceActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 55555;
    private ProbeDevice _probeDevice;

    @InjectView(R.id.text_device_name)
    TextView _deviceName;

    @InjectView(R.id.text_device_id)
    TextView _deviceType;

    Handler _uiThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _uiThreadHandler = new Handler();

        setContentView(R.layout.activity_probe_device);

        Views.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            //Connect the probe device to the first recognized Bluetooth SMART device
            _probeDevice = new ProbeDevice(this, bluetoothAdapter);
            _probeDevice.setDeviceReadyListener(this);

            _probeDevice.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        _probeDevice.disconnect();
    }

    @Override
    public void onDeviceReady(final Device device) {
        //Interrogate the object model for services, characteristics and descriptors and write out the results
        //to the activity and to the log (for convenience)

        _probeDevice.onDeviceReady(device);


        _uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                showDeviceInformation(device);
            }
        });
    }

    private void showDeviceInformation(Device device) {
        _deviceName.setText(device.getBluetoothDevice().getName());
        _deviceType.setText(String.format("0x%x", device.getBluetoothDevice().getType()));

        ArrayList<String> deviceInformation = new ArrayList<String>();

        for (Service service : device.getServices()) {
            deviceInformation.add(String.format("Service: %s", service.getUUID()));

            for (Characteristic characteristic : service.getCharacteristics()) {
                deviceInformation.add(String.format("  Characteristic: %s", characteristic.getUUID()));

                for (Descriptor descriptor : characteristic.getDescriptors()) {
                    deviceInformation.add(String.format("    Descriptor: %s", descriptor.getUUID()));
                }
            }
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceInformation));
    }

    @Override
    public void onDeviceDisconnect(Device device) {
        _deviceName.setText("Disconnected");
    }
}
