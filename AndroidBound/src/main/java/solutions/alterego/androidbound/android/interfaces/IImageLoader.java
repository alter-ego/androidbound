package solutions.alterego.androidbound.android.interfaces;

import android.widget.ImageView;

public interface IImageLoader {

    NullImageLoader nullImageLoader = new NullImageLoader();

    void loadImageFromUri(String uri, ImageView image);

    class NullImageLoader implements IImageLoader {

        @Override
        public void loadImageFromUri(String uri, ImageView image) {
            //do nothing
        }
    }

}
