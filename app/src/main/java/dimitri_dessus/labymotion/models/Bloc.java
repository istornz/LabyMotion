package dimitri_dessus.labymotion.models;

import android.graphics.RectF;

/**
 * Created by Dimitri on 14/04/2017.
 */

public class Bloc {
    public enum  Type { HOLE, START, END }

    private float SIZE = Ball.RADIUS * 2;

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
        this.mRectangle = new RectF(pX * SIZE, pY * SIZE, (pX + 1) * SIZE, (pY + 1) * SIZE);
    }
}
