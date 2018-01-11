package com.primetech.android.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.primetech.android.sensor.annotation.LocationModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager mSensorManager;

    @BindView(R.id.tv_current_X)
    TextView tv_current_X;

    @BindView(R.id.tv_current_Y)
    TextView tv_current_Y;

    @BindView(R.id.tv_current_Z)
    TextView tv_current_Z;

    @BindView(R.id.tv_max_X)
    TextView tv_Max_X;

    @BindView(R.id.tv_Max_Y)
    TextView tv_Max_Y;

    @BindView(R.id.tv_Max_Z)
    TextView tv_Max_Z;

    @BindView(R.id.btn_reset)
    Button btn_reset;

    long latUpdatedTime;
    private Sensor mAccelerometer;
    private float last_X, last_Y, last_Z;

    private float delta_X = 0f;
    private float delta_Y = 0f;
    private float delta_Z = 0f;

    private float delta_XMax = 0f;
    private float delta_YMax = 0f;
    private float delta_ZMax = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        LocationModel location= new LocationModel("45.45","34.34");
        location.save();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (mSensorManager != null) {
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);        // device has the sensor
            else {
                // we do not have accelerometer
            }
        }
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delta_XMax = delta_YMax = delta_ZMax = 0f;
                tv_Max_X.setText("00");
                tv_Max_Y.setText("00");
                tv_Max_Z.setText("00");
            }
        });

        latUpdatedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAccelerometer != null)
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        getAccelerometer(event);
    }

    private void getAccelerometer(SensorEvent event) {
  /*      Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

        }*/
        // display the current x,y,z accelerometer values

        displayCurrentValues();

        // display the max x,y,z accelerometer values

        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer

        delta_X = Math.abs(last_X - event.values[0]);
        delta_Y = Math.abs(last_Y - event.values[1]);
        delta_Z = Math.abs(last_Z - event.values[2]);

        if (delta_X < .5)
            delta_X = 0;
        if (delta_Y < .5)
            delta_Y = 0;
        if (delta_Z < .5)
            delta_Z = 0;

        last_X = event.values[0];
        last_Y = event.values[1];
        last_Z = event.values[2];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void displayMaxValues() {
        if (delta_X > delta_XMax)
            delta_XMax = delta_X;
        if (delta_YMax > delta_YMax)
            delta_YMax = delta_Y;
        if (delta_Z > delta_ZMax)
            delta_ZMax = delta_Z;

        tv_Max_X.setText(String.valueOf(delta_XMax));
        tv_Max_Z.setText(String.valueOf(delta_YMax));
        tv_Max_Z.setText(String.valueOf(delta_ZMax));
    }

    private void displayCurrentValues() {
        tv_current_X.setText(Float.toString(delta_X));
        tv_current_Y.setText(Float.toString(delta_Y));
        tv_current_Z.setText(Float.toString(delta_Z));
    }
}
