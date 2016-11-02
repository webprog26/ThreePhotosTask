package com.example.webprog26.threephotosuploading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by webprog26 on 02.11.2016.
 */

public class BitmapUtils {

    /**
     * Gets bitmap from the Internet
     * @param src
     * @return Bitmap
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the highest resolution Bitmap from ArrayList<Bitmap>
     * @param bitmaps ArrayList<Bitmap>
     * @return Bitmap
     */
    public static Bitmap getHighestResolutionBitmap(ArrayList<Bitmap> bitmaps)
    {
        Bitmap bitmap = bitmaps.get(0);

        for(int i = 0, n = bitmaps.size() - 1; i < n; i++)
        {
            if(bitmaps.get(i + 1).getWidth() > bitmap.getWidth() && (bitmaps.get(i + 1).getHeight() > bitmap.getHeight())){
                bitmap = bitmaps.get(i + 1);
            }
        }
        return bitmap;
    }
}
