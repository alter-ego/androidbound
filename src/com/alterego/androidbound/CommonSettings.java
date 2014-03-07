
package com.alterego.androidbound;

import com.alterego.androidbound.android.cache.ICache;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommonSettings {

    public static class CacheImage {
        public static ICache cache = null;
    }

    public static class Images {
        public static boolean isAnimated = false;
    }
    
    public static class UniversalImageLoader {
    	public static ImageLoader sImageLoader = null;
    }
}
