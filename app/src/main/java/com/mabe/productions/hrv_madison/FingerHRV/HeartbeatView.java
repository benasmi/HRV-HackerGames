package com.mabe.productions.hrv_madison.FingerHRV;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.mabe.productions.hrv_madison.R;


public class HeartbeatView extends View {

    private static Drawable greenDrawable = null;
    private static Drawable redDrawable = null;

    private static int parentWidth = 0;
    private static int parentHeight = 0;

    public HeartbeatView(Context context, AttributeSet attr) {
        super(context, attr);
        greenDrawable = getResources().getDrawable(R.drawable.ic_heart_white_hrv_finger);
        redDrawable = getResources().getDrawable(R.drawable.ic_heart);
    }

    public HeartbeatView(Context context) {
        super(context);
        greenDrawable = getResources().getDrawable(R.drawable.ic_heart_white_hrv_finger);
        redDrawable = getResources().getDrawable(R.drawable.ic_heart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(parentWidth, parentHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) throw new NullPointerException();

        Drawable d = null;
        if (HeartRateMonitor.getCurrent() == HeartRateMonitor.TYPE.GREEN) {
            d = greenDrawable;
        } else {
            d = redDrawable;
        }
        d.setBounds(canvas.getClipBounds());
        d.draw(canvas);
    }
}