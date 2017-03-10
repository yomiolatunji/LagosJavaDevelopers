package com.yomiolatunji.andela.lagosjavadev.data;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import com.yomiolatunji.andela.lagosjavadev.LagosJavaDevApplication;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class OkHttpSingleton {
    private static final String IMAGE_CACHE_PATH = "images";
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024;     // in bytes
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024;    // in bytes
    private static final int CONNECTION_TIMEOUT = 10 * 1000;            // in milliseconds
    private static OkHttpSingleton mInstance = null;
    protected OkHttpClient mOkHttpClient = null;

    public OkHttpSingleton() {
        initOkHttpClient();
    }

    public static OkHttpSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpSingleton();
        }
        return mInstance;
    }

    public static OkHttpClient getOkHttpClient() {
        return getInstance().mOkHttpClient;
    }

    private static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                available = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
            } else {
                // checked at runtime
                //noinspection deprecation
                available = statFs.getBlockCount() * statFs.getBlockSize();
            }
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }
        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

    private static File createCacheDir(Context context, String path) {
        File cacheDir = context.getApplicationContext().getExternalCacheDir();
        if (cacheDir == null)
            cacheDir = context.getApplicationContext().getCacheDir();
        File cache = new File(cacheDir, path);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    protected void initOkHttpClient() {
        if (mOkHttpClient != null) {
            return;
        }
        File cacheDir = createCacheDir(LagosJavaDevApplication.getAppContext(), IMAGE_CACHE_PATH);
        long size = calculateDiskCacheSize(cacheDir);
        Cache cache = new Cache(cacheDir, size);
        mOkHttpClient = new OkHttpClient.Builder().cache(cache)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS).build();
    }

}
