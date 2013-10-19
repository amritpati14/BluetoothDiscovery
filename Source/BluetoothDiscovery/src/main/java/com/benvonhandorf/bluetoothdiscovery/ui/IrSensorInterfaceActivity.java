package com.benvonhandorf.bluetoothdiscovery.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.Characteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.Device;
import com.benvonhandorf.bluetoothdiscovery.R;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDeviceWrapper.IRSensor.IrDataCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDeviceWrapper.SensorTagDevice;

/**
 * Created by benvh on 9/28/13.
 */
public class IrSensorInterfaceActivity extends Activity implements Device.OnDeviceStateChangedListener {
    private static final String TAG = IrSensorInterfaceActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 55555;
    private SensorTagDevice _sensorTagDevice;

    @InjectView(R.id.text_value)
    TextView _reading;

    Handler _uiThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _uiThreadHandler = new Handler();

        setContentView(R.layout.activity_simple_data);

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
//            _sensorTagDevice = new Device.Builder<SensorTagDevice>()
//                            .withContext(this)
//                            .withBluetoothAdapter(bluetoothAdapter)
//                            .withDeviceReadyListener(this)
//                            .build();

            _sensorTagDevice = new SensorTagDevice(this, bluetoothAdapter);

            _sensorTagDevice.setDeviceReadyListener(this);

            _sensorTagDevice.connect();
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
        _sensorTagDevice.getIrService()
                .getDataCharacteristic()
                .setCharacteristicListener(new Characteristic.CharacteristicListener() {
                    @Override
                    public void onValueChanged(Characteristic characteristic) {
                        final IrDataCharacteristic irDataCharacteristic = (IrDataCharacteristic) characteristic;

                        displayTemperatureData(irDataCharacteristic);
                    }
                });

        _sensorTagDevice.getIrService().getConfigCharacteristic().enable();
        _sensorTagDevice.getIrService().getDataCharacteristic().read();
        _sensorTagDevice.getIrService().getDataCharacteristic().enableNotification();
    }

    private void displayTemperatureData(final IrDataCharacteristic irDataCharacteristic) {
        _uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, String.format("IR Sensor Reading: %.1f %.1f"
                        , irDataCharacteristic.getAmbientTemperature()
                        , irDataCharacteristic.getTargetTemperature()));

                _reading.setText(String.format("Ambient: %.1f\nTarget: %.1f",
                        irDataCharacteristic.getAmbientTemperature(),
                        irDataCharacteristic.getTargetTemperature()));
            }
        });
    }

    @Override
    public void onDeviceDisconnect(Device device) {
        _reading.setText("Disconnected");
    }
}
