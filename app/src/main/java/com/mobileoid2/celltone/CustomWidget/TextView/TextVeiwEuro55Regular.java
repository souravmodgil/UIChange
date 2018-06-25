package com.mobileoid2.celltone.CustomWidget.TextView;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by root on 29/11/17.
 */

public class TextVeiwEuro55Regular extends TextView {
    public TextVeiwEuro55Regular(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }

    public TextVeiwEuro55Regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }

    public TextVeiwEuro55Regular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextVeiwEuro55Regular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }
}
