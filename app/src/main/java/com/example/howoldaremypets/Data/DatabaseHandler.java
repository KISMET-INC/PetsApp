package com.example.howoldaremypets.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.SyncStateContract;
import android.util.Log;

import com.example.howoldaremypets.Model.Pet;
import com.example.howoldaremypets.Util.Constants;
import com.example.howoldaremypets.Util.UtilMethods;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctxt;
    private UtilMethods util;


    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctxt = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PET_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_NAME  + " TEXT,"
                + Constants.KEY_BDAY + " TEXT,"
                + Constants.KEY_BDAY_LONG + " INTEGER,"
                + Constants.KEY_IMAGE_URI + " TEXT,"
                + Constants.KEY_IMAGE_BYTE + " BLOB);";

        db.execSQL(CREATE_PET_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF  EXISTS " + Constants.TABLE_NAME);

        onCreate(db);
    }

    //CRUD  Create - Remove - Update - Delete


    // Add Pet Object
    public void addPet ( Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, pet.getName());
        values.put(Constants.KEY_BDAY, pet.getBirthdayString());
        values.put(Constants.KEY_BDAY_LONG, pet.getBirthdayLong());
        values.put(Constants.KEY_IMAGE_URI, pet.getImageURI());
        values.put(Constants.KEY_IMAGE_BYTE, pet.getImageBYTE());

        db.insert(Constants.TABLE_NAME, null, values);
        db.close();


    }


    //Get Last Pet ID
     public int getLastId () {
         SQLiteDatabase db = this.getReadableDatabase();

         Cursor cursor =  db.query(Constants.TABLE_NAME, new String[]{Constants.KEY_ID},null,null,null,null,null);

         if (cursor != null)
             cursor.moveToLast();

         return Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID)));

     }

    //Get Pet Object
    public Pet getPet(int id) {

        //TODO add pet Image URI
        //TODO add pet Image BYTE

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{Constants.KEY_ID, Constants.KEY_NAME, Constants.KEY_BDAY}, Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


            Pet pet = new Pet();
            pet.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            pet.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
            pet.setBirthdayString(cursor.getString(cursor.getColumnIndex(Constants.KEY_BDAY)));


        return pet;
    }






    //Get All Pets

    public List<Pet> getAllPets() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Pet> petList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{
                Constants.KEY_ID, Constants.KEY_NAME, Constants.KEY_BDAY, Constants.KEY_BDAY_LONG, Constants.KEY_IMAGE_URI, Constants.KEY_IMAGE_BYTE}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                //create pet objects from db for use in petList array list which is sent to ListActivity
                Pet pet = new Pet();
                pet.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                pet.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
                pet.setBirthdayString(cursor.getString(cursor.getColumnIndex(Constants.KEY_BDAY)));
                pet.setBirthdayLong(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.KEY_BDAY_LONG))));
                pet.setImageBYTE(cursor.getBlob(cursor.getColumnIndex(Constants.KEY_IMAGE_BYTE)));
                pet.setImageURI(cursor.getString(cursor.getColumnIndex(Constants.KEY_IMAGE_URI)));

               // pet.setImageURI(cursor.getString(cursor.getColumnIndex(Constants.KEY_IMAGE_URI)));

//                Log.d("PetbytesQuery DB", pet.getImageURI().toString());

                petList.add(pet);

            } while (cursor.moveToNext());

        }
        db.close();
        return petList;
    }

    //Update Pet
    public int updatePet( Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();

        //TODO : Update Long when update pet

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, pet.getName());
        values.put(Constants.KEY_BDAY, pet.getBirthdayString());
        values.put(Constants.KEY_IMAGE_URI, pet.getImageURI());
        values.put(Constants.KEY_IMAGE_BYTE, pet.getImageBYTE());

        int id = db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + " = ?", new String[]{ String.valueOf(pet.getId())});
        db.close();
        return id;

    }

    //Delete Pet

    public void deletePet (int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE_NAME,Constants.KEY_ID + " = ?",
                new String[] {String.valueOf(id)});
        db.close();

        Log.d("ID ", String.valueOf(id) );

    }

    public int getCount () {

        String countQuery = "SELECT * FROM "  +  Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.rawQuery(countQuery,null);


        return cursor.getCount();
        
    }


}
