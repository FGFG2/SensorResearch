package com.example.alsimfer.sensorresearch;

import android.app.Activity;
import android.content.Context;     // ??
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyroscope;

    private long lastUpdateAccel, lastUpdateGyro = 0;
    private float last_x, last_y, last_z;
    private float last_maxX, last_maxY, last_maxZ;
    private float last_maxRollX, last_maxPitchY, last_maxYawZ;

    // Modifying THRESHOLD increases or decreases the sensitivity.
    private static final int GREEN_THRESHOLD = 100;
    private static final int YELLOW_THRESHOLD = 200;
    private static final int RED_THRESHOLD = 300;
    private static final int INTERVAL = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Register Accelerometer.
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Register Gyroscope.
         senGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
         senSensorManager.registerListener(this, senGyroscope , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // Method is invoked every time the built-in sensor detects a change.
        // This method is invoked repeatedly whenever the device is in motion.
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            // The event's values attribute is an array of floats.
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            // Get the data each INTERVAL ms.
            if ((curTime - lastUpdateAccel) > INTERVAL) {
                long diffTime = (curTime - lastUpdateAccel);
                lastUpdateAccel = curTime;

                refresh("accelerometer_dyn", 'x', x);
                refresh("accelerometer_dyn", 'y', y);
                refresh("accelerometer_dyn", 'z', z);

                if (x > last_maxX) {
                    refresh("accelerometer_max", 'x', x);
                    last_maxX = x;
                }

                if (y > last_maxY) {
                    refresh("accelerometer_max", 'y', y);
                    last_maxY = y;
                }

                if (z > last_maxZ) {
                    refresh("accelerometer_max", 'z', z);
                    last_maxZ = z;
                }

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                if (speed > GREEN_THRESHOLD) {
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }

                if (speed > YELLOW_THRESHOLD) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }

                if (speed > RED_THRESHOLD) {
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }

                last_x = x;
                last_y = y;
                last_z = z;

            }

        } else if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float rollX = sensorEvent.values[0];
            float pitchY = sensorEvent.values[1];
            float yawZ = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            // Get the data each INTERVAL ms.
            if ((curTime - lastUpdateGyro) > INTERVAL) {
                long diffTime = (curTime - lastUpdateGyro);

                lastUpdateGyro = curTime;
                refresh("gyroscope_dyn", 'x', rollX);
                refresh("gyroscope_dyn", 'y', pitchY);
                refresh("gyroscope_dyn", 'z', yawZ);

                if (rollX > last_maxRollX) {
                    refresh("gyroscope_max", 'x', rollX);
                    last_maxRollX = rollX;
                }

                if (pitchY > last_maxPitchY) {
                    refresh("gyroscope_max", 'y', pitchY);
                    last_maxPitchY = pitchY;
                }

                if (yawZ > last_maxYawZ) {
                    refresh("gyroscope_max", 'z', yawZ);
                    last_maxYawZ = yawZ;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        // It's good practice to unregister the sensor when the application hibernates ...
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        // ... and register the sensor again when the application resumes.
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void refresh(String line, char axis, float value) {
        TextView text;

        if (line.equals("accelerometer_dyn") == true) {
            switch (axis) {
                case 'x':
                    text = (TextView) findViewById(R.id.xAccelDyn);
                    break;
                case 'y':
                    text = (TextView) findViewById(R.id.yAccelDyn);
                    break;
                case 'z':
                    text = (TextView) findViewById(R.id.zAccelDyn);
                    break;
                default:
                    text = (TextView) findViewById(R.id.xAccelDyn);
                    break;
            }
        } else if (line.equals("accelerometer_max") == true) {
            switch (axis) {
                case 'x':
                    text = (TextView) findViewById(R.id.xAccelMax);
                    break;
                case 'y':
                    text = (TextView) findViewById(R.id.yAccelMax);
                    break;
                case 'z':
                    text = (TextView) findViewById(R.id.zAccelMax);
                    break;
                default:
                    text = (TextView) findViewById(R.id.xAccelMax);
                    break;
            }
        } else if (line.equals("gyroscope_max") == true) {
            switch (axis) {
                case 'x':
                    text = (TextView) findViewById(R.id.xGyroMax);
                    break;
                case 'y':
                    text = (TextView) findViewById(R.id.yGyroMax);
                    break;
                case 'z':
                    text = (TextView) findViewById(R.id.zGyroMax);
                    break;
                default:
                    text = (TextView) findViewById(R.id.xGyroMax);
                    break;
            }
        } else if (line.equals("gyroscope_dyn") == true) {
            switch (axis) {
                case 'x':
                    text = (TextView) findViewById(R.id.xGyroDyn);
                    break;
                case 'y':
                    text = (TextView) findViewById(R.id.yGyroDyn);
                    break;
                case 'z':
                    text = (TextView) findViewById(R.id.zGyroDyn);
                    break;
                default:
                    text = (TextView) findViewById(R.id.xGyroDyn);
                    break;
            }
        } else {
            text = (TextView) findViewById(R.id.xAccelMax);
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        text.setText("" + df.format(value));
        if (value > 3) {
            text.setTextColor(Color.parseColor("#FF0000"));
        } else {
            text.setTextColor(Color.parseColor("#000000"));
        }

    }


}
