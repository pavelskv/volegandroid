package com.greenzeal.voleg.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

    public static Bitmap decodeImageFromFiles(String path) {
        Bitmap bmp = BitmapFactory.decodeFile(path);

        float aspectRatio = bmp.getWidth() /
                (float) bmp.getHeight();

        int width = Math.min(bmp.getWidth(), 1242);
        int height = bmp.getWidth() < 1242 ? bmp.getHeight() : Math.round(width / aspectRatio);

        bmp = Bitmap.createScaledBitmap(
                bmp, width, height, false);

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int rotate = 0;

        if(ei != null){
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);

        return Bitmap.createBitmap(bmp, 0, 0,
                width, height, matrix, true);
    }

    public static Bitmap decodeImageFromFiles(InputStream path) {
        Bitmap bmp = BitmapFactory.decodeStream(path);

        float aspectRatio = bmp.getWidth() /
                (float) bmp.getHeight();
        int width = Math.min(bmp.getWidth(), 1242);
        int height = bmp.getWidth() < 1242 ? bmp.getHeight() : Math.round(width / aspectRatio);

        bmp = Bitmap.createScaledBitmap(
                bmp, width, height, false);

        return bmp;
    }

}
