package com.gmaxgps.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.gmaxgps.R;

import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;

    private Context context;
    private String url;

    public ImageDownloaderTask(ImageView imageView, String url, Context context) {
        imageViewReference = new WeakReference<ImageView>(imageView);

        this.url = url;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null)
        {
            ImageView imageView = imageViewReference.get();
            if (imageView != null)
            {
                if (bitmap != null)
                {
                             imageView.setImageBitmap(bitmap);
                } else
                    {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.defualt_image);
                    imageView.setImageDrawable(placeholder);
                }
            }

        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            Log.d("URLCONNECTIONERROR", e.toString());
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return null;
    }
}