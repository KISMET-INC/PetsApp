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
    private String notes;
    private String weight;

    public Pet() {
        this.imageURI = null;
        this.imageBYTE = null;

    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
