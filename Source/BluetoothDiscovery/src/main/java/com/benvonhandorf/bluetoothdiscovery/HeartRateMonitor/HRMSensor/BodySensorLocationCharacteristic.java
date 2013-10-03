package com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor.HRMSensor;

import android.bluetooth.BluetoothGattCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Characteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.ElementFactory;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Service;

/**
 * Created by benvh on 10/3/13.
 */
public class BodySensorLocationCharacteristic extends Characteristic {
    public static class Locations {
        public final static int OTHER = 0;
        public final static int CHEST = 1;
        public final static int WRIST = 2;
        public final static int FINGER = 3;
        public final static int HAND = 4;
        public final static int EAR_LOBE = 5;
        public final static int FOOT = 6;
    }
    public BodySensorLocationCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
        super(service, bluetoothGattCharacteristic, elementFactory);
    }

    public int getLocation() {
        int result = getBluetoothCharacteristic().getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

        return result ;
    }
}
