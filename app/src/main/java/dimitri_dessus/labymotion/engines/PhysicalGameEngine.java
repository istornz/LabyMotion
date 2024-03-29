package dimitri_dessus.labymotion.engines;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import dimitri_dessus.labymotion.GameActivity;
import dimitri_dessus.labymotion.models.Ball;
import dimitri_dessus.labymotion.models.Bloc;
import dimitri_dessus.labymotion.models.Bloc.Type;

/**
 * Created by Dimitri on 14/04/2017.
 * LabyMotion
 */

public class PhysicalGameEngine implements SensorEventListener {

    private Ball mBall              = null;
    private List<Bloc> mBlocks      = null;
    private GameActivity mActivity  = null;

    // Sensor init
    private SensorManager mManager  = null;
    private Sensor mAccelerometer   = null;

    // Acceleration helpers vars
    private final int ACC_RATE          = 50;
    private final double ACC_LIMIT      = 0.3;
    private int accCount                = 0;
    private double accSum               = 0.0f;
    private double accResult            = 0.0f;
    private double mAcceleration        = 0.0f;
    private double mAccelerationCurrent = SensorManager.GRAVITY_EARTH;
    private double mAccelerationLast    = SensorManager.GRAVITY_EARTH;

    /**
     * Constructor of PhysicalGameEngine class
     *
     * @param pView Main activity of the game.
     * @see GameActivity
     */
    public PhysicalGameEngine(GameActivity pView) {

        // Assign GameActivity
        mActivity = pView;
        mManager = (SensorManager) mActivity.getBaseContext().getSystemService(Service.SENSOR_SERVICE);
        mAccelerometer = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * Sensor change event listener for game.
     * Triggered when sensor capture data.
     *
     * @param pEvent Sensor event object.
     * @see SensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent pEvent) {
        float x = pEvent.values[0];
        float y = pEvent.values[1];
        float z = pEvent.values[2];

        // Moving ball if not null
        if(mBall != null) {
            // Updating ball coordinates
            RectF hitBox = mBall.putXAndY(x, y);

            for(Bloc block : mBlocks) {
                // Create a new bloc
                RectF inter = new RectF(block.getRectangle());
                if(inter.intersect(hitBox)) {
                    // Detect type of bloc
                    switch(block.getType()) {
                        case HOLE:
                            mActivity.showInfoDialog(GameActivity.DEFEAT_DIALOG);
                            break;
                        case END:
                            mActivity.showInfoDialog(GameActivity.VICTORY_DIALOG);
                            break;
                    }
                    break;
                }
            }
        }

        // Calculate acceleration
        mAccelerationLast       = mAccelerationCurrent;
        mAccelerationCurrent    = Math.sqrt(x * x + y * y + z * z);
        mAcceleration           = (mAcceleration * 0.9f) + (mAccelerationCurrent - mAccelerationLast);

        // If number of sample calculate exceed rate limit
        if (accCount <= ACC_RATE) {
            accCount++;
            accSum += Math.abs(mAcceleration);
        } else {

            // Result of acceleration
            accResult = accSum / ACC_RATE;

            // If limit exceed, display walking alert
            if(accResult > ACC_LIMIT) {
                mActivity.showInfoDialog(GameActivity.WALKING_DIALOG);
            }

            // Resetting all helpers vars
            accCount    = 0;
            accSum      = 0;
            accResult   = 0;
        }
    }

    /**
     * Sensors accuracy change event listener
     * Triggered when accuracy of sensor changed
     *
     * @param pSensor Sensor object.
     * @param pAccuracy New accuracy value.
     * @see Sensor
     */
    @Override
    public void onAccuracyChanged(Sensor pSensor, int pAccuracy) { }

    /**
     * Reset ball to original position
     */
    public void reset() {
        mBall.reset();
    }

    /**
     * Unregister event listener on accelerometer captor
     */
    public void stop() {

        // Unregister accelerometer listener
        mManager.unregisterListener(this, mAccelerometer);
    }

