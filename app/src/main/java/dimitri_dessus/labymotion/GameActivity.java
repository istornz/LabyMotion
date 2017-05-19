package dimitri_dessus.labymotion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.List;

import dimitri_dessus.labymotion.engines.GraphicGameEngine;
import dimitri_dessus.labymotion.engines.PhysicalGameEngine;
import dimitri_dessus.labymotion.models.Ball;
import dimitri_dessus.labymotion.models.Bloc;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Game";

    // Id of dialog
    public static final int VICTORY_DIALOG  = 0;
    public static final int DEFEAT_DIALOG   = 1;

    // Define screen height ratio
    private static final int SCREEN_HEIGHT_RATION = 143;

    // Definition of game objects
    private PhysicalGameEngine mEngine  = null;
    private GraphicGameEngine mView     = null;
    private Ball mBall                  = null;

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mLuminositySensor;
    private Sensor mMagneticSensor;

    // Sensors vars
    private float mLuminosity;
    private double mMagnetic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init sensor manager
        mSensorManager      = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLuminositySensor   = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mMagneticSensor     = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Init graphic game engine
        mView = new GraphicGameEngine(this);
        mEngine = new PhysicalGameEngine(this);
        setContentView(mView);

        // Change here radius according to screen height
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Ball.RADIUS = (metrics.heightPixels - SCREEN_HEIGHT_RATION) / GraphicGameEngine.SURFACE_RATIO;

        // Init ball
        mBall = new Ball();
        mView.setBall(mBall);
        mEngine.setBall(mBall);

        // Build the labyrinthe
        List<Bloc> mList = mEngine.buildLabyrinthe();
        mView.setBlocks(mList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Resume the game
        mEngine.resume();

        // Register listener
        mSensorManager.registerListener(this, mLuminositySensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop the game
        mEngine.stop();

        // Unregister the sensor listener
        mSensorManager.unregisterListener(this);
    }

    public void showInfoDialog(int id) {
        // Show dialog when event triggered
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        mEngine.stop();

        switch(id) {
            case VICTORY_DIALOG:
                builder.setCancelable(false)
                        .setMessage(R.string.victory_title)
                        .setTitle(R.string.victory_msg)
                        .setNeutralButton(R.string.restart_game, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEngine.reset();
                                mEngine.resume();
                            }
                        });
                break;

            case DEFEAT_DIALOG:
                builder.setCancelable(false)
                        .setMessage(R.string.defeat_msg)
                        .setTitle(R.string.defeat_title)
                        .setNeutralButton(R.string.restart_game, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEngine.reset();
                                mEngine.resume();
                            }
                        });
        }

        builder.show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                mLuminosity = sensorEvent.values[0];
                mView.setSurfaceBgColor(mLuminosity);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                float xMagnetic = sensorEvent.values[0];
                float yMagnetic = sensorEvent.values[1];
                float zMagnetic = sensorEvent.values[2];
                mMagnetic = Math.sqrt((double)(xMagnetic * xMagnetic + yMagnetic * yMagnetic + zMagnetic * zMagnetic));
                mBall.setBallColor(mMagnetic);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
