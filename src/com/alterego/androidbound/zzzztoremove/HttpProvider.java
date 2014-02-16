
package com.alterego.androidbound.zzzztoremove;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;


public abstract class HttpProvider<T> implements IContentProvider<T> {

    protected String mAccept = "application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    protected String mUserAgent = "Mozilla/5.0 (Linux; U; en-US) AppleWebKit/528.5+ (KHTML, like Gecko, Safari/528.5+) Version/4.0 Kindle/3.0";
    protected boolean mUseCaches = false;
    protected int mConnectionTimeout = -1;

    private IAndroidLogger mLogger = NullAndroidLogger.instance;
    private long mServerTime;
    private HttpURLConnection mConnection;
    private String mCookie;

    public HttpProvider() {
    }

    public void setUserAgent(String agent) {
        this.mUserAgent = agent;
    }

    public String getUserAgent() {
        return this.mUserAgent;
    }

    public void setUseCache(boolean value) {
        this.mUseCaches = value;
    }

    public boolean getUseCache() {
        return this.mUseCaches;
    }

    public void setAccept(String accept) {
        this.mAccept = accept;
    }

    public String getAccept() {
        return this.mAccept;
    }

    public Exceptional<T> getContent(String location) {
        try {
            mLogger.debug("Requested content for " + location);
            mLogger.warning("HttpProvider getContent process id = " + ((int) Thread.currentThread().getId()));
            mConnection = (HttpURLConnection) new URL(location).openConnection();
            mConnection.setUseCaches(this.mUseCaches);
            mConnection.setRequestProperty("Cookie", mCookie);
            if (this.mAccept != null) {
                mConnection.setRequestProperty("Accept", this.mAccept);
            }
            if (this.mUserAgent != null) {
                mConnection.setRequestProperty("User-Agent", this.mUserAgent);
            }
            if (mConnectionTimeout != -1) {
                mConnection.setConnectTimeout(mConnectionTimeout);
                mConnection.setReadTimeout(mConnectionTimeout);
            }
            T content = getContentFromStream(mConnection.getInputStream());

            this.mServerTime = mConnection.getDate();

            //mLogger.debug("Request is valid");
            return Exceptional.<T> right(content);

        } catch (Exception e) {
            mLogger.error("Request failed with error: " + e);
            return Exceptional.<T> wrong(e);
        }
    }

    protected abstract T getContentFromStream(InputStream stream) throws Exception;

    public void setLogger(IAndroidLogger logger) {
        this.mLogger = logger.getLogger(this);
    }

    public long getUnixServerTime() {
        return this.mServerTime;
    }

    public int getConnectionTimeout() {
        return mConnectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        mConnectionTimeout = connectionTimeout;
    }

    public void setCookie(String cookie) {
        mCookie = cookie;
    }
}
