package com.yomiolatunji.andela.lagosjavadev.data;

import android.net.Uri;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.yomiolatunji.andela.lagosjavadev.LagosJavaDevApplication;

public class PicassoSingleton {
    private static PicassoSingleton mInstance = null;
    protected Picasso mPicasso = null;

    public PicassoSingleton() {
        initPicasso();
    }

    public static PicassoSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new PicassoSingleton();
        }
        return mInstance;
    }

    public static Picasso getPicasso() {
        return getInstance().mPicasso;
    }

    protected void initPicasso() {
        if (mPicasso != null) {
            return;
        }
        mPicasso = new Picasso.Builder(LagosJavaDevApplication.getAppContext())
                .downloader(new OkHttp3Downloader(OkHttpSingleton.getOkHttpClient()))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        Log.e("Picasso", "Failed to load image: " + uri + "\n"
                                + Log.getStackTraceString(exception));
                    }
                })
                .build();
    }


}
