package solutions.alterego.androidbound.example;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import solutions.alterego.androidbound.converters.interfaces.IValueConverter;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class CustomValueConverters {

    private static Context mContext;

    public CustomValueConverters(Context ctx, IViewBinder viewBinder) {
        mContext = ctx;
        viewBinder.registerConverter("PrettifyDateTime", CustomValueConverters.PrettifyDateTime);
    }

    public static IValueConverter PrettifyDateTime = new IValueConverter() {
        @Override
        public Object convert(Object value, Class<?> targetType, Object parameter, Locale locale) {

            DateTimeZone timeZone = DateTimeZone.getDefault();
            String formattingParam = (String) parameter;
            return convertDateToString(mContext, (DateTime) value, locale, timeZone, formattingParam);
        }

        @Override
        public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
            return null;
        }
    };

    public static String convertDateToString(Context context, DateTime dateTime, Locale locale, DateTimeZone timeZone, String type) {
        String result = "";
        LocalDateTime localDateTime = dateTime.withZone(timeZone).toLocalDateTime();

        if (locale == null) {
            locale = Locale.getDefault();
        }

        DateTimeFormatter dateFormat;
        if (type.equalsIgnoreCase("longdate")) { // e.g. Tuesday, 12/11/2013
            DateTime local_dt = new DateTime(localDateTime.toDate());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", locale);
            result = sdf.format(localDateTime.toDate()) + ", "
                    + DateUtils.formatDateTime(context, local_dt.getMillis(), DateUtils.FORMAT_NUMERIC_DATE);
        } else if (type.equalsIgnoreCase("time")) { // e.g. 11:57
            java.text.DateFormat df = android.text.format.DateFormat.getTimeFormat(context);
            result = df.format(localDateTime.toDate());
        } else if (type.equalsIgnoreCase("timeDate")) { // e.g. 11:57 12/11/2013
            DateTime local_dt = new DateTime(localDateTime.toDate());
            dateFormat = DateTimeFormat.shortTime().withLocale(locale);
            result = local_dt.toString(dateFormat) + " " + DateUtils.formatDateTime(context, local_dt.getMillis(), DateUtils.FORMAT_NUMERIC_DATE);
        } else if (type.equalsIgnoreCase("dayOfWeek")) { // e.g. Wednesday
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", locale);
            result = sdf.format(localDateTime.toDate());
        } else if (type.equalsIgnoreCase("dayOfMonth")) { // e.g. 12
            SimpleDateFormat sdf = new SimpleDateFormat("dd", locale);
            result = sdf.format(localDateTime.toDate());
        }

        return result;
    }

}
