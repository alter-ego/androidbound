package solutions.alterego.androidbound.example.support.viewmodels;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.widget.Toast;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.example.support.MainActivity;
import solutions.alterego.androidbound.example.support.R;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

public class MainBindingActivityViewModel extends AndroidViewModel implements INeedsLogger {

    private Spannable mMainActivityTitle;

    private String mOpenNormalActivityText;

    private boolean mTextViewVisible = false;

    private String mEditTextText = "empty edit text";

    private SpannableString mTextViewBoundToEditText = new SpannableString("empty");

    private String mBoundEditTextText = mEditTextText;

    private ILogger mLogger = NullLogger.instance;

    public MainBindingActivityViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(activity);

        setMainActivityTitle("Bindable Activity");
        setOpenNormalActivityText("Open Normal Activity");
    }

    @Override
    public void onCreate(Bundle outState) {
        //do nothing
        raisePropertyChanged("BoundEditTextText");
    }

    public void setMainActivityTitle(String title) {
        SpannableStringBuilder builder = new SpannableStringBuilder()
                .append(title);
        builder.setSpan(new UnderlineSpan(), 0, title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        mMainActivityTitle = builder;
        raisePropertyChanged("MainActivityTitle");
    }

    public int getMainActivityTitleColor() {
        return Color.rgb(0, 255, 0);
    }

    public void setOpenNormalActivityText(String title) {
        mOpenNormalActivityText = title;
        raisePropertyChanged("OpenNormalActivityText");
    }

    public boolean canOpenNormalActivity() {
        return true;
    }

    public void doOpenNormalActivity() {
        Intent activityIntent = new Intent(getParentActivity(), MainActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public boolean canRelativeLayoutClick() {
        return true;
    }

    public void doRelativeLayoutClick() {
        Toast.makeText(getParentActivity(), "clicked relative layout!", Toast.LENGTH_SHORT).show();
    }

    public boolean canToggleTextViewVisibility() {
        return true;
    }

    public void doToggleTextViewVisibility() {
        setTextViewVisible(!isTextViewVisible());
    }

    public void setTextViewVisible(boolean visible) {
        mTextViewVisible = visible;
        raisePropertyChanged("TextViewVisible");
    }

    public String getImageViewSourceUrl() {
        return "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
    }

    public int getImageViewResource() {
        return R.mipmap.ic_launcher;
    }

    public boolean canImageViewClick() {
        return true;
    }

    public void doImageViewClick() {
        Toast.makeText(getParentActivity(), "clicked ImageView!", Toast.LENGTH_SHORT).show();
    }

    public boolean canImageViewLongClick() {
        return true;
    }

    public void doImageViewLongClick() {
        Toast.makeText(getParentActivity(), "long clicked ImageView!", Toast.LENGTH_SHORT).show();
    }

    public DateTime getCurrentDate() {
        return DateTime.now();
    }

    public boolean canClearEditTextText() {
        return true;
    }

    public void doClearEditTextText() {
        setBoundEditTextText("");
        raisePropertyChanged("BoundEditTextText");
    }

    public void setEditTextText(String text) {
        mLogger.info("text = " + text);
        mTextViewBoundToEditText = new SpannableString(text);
        raisePropertyChanged("TextViewBoundToEditText");
    }

    public int getEditTextColor() {
        return Color.rgb(255, 0, 255);
    }

    public boolean canTextViewClick() {
        return true;
    }

    public void doTextViewClick() {
        Toast.makeText(getParentActivity(), "clicked text view!", Toast.LENGTH_SHORT).show();
    }

    public boolean canTextViewLongClick() {
        return true;
    }

    public void doTextViewLongClick() {
        Toast.makeText(getParentActivity(), "long clicked text view!", Toast.LENGTH_SHORT).show();
    }

    public Spannable getMainActivityTitle() {
        return this.mMainActivityTitle;
    }

    public String getOpenNormalActivityText() {
        return this.mOpenNormalActivityText;
    }

    public boolean isTextViewVisible() {
        return this.mTextViewVisible;
    }

    public String getEditTextText() {
        return this.mEditTextText;
    }

    public SpannableString getTextViewBoundToEditText() {
        return this.mTextViewBoundToEditText;
    }

    public String getBoundEditTextText() {
        return this.mBoundEditTextText;
    }

    public void setBoundEditTextText(String mBoundEditTextText) {
        this.mBoundEditTextText = mBoundEditTextText;
    }

    @Override
    public void setLogger(ILogger logger) {
        mLogger = logger;
    }
}
