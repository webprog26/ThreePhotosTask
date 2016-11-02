package com.example.webprog26.threephotosuploading;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_TAG";

    private ProgressBar mProgressBar;
    private Button mBtnStartDownload;
    private PhotosDownloadThread mPhotosDownloadThread;
    private ImageView mImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] photosUrls = new String[]{
                getResources().getString(R.string.first_photo_url),
                getResources().getString(R.string.second_photo_url),
                getResources().getString(R.string.third_photo_url),
        };

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mBtnStartDownload = (Button) findViewById(R.id.btnStartDownload);
        mImageView = (ImageView) findViewById(R.id.imageView);

        PhotosUiHandler uiHandler = new PhotosUiHandler(mProgressBar, mBtnStartDownload, this);

        mPhotosDownloadThread = new PhotosDownloadThread(uiHandler, mImageView);
        mPhotosDownloadThread.setListener(new PhotosDownloadThread.OnHighestResolutionBitmapFoundListener() {
            @Override
            public void setBitmap(ImageView imageView, Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
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
    protected void onDestroy() {
        super.onDestroy();
        mPhotosDownloadThread.quit();
    }
}
