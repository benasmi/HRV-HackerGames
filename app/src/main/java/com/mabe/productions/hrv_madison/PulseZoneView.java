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

    private final float PULSE_ZONE_ELEMET_WIDTH = 0.2f; //20% screen's width
    private final float PULSE_ZONE_ELEMET_HEIGHT = 0.35f; //20% screen's height
    private final int PULSE_ZONE_ELEMET_PADDING = 70; //10px screen's padding
    private final int PULSE_ZONE_ELEMET_SELECTOR_WIDTH = 50; //10px screen's padding
    private final int PULSE_ZONE_ELEMET_SELECTOR_HEIGHT_PADDING = 25; //10px screen's padding


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

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progress = getProgressPercentage();
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        (Utils.intArrayContains(getRequiredPulseZones(), 1) ? selectedElement : basicElement).setBounds(PULSE_ZONE_ELEMET_PADDING / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH) - PULSE_ZONE_ELEMET_PADDING / 2, (int) (height * PULSE_ZONE_ELEMET_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 1) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 2) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH) + PULSE_ZONE_ELEMET_PADDING / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 2) - PULSE_ZONE_ELEMET_PADDING / 2, (int) (height * PULSE_ZONE_ELEMET_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 2) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 3) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH * 2) + PULSE_ZONE_ELEMET_PADDING / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 3) - PULSE_ZONE_ELEMET_PADDING / 2, (int) (height * PULSE_ZONE_ELEMET_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 3) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 4) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH * 3) + PULSE_ZONE_ELEMET_PADDING / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 4) - PULSE_ZONE_ELEMET_PADDING / 2, (int) (height * PULSE_ZONE_ELEMET_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 4) ? selectedElement : basicElement).draw(canvas);
        (Utils.intArrayContains(getRequiredPulseZones(), 5) ? selectedElement : basicElement).setBounds((int) (width * PULSE_ZONE_ELEMET_WIDTH * 4) + PULSE_ZONE_ELEMET_PADDING / 2, 0, (int) (width * PULSE_ZONE_ELEMET_WIDTH * 5) - PULSE_ZONE_ELEMET_PADDING / 2, (int) (height * PULSE_ZONE_ELEMET_HEIGHT));
        (Utils.intArrayContains(getRequiredPulseZones(), 5) ? selectedElement : basicElement).draw(canvas);

        elementSelector.draw(canvas);
    }

    private void setElementSelectorBoundsByMultiplier(float multiplier) {
        elementSelector.setBounds((int) (getWidth() * multiplier) - PULSE_ZONE_ELEMET_SELECTOR_WIDTH, (int) ((getHeight() * PULSE_ZONE_ELEMET_HEIGHT) + PULSE_ZONE_ELEMET_SELECTOR_HEIGHT_PADDING), (int) (getWidth() * multiplier + PULSE_ZONE_ELEMET_SELECTOR_WIDTH), getHeight());
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

    public void setProgressPercentage(float progressPercentage) {
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
