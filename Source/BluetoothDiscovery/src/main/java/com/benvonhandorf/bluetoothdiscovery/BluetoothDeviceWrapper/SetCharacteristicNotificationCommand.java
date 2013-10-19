package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class SetCharacteristicNotificationCommand extends DeviceCommand {
    private static final String TAG = SetCharacteristicNotificationCommand.class.getSimpleName();
    private final Characteristic _characteristic;
    private byte[] _value;

    public SetCharacteristicNotificationCommand(Characteristic characteristic) {
        _characteristic = characteristic;
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = _characteristic.getBluetoothCharacteristic();

        if (bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)) {
            setIsExecuting(false);
        } else {
            Log.v(TAG, "Error calling setCharacteristicNotification");
            setIsError(true);
        }
    }

    @Override
    public boolean isCommandCompleted(Device.BluetoothEvents bluetoothEvent, UUID itemUUID) {
        return true;
    }
}
