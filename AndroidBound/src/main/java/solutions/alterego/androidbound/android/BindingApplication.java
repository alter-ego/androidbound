package solutions.alterego.androidbound.android;

import com.alterego.advancedandroidlogger.implementations.AndroidLogger;
import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.zzzztoremove.HandlerScheduler;
import solutions.alterego.androidbound.zzzztoremove.reactive.IScheduler;

@Accessors(prefix = "m")
public class BindingApplication extends Application {

    @Getter
    @Setter
    protected IAndroidLogger mLogger = NullAndroidLogger.instance;

    @Getter
    @Setter
    protected IScheduler mHandlerScheduler;

    @Getter
    @Setter
    protected IViewBinder mViewBinder;

    @Override
    public void onCreate() {
        super.onCreate();

        setLogger(new AndroidLogger(getApplicationName()));
        setHandlerScheduler(new HandlerScheduler(new Handler()));
        setViewBinder(new ViewBinder(this, mHandlerScheduler, mLogger));
    }

    public String getApplicationName() {
        PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo a_info;
        String app_name = "UNKNOWN_APP";
        try {
            a_info = pm.getApplicationInfo(this.getPackageName(), 0);
            app_name = (String) pm.getApplicationLabel(a_info);
        } catch (final NameNotFoundException e) {
            //do nothing
        }
        return app_name;
    }

}
