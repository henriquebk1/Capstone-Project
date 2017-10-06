package com.henriquebecker.geocoordenadas;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


/**
 * create an instance of this fragment.
 */

@EFragment(R.layout.fragment_compass)
public class CompassFragment extends Fragment implements SensorEventListener {

    @ViewById
    ImageView compassBackground;
    @ViewById
    ImageView compassHands;
    @ViewById
    TextView textPrecision;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private float mAzimuth = 0f;
    private float mCurrectAzimuth = 0;

    @AfterViews
    void init(){
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    private void adjustHands() {
        Animation an;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            an = new RotateAnimation(-mCurrectAzimuth-90, -mAzimuth-90,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
        } else {
            an = new RotateAnimation(-mCurrectAzimuth, -mAzimuth,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
        }
        mCurrectAzimuth = mAzimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        compassHands.startAnimation(an);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mLastAccelerometer[0] = alpha * mLastAccelerometer[0] + (1 - alpha)
                        * event.values[0];
                mLastAccelerometer[1] = alpha * mLastAccelerometer[1] + (1 - alpha)
                        * event.values[1];
                mLastAccelerometer[2] = alpha * mLastAccelerometer[2] + (1 - alpha)
                        * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mLastMagnetometer[0] = alpha * mLastMagnetometer[0] + (1 - alpha)
                        * event.values[0];
                mLastMagnetometer[1] = alpha * mLastMagnetometer[1] + (1 - alpha)
                        * event.values[1];
                mLastMagnetometer[2] = alpha * mLastMagnetometer[2] + (1 - alpha)
                        * event.values[2];

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mLastAccelerometer,
                    mLastMagnetometer);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                mAzimuth = (float) Math.toDegrees(orientation[0]); // orientation
                mAzimuth = (mAzimuth + 360) % 360;
                adjustHands();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            if(accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE){
                textPrecision.setText(getString(R.string.unreliable));
                textPrecision.setTextColor(Color.RED);
            }
            else if(accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW){
                textPrecision.setText(getString(R.string.low));
                textPrecision.setTextColor(Color.RED);
            }
            else if(accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM){
                textPrecision.setText(getString(R.string.medium));
                textPrecision.setTextColor(Color.YELLOW);
            }
            else if(accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH){
                textPrecision.setText(getString(R.string.high));
                textPrecision.setTextColor(Color.GREEN);
            }
        }
    }

    @Click(R.id.buttonCalibrate)
    void showCalibrateDialog(){
        Dialogs.ShowCalibrateDialog(getContext(),getActivity());
    }
}
