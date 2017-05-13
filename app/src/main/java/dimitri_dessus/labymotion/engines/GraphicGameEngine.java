package dimitri_dessus.labymotion.engines;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import dimitri_dessus.labymotion.models.Ball;
import dimitri_dessus.labymotion.models.Bloc;

/**
 * Created by Dimitri on 14/04/2017.
 * LabyMotion
 */

public class GraphicGameEngine extends SurfaceView implements SurfaceHolder.Callback {
    private Ball mBall;
    public Ball getBall() {
        return mBall;
    }
    public void setBall(Ball pBall) {
        this.mBall = pBall;
    }
    public static final int SURFACE_RATIO = 28;

    private final SurfaceHolder mSurfaceHolder;
    private final DrawingThread mThread;

    private List<Bloc> mBlocks = null;
    public List<Bloc> getBlocks() {
        return mBlocks;
    }

    public void setBlocks(List<Bloc> pBlocks) {
        this.mBlocks = pBlocks;
    }

    private final Paint mPaint;

    public GraphicGameEngine(Context pContext) {
        super(pContext);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mThread = new DrawingThread();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        mBall = new Ball();
    }

    @Override
    public void draw(Canvas pCanvas) {
        super.draw(pCanvas);

        // Draw background
        pCanvas.drawColor(Color.BLUE);
        if(mBlocks != null) {
            // Draw blocs
            for(Bloc b : mBlocks) {
                switch(b.getType()) {
                    case START:
                        mPaint.setColor(Color.WHITE);
                        break;
                    case END:
                        mPaint.setColor(Color.RED);
                        break;
                    case HOLE:
                        mPaint.setColor(Color.BLACK);
                        break;
                }
                pCanvas.drawRect(b.getRectangle(), mPaint);
            }
        }

        // Draw ball
        if(mBall != null) {
            mPaint.setColor(Ball.COLOR);
            pCanvas.drawCircle(mBall.getX(), mBall.getY(), Ball.RADIUS, mPaint);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder pHolder, int pFormat, int pWidth, int pHeight) { }

    @Override
    public void surfaceCreated(SurfaceHolder pHolder) {
        mThread.keepDrawing = true;
        mThread.start();
        // Create ball using screen coordinates
        if(mBall != null ) {
            this.mBall.setHeight(getHeight());
            this.mBall.setWidth(getWidth());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder pHolder) {
        mThread.keepDrawing = false;
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.d("GraphicGameEngine", "Error when destroying surface");
            }
        }

    }

    private class DrawingThread extends Thread {
        boolean keepDrawing = true;

        @Override
        public void run() {
            Canvas canvas;
            while (keepDrawing) {
                canvas = null;

                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder) {
                        draw(canvas);
                    }
                } finally {
                    if (canvas != null)
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                }

                // Set FPS (images per second) by using sleep method
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Log.d("GraphicGameEngine", "Error executing sleep method");
                }
            }
        }
    }
}
