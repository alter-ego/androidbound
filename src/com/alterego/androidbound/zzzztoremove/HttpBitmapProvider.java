
package com.alterego.androidbound.zzzztoremove;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class HttpBitmapProvider extends HttpProvider<Bitmap> {

    @Override
    protected Bitmap getContentFromStream(InputStream stream) throws Exception {
        return BitmapFactory.decodeStream(stream);
    }
}
