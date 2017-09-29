package com.bernard.hollarena;

import android.util.Patterns;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Raani on 9/18/2017.
 */

public class Util {
    public boolean isEmailValid(CharSequence target){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    //Method To Covert Month From String to Integer
    public int getMonthInt(String month){
        DateTimeFormatter format = DateTimeFormat.forPattern("MMM");
        DateTime instance        = format.withLocale(Locale.ENGLISH).parseDateTime(month);
        return instance.getMonthOfYear();
    }
}
