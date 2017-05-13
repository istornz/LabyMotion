package dimitri_dessus.labymotion.models;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Dimitri on 14/04/2017.
 * LabyMotion
 */

public class Ball {
    // Radius of the ball
    public static float RADIUS = 10.0f;
    private int ballColor = Color.GREEN;

    // Max speed of ball
    private static final float MAX_SPEED = 20.0f;

    // Slow down ball
    private static final float COMPENSATOR = 8.0f;

    // Used to compensate bounds
    private static final float REBOUND = 1.75f;

    // Rect used to set original position of the ball
    private RectF mInitialRectangle = null;

    // Ball's initial position
    public void setInitialRectangle(RectF pInitialRectangle) {
        this.mInitialRectangle = pInitialRectangle;
        this.mX = pInitialRectangle.left + RADIUS;
        this.mY = pInitialRectangle.top + RADIUS;
    }

    // Rect collision
    private RectF mRectangle = null;

    // X coordinate
    private float mX;
    public float getX() {
        return mX;
    }
    private void setPosX(float pPosX) {
        mX = pPosX;

        // If ball out of bounds, bounce direction
        if(mX < RADIUS) {
            mX = RADIUS;
            // Change ball position when bounce
            mSpeedY = -mSpeedY / REBOUND;
        } else if(mX > mWidth - RADIUS) {
            mX = mWidth - RADIUS;
            mSpeedY = -mSpeedY / REBOUND;
        }
    }

    // Y coordinates
    private float mY;
    public float getY() {
        return mY;
    }

    private void setPosY(float pPosY) {
        mY = pPosY;
        if(mY < RADIUS) {
            mY = RADIUS;
            mSpeedX = -mSpeedX / REBOUND;
        } else if(mY > mHeight - RADIUS) {
            mY = mHeight - RADIUS;
            mSpeedX = -mSpeedX / REBOUND;
        }
    }

    // Axis X speed
    private float mSpeedX = 0;
    // X axis invert (when direction changed on bounds)
    public void changeXSpeed() {
        mSpeedX = -mSpeedX;
    }

    // Axis Y speed
    private float mSpeedY = 0;

    // Y axis invert (when direction changed on bounds)
    public void changeYSpeed() {
        mSpeedY = -mSpeedY;
    }

    // Screen height size
    private int mHeight = -1;
    public void setHeight(int pHeight) {
        this.mHeight = pHeight;
    }

    // Screen width size
    private int mWidth = -1;
    public void setWidth(int pWidth) {
        this.mWidth = pWidth;
    }

    public Ball() {
        mRectangle = new RectF();
    }

    // Set ball coordinate
    public RectF putXAndY(float pX, float pY) {
        mSpeedX += pX / COMPENSATOR;
        if(mSpeedX > MAX_SPEED)
            mSpeedX = MAX_SPEED;
        if(mSpeedX < -MAX_SPEED)
            mSpeedX = -MAX_SPEED;

        mSpeedY += pY / COMPENSATOR;
        if(mSpeedY > MAX_SPEED)
            mSpeedY = MAX_SPEED;
        if(mSpeedY < -MAX_SPEED)
            mSpeedY = -MAX_SPEED;

        setPosX(mX + mSpeedY);
        setPosY(mY + mSpeedX);

        // Set collision coordinate
        mRectangle.set(mX - RADIUS, mY - RADIUS, mX + RADIUS, mY + RADIUS);

        return mRectangle;
    }

    // Reset ball to original position
    public void reset() {
        mSpeedX = 0;
        mSpeedY = 0;
        this.mX = mInitialRectangle.left + RADIUS;
        this.mY = mInitialRectangle.top + RADIUS;
    }

    public int getBallColor() {
        return ballColor;
    }

    public void setBallColor(double magneticField) {

        int color;
        if(magneticField <= 100.0f) {
            color = Color.parseColor("#66ff33");
        } else if (magneticField > 100.0f && magneticField <= 200.0f) {
            color = Color.parseColor("#ff0066");
        } else if (magneticField > 200.0f && magneticField <= 300.0f) {
            color = Color.parseColor("#ff66ff");
        } else {
            color = Color.parseColor("#9900ff");
        }

        this.ballColor = color;
    }
}
