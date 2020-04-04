package com.example.newsfeed.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class AsyncTaskClass extends AsyncTask<String, Integer, AsyncTaskClass.Result> {

    private DownloadCallback<String> callback;

    public AsyncTaskClass(DownloadCallback<String> callback) {
        setCallback(callback);
    }

    private void setCallback(DownloadCallback<String> callback) {
        this.callback = callback;
    }


    static class Result {
        String resultValue;
        Exception exception;

        Result(String resultValue) {
            this.resultValue = resultValue;
        }

        Result(Exception exception) {
            this.exception = exception;
        }
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        if (callback != null) {
            NetworkInfo networkInfo = callback.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                callback.onProgressUpdate(DownloadCallback.Progress.NO_NETWORK);
                cancel(true);
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected AsyncTaskClass.Result doInBackground(String... urls) {
        Result result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = downloadUrl(url);
                if (resultString != null) {

                    result = new Result(resultString);

                } else {
                    throw new IOException("No response received.");
                }
            } catch (Exception e) {
                result = new Result(e);
            }
        }
        return result;
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    @Override
    protected void onPostExecute(Result result) {
        if (result != null && callback != null) {
            if (result.exception != null) {
                callback.updateFromDownload(result.exception.getMessage());
                callback.onProgressUpdate(DownloadCallback.Progress.ERROR);

            } else if (result.resultValue != null) {
                callback.onProgressUpdate(DownloadCallback.Progress.SUCCESS);

                callback.updateFromDownload(result.resultValue);
            }
            callback.finishDownloading();
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled(Result result) {
//            callback.updateFromDownload(result.resultValue);
    }


    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        final String REQUEST_METHOD = "GET";
        final int READ_TIMEOUT = 15000;
        final int CONNECTION_TIMEOUT = 15000;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                publishProgress(DownloadCallback.Progress.ERROR);
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
                publishProgress(DownloadCallback.Progress.SUCCESS);

            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }


    private String readStream(InputStream stream)
            throws IOException {
        BufferedReader reader;
        StringBuilder stringBuilder = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }
}

