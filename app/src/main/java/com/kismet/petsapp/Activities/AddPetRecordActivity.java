package com.kismet.petsapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kismet.petsapp.Data.DatabaseHandler;
import com.kismet.petsapp.Model.Pet;
import com.kismet.petsapp.R;
import com.kismet.petsapp.UI.MY_RecycleViewAdapter2;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

public class AddPetRecordActivity extends AppCompatActivity implements com.kismet.petsapp.UI.MY_RecycleViewAdapter2.MY_ItemClickListener {

    //Declare elements for recycle view adapter
    private MY_RecycleViewAdapter2 MY_RecycleViewAdapter2;
    private ArrayList<String> arrayListForFilenames_fromDB;
    private ArrayList<String> arrayListForFilenames_forRecycleViewr;

    //Declare elements pertaining to pet fields
    private Pet pet;
    private int petID;
    private int recordID;
    private String filename;
    //bundle from other activities containing elements of pet
    private Bundle bundle;

    //Declare interative elements in RecordActivity
    private TextView petName_TextView;

    //Gain access to utility classes
    private RecyclerView addRecord_RecyclerView;
    private DatabaseHandler databaseHandler1 = new DatabaseHandler(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_record_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Go Back and Add Record Menu items
        switch (item.getItemId()) {
            case R.id.go_Back_menuItem:
                this.finish();
                break;
            case R.id.add_record_menuItem:
                openGallery(this);
                break;
        }
        return true;

    }

    @Override
    protected void onResume() {
        //forse recreate of activity and recycle view if a new entry is added to DB
        super.onResume();
        if (databaseHandler1.entryAddedtoDB())
            recreate();
    }

    @Override
    public void onItemClick(View view, int position) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet_record2);

        //Get the bundle fields from the intent
        bundle = getIntent().getExtras();

        //get the petID from the bundle
        petID = bundle.getInt("petID");

        //Create a new pet, with grabbed characterisitics from database using the petID from
        //the bundle
        pet = databaseHandler1.getPet(petID);


        //Set the toolbar to custom color toolbar1
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);


        //initialize arraylists for recycle viewer and from database
        arrayListForFilenames_forRecycleViewr = new ArrayList<>();
        arrayListForFilenames_fromDB = new ArrayList<>();

        //link interactive Textview  (title) element from add record activity for petname
        //link to the interactive recycle viewer  on activity_add_pet_record.
        petName_TextView = findViewById(R.id.pet_name_in_records_TextView);
        addRecord_RecyclerView = findViewById(R.id.pet_records_recycle_viewer);


        Log.d("AddPet", String.valueOf(petID));

        //Set the title Textiew to the pet name from the pet object created from the database
        //  petName_TextView.setText(pet.getName());
        String titleString = pet.getName() + "'s Records";
        petName_TextView.setText(titleString);

        //get all the filenames associated with the pet created above, uses the pet.getID
        //add them to a locally created arrayslist from database.
        arrayListForFilenames_fromDB = databaseHandler1.getALL_Filenames_FROMDatabase(pet);


        //Scan the easily accessible locally created arraylist and add each element to an arraylist
        //that the recycleviewer will use.

        for (String c : arrayListForFilenames_fromDB) {

            arrayListForFilenames_forRecycleViewr.add(c);
            Log.d("AddPet", c);

        }


        //IF RECORD ID IS FOUND IN BUNDLE WE ARE IN EDITING RECORD MODE:
        if (bundle.containsKey("recordID")) {

            //get the record ID from the bundle
            recordID = bundle.getInt("recordID");
            //get the filename of this record from teh database and hold in filename variable
            filename = databaseHandler1.getRecord_FROMDatabase(recordID);

            openGallery(this);
        } // END FUNCTION TO RUN FOR EDITING ONLY


        //set the recycleViewer
        addRecord_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MY_RecycleViewAdapter2 = new MY_RecycleViewAdapter2(this, arrayListForFilenames_forRecycleViewr, pet);
        addRecord_RecyclerView.setAdapter(MY_RecycleViewAdapter2);

    } //END ON CREATE

    //****************************************************************************
    // openGallery Function Description                                         //
    // OpenGallery, pick image, crop image, and set to imagebutton              //
    //****************************************************************************
    public void openGallery(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                File myFilePath = new File(resultUri.getPath());
                myFilePath.getAbsolutePath();
                String imagePathFromCropResult = myFilePath.getAbsolutePath();
                //  byte [] byteArray = util.byteImageFromPath(imagePathFromCropResult);


                Intent intent = new Intent(this, PhotoView.class);
                bundle.putString("ImagePath", imagePathFromCropResult);
                bundle.putInt("petID", pet.getId());


                Log.d("AddPetX", imagePathFromCropResult);

                if (filename != null) {
                    bundle.putString("filename", filename);
                }


                intent.putExtras(bundle);
                startActivity(intent);

                //if (!databaseHandler1.entryAddedtoDB())
                //this.finish();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


} // END ADD PET RECORD ACTIVITY


