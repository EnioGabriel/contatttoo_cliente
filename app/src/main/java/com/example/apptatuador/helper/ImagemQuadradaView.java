package com.example.apptatuador.helper;

import android.content.Context;
import android.util.AttributeSet;

public class ImagemQuadradaView extends androidx.appcompat.widget.AppCompatImageView {

    public ImagemQuadradaView(Context context) {
        super(context);
    }

    public ImagemQuadradaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagemQuadradaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width,width);
    }

}