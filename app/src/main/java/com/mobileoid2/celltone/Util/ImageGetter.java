package com.mobileoid2.celltone.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.mobileoid2.celltone.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageGetter extends AsyncTask<Void, Void, Bitmap> {
    private String path;
    private ImageView imageView;
    private boolean isFilePresent;
    private interfaceOnImageSet onImageSet;

    public interface interfaceOnImageSet {
        void onImageSettingsFinished(ImageGetter thumbnailGetterInstance);
    }


    public ImageGetter(String path, ImageView imageView, interfaceOnImageSet onImageSet) {
        this.path = path;
        this.onImageSet = onImageSet;
        this.imageView = imageView;
        isFilePresent = loadImageFromStorage();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bMap = null;
        if (!isCancelled())
            if (!isFilePresent)
                bMap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        return bMap;
    }

    @Override
    protected void onPostExecute(Bitmap aVoid) {
        super.onPostExecute(aVoid);
        try {
            if (!isCancelled() && imageView != null && CelltoneApplication.getAppContext() != null)
                if (aVoid != null) {
                    Drawable d = new BitmapDrawable(CelltoneApplication.getAppContext().getResources(), aVoid);
                    imageView.setImageDrawable(d);
                    saveToInternalStorage(aVoid);
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (onImageSet != null)
            onImageSet.onImageSettingsFinished(this);

    }

    private boolean loadImageFromStorage() {
        boolean isFilePresent = true;

        try {
            String[] subparts;

            subparts = path.split("/");
            String fileName = ((subparts[subparts.length - 1]).split("\\."))[0];
            fileName = fileName.trim();
            fileName = fileName + ".jpg";
            fileName = fileName.replaceAll("\\s+", "_");
            File f = new File(Constant.RECORDING_BITMAP_PATH, fileName);

            if (f.exists()) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                if (b != null)
                    imageView.setImageBitmap(b);
                else {
                    isFilePresent = false;
                    imageView.setImageDrawable(CelltoneApplication.getAppContext().getResources().getDrawable(R.drawable.thumb_image));
                }
            } else {
                isFilePresent = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isFilePresent = false;
        }
        return isFilePresent;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {


        boolean isFolderPresent = true;
        if (!new File(Constant.RECORDING_BITMAP_PATH).exists())
            isFolderPresent = new File(Constant.RECORDING_BITMAP_PATH).mkdirs();

        if (!isFolderPresent) return "";

        String[] subparts = path.split("/");

        String fileName = ((subparts[subparts.length - 1]).split("\\."))[0];
        fileName = fileName.trim();
        fileName = fileName + ".jpg";
        fileName = fileName.replaceAll("\\s+", "_");

        File outputFile = new File(Constant.RECORDING_BITMAP_PATH, fileName);
        try {
            if (!outputFile.exists())
                outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputFile.getAbsolutePath();
    }
}