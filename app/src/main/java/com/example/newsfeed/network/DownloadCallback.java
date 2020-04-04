package com.example.newsfeed.network;

import android.net.NetworkInfo;

public interface DownloadCallback<T> {
    interface Progress {
        int ERROR = -1;
        int SUCCESS = 1;
        int NO_NETWORK = 2;
    }

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateFromDownload(T result);

    /**
     * Get the device's active network status in the form of a NetworkInfo object.
     */
    NetworkInfo getActiveNetworkInfo();

    /**
     * Indicate to callback handler for progress .
     *
     * @param progressCode must be one of the constants defined in DownloadCallback.Progress.
     */
    void onProgressUpdate(int progressCode);

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishDownloading();
}
