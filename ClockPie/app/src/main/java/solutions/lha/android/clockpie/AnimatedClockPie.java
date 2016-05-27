package solutions.lha.android.clockpie;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by [lha] on 19. 4. 2016.
 * <p/>
 * extension to static ClockPie class
 */
public class AnimatedClockPie
        extends ClockPie {

    //how much more degrees we will draw in one animation step
    final protected float ANIMATION_STEP_DEGREE = 6;

    //region FIELDS
    //listeners
    OnAnimationFinishedListener onAnimationFinishedListener;
    OnAnimatedSegmentChangedListener onAnimatedSegmentChangedListener;
    /**
     * is animation active / running?
     */
    private boolean animationRunning = false;
    private Timer mAnimationTimer;
    private TimerTask mAnimationTimerTask;
    private int lastActivatedSegment;   //to know when animation of new segment starts
    private float currentAngleToDraw;
    private int animationStepDuration;

    //endregion

    //region ctors (mandatory)

    public AnimatedClockPie(Context context) {
        super(context);
    }

    public AnimatedClockPie(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //endregion

    //region HELPERS

    /**
     * initialization of animation
     *
     * @param duration duration of animation in SECONDS
     */
    private void initAnimation(long duration) {
        this.currentAngleToDraw = 0;
        //in my case, where I work with full hours the result should be always int, but JAVA doesn't know (correctly), so Round it.
        //it has to be int because it's used as int parameter in timer schedule
        this.animationStepDuration = Math.round(duration / (this.getFullAngleRange() / ANIMATION_STEP_DEGREE) * 1000); //seconds * 1000 = miliseconds
        this.animationRunning = true;
        this.lastActivatedSegment = 0;
        this.pieVisible = true;
        //set a new Timer
        this.mAnimationTimer = new Timer();
        //initialize the TimerTask's job
        this.mAnimationTimerTask = new AnimateClockPieTask(this);
    }

    /**
     * end of the animation
     */
    private void finishAnimation() {
        this.mAnimationTimer.cancel();
        this.animationRunning = false;
        this.fireOnAnimationFinished();  //no parameters to pass at the moment
//        Log.v("animation", "finished");
    }

    @Override
    /**
     * override of the drawing method
     * here the animation happens
     */
    protected void drawClockPie(Canvas canvas) {

        if (!animationRunning) {

            //non-animated drawing is implemented in super class
            super.drawClockPie(canvas);

        } else if (this.currentAngleToDraw > 0) {   //there is nothing special to do to draw nothing :)

            if (this.currentAngleToDraw >= this.getFullAngleRange()) {

                //finish animation + draw (we draw full clock, it can be done the way as super class, no difficult algorithms)
                this.finishAnimation();
                super.drawClockPie(canvas);

            } else {

                float[] segmentAngle = getPieSegmentAngles();
                float fromAngle = CLOCK_START_ANGLE;

                boolean animationStepDone = false;
                int i = 0;
                float segmentAngleSum = 0;

                while (!animationStepDone) {
                    segmentAngleSum += segmentAngle[i];
                    piePaint.setColor(colors[i]);

                    if (segmentAngleSum <= this.currentAngleToDraw) {
                        canvas.drawArc(rectF, fromAngle, segmentAngle[i], true, piePaint);
                        animationStepDone = segmentAngleSum == this.currentAngleToDraw;   //based on the if condition, the only way we are done in this case is, if we finished drawing exactly in the desired angle
                    } else {    //segmentToAngle > this.currentAngleToDraw
                        canvas.drawArc(rectF, fromAngle, (this.currentAngleToDraw - (segmentAngleSum - segmentAngle[i])), true, piePaint);
                        animationStepDone = true;
                    }
                    //iterate
                    fromAngle += segmentAngle[i];
                    i++;

                    animationStepDone = animationStepDone || (i >= segmentAngle.length); //watch the index, just for sure
                }

                if (i > this.lastActivatedSegment) {
                    this.lastActivatedSegment = i;
                    this.fireOnAnimatedSegmentChanged(i);
                }
            }
        }
    }

    //endregion

    //region LIFE-CYCLE

    //endregion

    //region EVENTS

    /**
     * @param newSegment
     */
    protected void fireOnAnimatedSegmentChanged(int newSegment) {

        if (onAnimatedSegmentChangedListener != null)
            onAnimatedSegmentChangedListener.onAnimatedSegmentChanged(this, newSegment);

    }

    /**
     *
     */
    protected void fireOnAnimationFinished() {

        if (onAnimationFinishedListener != null)
            onAnimationFinishedListener.onAnimationFinished(this);

    }

    //endregion

    //region PUBLIC API - functions

    /**
     * next animation step = update the range of degrees (angle) of the pie to be drawn
     */
    public void nextAnimationStep() {
        this.currentAngleToDraw += ANIMATION_STEP_DEGREE;
    }

    /**
     * start animation
     *
     * @param duration duration of animation in SECONDS
     */
    public void startAnimation(long duration) {
        if (!animationRunning) {
            initAnimation(duration);
            //schedule the timer (task, first duration (ms), every next duration (ms))
            mAnimationTimer.schedule(mAnimationTimerTask, animationStepDuration, animationStepDuration);
            //Log.v("animation", "started");
        } //else {
        //Log.v("animation", "already running");
        //}
    }

    /**
     * stop animation, call for example in OnStop event in parent Activity
     */
    public void stopAnimation() {
        if (animationRunning) {
            this.finishAnimation();
            //Log.v("animation", "stopped");
        }
    }

    /**
     * set listener for change of the new/next segment that starts to be animated
     *
     * @param listener
     */
    public void setOnAnimatedSegmentChangedListener(OnAnimatedSegmentChangedListener listener) {
        this.onAnimatedSegmentChangedListener = listener;
    }

    /**
     * set listener for end of the animation
     *
     * @param listener
     */
    public void setOnAnimationFinishedListener(OnAnimationFinishedListener listener) {
        this.onAnimationFinishedListener = listener;
    }

    //endregion

    public interface OnAnimatedSegmentChangedListener {

        /**
         * when new segment starts to being animated
         *
         * @param newSegment order of the segment (1 - first, 2 - second, ....)
         */
        void onAnimatedSegmentChanged(AnimatedClockPie obj, int newSegment);

    }

    public interface OnAnimationFinishedListener {

        void onAnimationFinished(AnimatedClockPie obj);

    }

    /**
     * our task for updating the animation - calling next step of it
     */
    class AnimateClockPieTask
            extends TimerTask {

        /**
         * we need to reference the clockPie
         */
        private AnimatedClockPie clockPie;

        public AnimateClockPieTask(AnimatedClockPie clockPie) {
            this.clockPie = clockPie;
        }

        @Override
        public void run() {

            if (clockPie == null) {
                throw new RuntimeException("You didn't initialize the ClockPie (in constructor) or used NULL object to do it.");
            }

            clockPie.nextAnimationStep(); //invoke next step of animation
            clockPie.postInvalidate();  //make it redraw
        }

    }
}
