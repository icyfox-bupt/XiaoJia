package com.hmammon.familyphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.hmammon.familyphoto.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by icyfox on 2014/12/3.
 */
public class ImageHelper {

    static Context context = BaseApp.getInstance();
    static File cacheDir = StorageUtils.getCacheDirectory(context);

    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_loading) // resource or drawable
            .showImageForEmptyUri(R.drawable.ic_loading) // resource or drawable
            .showImageOnFail(R.drawable.ic_loading) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .delayBeforeLoading(0)
            .cacheInMemory(true) // default
            .cacheOnDisk(false) // default
            .considerExifParams(false) // default
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            .displayer(new SimpleBitmapDisplayer()) // default
            .handler(new Handler()) // default
            .build();

    public static ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
            .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
            .diskCacheExtraOptions(480, 800, null)
            .threadPoolSize(3) // default
            .threadPriority(Thread.NORM_PRIORITY - 2) // default
            .tasksProcessingOrder(QueueProcessingType.FIFO) // default
            .denyCacheImageMultipleSizesInMemory()
            .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .memoryCacheSizePercentage(13) // default
            .diskCache(new UnlimitedDiscCache(cacheDir)) // default
            .diskCacheSize(50 * 1024 * 1024)
            .diskCacheFileCount(100)
            .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
            .imageDownloader(new BaseImageDownloader(context)) // default
            .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
            .writeDebugLogs()
            .build();
}
