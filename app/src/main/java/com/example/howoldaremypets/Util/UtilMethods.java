package com.example.howoldaremypets.Util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.howoldaremypets.Data.DatabaseHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UtilMethods {
  //  public static Date today;

  private ImageView addFirstPet;
  private DatabaseHandler db;



    public byte[] getBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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

    public byte[] getBytesURI(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        try {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return byteBuffer.toByteArray();
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

    public static long ageInYears1(Calendar bday) {
        Calendar today = Calendar.getInstance();

     /*  Calendar bday1 = Calendar.getInstance();
       bday1.setTimeInMillis(ageInMilliseconds);*/

        int toDay = bday.get(Calendar.YEAR);
        long years = toDay;


        return years;
    }

    public static int ageInYears(long ageInMilliseconds) {
        today();

        Calendar bday1 = Calendar.getInstance();
        bday1.setTimeInMillis(ageInMilliseconds);

        Calendar today1 = Calendar.getInstance();

        int toYear = today1.get(Calendar.YEAR);
        int toDayOfYear = today1.get(today1.DAY_OF_YEAR);


        int bYear = bday1.get(Calendar.YEAR);
        int bDayOfYear = bday1.get(bday1.DAY_OF_YEAR);

        if (isLeapYear(toYear)){
            toDayOfYear--;
        }
        if (isLeapYear(bYear)){
            toDayOfYear++;
        }

        int years = toYear - bYear;

       // boolean leap = isLeapYear(bYear);

        if (toDayOfYear < bDayOfYear) {
            years--;

         ///   Log.d("leap ", String.valueOf(leap)+ String.valueOf(bYear));
            //  Log.d("b of year ", String.valueOf(bDayOfYear));


        }


        return years;

    }

    public static int ageInMonths(long ageInMilliseconds) {
        today();

        Calendar bday1 = Calendar.getInstance();
        bday1.setTimeInMillis(ageInMilliseconds);

        Calendar today1 = Calendar.getInstance();

        int toDayOfYear = today1.get(today1.DAY_OF_YEAR);
        int toMonth = today1.get(Calendar.MONTH);
        int toDay = today1.get(Calendar.DATE);

        int bDayOfYear = bday1.get(bday1.DAY_OF_YEAR);
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


    public static String reformatDateString(String stringDate) {
        Date date1 = stringToDate(stringDate);
        String prettyDate = dateToString(date1);

        return prettyDate;
    }

    public static Date stringToDate(String dateString) {

        String[] array = dateString.split("/");
        int month = Integer.valueOf(array[0]) - 1;
        int day = Integer.valueOf(array[1]);
        int year = Integer.valueOf(array[2]);

        Date date = new GregorianCalendar(year, month, day).getTime();

        Log.d("stringtodate", String.valueOf(date));


        return date;

    }

    //TODO: Restrict Birthday to past

    public static boolean dateValidation(String dateString) {
        Date date = null;
        String[] array = dateString.split("/");
        int month = Integer.valueOf(array[0]) - 1;
        int day = Integer.valueOf(array[1]);
        int year = Integer.valueOf(array[2]);


        Calendar today1 = Calendar.getInstance();
        Calendar dateInput = Calendar.getInstance();
        dateInput.setTime(stringToDate(dateString));


        int toYear = today1.get(Calendar.YEAR);
        int toDayOfYear = today1.get(today1.DAY_OF_YEAR);
        int toMonth = today1.get(Calendar.MONTH);
        int toDay = today1.get(Calendar.DATE);

        boolean isDateBefore = today1.after(dateInput);

        if ((month >= 0 && month <= 12) && (day >= 1 && day <= 31) && (year > 1000 && year <=toYear ) && isDateBefore) {
            return true;

        } else {
            return false;
        }

    }

    public static boolean isValidInput(String dateString) {

        if (dateString.matches("^[^a-zA-Z]*")) {
            return true;
        } else {
            return false;
        }


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


}


