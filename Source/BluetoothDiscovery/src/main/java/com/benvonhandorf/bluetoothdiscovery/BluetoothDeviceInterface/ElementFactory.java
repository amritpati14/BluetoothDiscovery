package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

/**
 * Created by benvh on 9/28/13.
 */


public class ElementFactory {

    public Service createSpecificService(Device device, BluetoothGattService bluetoothGattService, ElementFactory elementFactory) {
        return new Service(device, bluetoothGattService, elementFactory);
    }

    public Characteristic createSpecificCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory){
        //Default base implementation just creates a normal dumb characteristic
        return new Characteristic(service, bluetoothGattCharacteristic, elementFactory);
    }

    public Descriptor createSpecificDescriptor(Characteristic characteristic, BluetoothGattDescriptor bluetoothGattDescriptor, ElementFactory elementFactory) {
        return new Descriptor(characteristic, bluetoothGattDescriptor);
    }
}