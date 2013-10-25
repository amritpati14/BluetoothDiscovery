package com.benvonhandorf.bluetoothdiscovery.ui;

import android.bluetooth.*;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import butterknife.InjectView;
import butterknife.Views;
import com.benvonhandorf.bluetoothdiscovery.R;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDeviceWrapper.Sensors;

import static java.lang.Math.*;

import java.io.UnsupportedEncodingException;

public class IrSensorRawGattActivity extends Activity {
    private static final String TAG = IrSensorRawGattActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 55555;
    private BluetoothAdapter _bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback _leScanCallback;
    private BluetoothGattCallback _gattCallback;

    private BluetoothGatt _bluetoothGatt;

    @InjectView(R.id.text_value)
    android.widget.TextView _reading;

    private Handler _uiThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _uiThreadHandler = new Handler();

        setContentView(R.layout.activity_simple_data);

        Views.inject(this);

        _gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    _state = State.Connecting;
                    Log.v(TAG, "Connected");
                    Log.v(TAG, "Discovering services: " + gatt.discoverServices());
                    setReading("Connected");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                    Log.v(TAG, "Disconnected");
                    setReading("Disconnected");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                Log.v(TAG, "Services Discovered status: " + status);

                for (BluetoothGattService service : gatt.getServices()) {
                    Log.v(TAG, "Service: " + service.getUuid());

                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        Log.v(TAG, "     Characteristic: " + characteristic.getUuid());

                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                            Log.v(TAG, "          Descriptor:" + descriptor.getUuid());
                        }
                    }
                }

                _state = State.Connected;

                runStateMachine();
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                Log.v(TAG, "onDescriptorRead");

                if (Sensors.Descriptors.USER_DESCRIPTION.equals(descriptor.getUuid())) {
                    try {
                        String value = new String(descriptor.getValue(), "UTF-8");

                        Log.v(TAG, "User Name For Characteristic:" + descriptor.getCharacteristic().getUuid().toString() + " : " + value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);

                Log.v(TAG, "Writing characteristic: " + characteristic.getUuid().toString() + " = " + status);
                byte[] data = characteristic.getValue();
                if (data != null
                        && data.length > 1) {
                    Log.v(TAG, String.format("Written Value: 0x%2x", data[0]));
                }

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    runStateMachine();
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);

                Log.v(TAG, "Writing descriptor: " + descriptor.getCharacteristic().getUuid().toString() + " = " + status);

                if (status == 0) {
                    runStateMachine();
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                Log.v(TAG, "Characteristic Read: " + characteristic.getInstanceId() + " : " + characteristic.getUuid() + " Length: " + characteristic.getValue().length);

                if (Sensors.IR_SENSOR.DATA.equals(characteristic.getUuid())) {
                    final double ambient = extractAmbientTemperature(characteristic);
                    final double target = extractTargetTemperature(characteristic, ambient);

                    String reading = String.format("Ambient: %f\nTarget: %f",
                            ambient,
                            target);

                    Log.v(TAG, String.format("Got IR Sensor Data: %s", reading));

                    setReading(reading);
                }

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    runStateMachine();
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);

                Log.v(TAG, "Characteristic Changed: " + characteristic.getInstanceId() + " : " + characteristic.getUuid() + " Length: " + characteristic.getValue().length);

                if (Sensors.IR_SENSOR.DATA.equals(characteristic.getUuid())) {
                    final double ambient = extractAmbientTemperature(characteristic);
                    final double target = extractTargetTemperature(characteristic, ambient);

                    String reading = String.format("Ambient: %.1f\nTarget: %.1f",
                            ambient,
                            target);

                    Log.v(TAG, String.format("Got IR Sensor Data: %s", reading));

                    setReading(reading);
                }

                runStateMachine();
            }
        };


        _leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                Log.v(TAG, "Found BlueTooth Device: " + bluetoothDevice.getName());

                if ("SensorTag".equals(bluetoothDevice.getName())) {
                    Log.v(TAG, "Attempting to connect: " + bluetoothDevice.getName());
                    _bluetoothAdapter.stopLeScan(_leScanCallback);

                    _bluetoothGatt = bluetoothDevice.connectGatt(IrSensorRawGattActivity.this, false, _gattCallback);
                }
            }
        };
    }

    private enum State {
        None, Connecting, Connected, Enabling, Configuring, Configured, Notifying;


    }

    private State _state = State.None;

    private void runStateMachine() {
        switch (_state) {
            case Connected:
                wireIrSensor1();
                break;
            case Enabling:
                wireIrSensor2();
                break;
            case Configuring:
                wireIrSensor3();
                break;
            case Configured:
                wireIrSensor4();
                break;
        }
    }

    private void wireIrSensor1() {
        BluetoothGattService service = _bluetoothGatt.getService(Sensors.IR_SENSOR.SERVICE);

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(Sensors.IR_SENSOR.CONFIG);

        Log.v(TAG, "Enabling temperature sensor");
        characteristic.setValue(new byte[]{0x01});

        _state = State.Enabling;

        if (!_bluetoothGatt.writeCharacteristic(characteristic)) {
            Log.v(TAG, "Failed writeCharacteristic");
        }

    }

    private void wireIrSensor2() {
        BluetoothGattService service = _bluetoothGatt.getService(Sensors.IR_SENSOR.SERVICE);

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(Sensors.IR_SENSOR.DATA);

        final int properties = characteristic.getProperties();

        Log.v(TAG, String.format("wire stage 2: Characteristic properties: 0x%x", properties));

        _state = State.Configuring;

        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            Log.v(TAG, "Attempting read on characteristic");

            if (_bluetoothGatt.readCharacteristic(characteristic)) {
                Log.v(TAG, "Read characteristic failed");
            }
        }
    }

    private void wireIrSensor3() {
        BluetoothGattService service = _bluetoothGatt.getService(Sensors.IR_SENSOR.SERVICE);

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(Sensors.IR_SENSOR.DATA);

        Log.v(TAG, String.format("wire stage 3: Writing descriptor"));

        _state = State.Configured;

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Sensors.Descriptors.CLIENT_CHARACTERISTIC_CONFIGURATION);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (!_bluetoothGatt.writeDescriptor(descriptor)) {
            Log.v(TAG, "Failed writeDescriptor");
        }
    }

    private void wireIrSensor4() {
        BluetoothGattService service = _bluetoothGatt.getService(Sensors.IR_SENSOR.SERVICE);

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(Sensors.IR_SENSOR.DATA);

        final int properties = characteristic.getProperties();

        Log.v(TAG, String.format("wire stage 4: Characteristic properties: 0x%x", properties));

        _state = State.Notifying;

        _bluetoothGatt.setCharacteristicNotification(characteristic, true);
    }


    private double extractAmbientTemperature(BluetoothGattCharacteristic c) {
        int offset = 2;
        return shortUnsignedAtOffset(c, offset) / 128.0;
    }

    private double extractTargetTemperature(BluetoothGattCharacteristic c, double ambient) {
        Integer twoByteValue = shortSignedAtOffset(c, 0);

        double Vobj2 = twoByteValue.doubleValue();
        Vobj2 *= 0.00000015625;

        double Tdie = ambient + 273.15;

        double S0 = 5.593E-14;    // Calibration factor
        double a1 = 1.75E-3;
        double a2 = -1.678E-5;
        double b0 = -2.94E-5;
        double b1 = -5.7E-7;
        double b2 = 4.63E-9;
        double c2 = 13.4;
        double Tref = 298.15;
        double S = S0 * (1 + a1 * (Tdie - Tref) + a2 * pow((Tdie - Tref), 2));
        double Vos = b0 + b1 * (Tdie - Tref) + b2 * pow((Tdie - Tref), 2);
        double fObj = (Vobj2 - Vos) + c2 * pow((Vobj2 - Vos), 2);
        double tObj = pow(pow(Tdie, 4) + (fObj / S), .25);

        return tObj - 273.15;
    }

    /**
     * Gyroscope, Magnetometer, Barometer, IR temperature
     * all store 16 bit two's complement values in the awkward format
     * LSB MSB, which cannot be directly parsed as getIntValue(FORMAT_SINT16, offset)
     * because the bytes are stored in the "wrong" direction.
     * <p/>
     * This function extracts these 16 bit two's complement values.
     */
    private static Integer shortSignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        Integer lowerByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        Integer upperByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, offset + 1); // Note: interpret MSB as signed.

        return (upperByte << 8) + lowerByte;
    }

    private static Integer shortUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        Integer lowerByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        Integer upperByte = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 1); // Note: interpret MSB as unsigned.

        return (upperByte << 8) + lowerByte;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        _bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (_bluetoothAdapter == null || !_bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startScan();
        }
    }

    private void startScan() {
        _bluetoothAdapter.startLeScan(_leScanCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "Spinning down bluetooth connections");

        if (_bluetoothAdapter != null) {
            _bluetoothAdapter.stopLeScan(_leScanCallback);
        }

        if (_bluetoothGatt != null) {
            _bluetoothGatt.close();
        }

        Log.v(TAG, "Done spinning down bluetooth connections");
    }

    private void setReading(final String reading) {
        _uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                _reading.setText(reading);
            }
        });
    }
}
