package com.benvonhandorf.bluetoothdiscovery;

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
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Characteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Device;
import com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor.HRMSensor.BodySensorLocationCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor.HRMSensor.HeartRateCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor.PolarH6HRMDevice;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.IRSensor.IrDataCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.SensorTagDevice;

/**
 * Created by benvh on 9/28/13.
 */
public class HeartRateMonitorActivity extends Activity implements Device.OnDeviceStateChangedListener {
    private static final String TAG = HeartRateMonitorActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 55555;
    private PolarH6HRMDevice _device;

    @InjectView(R.id.text_value)
    TextView _textValue;
    private boolean _inFocus = false;
    private Handler _handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Views.inject(this);

        _handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        _inFocus = true ;

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            _device = new PolarH6HRMDevice(this, bluetoothAdapter);

            _device.setDeviceReadyListener(this);

            _device.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        _inFocus = false ;

        _device.disconnect();
    }

    @Override
    public void onDeviceReady(Device device) {
        Log.v(TAG, "Device ready");

        //Enable the IR Sensor
        _device.getHRMService()
                .getHeartRateCharacteristic()
                .setCharacteristicListener(new Characteristic.CharacteristicListener() {
                    @Override
                    public void onValueChanged(Characteristic characteristic) {
                        HeartRateCharacteristic heartRateCharacteristic = (HeartRateCharacteristic) characteristic;

                        final int bpmReading = heartRateCharacteristic.getBPM();

                        Log.v(TAG, String.format("Heart Rate Reading: %d bpm"
                                , bpmReading));

                        _handler.post(new Runnable() {
                            @Override
                            public void run() {
                                _textValue.setText(String.format("%d bpm", bpmReading));
                            }
                        });
                    }
                });

        _device.getHRMService()
                .getBodySensorLocationCharacteristic()
                .setCharacteristicListener(new Characteristic.CharacteristicListener() {
                    @Override
                    public void onValueChanged(Characteristic characteristic) {
                        BodySensorLocationCharacteristic bodySensorLocationCharacteristic = (BodySensorLocationCharacteristic) characteristic;

                        Log.v(TAG, String.format("Body Location Changed: %d"
                                , bodySensorLocationCharacteristic.getLocation()));
                    }
                });

        _device.getHRMService().getBodySensorLocationCharacteristic().read();
        _device.getHRMService().getHeartRateCharacteristic().enableNotification();
    }

    @Override
    public void onDeviceDisconnect(Device device) {
        Log.v(TAG, "Device disconnected");
        if (_inFocus) {
            //Attempt to reconnect
            Log.v(TAG, "Attempting reconnect");
            device.connect();
        }
    }
}
