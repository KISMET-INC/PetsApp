package com.kismet.petsapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kismet.petsapp.Data.DatabaseHandler;
import com.kismet.petsapp.Model.Pet;
import com.kismet.petsapp.R;
import com.kismet.petsapp.Util.UtilMethods;

import java.util.ArrayList;

//CALLED FROM ADD_PET_RECORD_ACTIVITY, OnActivityResult Function (Choosing Image from Galllery)
public class PhotoView extends AppCompatActivity {

    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;

    private EditText enterFilenameEditText;
    private Button saveFileButton;

    private ArrayList<String> arrayListOfFilenames = new ArrayList<>();
    private ArrayList<byte[]> ImageBytes_fromDatabase = new ArrayList<>();
    private Intent intent;
    private int petID;
    private Pet pet;
    private String imagePath;
    private int recordID_AdapterPosition;
    private int recordID_forSearchingDatabase;
    private String filename;
    private Bundle bundle;

    private UtilMethods util;

    private Uri imageURI;
    private Bitmap ImageBitmap;

    private String userCreatedFilename;

    private int toolbarSwitch;

    private CheckBox filename_CheckBox;
    private CheckBox filenameAndImage;
    private Button nextButton_EditWhat_Button;
    private TextView filename_TextView;

    DatabaseHandler databaseHandler1 = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        //Set the toolbar to custom color toolbar1
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        //link to interactive PhotoView element in photoView XML layout file
        com.github.chrisbanes.photoview.PhotoView photoView = findViewById(R.id.photo_view);

        //get information from bundle passed to this acivity from AddPetRecordActivity
        bundle = getIntent().getExtras();

        petID = bundle.getInt("petID");
        pet = databaseHandler1.getPet(petID);
        arrayListOfFilenames = databaseHandler1.getALL_Filenames_FROMDatabase(pet);

        Log.d("petID", Integer.toString(petID));


        //IF BUNDLE HAS IMAGE PATH WE ARE CREATING A BRAND NEW RECORD
        if (bundle.containsKey("ImagePath")) {
            toolbarSwitch = 1;
            imagePath = bundle.getString("ImagePath");

        }
        //IF BUNDLE CONTAINS RECORD ID WE ARE VIEW ONLY OR EDITING THE RECORD
        else if (bundle.containsKey("recordID")) {
            toolbarSwitch = 2;

            recordID_AdapterPosition = bundle.getInt("recordID");
            recordID_forSearchingDatabase = recordID_AdapterPosition + 1;

            Log.d("recordID_array", arrayListOfFilenames.get(recordID_AdapterPosition));
            Log.d("recordIDelse_position", Integer.toString(recordID_AdapterPosition));
            filename = arrayListOfFilenames.get(recordID_AdapterPosition);


            //Very round about way of getting the ONE IMAGE path associated with the recordID_AdapterPosition
            //and petID TODO: Find better way to get the image path from bundle information.
            //make array list to hold all image paths for the petID sent in the bundle
            ArrayList<String> ImagePaths_fromDatabase_ArrayList;
            ImagePaths_fromDatabase_ArrayList = databaseHandler1.getALL_ImagePaths_FROMDatabase(petID);
            imagePath = ImagePaths_fromDatabase_ArrayList.get(recordID_AdapterPosition);


            ImageBytes_fromDatabase = databaseHandler1.getALL_ImageBytes_FROMDatabase(petID);
            byte[] ImageByteArray = ImageBytes_fromDatabase.get(recordID_AdapterPosition);
            ImageBitmap = UtilMethods.bitmapFromByte(ImageByteArray);

            //Log.d("recordIDhere", ImageByteArray.toString());
            //Initialize a string imagePath to hold the path associated with a particular records id/

        }

        //IF BUNDLE CONTAINS FILE NAME WE ARE EDITING THE FILE NAME
        if (bundle.containsKey("filename")) {
            filename = bundle.getString("filename");
        }

//        Log.d("recordIDbytes", ImageBitmap.toString());
        /////////////////////////////////////////////
        if (ImageBitmap == null) {
            photoView.setImageURI(Uri.parse(imagePath));
            Log.d("recordIDhere", imagePath);
            Log.d("recordIDhere", "nnn");

        } else {
            photoView.setImageBitmap(ImageBitmap);
            Log.d("recordIDhere", "grr");
            Log.d("recordIDhere", ImageBytes_fromDatabase.get(recordID_AdapterPosition).toString());
        }


        // photoView.setImageURI(Uri.parse(imagePath));


