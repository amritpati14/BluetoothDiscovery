package com.benvonhandorf.bluetoothdiscovery.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by benvh on 8/13/13.
 */
public class SensorTagService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
