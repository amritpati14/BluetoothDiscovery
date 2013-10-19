package com.benvonhandorf.bluetoothdiscovery.HeartRateMonitorWrapper.HRMSensor;

import android.bluetooth.BluetoothGattCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.ElementFactory;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.NotifiableCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.Service;

/**
 * Created by benvh on 10/3/13.
 */
public class HeartRateCharacteristic extends NotifiableCharacteristic {
    public HeartRateCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
        super(service, bluetoothGattCharacteristic, elementFactory);
    }

    private static class Flags {
        private static final int FLAG_FORMAT_MASK = 0x01;
        private static final int FLAG_FORMAT_8BIT = 0x00;
        private static final int FLAG_FORMAT_16BIT = 0x01;

        private static final int FLAG_CONTACT_MASK = 0x06;
        private static final int FLAG_CONTACT_UNSUPPORTED = 0x00;
        private static final int FLAG_CONTACT_CURRENTLY_UNSUPPORTED = 0x02;
        private static final int FLAG_CONTACT_SUPPORTED_NO_CONTACT = 0x04;
        private static final int FLAG_CONTACT_SUPPORTED_CONTACT = 0x06;
    }

    public int getBPM() {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = getBluetoothCharacteristic();
        int flags = bluetoothGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

        int result = 0 ;

        if((flags & Flags.FLAG_FORMAT_MASK) == Flags.FLAG_FORMAT_8BIT) {
            result = bluetoothGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
        } else {
            result = bluetoothGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
        }

        return result ;
    }
}
