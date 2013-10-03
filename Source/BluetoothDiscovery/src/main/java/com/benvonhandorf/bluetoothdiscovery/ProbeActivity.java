package com.benvonhandorf.bluetoothdiscovery;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Characteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Device;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.ProbeDevice;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.IRSensor.IrDataCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.SensorTagDevice;

/**
 * Created by benvh on 9/28/13.
 */
public class ProbeActivity extends Activity{
    private static final String TAG = ProbeActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 55555;
    private ProbeDevice _probeDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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
//            _sensorTagDevice = new Device.Builder<SensorTagDevice>()
//                            .withContext(this)
//                            .withBluetoothAdapter(bluetoothAdapter)
//                            .withDeviceReadyListener(this)
//                            .build();

            _probeDevice = new ProbeDevice(this, bluetoothAdapter);

            _probeDevice.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        _probeDevice.disconnect();
    }
}
