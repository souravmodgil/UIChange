package com.mobileoid2.celltone.CustomWidget.TextView;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

public class OptimaItalicTextview extends TextView {
    public OptimaItalicTextview(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Optima-Italic_34079.ttf"));
    }

    public OptimaItalicTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Optima-Italic_34079.ttf"));
    }

    public OptimaItalicTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Optima-Italic_34079.ttf"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OptimaItalicTextview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OPTIMA_B.TTF"));
    }
}
