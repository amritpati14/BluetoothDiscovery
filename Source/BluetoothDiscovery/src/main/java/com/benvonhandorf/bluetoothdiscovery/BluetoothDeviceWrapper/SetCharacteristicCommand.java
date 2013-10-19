package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class SetCharacteristicCommand extends DeviceCommand {
    private static final String TAG = SetCharacteristicCommand.class.getSimpleName();
    private final Characteristic _characteristic;
    private byte[] _value;

    public SetCharacteristicCommand(Characteristic characteristic) {
        _characteristic = characteristic;
    }

    public SetCharacteristicCommand setValue(byte[] value) {
        _value = value;

        return this;
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = _characteristic.getBluetoothCharacteristic();

        bluetoothGattCharacteristic.setValue(_value);

        if (bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic)) {
            setIsExecuting(true);
        } else {
            Log.v(TAG, "Error calling writeCharacteristic");
            setIsError(true);
        }
    }

    @Override
    public boolean isCommandCompleted(Device.BluetoothEvents bluetoothEvent, UUID itemUUID) {
        if (bluetoothEvent == Device.BluetoothEvents.onCharacteristicWrite
                && _characteristic.getUUID().equals(itemUUID)) {
            setIsExecuting(false);

            onSuccess();

            return true;
        }
        return false;
    }
}
