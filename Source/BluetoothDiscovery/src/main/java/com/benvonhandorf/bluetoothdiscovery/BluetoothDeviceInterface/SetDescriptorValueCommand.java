package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class SetDescriptorValueCommand extends DeviceCommand {
    private static final String TAG = SetDescriptorValueCommand.class.getSimpleName();

    private final Descriptor _descriptor;
    private byte[] _value;

    public SetDescriptorValueCommand(Descriptor descriptor) {
        _descriptor = descriptor;
    }

    public SetDescriptorValueCommand setValue(byte[] value) {
        _value = value;

        return this;
    }


    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        BluetoothGattDescriptor bluetoothGattDescriptor = _descriptor.getBluetoothDescriptor();

        bluetoothGattDescriptor.setValue(_value);

        if (bluetoothGatt.writeDescriptor(bluetoothGattDescriptor)) {
            setIsExecuting(true);
        } else {
            setIsError(true);
        }
    }

    @Override
    public boolean isCommandCompleted(Device.BluetoothEvents bluetoothEvent, UUID itemUUID) {

        if (bluetoothEvent == Device.BluetoothEvents.onDescriptorWrite
                && _descriptor.getUUID().equals(itemUUID)) {
            setIsExecuting(false);

            return true;
        } else {
            return false;
        }
    }
}
