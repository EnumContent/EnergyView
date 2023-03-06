package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int inv=10,outv=30;
        BatteryView battery = findViewById(R.id.battery);
        BatterySimpleView battery2 = findViewById(R.id.barrier2);
        CreditSesameView battery3 = findViewById(R.id.barrier3);

        battery.setPower(inv,outv);
        battery2.setPower(inv);
        battery3.setProgress(outv,inv);

        SeekBar in = findViewById(R.id.in);
        SeekBar out = findViewById(R.id.out);
        in.setMax(100);
        in.setProgress(inv);
        out.setMax(100);
        out.setProgress(outv);

        in.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                battery.setPower(progress,battery.getOutPower());
                battery3.setProgress(battery3.getOutProgress(),progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        out.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                battery.setPower(battery.getInPower(),progress);
                battery2.setPower(progress);
                battery3.setProgress(progress,battery3.getInProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}