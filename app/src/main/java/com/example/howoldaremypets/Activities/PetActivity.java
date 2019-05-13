package com.example.howoldaremypets.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.howoldaremypets.Data.DatabaseHandler;
import com.example.howoldaremypets.Model.Pet;
import com.example.howoldaremypets.R;
import com.example.howoldaremypets.Util.UtilMethods;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class PetActivity extends AppCompatActivity {
    private EditText petNameInput;
    private EditText petBirthdayInput;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveButton;
    private DatabaseHandler db;
    private UtilMethods util;
    private ImageView imageButton;
    private static final int SELECT_PICTURE = 100;
    private Uri pickedImageUri;
    private int petIDBundle;
    private Uri croppedURI;
    private byte[] createdByteImage = null;
    private int isclosed = 0;
    private boolean imageResult = false;
    private String petUriPlaceholder;
    private byte[] petBytePlaceholder;

    private ListActivity listActivity;
    private RecyclerView recyclerView;

    private byte[] imageByteFromBundle;
    public static final int REQUEST_CODE = 1;
    String imagePathFromCropResult;
    private Bundle bundle = new Bundle();
    byte[] inputData;

    Uri uri = Uri.parse("android.resource://com.segf4ult.test/" + R.drawable.pencil);



    /*Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_activity);

        db = new DatabaseHandler(this);

        createPopUpDialog1();

        if(isclosed == 2) {
            PetActivity.this.finish();
        }

    }*/



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

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                    PetActivity.this.finish();

                    Intent intent = (new Intent(PetActivity.this,ListActivity.class));
                    startActivity(intent);




                    return true;
                }else {
                    return false;
                }
            }
        });




        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean isValidInput = util.isValidInput(petBirthdayInput.getText().toString().trim());

                if (!petNameInput.getText().toString().isEmpty() && !petBirthdayInput.getText().toString().trim().isEmpty() && isValidInput) {
                    Boolean isValidDate = util.dateValidation(petBirthdayInput.getText().toString().trim());

                    if (isValidDate) {

                        savePetToDB(v);

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
            String petNameBundle =  bundle.getString("petName");
            String petBirthdayBundle = bundle.getString("petBirthday");
            String petImageURIBundle = bundle.getString("petImageString");
            byte[] petImageByteBundle = bundle.getByteArray("petImageByte");


            petIDBundle = bundle.getInt("petID");

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

            petNameInput.setText(petNameBundle);
            petBirthdayInput.setText(petBirthdayBundle);
            petUriPlaceholder = petImageURIBundle;
            petBytePlaceholder = petImageByteBundle;

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
        }

        if (imageResult ) {
            pet.setImageURI(croppedURI.toString());
            createdByteImage = util.byteImageFromPath(imagePathFromCropResult);
            pet.setImageBYTE(createdByteImage);
        } /*else {
            Log.d("inputdata", pet.getImageURI());

            pet.setImageURI(null);
            pet.setImageBYTE(null);
        }*/



        if (bundle == null) {

            db.addPet(pet);
            db.close();
            Log.d("pet name no bundle", pet.getName());
            Log.d("petIDBundle", String.valueOf(pet.getId()));

            Snackbar.make(v, "Pet Added!", Snackbar.LENGTH_LONG).show();

        } else {

       //     Log.d("croppedURI3", createdByteImage.toString());
            pet.setId(petIDBundle);

            db.updatePet(pet);
            db.close();


            Log.d("petID", String.valueOf(pet.getId()));
        }

        dialog.dismiss();

        ListActivity.getInstance().finish();

        startActivity(new Intent(PetActivity.this, ListActivity.class));
      //end current activity

        this.finish();

    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(200,200)
                .start(this);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

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
        });

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            dialog.dismiss();
            PetActivity.this.finish();

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
