package com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.*;
import com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor.HRMSensor.HRMService;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.IRSensor.IrSensorService;

import java.util.UUID;

/**
 * Created by benvh on 10/3/13.
 */
public class HRMElementFactory extends ElementFactory {
    @Override
    public Service createSpecificService(Device device, BluetoothGattService bluetoothGattService, ElementFactory elementFactory) {
        UUID serviceId = bluetoothGattService.getUuid();

        if(Sensors.HEART_RATE.SERVICE.equals(serviceId)) {
            return new HRMService(device, bluetoothGattService, this);
        }

        //Use the base class implementation to create a generic service.
        return super.createSpecificService(device, bluetoothGattService, elementFactory);
    }

    @Override
    public Characteristic createSpecificCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
        return super.createSpecificCharacteristic(service, bluetoothGattCharacteristic, elementFactory);
    }

    @Override
    public Descriptor createSpecificDescriptor(Characteristic characteristic, BluetoothGattDescriptor bluetoothGattDescriptor, ElementFactory elementFactory) {
        return super.createSpecificDescriptor(characteristic, bluetoothGattDescriptor, elementFactory);
    }
}
