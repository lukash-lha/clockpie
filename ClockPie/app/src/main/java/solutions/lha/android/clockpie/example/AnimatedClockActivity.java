package solutions.lha.android.clockpie.example;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import solutions.lha.android.clockpie.AnimatedClockPie;
import solutions.lha.android.clockpie.R;

/**
 * Created by [lha] on 19. 4. 2016.
 */
public class AnimatedClockActivity
        extends AppCompatActivity
        implements View.OnClickListener, AnimatedClockPie.OnAnimationFinishedListener, AnimatedClockPie.OnAnimatedSegmentChangedListener {

    ImageView clockImageAM, clockImagePM;

    AnimatedClockPie pieAm, piePm, bigClockPie;

    TextView price1, price2, price3, price4, bonus;

    Button redrawClockPie, animateClockPie;

    Animation highlight;

    //region HELPERS

    private void highlightPrice(TextView price) {
        if (price.getVisibility() == View.INVISIBLE) {
            price.setVisibility(View.VISIBLE);
        } else {
            price.startAnimation(highlight);
        }
    }

    //endregion

    //region LIFE-CYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_animated_clock);

        clockImageAM = (ImageView) findViewById(R.id.clockBackgroundAM);
        clockImagePM = (ImageView) findViewById(R.id.clockBackgroundPM);

        price1 = (TextView) findViewById(R.id.lblPrice1);
        price2 = (TextView) findViewById(R.id.lblPrice2);
        price3 = (TextView) findViewById(R.id.lblPrice3);
        price4 = (TextView) findViewById(R.id.lblPrice4);
        bonus = (TextView) findViewById(R.id.lblBonus);

        highlight = AnimationUtils.loadAnimation(this, R.anim.highlight);

        //clockPie 1 (AM)

        pieAm = (AnimatedClockPie) findViewById(R.id.pie_chart_am);
        pieAm.setTag("pieAm");
        pieAm.setBackgroundView(clockImageAM);
        //pieAm.setDiameter(300);
        int[] dataAM = {
                6,
                3,
                3};
        int[] colorsAM = {
                ContextCompat.getColor(getBaseContext(), R.color.price_lowest),
                ContextCompat.getColor(getBaseContext(), R.color.price_highest),
                ContextCompat.getColor(getBaseContext(), R.color.price_high)
        };
        pieAm.setData(dataAM, colorsAM);
        pieAm.setOnAnimationFinishedListener(this);
        pieAm.setOnAnimatedSegmentChangedListener(this);

        //clockPie 2 (PM)

        piePm = (AnimatedClockPie) findViewById(R.id.pie_chart_pm);
        piePm.setTag("piePm");
        piePm.setBackgroundView(clockImagePM);
        //piePm.setDiameter(300);
        int[] dataPM = {
                1,
                6,
                3,
                2};
        int[] colorsPM = {
                ContextCompat.getColor(getBaseContext(), R.color.price_high),
                ContextCompat.getColor(getBaseContext(), R.color.price_low),
                ContextCompat.getColor(getBaseContext(), R.color.price_highest),
                ContextCompat.getColor(getBaseContext(), R.color.price_lowest),
        };
        piePm.setData(dataPM, colorsPM);
        piePm.setOnAnimationFinishedListener(this);
        piePm.setOnAnimatedSegmentChangedListener(this);

        // others
        redrawClockPie = (Button) findViewById(R.id.btnHideClockPie);
        redrawClockPie.setOnClickListener(this);

        animateClockPie = (Button) findViewById(R.id.btnAnimateClockPie);
        animateClockPie.setOnClickListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        bigClockPie.stopAnimation();
    }

    //endregion

    //region IMPLEMENTS OnClick

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnHideClockPie:
                pieAm.reDraw(false);
                piePm.reDraw(false);
                break;
            case R.id.btnAnimateClockPie:
                pieAm.startAnimation(20);   //20s
                //piePm.reDraw(false);
                break;
        }
    }

    //endregion

    //region IMPLEMENTS AnimatedClockPieListener

    @Override
    public void onAnimationFinished(AnimatedClockPie obj) {
        int id = obj.getId();

        switch (id) {
            case R.id.pie_chart_am:
                piePm.startAnimation(20);
                Toast.makeText(this, "END AM", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pie_chart_pm:
                this.highlightPrice(bonus);
                Toast.makeText(this, "END PM", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onAnimatedSegmentChanged(AnimatedClockPie obj, int newSegment) {
        int id = obj.getId();
        //Toast.makeText(this, Integer.toString(newSegment), Toast.LENGTH_SHORT).show();

        switch (id) {
            case R.id.pie_chart_am:
                switch (newSegment) {
                    case 1:
                        this.highlightPrice(price1);
                        break;
                    case 2:
                        this.highlightPrice(price4);
                        break;
                    case 3:
                        this.highlightPrice(price3);
                        break;
                }
                break;
            case R.id.pie_chart_pm:
                switch (newSegment) {
                    case 1:
                        //continuing from AM
                        this.highlightPrice(price3);
                        break;
                    case 2:
                        this.highlightPrice(price2);
                        break;
                    case 3:
                        this.highlightPrice(price4);
                        break;
                    case 4:
                        this.highlightPrice(price1);
                        break;
                }
                break;
        }

    }

    //endregion
}
