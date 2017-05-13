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
import android.util.Log;

import java.util.List;

import dimitri_dessus.labymotion.engines.GraphicGameEngine;
import dimitri_dessus.labymotion.engines.PhysicalGameEngine;
import dimitri_dessus.labymotion.models.Ball;
import dimitri_dessus.labymotion.models.Bloc;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Game";

    // Id of the victory dialog
    public static final int VICTORY_DIALOG = 0;

    // Id of the defeat dialog
    public static final int DEFEAT_DIALOG = 1;

    // Define screen height ratio
    private static final int SCREEN_HEIGHT_RATION = 143;

    // Definition of PhysicalEngine object
    private PhysicalGameEngine mEngine = null;
    private GraphicGameEngine mView = null;

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mLuminositySensor;

    // Sensors vars
    private float mLuminosity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLuminositySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Init graphic game engine
        mView = new GraphicGameEngine(this);
        mEngine = new PhysicalGameEngine(this);
        setContentView(mView);

        // Change here radius according to screen height
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Ball.RADIUS = (metrics.heightPixels - SCREEN_HEIGHT_RATION) / GraphicGameEngine.SURFACE_RATIO;

        // Init ball
        Ball b = new Ball();
        mView.setBall(b);
        mEngine.setBall(b);

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
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            mLuminosity = sensorEvent.values[0];
            Log.d(TAG, "Luminosity val -> " + mLuminosity);
            mView.setSurfaceBgColor(mLuminosity);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
