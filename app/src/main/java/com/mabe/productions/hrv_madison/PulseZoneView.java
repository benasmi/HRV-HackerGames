package com.mabe.productions.hrv_madison;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PulseZoneView extends View {

    private static final float PULSE_ZONE_ELEMET_WIDTH = 0.2f; //20% screen's width
    private static final float PULSE_ZONE_ELEMENT_HEIGHT = 0.35f; //20% screen's height
    private static final int PULSE_ZONE_ELEMENT_PADDING = 10; //Specified in dp
    private static final int PULSE_ZONE_ELEMENT_SELECTOR_WIDTH = 15; //Specified in dp
    private static final int PULSE_ZONE_ELEMENT_SELECTOR_HEIGHT_PADDING = 3; //Specified in dp

    private int horizontalElementPaddingInPx;
    private int verticalElementPaddingInPx;
    private int selectorWidthInPx;

    private int[] requiredPulseZones = {1, 2};
    private float progressPercentage = 5f;

    private Context context;
    private Drawable basicElement;
    private Drawable selectedElement;
    private Drawable elementSelector;
    private static final long CURSOR_MOVEMENT_DURATION = 500;


    public PulseZoneView(Context context, AttributeSet st) {
        super(context, st);
        this.context = context;
        Resources resources = getContext().getResources();
        basicElement = resources.getDrawable(R.drawable.pulse_zone_element);
        selectedElement = resources.getDrawable(R.drawable.pulse_zone_element_selected);
        elementSelector = resources.getDrawable(R.drawable.pulse_zone_element_selector);
        setElementSelectorBoundsByMultiplier(50f);
        this.setMinimumHeight(100);

        horizontalElementPaddingInPx = Utils.convertDpToPixel(PULSE_ZONE_ELEMENT_PADDING, this.getContext());
        verticalElementPaddingInPx = Utils.convertDpToPixel(PULSE_ZONE_ELEMENT_SELECTOR_HEIGHT_PADDING, this.getContext());
        selectorWidthInPx = Utils.convertDpToPixel(PULSE_ZONE_ELEMENT_SELECTOR_WIDTH, this.getContext());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progress = getProgressPercentage();
        int width = getWidth();
        int height = getHeight();

        (Utils.intArrayContains(getRequiredPulseZones(), 1) ? selectedElement : basicElement).setBounds(horizontalElementPaddingInPx / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH) - horizontalElementPaddingInPx / 2, (int) (height * PULSE_ZONE_ELEMENT_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 1) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 2) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH) + horizontalElementPaddingInPx / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 2) - horizontalElementPaddingInPx / 2, (int) (height * PULSE_ZONE_ELEMENT_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 2) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 3) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH * 2) + horizontalElementPaddingInPx / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 3) - horizontalElementPaddingInPx / 2, (int) (height * PULSE_ZONE_ELEMENT_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 3) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 4) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH * 3) + horizontalElementPaddingInPx / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 4) - horizontalElementPaddingInPx / 2, (int) (height * PULSE_ZONE_ELEMENT_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 4) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 5) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH * 4) + horizontalElementPaddingInPx / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 5) - horizontalElementPaddingInPx / 2, (int) (height * PULSE_ZONE_ELEMENT_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 5) ? selectedElement : basicElement).draw(canvas);

        selectedElement.draw(canvas);
        elementSelector.draw(canvas);
    }

    private void setElementSelectorBoundsByMultiplier(float multiplier) {
        elementSelector.setBounds((int) (getWidth() * multiplier) - selectorWidthInPx, (int) ((getHeight() * PULSE_ZONE_ELEMENT_HEIGHT) + verticalElementPaddingInPx), (int) (getWidth() * multiplier + selectorWidthInPx), getHeight());
        Log.i("bounds", elementSelector.getBounds().flattenToString());
    }

    public int[] getRequiredPulseZones() {
        return requiredPulseZones;
    }

    public void setRequiredPulseZones(int[] requiredPulseZones) {
        this.requiredPulseZones = requiredPulseZones;
        invalidate();
    }

    public float getProgressPercentage() {
        return progressPercentage;
    }

    /**
     * Sets the cursor's horizontal position based on given floating point value (0.0f-1.0f)
     * @param progressPercentage The progress multiplier (0.0f to 1.0f)
     */
    public void setProgressMultiplier(float progressPercentage) {
        this.progressPercentage = progressPercentage;
        setElementSelectorBoundsByMultiplier(progressPercentage);
        invalidate();
    }

    public void setProgressPercentageWithAnim(float progressPercentage) {
        ValueAnimator va = ValueAnimator.ofFloat(this.progressPercentage, progressPercentage);
        va.setDuration(CURSOR_MOVEMENT_DURATION);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                setElementSelectorBoundsByMultiplier((float) animation.getAnimatedValue());
                postInvalidate();
            }
        });
        va.start();
        this.progressPercentage = progressPercentage;

    }

}
