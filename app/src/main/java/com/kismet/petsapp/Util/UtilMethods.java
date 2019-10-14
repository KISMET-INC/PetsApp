package com.kismet.petsapp.Util;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UtilMethods {
    //  public static Date today;


    public static int ageInYears(long ageInMilliseconds) {
        today();

        Calendar bday1 = Calendar.getInstance();
        bday1.setTimeInMillis(ageInMilliseconds);

        Calendar today1 = Calendar.getInstance();

        int toYear = today1.get(Calendar.YEAR);
        int toDayOfYear = today1.get(Calendar.DAY_OF_YEAR);


        int bYear = bday1.get(Calendar.YEAR);
        int bDayOfYear = bday1.get(Calendar.DAY_OF_YEAR);

        if (isLeapYear(toYear)) {
            toDayOfYear--;
        }
        if (isLeapYear(bYear)) {
            toDayOfYear++;
        }

        int years = toYear - bYear;

        // boolean leap = isLeapYear(bYear);

        if (toDayOfYear < bDayOfYear) {
            years--;


        }


        return years;

    }

    public static byte[] byteImageFromPath(String imagePath) {

        byte[] createdByteImage = null;

        try {

            //get address of createdByteImage chosen from gallery
            FileInputStream fis = new FileInputStream(imagePath);

            createdByteImage = new byte[fis.available()];
            fis.read(createdByteImage);

            ContentValues values = new ContentValues();
            values.put(Constants.KEY_IMAGE_BYTE, createdByteImage);

            fis.close();


        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return createdByteImage;
    }



    public static int ageInDays(long ageInMilliseconds) {
        today();

        Calendar bday1 = Calendar.getInstance();
        bday1.setTimeInMillis(ageInMilliseconds);

        Calendar today1 = Calendar.getInstance();


        int toDay = today1.get(Calendar.DATE);

        int bDay = bday1.get(Calendar.DATE);


        int days = toDay - bDay;

        if (toDay < bDay) {
            days = 31 - Math.abs(toDay - bDay);
        }
        return days;

    }

    public static String prettyDate(String stringDate) {
        Date date1 = stringToDate(stringDate);
        String prettyDate = dateToString(date1);

        return prettyDate;
    }

    public static int ageInMonths(long ageInMilliseconds) {
        today();

        Calendar bday1 = Calendar.getInstance();
        bday1.setTimeInMillis(ageInMilliseconds);

        Calendar today1 = Calendar.getInstance();

        int toDayOfYear = today1.get(Calendar.DAY_OF_YEAR);
        int toMonth = today1.get(Calendar.MONTH);
        int toDay = today1.get(Calendar.DATE);

        int bDayOfYear = bday1.get(Calendar.DAY_OF_YEAR);
        int bMonth = bday1.get(Calendar.MONTH);
        int bDay = bday1.get(Calendar.DATE);


        int months = 12 - Math.abs((toMonth - bMonth));

        if (toDayOfYear > bDayOfYear) {
            months = Math.abs(toMonth - bMonth);
        }
        if (toDay < bDay) {
            months--;
        }

        if (months == 12) {
            months = 0;
        }

        return months;

    }

    public static void today() {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
    }


    public static String dateToString(Date date) {
        long newDateString = date.getTime();
        String dateString = DateFormat.format("E MMMM dd, yyyy", new Date(newDateString)).toString();
        return dateString;
    }

    public static Date stringToDate(String dateString) {

        String date = checkBirthdayFormat(dateString);
        Log.d("stringtodate", date);

        String[] array = date.split("/");
        int month = Integer.valueOf(array[0]) - 1;
        int day = Integer.valueOf(array[1]);
        int year = Integer.valueOf(array[2]);

        Date newDate = new GregorianCalendar(year, month, day).getTime();

        return newDate;

    }

    public static int dateValidation(String dateString) {

        if (dateString.length() != 8 && (dateString.length() != 10)) {
            Log.d("UtilMethods_ValidDate", "1");
            Log.d("UtilMethods_ValidDate", dateString);
            return 0; // false wrong format

        } else if (dateString.length() == 10 && !dateString.contains("/")) {
            Log.d("UtilMethods_ValidDate", "2");
            return 0; // false wrong format
        }

        //Date date = null;
        String date = checkBirthdayFormat(dateString);
        String[] array = date.split("/");
        int month = Integer.valueOf(array[0]) - 1;
        int day = Integer.valueOf(array[1]);
        int year = Integer.valueOf(array[2]);


        Calendar today1 = Calendar.getInstance();
        Calendar dateInput = Calendar.getInstance();
        dateInput.setTime(stringToDate(dateString));


        int toYear = today1.get(Calendar.YEAR);
        int toDayOfYear = today1.get(Calendar.DAY_OF_YEAR);
        int toMonth = today1.get(Calendar.MONTH);
        int toDay = today1.get(Calendar.DATE);

        boolean isDateBefore = today1.after(dateInput);


        if ((!isDateBefore)) {
            return 2; //false not before

        }
        if ((month >= 0 && month <= 12) && (day >= 1 && day <= 31) && (year > 1000)) {
            return 1; // true

        } else {
            Log.d("UtilMethods_ValidDate", "3");
            return 0; //false wrong format
        }

    }

    public static boolean Validate_is_NumbersOnly(String dateString) {

        return dateString.matches("^[^a-zA-Z]*");


    }

    public static String dateSeperator(String date) {

        if (!date.contains("/")) {
            String month = date.substring(0, 2);
            String day = date.substring(2, 4);
            String year = date.substring(4, 8);
            return month + "/" + day + "/" + year;

        } else
            return date;
    }

    public static String checkBirthdayFormat(String petBirthdayInput) {
        if (petBirthdayInput.length() == 8) {
            return dateSeperator(petBirthdayInput);

        } else if (petBirthdayInput.length() == 10 && petBirthdayInput.contains("/")) ;
        return petBirthdayInput;
    }


    public static String capitalizeFirstLetter(String nameString) {
        String firstLetter = nameString.substring(0, 1).toUpperCase();
        String restOfName = nameString.substring(1);

        return firstLetter + restOfName;

    }

    public static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 4 == 0 & year % 100 != 0) {
            return true;
        } else if (year % 4 == 0 && year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 4 == 0 && year % 100 == 0 && year % 400 != 0)
            return false;

        return false;
    }

    public static String prettyAgeString(long years, long months) {

        String age = years + " years " + months + " months";
        return age;

    }

    public static Bitmap bitmapFromByte(byte[] imageByteArray) {
        Bitmap petImageBitmapFromByte = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

        return petImageBitmapFromByte;
    }

    public static String birthdayStringToPrettyAge(String birthdayStringInput) {

        String seperatedStringInput = dateSeperator(birthdayStringInput);
        Date date = stringToDate(seperatedStringInput);
        long milliseconds = dateToMilliseconds(date);
        long years = ageInYears(milliseconds);
        long months = ageInMonths(milliseconds);
        String prettyAgeString = prettyAgeString(years, months);
        return prettyAgeString;


    }

    private static long dateToMilliseconds(Date date) {
        return date.getTime();

    }

    public byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}



