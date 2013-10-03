package com.benvonhandorf.bluetoothdiscovery.SensorTagDevice;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.*;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.IRSensor.IrSensorService;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class SensorTagElementFactory extends ElementFactory {

    @Override
    public Service createSpecificService(Device device, BluetoothGattService bluetoothGattService, ElementFactory elementFactory) {
        UUID serviceId = bluetoothGattService.getUuid();

        if(Sensors.IR_SENSOR.SERVICE.equals(serviceId)) {
            return new IrSensorService(device, bluetoothGattService);
        }

        //Use the base class implementation to create a generic service.
        return super.createSpecificService(device, bluetoothGattService, elementFactory);
    }

}
