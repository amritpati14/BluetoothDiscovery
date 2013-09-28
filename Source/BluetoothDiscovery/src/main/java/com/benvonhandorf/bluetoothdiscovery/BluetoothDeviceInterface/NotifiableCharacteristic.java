package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by benvh on 9/28/13.
 */
public abstract class NotifiableCharacteristic extends Characteristic {
    public NotifiableCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
        super(service, bluetoothGattCharacteristic, elementFactory);
    }

    public void enableNotification() {
        Descriptor descriptor = getDescriptor(Descriptor.DescriptorIds.CLIENT_CHARACTERISTIC_CONFIGURATION);

        if(descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        }

        SetCharacteristicNotificationCommand setCharacteristicNotificationCommand = new SetCharacteristicNotificationCommand(this);

        getParent().getDevice().executeCommand(setCharacteristicNotificationCommand);
    }

}
