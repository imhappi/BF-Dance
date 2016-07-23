package naomi.cwang.me.brainfuckdance;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;

    private float[] mHistory;

    private TextView mBrainfuckText;

    private MediaPlayer mediaPlayer;

    private TalkToServerRunnable mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHistory = new float[2];

        mediaPlayer = new MediaPlayer();

        mBrainfuckText = (TextView) findViewById(R.id.brainfuck_text);

        mClient = new TalkToServerRunnable();

        Thread thread = new Thread(mClient);

        thread.start();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                2000000,
                16667
        );

    }

    protected static float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;
//        return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + 0.8f * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float[] values = new float[3];
            values = lowPass(event.values.clone(), values);

            float xChange = mHistory[0] - values[0];
            float yChange = mHistory[1] - values[1];

            mHistory[0] = values[0];
            mHistory[1] = values[1];

            if (Math.abs(xChange) > Math.abs(yChange)) {
                if (xChange < -11) {
                    Log.d("Dir", "Left");
                    playSound("Left");
                    mClient.send('l');
                    return;

                } else if (xChange > 11) {
                    Log.d("Dir", "Right");
                    playSound("Right");
                    mClient.send('r');
                    return;

                }
            } else {
                if (yChange > 11) {
                    Log.d("Dir", "Up");
                    playSound("Up");
                    mClient.send('u');

                } else if (yChange < -11) {
                    Log.d("Dir", "Down");
                    playSound("Down");
                    mClient.send('d');

                }

            }
        }

    }

    private void playSound(String direction) {
        mediaPlayer.reset();

        if (direction.equals("Left")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.left);
        } else if (direction.equals("Right")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.right);
        } else if (direction.equals("Up")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.up);
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.down);
        }

        mediaPlayer.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 2000000);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mediaPlayer.release();
    }

}
