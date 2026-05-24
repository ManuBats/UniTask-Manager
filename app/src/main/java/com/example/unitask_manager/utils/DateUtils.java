package com.example.unitask_manager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String API_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_FORMAT = "d MMMM yyyy";

    public static String toDisplay(String apiDate) {
        if (apiDate == null || apiDate.isEmpty()) return "";
        try {
            SimpleDateFormat sdfApi = new SimpleDateFormat(API_FORMAT, new Locale("es", "ES"));
            SimpleDateFormat sdfDisplay = new SimpleDateFormat(DISPLAY_FORMAT, new Locale("es", "ES"));
            Date date = sdfApi.parse(apiDate);
            return sdfDisplay.format(date);
        } catch (ParseException e) {
            return apiDate;
        }
    }

    public static String toApi(String displayDate) {
        if (displayDate == null || displayDate.isEmpty()) return "";
        try {
            SimpleDateFormat sdfDisplay = new SimpleDateFormat(DISPLAY_FORMAT, new Locale("es", "ES"));
            SimpleDateFormat sdfApi = new SimpleDateFormat(API_FORMAT, new Locale("es", "ES"));
            Date date = sdfDisplay.parse(displayDate);
            return sdfApi.format(date);
        } catch (ParseException e) {
            return displayDate;
        }
    }

    public static String format(String dateStr, String fromFormat, String toFormat) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            SimpleDateFormat sdfFrom = new SimpleDateFormat(fromFormat, Locale.getDefault());
            SimpleDateFormat sdfTo = new SimpleDateFormat(toFormat, Locale.getDefault());
            Date date = sdfFrom.parse(dateStr);
            return sdfTo.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }
}
