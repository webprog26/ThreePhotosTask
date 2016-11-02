package com.example.webprog26.threephotosuploading;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import static com.example.webprog26.threephotosuploading.PhotosDownloadThread.PHOTOS_DOWNLOAD_FINISHED;
import static com.example.webprog26.threephotosuploading.PhotosDownloadThread.PHOTOS_DOWNLOAD_IN_PROGRESS;
import static com.example.webprog26.threephotosuploading.PhotosDownloadThread.PHOTOS_DOWNLOAD_STARTED;


/**
 * Created by webprog26 on 02.11.2016.
 */

class PhotosUiHandler extends Handler {

    private static final String TAG = "PhotosUiHandler";

    private ProgressBar mProgressBar;
    private Button mBtnStartDownload;
    private Context mContext;

    private boolean isTaskExecuting = false;

    PhotosUiHandler(ProgressBar mProgressBar, Button btnStartDownload, Context context) {
        this.mProgressBar = mProgressBar;
        this.mBtnStartDownload = btnStartDownload;
        this.mContext = context;
    }

    @Override
    public void handleMessage(final Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case PHOTOS_DOWNLOAD_STARTED:
                Log.i(TAG, "Download started");
                post(new Runnable() {
                    @Override
                    public void run() {
                        makeToast(mContext.getResources().getString(R.string.download_started));
                        changeProgressBarVisibility(mProgressBar);
                        mBtnStartDownload.setEnabled(isTaskExecuting);
                    }
                });
                break;
            case PHOTOS_DOWNLOAD_IN_PROGRESS:
                post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Download in progress");
                        makeToast(mContext.getResources().getString(R.string.download_in_progress));
                    }
                });
                break;
            case PHOTOS_DOWNLOAD_FINISHED:
                Log.i(TAG, "Download finished");
                post(new Runnable() {
                    @Override
                    public void run() {
                        makeToast(mContext.getResources().getString(R.string.download_finished));
                        changeProgressBarVisibility(mProgressBar);
                        mBtnStartDownload.setEnabled(!isTaskExecuting);
                    }
                });
                break;
        }
    }

    /**
     * Changes the visibility of ProgressBar to let user know that download process is in progress
     * @param progressBar ProgressBar
     */
    private void changeProgressBarVisibility(ProgressBar progressBar){
        if(progressBar.getVisibility() == View.GONE){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Makes toast with received message
     * @param message String
     */
    private void makeToast(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
