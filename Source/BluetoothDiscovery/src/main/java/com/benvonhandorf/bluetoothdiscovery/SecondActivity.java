package com.benvonhandorf.bluetoothdiscovery;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Characteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Device;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.IRSensor.IrDataCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.SensorTagDevice;

/**
 * Created by benvh on 9/28/13.
 */
public class SecondActivity extends Activity implements Device.DeviceReadyListener {
    private static final String TAG = SecondActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 55555;
    private SensorTagDevice _sensorTagDevice;

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
            _sensorTagDevice = new SensorTagDevice(this, bluetoothAdapter);
            _sensorTagDevice.setDeviceReadyListener(this).connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        _sensorTagDevice.disconnect();
    }

    @Override
    public void onDeviceReady(Device device) {
        //Enable the IR Sensor
        _sensorTagDevice.getIrService().getDataCharacteristic().setCharacteristicListener(new Characteristic.CharacteristicListener() {
            @Override
            public void onValueChanged(Characteristic characteristic) {
                IrDataCharacteristic irDataCharacteristic = (IrDataCharacteristic) characteristic;

                Log.v(TAG, String.format("IR Sensor Reading: %f %f", irDataCharacteristic.getAmbientTemperature(), irDataCharacteristic.getTargetTemperature()));
            }
        });

        _sensorTagDevice.getIrService().getConfigCharacteristic().enable();
        _sensorTagDevice.getIrService().getDataCharacteristic().read();
        _sensorTagDevice.getIrService().getDataCharacteristic().enableNotification();
    }
}
