package com.mabe.productions.hrv_madison;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Benas on 4/22/2018.
 */


public class PulseZoneView extends View {

    private final float PULSE_ZONE_ELEMET_WIDTH = 0.2f; //20% screen's width
    private final float PULSE_ZONE_ELEMET_HEIGHT = 0.35f; //20% screen's height
    private final int PULSE_ZONE_ELEMET_PADDING = 70; //10px screen's padding
    private final int PULSE_ZONE_ELEMET_SELECTOR_WIDTH = 50; //10px screen's padding
    private final int PULSE_ZONE_ELEMET_SELECTOR_HEIGHT_PADDING = 25; //10px screen's padding



    private int requiredPulseZone = 0;
    private float progressPercentage = 5f;

    private Context context;
    private Drawable basicElement;
    private Drawable selectedElement;
    private Drawable elementSelector;


    public PulseZoneView(Context context, AttributeSet st) {
        super(context, st);
        this.context = context;
        Resources resources = getContext().getResources();
        basicElement = resources.getDrawable(R.drawable.pulse_zone_element);
        selectedElement = resources.getDrawable(R.drawable.pulse_zone_element_selected);
        elementSelector = resources.getDrawable(R.drawable.pulse_zone_element_selector);
        this.setMinimumHeight(100);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int pulseZone = getRequiredPulseZone();
        float progress = getProgressPercentage();
        Log.i("TEST", "DrawProgress: " + progress);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        (pulseZone==1 ? selectedElement : basicElement).setBounds(PULSE_ZONE_ELEMET_PADDING/2,0, (int) (width*PULSE_ZONE_ELEMET_WIDTH)-PULSE_ZONE_ELEMET_PADDING/2, (int) (height*PULSE_ZONE_ELEMET_HEIGHT));
        (pulseZone==1 ? selectedElement : basicElement).draw(canvas);
        (pulseZone==2 ? selectedElement : basicElement).setBounds((int) (width*PULSE_ZONE_ELEMET_WIDTH)+PULSE_ZONE_ELEMET_PADDING/2,0, (int) (width*PULSE_ZONE_ELEMET_WIDTH*2)-PULSE_ZONE_ELEMET_PADDING/2, (int) (height*PULSE_ZONE_ELEMET_HEIGHT));
        (pulseZone==2 ? selectedElement : basicElement).draw(canvas);
        (pulseZone==3 ? selectedElement : basicElement).setBounds((int) (width*PULSE_ZONE_ELEMET_WIDTH*2)+PULSE_ZONE_ELEMET_PADDING/2,0, (int) (width*PULSE_ZONE_ELEMET_WIDTH*3)-PULSE_ZONE_ELEMET_PADDING/2, (int) (height*PULSE_ZONE_ELEMET_HEIGHT));
        (pulseZone==3 ? selectedElement : basicElement).draw(canvas);
        (pulseZone==4 ? selectedElement : basicElement).setBounds((int) (width*PULSE_ZONE_ELEMET_WIDTH*3)+PULSE_ZONE_ELEMET_PADDING/2,0, (int) (width*PULSE_ZONE_ELEMET_WIDTH*4)-PULSE_ZONE_ELEMET_PADDING/2, (int) (height*PULSE_ZONE_ELEMET_HEIGHT));
        (pulseZone==4 ? selectedElement : basicElement).draw(canvas);
        (pulseZone==5 ? selectedElement : basicElement).setBounds((int) (width*PULSE_ZONE_ELEMET_WIDTH*4)+PULSE_ZONE_ELEMET_PADDING/2,0, (int) (width*PULSE_ZONE_ELEMET_WIDTH*5)-PULSE_ZONE_ELEMET_PADDING/2, (int) (height*PULSE_ZONE_ELEMET_HEIGHT));
        (pulseZone==5 ? selectedElement : basicElement).draw(canvas);
//
        elementSelector.setBounds((int) (width*progress)-PULSE_ZONE_ELEMET_SELECTOR_WIDTH, (int) ((height*PULSE_ZONE_ELEMET_HEIGHT)+PULSE_ZONE_ELEMET_SELECTOR_HEIGHT_PADDING), (int) (width*progress + PULSE_ZONE_ELEMET_SELECTOR_WIDTH),height);
        elementSelector.draw(canvas);
    }

    public int getRequiredPulseZone() {
        return requiredPulseZone;
    }

    public void setRequiredPulseZone(int requiredPulseZone) {
        this.requiredPulseZone = requiredPulseZone;
    }

    public float getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(float progressPercentage) {
        this.progressPercentage = progressPercentage;
        invalidate();
    }

}
