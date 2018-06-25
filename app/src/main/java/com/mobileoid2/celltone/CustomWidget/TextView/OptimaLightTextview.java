package com.mobileoid2.celltone.CustomWidget.TextView;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

public class OptimaLightTextview  extends TextView {
    public OptimaLightTextview(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Light.otf"));
    }

    public OptimaLightTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Light.otf"));
    }

    public OptimaLightTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Light.otf"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OptimaLightTextview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Light.otf"));
    }
}