    /**
     * Attach accelerometer sensor to the event listener (to start tracking data)
     */
    public void resume() {

        // Register accelerometer listener
        mManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Set ball of the game
     *
     * @param pBall New ball object
     * @see Ball
     */
    public void setBall(Ball pBall) {
        this.mBall = pBall;
    }

    /**
     * Define list of bloc used to create pattern of the game
     * Bloc are instantiated with type, X & Y values.
     *
     * @return Nothing.
     * @see Bloc
     */
    public List<Bloc> buildLabyrinthe() {
        mBlocks = new ArrayList<>();
        mBlocks.add(new Bloc(Type.HOLE, 0, 0));
        mBlocks.add(new Bloc(Type.HOLE, 0, 1));
        mBlocks.add(new Bloc(Type.HOLE, 0, 2));
        mBlocks.add(new Bloc(Type.HOLE, 0, 3));
        mBlocks.add(new Bloc(Type.HOLE, 0, 4));
        mBlocks.add(new Bloc(Type.HOLE, 0, 5));
        mBlocks.add(new Bloc(Type.HOLE, 0, 6));
        mBlocks.add(new Bloc(Type.HOLE, 0, 7));
        mBlocks.add(new Bloc(Type.HOLE, 0, 8));
        mBlocks.add(new Bloc(Type.HOLE, 0, 9));
        mBlocks.add(new Bloc(Type.HOLE, 0, 10));
        mBlocks.add(new Bloc(Type.HOLE, 0, 11));
        mBlocks.add(new Bloc(Type.HOLE, 0, 12));
        mBlocks.add(new Bloc(Type.HOLE, 0, 13));

        mBlocks.add(new Bloc(Type.HOLE, 1, 0));
        mBlocks.add(new Bloc(Type.HOLE, 1, 13));

        mBlocks.add(new Bloc(Type.HOLE, 2, 0));
        mBlocks.add(new Bloc(Type.HOLE, 2, 13));

        mBlocks.add(new Bloc(Type.HOLE, 3, 0));
        mBlocks.add(new Bloc(Type.HOLE, 3, 13));

        mBlocks.add(new Bloc(Type.HOLE, 4, 0));
        mBlocks.add(new Bloc(Type.HOLE, 4, 1));
        mBlocks.add(new Bloc(Type.HOLE, 4, 2));
        mBlocks.add(new Bloc(Type.HOLE, 4, 3));
        mBlocks.add(new Bloc(Type.HOLE, 4, 4));
        mBlocks.add(new Bloc(Type.HOLE, 4, 5));
        mBlocks.add(new Bloc(Type.HOLE, 4, 6));
        mBlocks.add(new Bloc(Type.HOLE, 4, 7));
        mBlocks.add(new Bloc(Type.HOLE, 4, 8));
        mBlocks.add(new Bloc(Type.HOLE, 4, 9));
        mBlocks.add(new Bloc(Type.HOLE, 4, 10));
        mBlocks.add(new Bloc(Type.HOLE, 4, 13));

        mBlocks.add(new Bloc(Type.HOLE, 5, 0));
        mBlocks.add(new Bloc(Type.HOLE, 5, 13));

        mBlocks.add(new Bloc(Type.HOLE, 6, 0));
        mBlocks.add(new Bloc(Type.HOLE, 6, 13));

        mBlocks.add(new Bloc(Type.HOLE, 7, 0));
        mBlocks.add(new Bloc(Type.HOLE, 7, 1));
        mBlocks.add(new Bloc(Type.HOLE, 7, 2));
        mBlocks.add(new Bloc(Type.HOLE, 7, 5));
        mBlocks.add(new Bloc(Type.HOLE, 7, 6));
        mBlocks.add(new Bloc(Type.HOLE, 7, 9));
        mBlocks.add(new Bloc(Type.HOLE, 7, 10));
        mBlocks.add(new Bloc(Type.HOLE, 7, 11));
        mBlocks.add(new Bloc(Type.HOLE, 7, 12));
        mBlocks.add(new Bloc(Type.HOLE, 7, 13));

        mBlocks.add(new Bloc(Type.HOLE, 8, 0));
        mBlocks.add(new Bloc(Type.HOLE, 8, 5));
        mBlocks.add(new Bloc(Type.HOLE, 8, 9));
        mBlocks.add(new Bloc(Type.HOLE, 8, 13));

        mBlocks.add(new Bloc(Type.HOLE, 9, 0));
        mBlocks.add(new Bloc(Type.HOLE, 9, 5));
        mBlocks.add(new Bloc(Type.HOLE, 9, 9));
        mBlocks.add(new Bloc(Type.HOLE, 9, 13));

        mBlocks.add(new Bloc(Type.HOLE, 10, 0));
        mBlocks.add(new Bloc(Type.HOLE, 10, 5));
        mBlocks.add(new Bloc(Type.HOLE, 10, 9));
        mBlocks.add(new Bloc(Type.HOLE, 10, 13));

        mBlocks.add(new Bloc(Type.HOLE, 11, 0));
        mBlocks.add(new Bloc(Type.HOLE, 11, 5));
        mBlocks.add(new Bloc(Type.HOLE, 11, 9));
        mBlocks.add(new Bloc(Type.HOLE, 11, 13));

        mBlocks.add(new Bloc(Type.HOLE, 12, 0));
        mBlocks.add(new Bloc(Type.HOLE, 12, 1));
        mBlocks.add(new Bloc(Type.HOLE, 12, 2));
        mBlocks.add(new Bloc(Type.HOLE, 12, 3));
        mBlocks.add(new Bloc(Type.HOLE, 12, 4));
        mBlocks.add(new Bloc(Type.HOLE, 12, 5));
        mBlocks.add(new Bloc(Type.HOLE, 12, 9));
        mBlocks.add(new Bloc(Type.HOLE, 12, 8));
        mBlocks.add(new Bloc(Type.HOLE, 12, 13));

        mBlocks.add(new Bloc(Type.HOLE, 13, 0));
        mBlocks.add(new Bloc(Type.HOLE, 13, 8));
        mBlocks.add(new Bloc(Type.HOLE, 13, 13));

        mBlocks.add(new Bloc(Type.HOLE, 14, 0));
        mBlocks.add(new Bloc(Type.HOLE, 14, 8));
        mBlocks.add(new Bloc(Type.HOLE, 14, 13));

        mBlocks.add(new Bloc(Type.HOLE, 15, 0));
        mBlocks.add(new Bloc(Type.HOLE, 15, 8));
        mBlocks.add(new Bloc(Type.HOLE, 15, 13));

        mBlocks.add(new Bloc(Type.HOLE, 16, 0));
        mBlocks.add(new Bloc(Type.HOLE, 16, 4));
        mBlocks.add(new Bloc(Type.HOLE, 16, 5));
        mBlocks.add(new Bloc(Type.HOLE, 16, 6));
        mBlocks.add(new Bloc(Type.HOLE, 16, 7));
        mBlocks.add(new Bloc(Type.HOLE, 16, 8));
        mBlocks.add(new Bloc(Type.HOLE, 16, 9));
        mBlocks.add(new Bloc(Type.HOLE, 16, 13));

        mBlocks.add(new Bloc(Type.HOLE, 17, 0));
        mBlocks.add(new Bloc(Type.HOLE, 17, 13));

        mBlocks.add(new Bloc(Type.HOLE, 18, 0));
        mBlocks.add(new Bloc(Type.HOLE, 18, 13));

        mBlocks.add(new Bloc(Type.HOLE, 19, 0));
        mBlocks.add(new Bloc(Type.HOLE, 19, 1));
        mBlocks.add(new Bloc(Type.HOLE, 19, 2));
        mBlocks.add(new Bloc(Type.HOLE, 19, 3));
        mBlocks.add(new Bloc(Type.HOLE, 19, 4));
        mBlocks.add(new Bloc(Type.HOLE, 19, 5));
        mBlocks.add(new Bloc(Type.HOLE, 19, 6));
        mBlocks.add(new Bloc(Type.HOLE, 19, 7));
        mBlocks.add(new Bloc(Type.HOLE, 19, 8));
        mBlocks.add(new Bloc(Type.HOLE, 19, 9));
        mBlocks.add(new Bloc(Type.HOLE, 19, 10));
        mBlocks.add(new Bloc(Type.HOLE, 19, 11));
        mBlocks.add(new Bloc(Type.HOLE, 19, 12));
        mBlocks.add(new Bloc(Type.HOLE, 19, 13));

        Bloc b = new Bloc(Type.START, 2, 2);
        mBall.setInitialRectangle(new RectF(b.getRectangle()));
        mBlocks.add(b);

        mBlocks.add(new Bloc(Type.END, 8, 11));

        return mBlocks;
    }
}
