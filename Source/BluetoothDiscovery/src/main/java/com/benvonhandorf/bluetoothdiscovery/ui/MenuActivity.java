package com.benvonhandorf.bluetoothdiscovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import butterknife.OnClick;
import butterknife.Views;
import com.benvonhandorf.bluetoothdiscovery.R;

/**
 * Created by benvh on 10/19/13.
 */
public class MenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Views.inject(this);
    }

    @OnClick(R.id.button_ir_raw)
    public void irRawActivity() {
        Intent intent = new Intent(this, IrSensorRawGattActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_ir_interface)
    public void irInterfaceActivity() {
        Intent intent = new Intent(this, IrSensorInterfaceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_ir_fast_poll)
    public void irFastPollActivity(){
        Intent intent = new Intent(this, FastPollIrSensorInterfaceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_ir_probe)
    public void probeActivity() {
        Intent intent = new Intent(this, ProbeDeviceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_hrm_interface)
    public void hrmActivity() {
        Intent intent = new Intent(this, HeartRateMonitorActivity.class) ;
        startActivity(intent);
    }
}
