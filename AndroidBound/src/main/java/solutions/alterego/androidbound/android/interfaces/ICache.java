package solutions.alterego.androidbound.android.interfaces;

public interface ICache {

    Object retrieve(String url);

    void invalidate();

    void store(String url, Object dm);

}
