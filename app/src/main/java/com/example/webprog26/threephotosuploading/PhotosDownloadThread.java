package com.example.webprog26.threephotosuploading;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * Created by webprog26 on 02.11.2016.
 */

class PhotosDownloadThread extends HandlerThread {

    private static final String TAG = "PhotosDownloadThread";

    private static final String PHOTOS_URLS = "photos_urls";

    static final int PHOTOS_DOWNLOAD_STARTED = 100;
    static final int PHOTOS_DOWNLOAD_IN_PROGRESS = 101;
    static final int PHOTOS_DOWNLOAD_FINISHED = 102;


    private Handler mWorkerHandler;
    private Handler mUiHandler;
    private ImageView mImageView;
    private ArrayList<Bitmap> mPhotos;
    private OnHighestResolutionBitmapFoundListener mListener;

    interface OnHighestResolutionBitmapFoundListener{
        /**
         * Sets {@link Bitmap} as {@link ImageView} image
         * @param imageView {@link ImageView}
         * @param bitmap {@link Bitmap}
         */
        void setBitmap(ImageView imageView, Bitmap bitmap);
    }

    void setListener(OnHighestResolutionBitmapFoundListener listener)
    {
        this.mListener = listener;
    }

    PhotosDownloadThread(Handler uiHandler, ImageView imageView){
        super(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        this.mUiHandler = uiHandler;
        this.mImageView = imageView;
        mPhotos = new ArrayList<>();
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        Log.i(TAG, Thread.currentThread().getName());
        mWorkerHandler = new Handler(this.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Bitmap bitmap;
                switch (msg.what){
                    case PHOTOS_DOWNLOAD_STARTED:

                        //Bundle to receive String[] photosUrls
                        Bundle bundle = (Bundle) msg.obj;
                        String[] photosUrls = bundle.getStringArray(PHOTOS_URLS);
                        mUiHandler.obtainMessage(PHOTOS_DOWNLOAD_STARTED).sendToTarget();//download started

                        //In this cycle images are downloading and mUiHandler receives he messages about download progress
                        assert photosUrls != null;
                        for (String photosUrl : photosUrls) {

                            if(!mUiHandler.hasMessages(PHOTOS_DOWNLOAD_FINISHED)) {
                                mUiHandler.sendMessage(mUiHandler.obtainMessage(PHOTOS_DOWNLOAD_IN_PROGRESS));
                            }

                            if ((bitmap = BitmapUtils.getBitmapFromURL(photosUrl)) != null) {
                                mPhotos.add(bitmap);
                                Log.i(TAG, bitmap.toString());
                            }
                        }
                       mUiHandler.obtainMessage(PHOTOS_DOWNLOAD_FINISHED).sendToTarget();//download successfully finished

                       final Bitmap highestResolutionBitmap = BitmapUtils.getHighestResolutionBitmap(mPhotos);//the highest resolution Bitmap found
                       mUiHandler.post(new Runnable() {
                           @Override
                           public void run() {
                               mListener.setBitmap(mImageView, highestResolutionBitmap);//set bitmap to ImageView in MainActivity
                           }
                       });
                        //Clearing MessageQueue to avoid memory leaks
                       mWorkerHandler.removeMessages(PHOTOS_DOWNLOAD_STARTED);
                       break;
                }
            }
        };
    }

    /**
     * Sends the message to mWorkerHandler, so download starts
     * @param photosUrls {@link String[])
     */
    void setPhotosDownloadStarted(String[] photosUrls){
        Bundle bundle = new Bundle();
        bundle.putStringArray(PHOTOS_URLS, photosUrls);
            mWorkerHandler.obtainMessage(PHOTOS_DOWNLOAD_STARTED, bundle).sendToTarget();

    }
}