        /////////////////////////////////////////////
        bundle.clear();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (toolbarSwitch == 1) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.records_menu, menu);
        }
        if (toolbarSwitch == 2) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.records_menu_viewonly, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //AddPet Record , GoBack, and Edit Record,
        //Menu Choices
        switch (item.getItemId()) {
            case R.id.recycleView_menuItem:
                addAPetRecord();
                break;
            case R.id.recycleView_menuItem_goback:
                Intent intent = new Intent(this, AddPetRecordActivity.class);
                bundle.putInt("petID", petID);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.edit_record_viewonly_menuItem:
                whatToEdit_Popup();
                break;
        }
        return true;
    }

    public void addAPetRecord() {

        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.enter_pet_records_popup, null);


        //Link interactive objects *USE view. when within a POPUP*
        enterFilenameEditText = view.findViewById(R.id.addRecord_popup_EditText);
        saveFileButton = view.findViewById(R.id.saveRecord_popup_Button);

        //IF THERE IS A FILENAME WE ARE EDITING THE RECROD
        //Set the filename in the popup so user can see the record they chose
        if (filename != null) {
            enterFilenameEditText.setText(filename);
        }


        //Build the Dialog and show.
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();



        //WHEN SAVE BUTTON IS PRESSED
        saveFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO fix datavalidation for filenames
                //Set user input into a holding string
                userCreatedFilename = enterFilenameEditText.getText().toString().trim();
                // Log.d("Filename2", filename);
                // Log.d("Filename2", String.valueOf(filename.equals(userCreatedFilename)));
                if (!arrayListOfFilenames.contains(userCreatedFilename) || filename == null || filename.equals(userCreatedFilename)) {

                    //IF THERE IS NO FILENAME A BRAND NEW RECORD WILL BE ADDED TO DATABASE
                    if (filename == null && !arrayListOfFilenames.contains(userCreatedFilename)) {
                        databaseHandler1.addRecord_toDatabase(pet, userCreatedFilename, imagePath);
                        alertDialog.dismiss();
                        finish();

                    } else {

                        Log.d("Filename2", filename);
                        Log.d("Filename2", userCreatedFilename);
                        //IF THERE IS  A FILENAME WE PROCEED TO EDIT PRE EXISTING DATABASE ENTRY
                        //clear the bundle so there is not "straggler" data.
                        bundle.clear();
                        //update the record
//                    Log.d("imagePath", imagePath);

                        if (imagePath != null) {
                            databaseHandler1.updateRecord_toDatabase(recordID_forSearchingDatabase, userCreatedFilename, imagePath);
                            Log.d("Filename1", userCreatedFilename);
                        } else {
                            Log.d("Filename2", Integer.toString(recordID_forSearchingDatabase));
                            Log.d("Filename2", filename);
                            Log.d("Filename2", userCreatedFilename);
                            databaseHandler1.updateRecord_toDatabase_Filename(recordID_forSearchingDatabase, filename, userCreatedFilename);
                        }
                        //Entry has now been updated.
                        //Send the petID in a bundle to the AddPetRecord Activity so we can "go Back where we started"
                        Intent intent = new Intent(PhotoView.this, AddPetRecordActivity.class);
                        bundle.putInt("petID", petID);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        alertDialog.dismiss();
                        finish();
                    }


                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "A file with this name already exists, please pick another.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, -10);
                    toast.show();
                    Log.d("Exist", "filename already exists");
                }

                //the default end to this function


            }

        });

    }//END ADD PET RECORD


    //************************************************************************
    // whatToEdit_Popup()                                                   //
    // Popup to allow the user to choose what elements of the record they   //
    //  want to edit. Either just the filename or the filename AND image    //
    //  If filename and image is chosen current filename appears in         //
    // filename creation dialog so name doesn't have to be changed          //
    //************************************************************************
    public void whatToEdit_Popup() {

        //initiate the builder
        alertDialogBuilder = new AlertDialog.Builder(this);
        //set the view to inflate
        View view = getLayoutInflater().inflate(R.layout.edit_what_part_of_record, null);

        //Link the interactive elements from the XML layout file
        filename_CheckBox = view.findViewById(R.id.filename_editWhat_CheckBox);
        filenameAndImage = view.findViewById(R.id.filenameAndImage_editWhat_Checkbox);
        nextButton_EditWhat_Button = view.findViewById(R.id.next_editWhat_button);
        filename_TextView = view.findViewById(R.id.filename_editWhat_TextView);

        //Make a string to show the current file
        String string = "Current file : " + filename;
        //pass this string to the TextView for display
        filename_TextView.setText(string);

        //finish putting the view together
        alertDialogBuilder.setView(view);
        //create the view and link it the dialog
        alertDialog = alertDialogBuilder.create();
        //show the dialog
        alertDialog.show();

        //WHEN NEXT BUTTON IS PRESSED
        nextButton_EditWhat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if filename and image is checked
                if (filenameAndImage.isChecked()) {

                    //completely restart the add record process. Pass the petID and the recordID_AdapterPosition through
                    //the process so these elements can be updated at the end.
                    Intent intent = new Intent(PhotoView.this, AddPetRecordActivity.class);
                    bundle.putInt("recordID", recordID_AdapterPosition);
                    bundle.putInt("petID", pet.getId());
                    intent.putExtras(bundle);

                    Log.d("recordIDONCLICK", Integer.toString(recordID_AdapterPosition));

                    startActivity(intent);
                    finish();

                    //if filename only is checked. open the addRecord function. Pass the filename
                    //so it will show up in the EditText View for editing
                } else if (filename_CheckBox.isChecked()) {
                    // filename = databaseHandler1.getRecord_FROMDatabase(recordID_forSearchingDatabase);
                    alertDialog.dismiss();
                    addAPetRecord();
                }

            }//END ON CLICK
        }); //END NEXT BUTTON
    } //END WHAT TO EDIT FUNCTION


}//END PHOTOVIEW
