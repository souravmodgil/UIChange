package com.mobileoid2.celltone.CustomWidget.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppUtils;

import java.util.ArrayList;

/**
 * Created by root on 17/12/17.
 */

public enum DialogsCustom {
    instance;
    private Dialog dialog;
    private TextView textViewSubject, textViewHeader;


    public void showPermissionsDialog(Activity activity, ArrayList<BeanDialogsPermissions> permissions, View.OnClickListener actionListner) {
        try {

            if (dialog != null) {
                if (dialog.isShowing()) dialog.dismiss();
                dialog = null;
            }
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_permission_descriptions);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemeNoInternet;
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_layout_white_solid));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity != null) {
                        Toast.makeText(activity, R.string.text_permissions_are_necessary, Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                }
            });

            ((LinearLayout) dialog.findViewById(R.id.layout_modules)).removeAllViews();

            if (activity != null && permissions != null) {
                for (int position = 0; position < permissions.size(); position++) {
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View view = inflater.inflate(R.layout.layout_dialog_permission_single_module, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(5, 10, 5, 10);
                    ((LinearLayout) dialog.findViewById(R.id.layout_modules)).addView(view);
                    ((TextView) view.findViewById(R.id.text_view_title)).setText(permissions.get(position).getTitle());
                    ((TextView) view.findViewById(R.id.text_view_description)).setText(permissions.get(position).getDescription());
                }
            }
            dialog.findViewById(R.id.button_okay).setOnClickListener(actionListner);

            dialog.show();

        } catch (Exception e) {
            Log.e("Permissions", AppUtils.instance.getExceptionString(e));
        }
    }

    public void cancelDialog() {
        if (dialog != null) dialog.cancel();
    }

    public void showOptionsDialog(Activity activity, ArrayList<BeanDialogsOption> permissions, String title) {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) dialog.dismiss();
                dialog = null;
            }
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemeNoInternet;
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_layout_white_solid));
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);


            ((TextView) dialog.findViewById(R.id.textview_header_message)).setText(title);

            ((LinearLayout) dialog.findViewById(R.id.layout_modules)).removeAllViews();

            if (activity != null && permissions != null) {
                for (int position = 0; position < permissions.size(); position++) {
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View view = inflater.inflate(R.layout.layout_dialog_options_single_module, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(5, 10, 5, 10);
                    ((LinearLayout) dialog.findViewById(R.id.layout_modules)).addView(view);

                    ((TextView) view.findViewById(R.id.text_view_option_text)).setText(permissions.get(position).getTitle());
                    ((TextView) view.findViewById(R.id.text_view_option_text)).setSelected(true);
                    ((TextView) view.findViewById(R.id.text_view_option_image)).setCompoundDrawablesWithIntrinsicBounds(permissions.get(position).getDrawableResource(), null, null, null);
                    ((CardView) view.findViewById(R.id.card_view_option)).setOnClickListener(permissions.get(position).getActionListner());
                }
            }

            dialog.show();

        } catch (Exception e) {
            Log.e("Permissions", AppUtils.instance.getExceptionString(e));
        }
    }

    public void showMessageDialog(Activity activity, String title, String message) {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) dialog.dismiss();
                dialog = null;
            }
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_message);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemeNoInternet;
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            dialog.getWindow().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_layout_white_solid));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            textViewHeader = ((TextView) dialog.findViewById(R.id.textview_header_message));
            textViewHeader.setText(title);
            textViewSubject = ((TextView) dialog.findViewById(R.id.textview_subject_message));
            updateSubjectMessage(message);
            dialog.show();

        } catch (Exception e) {
            Log.e("Permissions", AppUtils.instance.getExceptionString(e));
        }
    }

    public void updateSubjectMessage(String message) {
        if (dialog != null)
            if (textViewSubject != null) textViewSubject.setText(message);
    }

    public void updateHeaderMessage(String message) {
        if (dialog != null)
            if (textViewHeader != null) textViewHeader.setText(message);
    }

}
