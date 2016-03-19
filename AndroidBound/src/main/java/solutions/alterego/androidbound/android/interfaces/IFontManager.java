package solutions.alterego.androidbound.android.interfaces;

import android.graphics.Typeface;

public interface IFontManager {

    Typeface getDefaultFont();

    void setDefaultFont(Typeface font);

    void registerFont(String name, Typeface font);

    Typeface getFont(String name);

}
