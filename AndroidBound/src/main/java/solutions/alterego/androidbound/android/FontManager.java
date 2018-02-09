package solutions.alterego.androidbound.android;

import android.graphics.Typeface;

import java.util.HashMap;

import solutions.alterego.androidbound.android.interfaces.IFontManager;


public class FontManager implements IFontManager {

    private Typeface mDefaultFont;

    private HashMap<String, Typeface> mRegisteredFonts = new HashMap<String, Typeface>();

    public FontManager() {
    }

    @Override
    public Typeface getDefaultFont() {
        return mDefaultFont;
    }

    @Override
    public void setDefaultFont(Typeface font) {
        mDefaultFont = font;
    }

    @Override
    public void registerFont(String name, Typeface font) {
        mRegisteredFonts.put(name.toLowerCase(), font);
    }

    @Override
    public Typeface getFont(String name) {
        if ((name != null) && (!name.equalsIgnoreCase("")) && (mRegisteredFonts.containsKey(name.toLowerCase()))) {
            return mRegisteredFonts.get(name.toLowerCase());
        } else {
            return mDefaultFont;
        }
    }
}
