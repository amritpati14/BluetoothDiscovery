package com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor.HRMSensor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Characteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Device;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.ElementFactory;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface.Service;
import com.benvonhandorf.bluetoothdiscovery.HeartRateMonitor.Sensors;


import java.util.UUID;

/**
 * Created by benvh on 10/3/13.
 */
public class HRMService extends Service{
    private static final ElementFactory _hrmElementFactory = new ElementFactory() {
        @Override
        public Characteristic createSpecificCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
            UUID characteristicId = bluetoothGattCharacteristic.getUuid();

            if (Sensors.HEART_RATE.BODY_SENSOR_LOCATION.equals(characteristicId)) {
                return new BodySensorLocationCharacteristic(service, bluetoothGattCharacteristic, elementFactory);
            } else if (Sensors.HEART_RATE.DATA.equals(characteristicId)) {
                return new HeartRateCharacteristic(service, bluetoothGattCharacteristic, elementFactory);
            }

            return super.createSpecificCharacteristic(service, bluetoothGattCharacteristic, elementFactory);
        }
    };

    public HRMService(Device device, BluetoothGattService bluetoothGattService, ElementFactory elementFactory) {
        super(device, bluetoothGattService, _hrmElementFactory);
    }

    public HeartRateCharacteristic getHeartRateCharacteristic() {
        return (HeartRateCharacteristic) getCharacteristic(Sensors.HEART_RATE.DATA);
    }

    public BodySensorLocationCharacteristic getBodySensorLocationCharacteristic() {
        return (BodySensorLocationCharacteristic) getCharacteristic(Sensors.HEART_RATE.BODY_SENSOR_LOCATION);
    }
}
