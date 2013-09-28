package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceInterface;

import android.bluetooth.BluetoothGatt;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public abstract class DeviceCommand {
    public interface FollowUpListener {
        void success(DeviceCommand previousCommand);
    }

    private FollowUpListener _followUpListener;
    private boolean _isError;
    private boolean _isExecuting;

    public abstract void execute(BluetoothGatt bluetoothGatt);
    public abstract boolean isCommandCompleted(Device.BluetoothEvents bluetoothEvent, UUID itemUUID);

    public boolean isError() {
        return _isError ;
    }

    public boolean isExecuting() {
        return _isExecuting;
    }

    protected void setIsError(boolean isError) {
        _isError = isError;
    }

    protected void setIsExecuting(boolean isExecuting) {
        _isExecuting = isExecuting;
    }

    public void setFollowUpListener(FollowUpListener followUpListener) {
        _followUpListener = followUpListener;
    }

    protected void onSuccess() {
        if(_followUpListener != null) {
            _followUpListener.success(this);
        }
    }
}
