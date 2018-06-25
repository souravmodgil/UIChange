package com.mobileoid2.celltone.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public enum AppUtils {
    instance;

    public String stringValidator(String value) {
        if (value == null) value = "";
        return value;
    }

    public Integer integerValidator(Integer value) {
        if (value == null) value = 0;
        return value;
    }


    public void hideKeyboard(Activity context) {
        // Check if no view has focus:
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // shows a Toast message.
    public void showToast(String message) {

        Toast.makeText(AppLevelConstraints.getAppContext(), message, Toast.LENGTH_SHORT).show();

    }


    public boolean isLandscape(Context context) {
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return false;
            case Surface.ROTATION_90:
                return true;
            case Surface.ROTATION_180:
                return false;
            default:
                return true;
        }
    }


	/*
       DATE AND TIME FORMAT FOR SimpleDateFormat()
	   "yyyy.MM.dd G 'at' HH:mm:ss z" ---- 2001.07.04 AD at 12:08:56 PDT
	   "hh 'o''clock' a, zzzz" ----------- 12 o'clock PM, Pacific Daylight Time
	   "EEE, d MMM yyyy HH:mm:ss Z" ------ Wed, 4 Jul 2001 12:08:56 -0700
	   "yyyy-MM-dd'T'HH:mm:ss.SSSZ" ------ 2001-07-04T12:08:56.235-0700
	   "yyMMddHHmmssZ" ------------------- 010704120856-0700
	   "K:mm a, z" ----------------------- 0:08 PM, PDT
	   "h:mm a" -------------------------- 12:08 PM
	   "EEE, MMM d, ''yy" ---------------- Wed, Jul 4, '01
	 */


    //2017-07-27T11:57:11.854
    public final String parseServerTime(String incoming) {
        String result = "";
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            long dd = format.parse(incoming).getTime();

            DateFormat newDateFormat = new SimpleDateFormat("d MMM yyyy,h:mm a", Locale.US);
            result = newDateFormat.format(dd);
        } catch (Exception e) {

        }
        return result;
    }

    public final String parseDate(long dd) {
        String result = "";
        try {
            DateFormat newDateFormat = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a", Locale.US);
            result = newDateFormat.format(dd);
        } catch (Exception e) {

        }
        return result;
    }

    public final String parseDateForNews(long dd) {
        String result = "";
        try {
            DateFormat newDateFormat = new SimpleDateFormat("d MMM yyyy,h:mm a", Locale.US);
            result = newDateFormat.format(dd);
        } catch (Exception e) {

        }
        return result;
    }


    // used in weather API
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mmm dd, yyyy",
                Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // TO CHECK INTERNET CONNECTION
    public boolean checkInternetConnection(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            // //Log.v("TEST", "Internet Connection Not Present");

            return false;
        }

    }


    public String getSyncDateTime(Date fetchDate) {
        try {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            //Date date = df.parse(fetchDate);
            return df.format(fetchDate);
        } catch (Exception ex) {
            return null;
        }
    }


    public String getExceptionString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public Date getGetCurrentDate() {
        return new Date();
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }


    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public void writeBufferDataToFile(Context context, String dataString) {

        try {
            File root = new File(context.getFilesDir() + "/bufferresources");
            if (!root.exists()) root.mkdir();
            File gpxfile = new File(root, "buffer.txt");
            FileWriter writer = new FileWriter(gpxfile, false);
            writer.append(dataString);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String readBufferDataFromFile(Context context) {
        File file = new File(context.getFilesDir() + "/bufferresources", "buffer.txt");


        if (!file.exists()) return "";

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);


        //  if (output.getWidth() > 500)
        //    output = Bitmap.createScaledBitmap(output, output.getWidth() / 3, output.getWidth() / 3, false);


        if (output.getWidth() > AppSharedPref.instance.getScreenWidth() * 2)
            output = Bitmap.createScaledBitmap(output, output.getWidth() / 3, output.getWidth() / 3, false);
        else
            output = Bitmap.createScaledBitmap(output, output.getWidth() / 2, output.getWidth() / 2, false);
        return output;
    }


    public Bitmap getRoundedSquareBitmap(Bitmap bitmap) {
        Bitmap output;

        output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());


        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);

        canvas.drawPath(RoundedRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), 5, 5, true), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (output.getWidth() > AppSharedPref.instance.getScreenWidth() * 2)
            output = Bitmap.createScaledBitmap(output, output.getWidth() / 3, output.getWidth() / 3, false);
        else
            output = Bitmap.createScaledBitmap(output, output.getWidth() / 2, output.getWidth() / 2, false);
        return output;
    }

    public Path RoundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        path.rLineTo(0, heightMinusCorners);

        if (conformToOriginalPost) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        } else {
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
            path.rLineTo(widthMinusCorners, 0);
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }


    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) AppLevelConstraints.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    public String fixPhoneNo(String phoneNo) {
//        phoneNo = phoneNo.replaceAll("-", "");
//        phoneNo = phoneNo.replaceAll("\\+", "");
//        phoneNo = phoneNo.replaceAll("\\s+", "");
//        phoneNo = phoneNo.trim();
        phoneNo = phoneNo.replaceAll("[^\\d]", "");

        return phoneNo;
    }
}
