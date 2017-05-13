package dimitri_dessus.labymotion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.List;

import dimitri_dessus.labymotion.engines.GraphicGameEngine;
import dimitri_dessus.labymotion.engines.PhysicalGameEngine;
import dimitri_dessus.labymotion.models.Ball;
import dimitri_dessus.labymotion.models.Bloc;

public class GameActivity extends AppCompatActivity {

    // Id of the victory dialog
    public static final int VICTORY_DIALOG = 0;

    // Id of the defeat dialog
    public static final int DEFEAT_DIALOG = 1;

    // Define screen height ratio
    private static final int SCREEN_HEIGHT_RATION = 143;

    // Definition of PhysicalEngine object
    private PhysicalGameEngine mEngine = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GraphicGameEngine mView = new GraphicGameEngine(this);
        setContentView(mView);

        mEngine = new PhysicalGameEngine(this);

        // Change here radius according to screen height
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Ball.RADIUS = (metrics.heightPixels - SCREEN_HEIGHT_RATION) / GraphicGameEngine.SURFACE_RATIO;

        Ball b = new Ball();
        mView.setBall(b);
        mEngine.setBall(b);

        List<Bloc> mList = mEngine.buildLabyrinthe();
        mView.setBlocks(mList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEngine.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEngine.stop();
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
}
