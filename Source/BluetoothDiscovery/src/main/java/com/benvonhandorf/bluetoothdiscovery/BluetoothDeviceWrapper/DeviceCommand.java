package com.benvonhandorf.bluetoothdiscovery.BluetoothDeviceWrapper;

import android.bluetooth.BluetoothGatt;

import java.util.UUID;

/**
 * Created by benvh on 9/28/13.
 */
public abstract class DeviceCommand {
    private static final String TAG = DeviceCommand.class.getSimpleName();

    private long _started;
    private long _finished;

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
        _started = System.currentTimeMillis();
    }

    public void setFollowUpListener(FollowUpListener followUpListener) {
        _followUpListener = followUpListener;
    }

    protected void onSuccess() {
        if(_followUpListener != null) {
            _followUpListener.success(this);
        }
        _finished = System.currentTimeMillis();

        //Log.v(TAG, String.format("Command type %s took %d ms to execute", this.getClass().getSimpleName(), _finished - _started));
    }
}
