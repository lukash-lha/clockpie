package solutions.lha.android.clockpie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Lukas Hanzlik on 19. 4. 2016.
 */
public class ClockPie
        extends View {

    //draw arc starts drawing at "3 o'clock" so we have to return 90°
    final protected float CLOCK_START_ANGLE = -90;
    final protected int TOTAL_PARTS = 12;   //means the clock is divided into 12 hours, if you want minutes, make it 60, seconds -> 3600, etc.

    //region FIELDS

    protected Paint piePaint;
    protected RectF rectF;
    protected int[] hoursInSegment;
    protected int[] colors;
    protected int diameter;
    protected View backgroundView;
    protected int hoursTotal;
    protected boolean pieVisible = true;

    //endregion

    //region ctors

    /**
     * Constructor needed if instantiating the object manually (not from a layout XML file).
     *
     * @param context context
     */
    public ClockPie(Context context) {
        super(context);
        initClockPie();
    }

    /**
     * Constructor initializing with any attributes from a layout file.
     *
     * @param context context
     * @param attrs   attributes for initialization
     */
    public ClockPie(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClockPie();
    }

    /**
     * initialization of class specific properties
     */
    private final void initClockPie() {
        this.diameter = 0;
        this.backgroundView = null;
        this.hoursInSegment = null;
        this.colors = null;
        this.hoursTotal = TOTAL_PARTS;   //default is full 12 hours clock

        piePaint = new Paint();
        piePaint.setAntiAlias(true);
        piePaint.setDither(true);
        piePaint.setStyle(Paint.Style.FILL);
    }

    //endregion

    //region CALCULATED PROPS

    protected int getFullAngleRange() {
        return 360 / TOTAL_PARTS * this.hoursTotal;
    }

    //endregion

    //region PUBLIC API - PROPS

    /**
     * sets the view in background, mostly ImageView (makes sense), but it's not forced to be just ImageView
     *
     * @param backgroundView
     */
    public void setBackgroundView(View backgroundView) {
        this.backgroundView = backgroundView;
        //invalidate();
    }

    /**
     * sets the total amount of hours we want to use, for example: 12 - full clock (this is default), 6 - half of it, 3 - just 1/4 of clock
     *
     * @param hoursTotal
     */
    public void setHoursTotal(int hoursTotal) {
        if (hoursTotal > TOTAL_PARTS) {
            throw new IllegalArgumentException("Max value for Hours Total is " + TOTAL_PARTS);
        }
        this.hoursTotal = hoursTotal;
        //invalidate();
    }

    /**
     * sets the data for segments
     * - how many hours are in it
     * - how it should be colored
     *
     * @param hoursInSegments   amount of hours in each segment
     * @param colorsInSegements color of each segment
     */
    public void setData(int[] hoursInSegments, int[] colorsInSegements) {

        if (hoursInSegments == null || colorsInSegements == null) {
            throw new IllegalArgumentException("You're passing null data for segments!");
        }

        if (hoursInSegments.length != colorsInSegements.length) {
            throw new IllegalArgumentException("Not equal length of hours and colors input arrays!");
        }

        int sum = 0;
        for (int hours : hoursInSegments) {
            sum += hours;
        }

        if (sum > TOTAL_PARTS) {
            throw new IllegalArgumentException("You can not divide 12 hours in more than 12 hours in segments (param hoursInSegments)!");
        }

        this.hoursInSegment = hoursInSegments;
        this.colors = colorsInSegements;
        invalidate();
    }

    /**
     * set diameter of the circle = length of side of rectangle
     *
     * @param diameter
     */
    public void setDiameter(int diameter) {
        //this.setDiameter(diameter, true);
        this.setDiameter(diameter, false);
    }

    public void setPieVisible(boolean pieVisible) {
        this.pieVisible = pieVisible;
        invalidate();   //this really needs to be drawn again
    }

    //endregion

    //region HELPERS

    /**
     * set diameter of the circle = length of side of rectangle with option to decide if invalidate or not
     *
     * @param diameter
     * @param invalidate
     */
    protected void setDiameter(int diameter, boolean invalidate) {
        this.diameter = diameter;
        //this.setMeasuredDimension(diameter, diameter);
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = diameter;
        params.width = diameter;
        this.setLayoutParams(params);

        if (invalidate)
            invalidate();
    }

    /**
     * function to set circle diameter (side length of the rectangle around) from view in the background
     * as I expect use on some backround image of clock, but I made it more general, so no ImageView but View
     */
    protected void setDiameterFromBackgroundView() {
        //check if the view was already created
        if (backgroundView != null) {
            //first get dimensions of the background View
            int height = backgroundView.getHeight();
            int width = backgroundView.getWidth();
            //as we are drawing circle - in square, we need just one dimension, more logical is the shorter one
            this.setDiameter(Math.min(height, width), false);
        } else {
            this.diameter = 0;  //default value
            Log.e("Null background view", "The background view is null, it wasn't created yet!");
        }
        //invalidate();
    }

    /**
     * calculates the segments for drawing from input data
     *
     * @return calculated segments
     */
    protected float[] getPieSegmentAngles() {

        float[] segValues = new float[this.hoursInSegment.length];

        //in case we don't divide full 12 hours but less, then we can't multiply by 360 degrees, we have to take the part of it that we are interested in
        int fullAngleRange = getFullAngleRange();

        for (int i = 0; i < this.hoursInSegment.length; i++) {

            segValues[i] = ((float) this.hoursInSegment[i] / this.hoursTotal) * fullAngleRange;  //as all input data are int, there has to be one float typecast to make the result of type float
        }

        return segValues;
    }

    /**
     * where "the magic" happens :) here the clock pie is drawn on screen / canvas
     * ment to be overriden in ancestros, like the AnimateClockPie
     */
    protected void drawClockPie(Canvas canvas) {
        float[] segmentAngle = getPieSegmentAngles();
        float fromAngle = CLOCK_START_ANGLE;
        for (int i = 0; i < segmentAngle.length; i++) {

            piePaint.setColor(colors[i]);
            canvas.drawArc(rectF, fromAngle, segmentAngle[i], true, piePaint);
            //Log.v("animation-base", "canvas.drawArc(rectF, " + fromAngle + ", " + (segmentAngle[i]) + ", true, piePaint);");
            fromAngle += segmentAngle[i];
        }
    }

    //endregion

    //region LIFE-CYCLE

    @Override
    protected void onDraw(Canvas canvas) {

        if (!this.pieVisible) {
            //if the pie should not be drawn, don't draw it and "hide" it if it was already drawn
            if (rectF != null) {
                rectF.setEmpty();
                return;
            }
        }

        if (hoursInSegment != null) {

            if (this.backgroundView != null) {
                this.setDiameterFromBackgroundView();
            }

            if (rectF != null) {
                rectF.setEmpty();
                rectF.left = 0;
                rectF.top = 0;
                rectF.right = this.diameter;
                rectF.bottom = this.diameter;
            } else {
                rectF = new RectF(0, 0, this.diameter, this.diameter);
            }

            drawClockPie(canvas);
        }
    }

    //endregion

    //region PUBLIC API - functions

    /**
     * when I invalidated automatically after property settings there was simply too much redrawing...
     * so I left invalidate() only in function for setting data (as without data you have nothing to draw and I have to pass data after the ClockPie was created)
     * once I'm wiser and have time :) I'll try to make it somehow smarter automatically redrawing, till then - use this function,
     * if you need change properties later after the ClockPie was drawn
     *
     * @param pieVisible if the clockPie should be visible or not after redraw
     */
    public void reDraw(boolean pieVisible) {
        this.pieVisible = pieVisible;
        this.postInvalidate();  //in case it was called from different thread
    }

    //endregion

}
