package com.mercy.virtualboard.db;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.Calendar;

public class Util {

    public static String getCurrentDate(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return (month+1) +"/"+ day + "/" + year;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
