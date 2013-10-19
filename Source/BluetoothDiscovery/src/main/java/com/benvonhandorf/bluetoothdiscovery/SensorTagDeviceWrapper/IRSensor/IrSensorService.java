package com.benvonhandorf.bluetoothdiscovery.SensorTagDeviceWrapper.IRSensor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.Characteristic;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.Device;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.ElementFactory;
import com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper.Service;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDeviceWrapper.Sensors;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class IrSensorService extends Service {
    private static final ElementFactory _irSensorElementFactory = new ElementFactory() {
        @Override
        public Characteristic createSpecificCharacteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
            UUID characteristicId = bluetoothGattCharacteristic.getUuid();

            if (Sensors.IR_SENSOR.DATA.equals(characteristicId)) {
                return new IrDataCharacteristic(service, bluetoothGattCharacteristic, elementFactory);
            } else if (Sensors.IR_SENSOR.CONFIG.equals(characteristicId)) {
                return new IrConfigCharacteristic(service, bluetoothGattCharacteristic, elementFactory);
            }

            return super.createSpecificCharacteristic(service, bluetoothGattCharacteristic, elementFactory);
        }
    };

    public IrSensorService(Device device, BluetoothGattService bluetoothGattService) {
        //Use our own, smarter element factory.
        super(device, bluetoothGattService, _irSensorElementFactory);
    }

    public IrConfigCharacteristic getConfigCharacteristic() {
        return (IrConfigCharacteristic) getCharacteristic(Sensors.IR_SENSOR.CONFIG);
    }

    public IrDataCharacteristic getDataCharacteristic() {
        return (IrDataCharacteristic) getCharacteristic(Sensors.IR_SENSOR.DATA);
    }
}
