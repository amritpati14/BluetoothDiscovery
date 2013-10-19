package com.benvonhandorf.bluetoothdiscovery.SensorTagDeviceWrapper.IRSensor;

import android.bluetooth.BluetoothGattCharacteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.*;

/**
 * Created by benvh on 9/28/13.
 */
public class IrConfigCharacteristic extends Characteristic {
    public IrConfigCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
        super(service, bluetoothGattCharacteristic, elementFactory);
    }

    public void enable() {
        SetCharacteristicCommand enableSensorCommand = new SetCharacteristicCommand(this).setValue(new byte[]{0x01});

        getParent().getDevice().executeCommand(enableSensorCommand);
    }

    public void disable() {
        SetCharacteristicCommand enableSensorCommand = new SetCharacteristicCommand(this).setValue(new byte[]{0x00});

        getParent().getDevice().executeCommand(enableSensorCommand);
    }
}
