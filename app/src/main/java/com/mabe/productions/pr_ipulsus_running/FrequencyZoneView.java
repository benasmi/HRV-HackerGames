package com.mabe.productions.pr_ipulsus_running;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;


public class FrequencyZoneView extends View {

    private final float BAR_HEIGHT = 0.35f;
    private final float RED_TO_GREEN_WIDTH = 0.3333333333333f; //20% screen's height
    private final float GREEN_WIDTH = 0.3333333333333f; //60% screen's height
    private final float GREEN_TO_RED_WIDTH = 0.3333333333333f; //20% screen's height

    private final int PULSE_ZONE_ELEMET_SELECTOR_WIDTH = 40; //10px screen's padding
    private final int PULSE_ZONE_ELEMET_SELECTOR_HEIGHT_PADDING = 15; //10px screen's padding
    private final float TEXT_PADDING = 0.02f;


    public double multiplier;

    private Drawable red_to_green;
    private Drawable green;
    private Drawable green_to_red;
    private Drawable elementSelector;
    private Paint txt_paint;


    public FrequencyZoneView(Context context, AttributeSet st) {
        super(context, st);
        Resources resources = getContext().getResources();


        txt_paint = new Paint();
        txt_paint.setColor(Color.WHITE);
        txt_paint.setTextSize(38f);


        red_to_green = resources.getDrawable(R.drawable.red_to_green_rectangle);
        green = resources.getDrawable(R.drawable.green_to_green_rectangle);
        green_to_red = resources.getDrawable(R.drawable.green_to_red_rectangle);
        elementSelector = resources.getDrawable(R.drawable.pulse_zone_element_selector);
        this.setMinimumHeight(100);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        red_to_green.setBounds(0, 0, (int) (width * RED_TO_GREEN_WIDTH), (int) (height * BAR_HEIGHT));
        red_to_green.draw(canvas);
        green.setBounds((int) (width * RED_TO_GREEN_WIDTH), 0, (int) (width * GREEN_WIDTH) + (int) (width * RED_TO_GREEN_WIDTH), (int) (height * BAR_HEIGHT));
        green.draw(canvas);
        green_to_red.setBounds((int) (width * RED_TO_GREEN_WIDTH) + (int) (width * GREEN_WIDTH), 0, (int) ((width * GREEN_TO_RED_WIDTH) + (width * RED_TO_GREEN_WIDTH) + (width * GREEN_WIDTH)), (int) (height * BAR_HEIGHT));
        green_to_red.draw(canvas);
        elementSelector.setBounds((int) (getWidth() * multiplier) - PULSE_ZONE_ELEMET_SELECTOR_WIDTH, (int) ((height * BAR_HEIGHT) + PULSE_ZONE_ELEMET_SELECTOR_HEIGHT_PADDING), (int) (getWidth() * multiplier + PULSE_ZONE_ELEMET_SELECTOR_WIDTH), height);
        elementSelector.draw(canvas);


        float width_symp = txt_paint.measureText("Sympathetic");
        float width_balance = txt_paint.measureText("ANS Balance");
        //canvas.drawText("Parasympathetic",0+width*TEXT_PADDING,45,txt_paint);
        //canvas.drawText("Sympathetic",width-width_symp-(width*TEXT_PADDING),45,txt_paint);
        //canvas.drawText("ANS Balance",width/2-width_balance/2,45,txt_paint);
    }

    public void setElementPosition(float ratio) {
        float calcPercentage = ratio * 100 / 2.5f;
        multiplier = calculatePercentage(calcPercentage / 100f);

    }


    private double calculatePercentage(float x) {
        if (x <= 0.2) {
            return 5f * x / 3f;
        }
        if (x > 0.2 && x < 0.8) {
            return (5f * x / 9f) + (2f / 9f);
        }
        if (x >= 0.8) {
            return (5f * x / 3f) - 2f / 3f;
        }
        return 0;
    }


}
