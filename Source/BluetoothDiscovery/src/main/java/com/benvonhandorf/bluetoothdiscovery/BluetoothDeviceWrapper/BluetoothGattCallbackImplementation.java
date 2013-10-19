package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper;

import android.bluetooth.*;
import android.util.Log;

/**
 * Created by benvh on 9/28/13.
 */
public class BluetoothGattCallbackImplementation extends BluetoothGattCallback {
    private static final String TAG = BluetoothGattCallbackImplementation.class.getSimpleName();

    private final BluetoothGattEvents _bluetoothEventCallback;

    public interface BluetoothGattEvents {
        void onConnected();

        void onDisconnected();

        void onServicesDiscovered();

        void onCharacteristicEvent(Device.BluetoothEvents eventType, BluetoothGattCharacteristic characteristic);

        void onDescriptorEvent(Device.BluetoothEvents eventType, BluetoothGattDescriptor descriptor);

        void onError(Device.BluetoothEvents eventType, int status);
    }

    public BluetoothGattCallbackImplementation(BluetoothGattEvents bluetoothEventCallback) {
        _bluetoothEventCallback = bluetoothEventCallback;
    }

    private boolean isSuccessful(Device.BluetoothEvents eventType, int status) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            _bluetoothEventCallback.onError(eventType, status);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        Log.v(TAG, String.format("onConnectionStateChange: %d - %d", status, newState));

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            _bluetoothEventCallback.onConnected();
        } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
            _bluetoothEventCallback.onDisconnected();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        Log.v(TAG, String.format("onServicesDiscovered: %d", status));

        if (isSuccessful(Device.BluetoothEvents.onServicesDiscovered, status)) {
            _bluetoothEventCallback.onServicesDiscovered();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);

        Log.v(TAG, String.format("onCharacteristicRead: %d", status));

        if (isSuccessful(Device.BluetoothEvents.onCharacteristicRead, status)) {
            _bluetoothEventCallback.onCharacteristicEvent(Device.BluetoothEvents.onCharacteristicRead, characteristic);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);

        Log.v(TAG, String.format("onCharacteristicWrite: %d", status));

        if (isSuccessful(Device.BluetoothEvents.onCharacteristicWrite, status)) {
            _bluetoothEventCallback.onCharacteristicEvent(Device.BluetoothEvents.onCharacteristicWrite, characteristic);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

        Log.v(TAG, String.format("onCharacteristicChanged"));

        _bluetoothEventCallback.onCharacteristicEvent(Device.BluetoothEvents.onCharacteristicChanged, characteristic);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);

        Log.v(TAG, String.format("onDescriptorRead: %d", status));

        if (isSuccessful(Device.BluetoothEvents.onDescriptorRead, status)) {
            _bluetoothEventCallback.onDescriptorEvent(Device.BluetoothEvents.onDescriptorRead, descriptor);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);

        Log.v(TAG, String.format("onDescriptorWrite: %d", status));

        if (isSuccessful(Device.BluetoothEvents.onDescriptorWrite, status)) {
            _bluetoothEventCallback.onDescriptorEvent(Device.BluetoothEvents.onDescriptorWrite, descriptor);
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
    }
}
