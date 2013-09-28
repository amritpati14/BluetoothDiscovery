package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public class Characteristic implements UUIDHolder {

    private CharacteristicListener _characteristicListener;

    public interface CharacteristicListener {
        public void onValueChanged(Characteristic characteristic);
    }

    private final UUID _characteristicId;
    private final BluetoothGattCharacteristic _bluetoothGattCharacteristic;
    private final HashMap<UUID, Descriptor> _descriptorMap;
    private final Service _parentService;

    public Characteristic(Service service, BluetoothGattCharacteristic bluetoothGattCharacteristic, ElementFactory elementFactory) {
        _parentService = service;

        _characteristicId = bluetoothGattCharacteristic.getUuid();
        _bluetoothGattCharacteristic = bluetoothGattCharacteristic;

        _descriptorMap = new HashMap<UUID, Descriptor>();

        scanCharacteristicForDescriptors(elementFactory);
    }

    private void scanCharacteristicForDescriptors(ElementFactory elementFactory) {
        for (BluetoothGattDescriptor bluetoothGattDescriptor : _bluetoothGattCharacteristic.getDescriptors()) {
            Descriptor descriptor = elementFactory.createSpecificDescriptor(this, bluetoothGattDescriptor, elementFactory);

            _descriptorMap.put(descriptor.getUUID(), descriptor);
        }
    }

    @Override
    public UUID getUUID() {
        return _characteristicId;
    }

    public Service getParent() {
        return _parentService;
    }

    public Descriptor getDescriptor(UUID descriptorId) {
        return _descriptorMap.get(descriptorId);
    }

    protected void onValueChanged() {
    }

    protected BluetoothGattCharacteristic getBluetoothCharacteristic() {
        return _bluetoothGattCharacteristic;
    }

    public void read() {
        ReadCharacteristicCommand readCharacteristicCommand = new ReadCharacteristicCommand(this);

        getParent().getDevice().executeCommand(readCharacteristicCommand);
    }

    public void setCharacteristicListener(CharacteristicListener characteristicListener) {
        _characteristicListener = characteristicListener;
    }

    public void notifyEvent(Device.BluetoothEvents eventType) {
        switch (eventType) {
            case onCharacteristicRead:
            case onCharacteristicChanged:
                //A new value is available for this characteristic
                if(_characteristicListener != null) {
                    onValueChanged();
                    _characteristicListener.onValueChanged(this);
                }
                break;
        }
    }
}
