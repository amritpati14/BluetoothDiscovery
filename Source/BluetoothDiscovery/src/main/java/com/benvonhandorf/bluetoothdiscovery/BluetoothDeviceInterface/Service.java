package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import com.benvonhandorf.bluetoothdiscovery.SensorTagDevice.IRSensor.IrConfigCharacteristic;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class Service implements UUIDHolder{
    private final UUID _serviceId;
    private final BluetoothGattService _bluetoothGattService;
    private final HashMap<UUID, Characteristic> _characteristicMap;
    private final Device _device;

    public Service(Device device, BluetoothGattService bluetoothGattService, ElementFactory elementFactory) {
        _device = device ;
        _bluetoothGattService = bluetoothGattService;

        _serviceId = _bluetoothGattService.getUuid();

        _characteristicMap = new HashMap<UUID, Characteristic>();
        scanServiceForCharacteristics(elementFactory);
    }

    private void scanServiceForCharacteristics(ElementFactory elementFactory) {
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : _bluetoothGattService.getCharacteristics()) {
            Characteristic c = elementFactory.createSpecificCharacteristic(this, bluetoothGattCharacteristic, elementFactory);

            _characteristicMap.put(c.getUUID(), c);
        }
    }


    protected Characteristic getCharacteristic(UUID config) {
        return _characteristicMap.get(config);
    }

    public Collection<Characteristic> getCharacteristics() {
        return _characteristicMap.values();
    }

    @Override
    public UUID getUUID() {
        return _serviceId;
    }

    public Device getDevice() {
        return _device;
    }
}
