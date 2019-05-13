package com.example.howoldaremypets.Activities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.howoldaremypets.Data.DatabaseHandler;
import com.example.howoldaremypets.Model.Pet;
import com.example.howoldaremypets.R;
import com.example.howoldaremypets.UI.RecyclerViewAdapter;
import com.example.howoldaremypets.Util.UtilMethods;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Toolbar toolbar;
    private List<Pet> petsListFromDB;
    private List<Pet> petListForRecycleViewer;
    private DatabaseHandler db;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private UtilMethods util;
    private EditText petNameInput;
    private EditText petBirthdayInput;
    private Button saveButton;
    private static ListActivity listActivty;

    private ImageView imageButton;
    private static int SELECT_PICTURE = 100;
    private Uri pickedImageUri;
    private byte[] imageByte;
    public static final int REQUEST_CODE = 1;
    String imagePath;
    private int petIDBundle;
    private Uri croppedURI;
    private byte[] createdByteImage = null;
    private int isclosed = 0;
    private boolean imageResult = false;
    private String petUriPlaceholder;
    private byte[] petBytePlaceholder;

    private ListActivity listActivity;

    private byte[] imageByteFromBundle;
    String imagePathFromCropResult;
    private Bundle bundle = new Bundle();
    byte[] inputData;
    int petIndex;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
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



        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
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

    public static ListActivity getInstance(){
        return   listActivty;
    }


/////////////////////////////////

    public void createPopUpDialog1() {

        bundle = getIntent().getExtras();

        dialogBuilder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.popup, null);
        petNameInput = (EditText) view.findViewById(R.id.petName);
        petBirthdayInput = (EditText) view.findViewById(R.id.petBirthday);
        saveButton = (Button) view.findViewById(R.id.savePet);
        imageButton = (ImageView) view.findViewById(R.id.imageButton);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();


        //Default Input for test purposes
        petBirthdayInput.setText("12/12/2002");


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean isValidInput = util.isValidInput(petBirthdayInput.getText().toString().trim());

                if (!petNameInput.getText().toString().isEmpty() && !petBirthdayInput.getText().toString().trim().isEmpty() && isValidInput) {
                    Boolean isValidDate = util.dateValidation(petBirthdayInput.getText().toString().trim());

                    if (isValidDate) {

                        savePetToDB(v);
                      //  recyclerViewAdapter.notifyDataSetChanged();

                       /* NotificationManager notification = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notify = new Notification.Builder(getApplicationContext()).setContentTitle("Hello").setContentText("hi").setSmallIcon(R.drawable.ic_launcher_foreground).build();
                        notify.flags |= Notification.FLAG_AUTO_CANCEL;
                        notification.notify(0,notify);*/




                    } else {
                        Snackbar.make(v, "Please enter a valid date  MM/DD/YYYY ", Snackbar.LENGTH_LONG).show();
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
        String newPetData = util.capitalizeFirstLetter(petNameInput.getText().toString().trim());
        String newBirthdayData = petBirthdayInput.getText().toString().trim();

        //set pet Information
        pet.setName(newPetData);
        pet.setBirthdayString(newBirthdayData);


        if (petUriPlaceholder != null){
            pet.setImageURI(petUriPlaceholder);
            pet.setImageBYTE(petBytePlaceholder);
            petBytePlaceholder = null;
            petUriPlaceholder = null;
        }

        if (imageResult ) {
            pet.setImageURI(croppedURI.toString());
            createdByteImage = util.byteImageFromPath(imagePathFromCropResult);
            pet.setImageBYTE(createdByteImage);
            imageResult = false;

        }



        if (bundle == null) {

            db.addPet(pet);
            recyclerViewAdapter.notifyDataSetChanged();
            petListForRecycleViewer.add(pet);
            pet.setId(db.getLastId());
            Log.d("dbcount",String.valueOf(db.getCount()));


            db.close();

            Snackbar.make(v, "Pet Added!", Snackbar.LENGTH_LONG).show();

        } else {

            Log.d("index", String.valueOf(petListForRecycleViewer.indexOf(pet)));

            pet.setId(petIDBundle);

            db.updatePet(pet);
            recyclerViewAdapter.notifyDataSetChanged();
            db.close();

            petListForRecycleViewer.set(petIndex,pet);

        }


        dialog.dismiss();


    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(200,200)
                .start(this);

        /*dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                    PetActivity.this.finish();


                    return true;
                }else {
                    return false;
                }
            }
        });*/

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            dialog.dismiss();
   //         PetActivity.this.finish();

        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                croppedURI = result.getUri();

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

}





////////////////////////////////////











