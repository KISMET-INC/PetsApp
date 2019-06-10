package com.kismet.petsapp.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.kismet.petsapp.Data.DatabaseHandler;
import com.kismet.petsapp.Model.Pet;
import com.kismet.petsapp.R;
import com.kismet.petsapp.UI.RecyclerViewAdapter;
import com.kismet.petsapp.Util.UtilMethods;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Toolbar toolbar;
    private List<Pet> petsListFromDB;
    private List<Pet> petListForRecycleViewer;
    private DatabaseHandler db;
    private ImageView addFirstPet;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private UtilMethods util;
    private EditText petNameInput;
    private EditText petBirthdayInput;
    private Button saveButton;
    private static ListActivity listActivty;

    private ImageView imageButton;

    private int petIDBundle;
    private Uri croppedURI;
    private byte[] createdByteImage = null;
    private boolean imageResult = false;
    private String petUriPlaceholder;
    private byte[] petBytePlaceholder;

    String imagePathFromCropResult;
    private Bundle bundle = new Bundle();
    int petIndex;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        createPopUpDialog1();

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar);
        listActivty = this;


        toolbar = findViewById(R.id.toolbar1);
        addFirstPet = findViewById(R.id.addpetimageview);

        setSupportActionBar(toolbar);

        db = new DatabaseHandler(this);
        checkCount();


        Log.d("dbcount", String.valueOf(db.getCount()));


        if (db.getCount() >= 1) {
            addFirstPet.setVisibility(View.INVISIBLE);
        }
        recyclerView = findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        petsListFromDB = new ArrayList<>();  // Holds all pets received from DB
        petListForRecycleViewer = new ArrayList<>(); // Captures all pets from petsListFromDB to be viewed by Recycler Viewer

        //get items from database

        petsListFromDB = db.getAllPets();

        for (Pet c : petsListFromDB) {
            Pet pet = new Pet();
            pet.setName(c.getName());
            pet.setBirthdayString(c.getBirthdayString());
            pet.setBirthdayLong(c.getBirthdayLong());
            pet.setId(c.getId());
            pet.setImageURI(c.getImageURI());
            pet.setImageBYTE(c.getImageBYTE());

            Log.d("Item .id", String.valueOf(pet.getId()));

            petListForRecycleViewer.add(pet);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, petListForRecycleViewer);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    public static ListActivity getInstance() {
        return listActivty;
    }



    public void createPopUpDialog1() {

        bundle = getIntent().getExtras();

        dialogBuilder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.popup, null);
        petNameInput = view.findViewById(R.id.petName);
        petBirthdayInput = view.findViewById(R.id.petBirthday);
        saveButton = view.findViewById(R.id.savePet);
        imageButton = view.findViewById(R.id.imageButton);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();


        //TODO: Default Input for test purposes
        petNameInput.setText("trevor");
        petBirthdayInput.setText("12/12/2002");


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidInput = UtilMethods.isValidInput(petBirthdayInput.getText().toString().trim());

                if (!petNameInput.getText().toString().isEmpty() && !petBirthdayInput.getText().toString().trim().isEmpty() && isValidInput) {
                    int isValidDate = UtilMethods.dateValidation(petBirthdayInput.getText().toString().trim());

                    if (isValidDate == 1) {

                        savePetToDB(v);
                        checkCount();

                    } if (isValidDate ==0) {
                        Snackbar.make(v, "Please enter a valid date  MM/DD/YYYY ", Snackbar.LENGTH_LONG).show();

                    } if (isValidDate ==2) {
                        Snackbar.make(v, "Date must be before Today ", Snackbar.LENGTH_LONG).show();
                    }

                } else

                    Snackbar.make(v, "Please enter a valid name and petBirthdayTextView", Snackbar.LENGTH_LONG).show();
            }


        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // if you are editing a pet info

        if (bundle != null) {
            String petNameBundle = bundle.getString("petName");
            String petBirthdayBundle = bundle.getString("petBirthday");
            String petImageURIBundle = bundle.getString("petImageString");
            byte[] petImageByteBundle = bundle.getByteArray("petImageByte");
            petIndex = bundle.getInt("petIndex");
            petIDBundle = bundle.getInt("petID");

            petNameInput.setText(bundle.getString("petName"));
            petBirthdayInput.setText(petBirthdayBundle);
            petUriPlaceholder = bundle.getString("petImageString");
            petBytePlaceholder = bundle.getByteArray("petImageByte");


            if (bundle.getByteArray("petImageByte") != null) {

                //create bitmap form BYTE bundle
                Bitmap petImageBitmapFromByteBundle = BitmapFactory.decodeByteArray(petImageByteBundle, 0, petImageByteBundle.length);

                //get image path from URI
                croppedURI = Uri.parse(petImageURIBundle);
                File myFilePath = new File(croppedURI.getPath());
                myFilePath.getAbsolutePath();
                imagePathFromCropResult = myFilePath.getAbsolutePath();

                //put image bitmap into image button
                imageButton.setImageBitmap(petImageBitmapFromByteBundle);
            }
        }
        dialog.show();


    }


    private void savePetToDB(View v) {

        Pet pet = new Pet();

        //trim and capitalize inputs and refactor
        String newPetData = UtilMethods.capitalizeFirstLetter(petNameInput.getText().toString().trim());
        String newBirthdayData = petBirthdayInput.getText().toString().trim();

        //set pet Information
        pet.setName(newPetData);
        pet.setBirthdayString(newBirthdayData);


        if (petUriPlaceholder != null) {
            pet.setImageURI(petUriPlaceholder);
            pet.setImageBYTE(petBytePlaceholder);
            petBytePlaceholder = null;
            petUriPlaceholder = null;
        }

        if (imageResult) {
            pet.setImageURI(croppedURI.toString());
            createdByteImage = UtilMethods.byteImageFromPath(imagePathFromCropResult);
            pet.setImageBYTE(createdByteImage);
            imageResult = false;

        }


        if (bundle == null) {
            db.addPet(pet);
            recyclerViewAdapter.notifyDataSetChanged();
            petListForRecycleViewer.add(pet);
            pet.setId(db.getLastId());
            Log.d("dbcount", String.valueOf(db.getCount()));

            db.close();


            Snackbar.make(v, "Pet Added!", Snackbar.LENGTH_LONG).show();

        } else {

            Log.d("index", String.valueOf(petListForRecycleViewer.indexOf(pet)));

            pet.setId(petIDBundle);

            db.updatePet(pet);
            recyclerViewAdapter.notifyDataSetChanged();
            db.close();

            petListForRecycleViewer.set(petIndex, pet);

        }


        dialog.dismiss();


    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(200, 200)
                .start(this);


    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            dialog.dismiss();
            Log.d("result", String.valueOf(resultCode));


        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                croppedURI = result.getUri();
                Log.d("result", String.valueOf(resultCode));


                if (resultCode == RESULT_OK) {

                    Log.d("result", String.valueOf(resultCode));


                    imageButton.setImageURI(croppedURI);
                    File myFilePath = new File(croppedURI.getPath());
                    myFilePath.getAbsolutePath();
                    imagePathFromCropResult = myFilePath.getAbsolutePath();
                    imageResult = true;

                    Log.d("croppedURI1", imagePathFromCropResult);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

            }
        }
    }


    public void checkCount() {
        if (db.getCount() == 0) {
            addFirstPet.setVisibility(View.VISIBLE);
            Log.d("zero", "db count is zero");
        } else {
            addFirstPet.setVisibility(View.INVISIBLE);
        }
    }
}













