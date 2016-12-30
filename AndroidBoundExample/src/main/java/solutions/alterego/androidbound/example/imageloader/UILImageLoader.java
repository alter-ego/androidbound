package solutions.alterego.androidbound.example.imageloader;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.widget.ImageView;

import solutions.alterego.androidbound.android.interfaces.IImageLoader;

public class UILImageLoader implements IImageLoader {

    public UILImageLoader(Context context, ImageLoaderConfiguration imageLoaderConfiguration) {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(imageLoaderConfiguration != null ? imageLoaderConfiguration : getDefaultImageLoaderConfig(context));
        }
    }

    private ImageLoaderConfiguration getDefaultImageLoaderConfig(Context ctx) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        return config;
    }

    @Override
    public void loadImageFromUri(String uri, ImageView image) {
        ImageLoader.getInstance().displayImage(uri, image);
    }
}
