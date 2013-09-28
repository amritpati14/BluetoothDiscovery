package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class Descriptor implements UUIDHolder {

    public static class DescriptorIds {
        public static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        public static final UUID USER_DESCRIPTION = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    }


    private final BluetoothGattDescriptor _bluetoothGattDescriptor;
    private final UUID _descriptorId;
    private final Characteristic _parentCharacateristic;

    public Descriptor(Characteristic characteristic, BluetoothGattDescriptor bluetoothGattDescriptor) {
        _parentCharacateristic = characteristic;
        _bluetoothGattDescriptor = bluetoothGattDescriptor;
        _descriptorId = _bluetoothGattDescriptor.getUuid();
    }

    @Override
    public UUID getUUID() {
        return _descriptorId;
    }

    public Characteristic getParent() {
        return _parentCharacateristic;
    }

    public BluetoothGattDescriptor getBluetoothDescriptor() {
        return _bluetoothGattDescriptor;
    }

    public void setValue(byte[] value) {
        SetDescriptorValueCommand setDescriptorValueCommand = new SetDescriptorValueCommand(this);

        setDescriptorValueCommand.setValue(value);

        getParent().getParent().getDevice().executeCommand(setDescriptorValueCommand);
    }
}
