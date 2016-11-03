package com.example.webprog26.threephotosuploading;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_TAG";

    private PhotosDownloadThread mPhotosDownloadThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] photosUrls = new String[]{
                getResources().getString(R.string.first_photo_url),
                getResources().getString(R.string.second_photo_url),
                getResources().getString(R.string.third_photo_url),
        };

        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button mBtnStartDownload = (Button) findViewById(R.id.btnStartDownload);
        ImageView mImageView = (ImageView) findViewById(R.id.imageView);

        PhotosUiHandler uiHandler = new PhotosUiHandler(mProgressBar, mBtnStartDownload, this, mImageView);

        mPhotosDownloadThread = new PhotosDownloadThread(uiHandler, mImageView);
        mPhotosDownloadThread.setListener(new PhotosDownloadThread.OnHighestResolutionBitmapFoundListener() {
            @Override
            public void setBitmap(ImageView imageView, Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
        mPhotosDownloadThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.i(TAG, throwable.toString());
            }
        });
        mPhotosDownloadThread.start();
        mPhotosDownloadThread.getLooper();


        mBtnStartDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotosDownloadThread.setPhotosDownloadStarted(photosUrls);
            }
        });
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if(mPhotosDownloadThread != null && mPhotosDownloadThread.isAlive()){
            return mPhotosDownloadThread;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotosDownloadThread.quit();
    }
}
