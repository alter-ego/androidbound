package solutions.alterego.androidbound.android.interfaces;

public interface ICache {

    public abstract Object retrieve(String url);

    public abstract void invalidate();

    public abstract void store(String url, Object dm);

}
