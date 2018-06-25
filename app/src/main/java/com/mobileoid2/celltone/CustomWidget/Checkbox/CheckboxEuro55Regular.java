package com.mobileoid2.celltone.CustomWidget.Checkbox;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by root on 29/11/17.
 */

public class CheckboxEuro55Regular extends CheckBox {
    public CheckboxEuro55Regular(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }

    public CheckboxEuro55Regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }

    public CheckboxEuro55Regular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CheckboxEuro55Regular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/eurof55.ttf"));
    }
}
