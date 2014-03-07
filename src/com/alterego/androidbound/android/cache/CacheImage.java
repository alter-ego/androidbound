
package com.alterego.androidbound.android.cache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;

import lombok.Setter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.LruCache;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

public class CacheImage implements ICache {

    @Setter private IAndroidLogger logger = NullAndroidLogger.instance;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int MAX_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private LruCache<String, Bitmap> mMemoryCache;
    private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.PNG;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;
    private static final int DISK_CACHE_INDEX = 0;
    private HashSet<SoftReference<Bitmap>> mReusableBitmaps;

    public CacheImage(Context context, IAndroidLogger log) {
    	logger = log;
        File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        if (cacheDir != null)
            new InitDiskCacheTask().execute(cacheDir);
        else
            return;
    }

    public DiskLruCache getDiskLruCache() {
        return mDiskLruCache;
    }

    private int getMaxDiskCacheSize(File path) {
        try {
            StatFs stat = new StatFs(path.getPath());
            int availBlocks = stat.getAvailableBlocks();
            int blockSize = stat.getBlockSize();
            long free_memory = (long)availBlocks * (long)blockSize;
            logger.info("CacheImage getMaxDiskCacheSize MAX_DISK_CACHE_SIZE = " + MAX_DISK_CACHE_SIZE + ", free_memory = " + free_memory);
            if (free_memory > MAX_DISK_CACHE_SIZE)
                return MAX_DISK_CACHE_SIZE;
            else
                return (int)(free_memory / 2);
        } catch (Exception e) {
            logger.info("CacheImage getMaxDiskCacheSize Exception e = " + e.getMessage());
            return MAX_DISK_CACHE_SIZE;
        }
    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    //mDiskLruCache = DiskLruCache.open(cacheDir, MAX_DISK_CACHE_SIZE, 1, Integer.MAX_VALUE);
                    mDiskLruCache = DiskLruCache.open(cacheDir, getMaxDiskCacheSize(cacheDir), 1, Integer.MAX_VALUE);
                } catch (IOException e) {
                    logger.error("CacheImage ERROR InitDiskCacheTask exception = " + e.getMessage());
                    //e.printStackTrace();
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

        @Override
    public Object retrieve(String url) {
        String hashUrl = Util.hashKeyForDisk(url);
        Bitmap bitmap = null;

        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread			
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    logger.error("INTERRUPTED");
                }
            }
            if (mDiskLruCache != null) {
                InputStream inputStream = null;
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hashUrl);
                    if (snapshot != null) {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                        if (inputStream != null) {
                            FileDescriptor fd = ((FileInputStream)inputStream).getFD();

                            bitmap = decodeSampledBitmapFromDescriptor(fd, Integer.MAX_VALUE, Integer.MAX_VALUE, this);

                        }
                    }

                } catch (final IOException e) {
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
            return bitmap;
        }
    }

    @Override
    public void invalidate() {
        //        try {
        //            mDiskLruCache.delete();
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
    }

    /**
     * Get from memory cache.
     * 
     * @param data Unique identifier for which item to get
     * @return The bitmap drawable if found in cache, null otherwise
     */
    public Bitmap getBitmapFromMemCache(String data) {
        Bitmap memValue = null;

        if (mMemoryCache != null) {
            memValue = mMemoryCache.get(data);
        }
        return memValue;
    }

    @SuppressWarnings("null")
    @Override
    public void store(String url, Object dm) {
        synchronized (mDiskCacheLock) {
            String hashUrl = Util.hashKeyForDisk(url);
            OutputStream out = null;
            try {
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hashUrl);
                if (snapshot == null) {
                    final DiskLruCache.Editor editor = mDiskLruCache.edit(hashUrl);
                    if (editor != null) {
                        out = editor.newOutputStream(DISK_CACHE_INDEX);
                        ((Bitmap)dm).compress(DEFAULT_COMPRESS_FORMAT, DEFAULT_COMPRESS_QUALITY, out);
                        editor.commit();
                        out.close();
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    }
                }
            } catch (IOException e) {
                logger.error("Error store this : " + url + "(" + hashUrl + ")");
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    // Creates a unique subdirectory of the designated app cache directory.
    // Tries to use external
    // but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        if (getExternalCacheDir(context) != null) {
            final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable() ? getExternalCacheDir(
                    context).getPath()
                    : context.getCacheDir().getPath();
            if (cachePath != null)
                return new File(cachePath + File.separator + uniqueName);
            else
                return null;
        } else {
            return null;
        }
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding bitmaps using the decode* methods from
     * {@link BitmapFactory}. This implementation calculates the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not ensure a power of 2 is returned for inSampleSize
     * which can be faster when decoding but results in a larger bitmap which isn't as useful for caching purposes.
     * 
     * @param options An options object with out* params already populated (run through a decode* method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float)height / (float)reqHeight);
            final int widthRatio = Math.round((float)width / (float)reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     * 
     * @param fileDescriptor The file descriptor to read from
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param cache The ImageCache used to find candidate bitmaps for use with inBitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or greater than the requested width
     *         and height
     */
    public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight, CacheImage cache) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addInBitmapOptions(options, cache);
        }

        // If we're running on Honeycomb or newer, try to use inBitmap
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /**
     * Check if external storage is built-in or removable.
     * 
     * @return True if external storage is removable (like an SD card), false otherwise.
     */

    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return context.getExternalCacheDir();
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    @SuppressLint("NewApi")
    private static void addInBitmapOptions(BitmapFactory.Options options, CacheImage cache) {
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try and find a bitmap to use for inBitmap
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
            Bitmap item;

            while (iterator.hasNext()) {
                item = iterator.next().get();

                if (null != item && item.isMutable()) {
                    // Check to see it the item can be used for inBitmap
                    if (canUseForInBitmap(item, options)) {
                        bitmap = item;

                        // Remove from reusable set so it can't be used again
                        iterator.remove();
                        break;
                    }
                } else {
                    // Remove from the set if the reference has been cleared.
                    iterator.remove();
                }
            }
        }

        return bitmap;
    }

    /**
     * @param candidate - Bitmap to check
     * @param targetOptions - Options that have the out* value populated
     * @return true if <code>candidate</code> can be used for inBitmap re-use with <code>targetOptions</code>
     */
    private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        int width = targetOptions.outWidth / targetOptions.inSampleSize;
        int height = targetOptions.outHeight / targetOptions.inSampleSize;

        return candidate.getWidth() == width && candidate.getHeight() == height;
    }

}
