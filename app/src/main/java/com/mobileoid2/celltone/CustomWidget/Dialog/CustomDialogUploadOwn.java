package com.mobileoid2.celltone.CustomWidget.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mobileoid2.celltone.R;

import butterknife.BindView;

public class CustomDialogUploadOwn extends Dialog
{



    public CustomDialogUploadOwn(@NonNull Context context) {
        super(context);
// this.context = context;

        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dailog_chooser);
//        ButterKnife.bind(this);
//        sendbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CustomDialogOTPFragment.this.dismiss();
//            }
//        });


}

}
