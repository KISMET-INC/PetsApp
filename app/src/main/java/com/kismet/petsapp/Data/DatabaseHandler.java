package com.kismet.petsapp.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kismet.petsapp.Model.Pet;
import com.kismet.petsapp.Util.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.kismet.petsapp.Util.Constants.KEY_ID;
import static com.kismet.petsapp.Util.Constants.KEY_WEIGHT;
import static com.kismet.petsapp.Util.Constants.PET_RECORD_FILENAMES;
import static com.kismet.petsapp.Util.Constants.PET_RECORD_IMAGE_PATHS;
import static com.kismet.petsapp.Util.Constants.RECORD_INDEX_ID;
import static com.kismet.petsapp.Util.Constants.TABLE2_NAME;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctxt;
    private static boolean entryToDB = false;
    private String CREATE_PET_TABLE2;
    //Declare utility variables
    private int columnNumber;

    //Constructor
    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctxt = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //TABLE FOR PET CHARACTERISTICS FIELDS (No weight or notes, introduced in a later version.
        String CREATE_PET_TABLE = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE1_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_NAME + " TEXT,"
                + Constants.KEY_BDAY + " TEXT,"
                + Constants.KEY_BDAY_LONG + " INTEGER,"
                + Constants.KEY_IMAGE_URI + " TEXT,"
                + Constants.KEY_IMAGE_BYTE + " BLOB);";

        db.execSQL(CREATE_PET_TABLE);

        Cursor cursor1 = db.query(Constants.TABLE1_NAME, null, null, null, null, null, null);
        columnNumber = cursor1.getColumnCount();
        cursor1.close();

        //TABLE OF FILENAMES AND IMAGES with PET ID as joining variable
        CREATE_PET_TABLE2 = "CREATE TABLE IF NOT EXISTS " + TABLE2_NAME + "("
                + RECORD_INDEX_ID + " INTEGER PRIMARY KEY,"
                + KEY_ID + " INTEGER,"
                + PET_RECORD_IMAGE_PATHS + " TEXT,"
                + PET_RECORD_FILENAMES + " TEXT);";
        db.execSQL(CREATE_PET_TABLE2);


        onUpgrade(db, 1, 2);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //If number of columsns in the database are the old database number of columns, add new attributes
        if (columnNumber < 8) {
            db.execSQL("ALTER TABLE " + Constants.TABLE1_NAME + " ADD COLUMN " + Constants.KEY_WEIGHT + " TEXT");
            db.execSQL("ALTER TABLE " + Constants.TABLE1_NAME + " ADD COLUMN " + Constants.KEY_NOTES + " TEXT");

        }

        //To upgrade the TABLE when needed
        if (newVersion == 2) {
            CREATE_PET_TABLE2 = "CREATE TABLE IF NOT EXISTS " + TABLE2_NAME + "("
                    + RECORD_INDEX_ID + " INTEGER PRIMARY KEY,"
                    + KEY_ID + " INTEGER,"
                    + PET_RECORD_IMAGE_PATHS + " TEXT,"
                    + PET_RECORD_FILENAMES + " TEXT);";
            db.execSQL(CREATE_PET_TABLE2);
        }


        //For debug puposes check if column add was successful
        Cursor cursor2 = db.query(Constants.TABLE1_NAME, null, null, null, null, null, null);
        columnNumber = cursor2.getColumnCount();
        cursor2.close();
    } // END ON UPGRADE





    //CRUD  Create - Remove - Update - Delete


    //************************************************************************
    // addPet()                                                             //
    // add a pet entry into database                                        //
    //************************************************************************
    public void addPet(Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, pet.getName());
        values.put(Constants.KEY_BDAY, pet.getBirthdayString());
        values.put(Constants.KEY_BDAY_LONG, pet.getBirthdayLong());
        values.put(Constants.KEY_IMAGE_URI, pet.getImageURI());
        values.put(Constants.KEY_IMAGE_BYTE, pet.getImageBYTE());
        values.put(KEY_WEIGHT, pet.getWeight());

        db.insert(Constants.TABLE1_NAME, null, values);
        db.close();

    }


    //************************************************************************
    // getLastID()                                                          //
    // get the PrimaryKEY of the last element entered into the database     //
    // return an INT                                                        //
    //************************************************************************
    public int getLastId() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE1_NAME, new String[]{KEY_ID}, null, null, null, null, null);

        if (cursor != null)
            cursor.moveToLast();
        int lastID = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID)));

        cursor.close();
        db.close();

        return lastID;
    }


    //************************************************************************
    // getPet()                                                             //
    //  get all the characteristics of the pet from TABLE1 and return this  //
    //  blueprint pet object                                                //
    //************************************************************************
    public Pet getPet(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE1_NAME, new String[]{KEY_ID, Constants.KEY_NAME, Constants.KEY_BDAY, Constants.KEY_IMAGE_BYTE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        //Create the blueprint pet object

        Pet pet = new Pet();
        pet.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
        pet.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
        pet.setBirthdayString(cursor.getString(cursor.getColumnIndex(Constants.KEY_BDAY)));
        pet.setImageBYTE(cursor.getBlob(cursor.getColumnIndex(Constants.KEY_IMAGE_BYTE)));
        cursor.close();

        //for debugging
        Cursor cursor1 = db.query(Constants.TABLE1_NAME, null, null, null, null, null, null);
        columnNumber = cursor1.getColumnCount();
        cursor1.close();

        db.close();

        checkColumnNumber(pet);
        //return the blueprint pet object
        return pet;
    }

    //************************************************************************
    // getPetWeight()                                                       //
    //  return a String of the pets weight. a petID is passed to it         //
    //************************************************************************
    public String getPetWeight(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE1_NAME, new String[]{Constants.KEY_WEIGHT}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Pet pet = new Pet();
        pet.setWeight(cursor.getString(cursor.getColumnIndex(Constants.KEY_WEIGHT)));

        String weight = cursor.getString(cursor.getColumnIndex(Constants.KEY_WEIGHT));
        cursor.close();
        db.close();

        return weight;

    }

    //************************************************************************
    // getAllPets()                                                         //
    // Return a list of all the pet objects in the table 1 database         //
    //************************************************************************
    public List<Pet> getAllPets() {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db2 = this.getWritableDatabase();
        onCreate(db2);
        onUpgrade(db2, 1, 2);

        List<Pet> petList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE1_NAME, new String[]{
                KEY_ID, Constants.KEY_NAME, Constants.KEY_BDAY, Constants.KEY_BDAY_LONG, Constants.KEY_IMAGE_URI, Constants.KEY_IMAGE_BYTE}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                //create pet objects from db for use in petList array list which is sent to ListActivity
                Pet pet = new Pet();
                pet.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                pet.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
                pet.setBirthdayString(cursor.getString(cursor.getColumnIndex(Constants.KEY_BDAY)));
                pet.setBirthdayLong(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.KEY_BDAY_LONG))));
                pet.setImageBYTE(cursor.getBlob(cursor.getColumnIndex(Constants.KEY_IMAGE_BYTE)));
                pet.setImageURI(cursor.getString(cursor.getColumnIndex(Constants.KEY_IMAGE_URI)));

                //for debugging
                checkColumnNumber(pet);

                petList.add(pet);

            } while (cursor.moveToNext());

        }
        db.close();
        db2.close();
        return petList;
    }

    //************************************************************************
    // updatePet()                                                          //
    // Return a list of all the pet objects in the table 1 database         //
    //************************************************************************
    public int updatePet(Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, pet.getName());
        values.put(Constants.KEY_BDAY, pet.getBirthdayString());
        values.put(Constants.KEY_IMAGE_URI, pet.getImageURI());
        values.put(Constants.KEY_IMAGE_BYTE, pet.getImageBYTE());

        int id = db.update(Constants.TABLE1_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(pet.getId())});
        db.close();
        return id;

    }

    public int updatePetNotes(Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1, 2);

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NOTES, pet.getNotes());

        int id = db.update(Constants.TABLE1_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(pet.getId())});
        Log.d("PetNotes_updateNotes_DB", pet.getNotes());
        db.close();

        return id;


    }

    //TODO Data validation for pet weight
    public void updatePetWeight_in_Database(Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1, 2);

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_WEIGHT, pet.getWeight());
        int i = db.update(Constants.TABLE1_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(pet.getId())});
        Log.d("DB handler", String.valueOf(i));
        db.close();

    }

    //Delete Pet
    public void deletePet(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE1_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();

        Log.d("ID ", String.valueOf(id));

    }

    public void deletePetEverythig(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE1_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.delete(TABLE2_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();

        Log.d("ID ", String.valueOf(id));

    }

    public void deletPetRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE2_NAME, RECORD_INDEX_ID + " = ?",
                new String[]{String.valueOf(id)});
        Log.d("deletePetRecord", String.valueOf(id));
        db.close();
    }

    public int getCount() {

        String countQuery = "SELECT * FROM " + Constants.TABLE1_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        int getCount = cursor.getCount();

        cursor.close();
        db.close();


        return getCount;

    }

    public String getNotesFromDatabase(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE1_NAME, new String[]{KEY_ID, Constants.KEY_NAME, Constants.KEY_NOTES}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String petNotes = (cursor.getString(cursor.getColumnIndex(Constants.KEY_NOTES)));

        cursor.close();
        db.close();

        return petNotes;
    }


    //If notes were previously set for pet, the database will have an extra column.
    // This function checks for this notes column the sets the notes for the pet object passed in.
    public void checkColumnNumber(Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (columnNumber == 8) {
            Cursor cursor = db.query(Constants.TABLE1_NAME, new String[]{Constants.KEY_NOTES}, KEY_ID + " = " + pet.getId(), null, null, null, null, null);
            if (cursor.moveToFirst()) {
                pet.setNotes(cursor.getString(cursor.getColumnIndex(Constants.KEY_NOTES)));
                cursor.close();
                db.close();
            }

        }

    }


    public void addRecord_toDatabase(Pet pet, String filename, String imagePath) {
        entryToDB = true;


        //convert imagePath to a byte array for entrance into database
        //byte [] imageByteArray =  byteImageFromPath(imagePath);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PET_RECORD_FILENAMES, filename);
        values.put(PET_RECORD_IMAGE_PATHS, imagePath);
        values.put(KEY_ID, pet.getId());

        db.insert(TABLE2_NAME, null, values);

        Log.d("PetaddRecord", filename);
        db.close();

    }

    public void updateRecord_toDatabase(int recordID, String filename, String imagePath) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PET_RECORD_FILENAMES, filename);
        values.put(PET_RECORD_IMAGE_PATHS, imagePath);

        db.update(TABLE2_NAME, values, RECORD_INDEX_ID + "=?", new String[]{String.valueOf(recordID + 1)});
        db.close();

    }

    public ArrayList<String> getALL_Filenames_FROMDatabase(Pet pet) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> getALL_Filenames_ArrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE2_NAME, new String[]{RECORD_INDEX_ID, KEY_ID, PET_RECORD_FILENAMES}, "id = ?", new String[]{String.valueOf(pet.getId())}, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                getALL_Filenames_ArrayList.add(cursor.getString(cursor.getColumnIndex(PET_RECORD_FILENAMES)));

            } while (cursor.moveToNext());

            cursor.close();
            db.close();

        }
        return getALL_Filenames_ArrayList;
    }

    public ArrayList<String> getALL_ImagePaths_FROMDatabase(int petID) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> getALL_ImageByteArrays_ArrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE2_NAME, new String[]{RECORD_INDEX_ID, KEY_ID, PET_RECORD_IMAGE_PATHS}, "id = ?", new String[]{String.valueOf(petID)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                getALL_ImageByteArrays_ArrayList.add(cursor.getString(cursor.getColumnIndex(PET_RECORD_IMAGE_PATHS)));

            } while (cursor.moveToNext());

            cursor.close();
            db.close();

        }
        return getALL_ImageByteArrays_ArrayList;
    }

    public boolean entryAddedtoDB() {

        Log.d("entry", String.valueOf(entryToDB));
        if (entryToDB) {
            entryToDB = false;
            return true;
        } else
            return false;
    }

    public String getRecord_FROMDatabase(int recordID) {
        SQLiteDatabase db = getReadableDatabase();
        Log.d("petRecords", String.valueOf(recordID));

        Cursor cursor = db.query(TABLE2_NAME, new String[]{RECORD_INDEX_ID, KEY_ID, PET_RECORD_FILENAMES}, RECORD_INDEX_ID + "=?",
                new String[]{String.valueOf(recordID + 1)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        String filename = cursor.getString(cursor.getColumnIndex(PET_RECORD_FILENAMES));

        cursor.close();
        db.close();
        return filename;

    }
} //END DATABASE HANDLER
