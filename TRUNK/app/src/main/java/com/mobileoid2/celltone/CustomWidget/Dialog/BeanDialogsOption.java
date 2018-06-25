package com.mobileoid2.celltone.CustomWidget.Dialog;

import android.graphics.drawable.Drawable;
import android.view.View;

public class BeanDialogsOption {

    private String title;
    private Drawable drawableResource;
    private View.OnClickListener actionListner;


    public BeanDialogsOption(String title, Drawable drawableResource, View.OnClickListener actionListner) {
        this.title = title;
        this.drawableResource = drawableResource;
        this.actionListner = actionListner;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getDrawableResource() {
        return drawableResource;
    }

    public View.OnClickListener getActionListner() {
        return actionListner;
    }
}