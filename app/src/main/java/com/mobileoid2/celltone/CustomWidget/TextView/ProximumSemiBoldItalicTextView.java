package com.mobileoid2.celltone.CustomWidget.TextView;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

public class ProximumSemiBoldItalicTextView extends TextView {
    public ProximumSemiBoldItalicTextView(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-SemiboldItalic.otf"));
    }

    public ProximumSemiBoldItalicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-SemiboldItalic.otf"));
    }

    public ProximumSemiBoldItalicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-SemiboldItalic.otf"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ProximumSemiBoldItalicTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-SemiboldItalic.otf"));
    }
}

