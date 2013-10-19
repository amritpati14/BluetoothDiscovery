package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

/**
 * Created by benvh on 10/3/13.
 */
public class ProbeDevice extends Device  implements Device.OnDeviceStateChangedListener{
    private static final String TAG = ProbeDevice.class.getSimpleName();
    private String _deviceName;
    private String _deviceAddress;
    private BluetoothClass _deviceClass;

    public ProbeDevice(Context context, BluetoothAdapter bluetoothAdapter) {
        super(context, bluetoothAdapter);
        //By default, our implementation of the device ready listener dumps the
        //data to the log.  The owner is free to override this
        setDeviceReadyListener(this);
    }

    @Override
    protected boolean isTargetDevice(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        _deviceName = bluetoothDevice.getName();
        _deviceAddress = bluetoothDevice.getAddress();
        _deviceClass = bluetoothDevice.getBluetoothClass();

        //For the probe device, we're always looking for what we find
        return true;
    }

    @Override
    public void onDeviceReady(Device device) {
        Log.v(TAG, "Device Probe");
        Log.v(TAG, String.format("Name: %s", _deviceName));
        Log.v(TAG, String.format("Address: %s", _deviceAddress));
        Log.v(TAG, String.format("Class: %d %d", _deviceClass.getMajorDeviceClass(), _deviceClass.getDeviceClass()));

        for(Service service : getServices()) {
            Log.v(TAG, String.format("Service: %s", service.getUUID()));

            for(Characteristic characteristic : service.getCharacteristics()) {
                Log.v(TAG, String.format("  Characteristic: %s", characteristic.getUUID()));

                for(Descriptor descriptor : characteristic.getDescriptors()) {
                    Log.v(TAG, String.format("    Descriptor: %s", descriptor.getUUID()));
                }
            }
        }
    }

    @Override
    public void onDeviceDisconnect(Device device) {

    }
}
