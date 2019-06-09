package com.kismet.petsapp.Model;

import com.kismet.petsapp.Util.UtilMethods;

public class Pet {

    private UtilMethods util;

    private String name;
    private String birthdayString;
    private long birthdayLong;
    private int id;
    private String imageURI;
    private byte[] imageBYTE;



    public Pet() {
        this.imageURI = null;
        this.imageBYTE = null;

    }

    public Pet(String name, String birthdayString, int id) {
        this.name = name;
        this.birthdayString = birthdayString;
        this.id = id;


        long newlong = UtilMethods.stringToDate(this.getBirthdayString()).getTime();
        this.birthdayLong = newlong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdayString() {
        return birthdayString;
    }

    public void setBirthdayString(String birthdayString) {
        this.birthdayString = birthdayString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBirthdayLong() {
        this.birthdayLong = UtilMethods.stringToDate(this.getBirthdayString()).getTime();
        return birthdayLong;
    }

    public void setBirthdayLong(long birthdayLong) {
        this.birthdayLong = birthdayLong;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public byte[] getImageBYTE() {
        return imageBYTE;
    }

    public void setImageBYTE(byte[] imageBYTE) {
        this.imageBYTE = imageBYTE;
    }




}
