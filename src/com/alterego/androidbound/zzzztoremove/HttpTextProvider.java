
package com.alterego.androidbound.zzzztoremove;

import java.io.InputStream;
import java.net.HttpURLConnection;


public class HttpTextProvider extends HttpProvider<String> {

    private HttpURLConnection mConnection;

    @Override
    protected String getContentFromStream(InputStream stream) throws Exception {
        return Streams.readAllText(stream);
    }
}
