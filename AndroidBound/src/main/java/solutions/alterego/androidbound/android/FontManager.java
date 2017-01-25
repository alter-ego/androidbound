package solutions.alterego.androidbound.android;

import android.graphics.Typeface;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.interfaces.IFontManager;
import solutions.alterego.androidbound.interfaces.ILogger;


public class FontManager implements IFontManager {

    private ILogger mLogger = NullLogger.instance;

    @Getter
    @Setter
    private Typeface mDefaultFont;

    private HashMap<String, Typeface> mRegisteredFonts = new HashMap<String, Typeface>();

    public FontManager(ILogger logger) {
        mLogger = logger;
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
