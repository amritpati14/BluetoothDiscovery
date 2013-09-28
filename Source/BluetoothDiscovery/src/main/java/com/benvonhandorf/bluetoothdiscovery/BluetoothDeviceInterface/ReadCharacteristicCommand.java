package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class ReadCharacteristicCommand extends DeviceCommand {
    private static final String TAG = SetCharacteristicCommand.class.getSimpleName();

    private final Characteristic _characteristic;

    public ReadCharacteristicCommand(Characteristic characteristic) {
        _characteristic = characteristic;
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = _characteristic.getBluetoothCharacteristic();

        if (bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic)) {
            setIsExecuting(true);
        } else {
            Log.v(TAG, "Error calling writeCharacteristic");
            setIsError(true);
        }
    }

    @Override
    public boolean isCommandCompleted(Device.BluetoothEvents bluetoothEvent, UUID itemUUID) {
        if (bluetoothEvent == Device.BluetoothEvents.onCharacteristicRead
                && _characteristic.getUUID().equals(itemUUID)) {
            setIsExecuting(false);

            onSuccess();

            return true;
        }
        return false;
    }
}
