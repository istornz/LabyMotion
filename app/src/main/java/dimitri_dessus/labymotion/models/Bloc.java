package dimitri_dessus.labymotion.models;

import android.graphics.RectF;

/**
 * Created by Dimitri on 14/04/2017.
 * LabyMotion
 */

public class Bloc {
    public enum  Type { HOLE, START, END }

    private Type mType = null;
    private RectF mRectangle = null;

    public Type getType() {
        return mType;
    }

    public RectF getRectangle() {
        return mRectangle;
    }

    public Bloc(Type pType, int pX, int pY) {
        this.mType = pType;
        float blocSize = Ball.RADIUS * 2;
        this.mRectangle = new RectF(pX * blocSize, pY * blocSize, (pX + 1) * blocSize, (pY + 1) * blocSize);
    }
}
