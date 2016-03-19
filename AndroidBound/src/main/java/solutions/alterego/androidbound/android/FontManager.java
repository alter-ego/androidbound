package solutions.alterego.androidbound.android;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.graphics.Typeface;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import solutions.alterego.androidbound.android.interfaces.IFontManager;


public class FontManager implements IFontManager {

    private IAndroidLogger mLogger = NullAndroidLogger.instance;

    @Getter
    @Setter
    private Typeface mDefaultFont;

    private HashMap<String, Typeface> mRegisteredFonts = new HashMap<String, Typeface>();

    public FontManager(IAndroidLogger logger) {
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
