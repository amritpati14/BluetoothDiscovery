package com.benvonhandorf.bluetoothdiscovery.HeartRateMonitorWrapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.Device;
import com.benvonhandorf.bluetoothdiscovery.HeartRateMonitorWrapper.HRMSensor.HRMService;

/**
 * Created by benvh on 10/3/13.
 */
public class PolarH6HRMDevice extends Device {

    private static final String DEVICE_NAME = "Polar H6 25D23717";

    public PolarH6HRMDevice(Context context, BluetoothAdapter bluetoothAdapter) {
        super(context, bluetoothAdapter, new HRMElementFactory());
    }

    @Override
    protected boolean isTargetDevice(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        return DEVICE_NAME.equals(bluetoothDevice.getName());
    }

    public HRMService getHRMService() {
        return (HRMService) getService(Sensors.HEART_RATE.SERVICE);
    }
}
