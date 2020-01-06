package com.kismet.petsapp.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;


public class PetNotesActivity extends AppCompatActivity {
    String imagePathFromCropResult;
    Boolean imageResult = false;
    //declare custom color Toolbar
    private Toolbar toolbar;
    //declare interactive elements from pet_notes_activity.xml
    private EditText petNotesEditText;
    private TextView petNameTextView;
    private TextView petWeightTextView;
    private TextView petHumanAgeTextView;
    private TextView petAgeTextView;
    private ImageView petImageButton;
    private TableRow petNotes_titleRow;
    //declare interactive elements from notes_popup
    private EditText petName_NotesPopup_EditText;
    private EditText petBirthday_NotesPopup_EditText;
    private EditText petWeight_NotesPopup_EditText;


    //Constant for pet Linked to Database
    private ImageView petImage_NotesPopup_ImageButton;
    private Button saveButton_NotesPopup;
    //Create a new empty pet to fill and use during notes activity
    private Pet petCreatedFromDatabase = new Pet();
    //Gain access to the pet Database using functions
    private DatabaseHandler petRecordsDatabase;
    //Gain access to UtilMethods
    private UtilMethods utilMethods;
    private ListActivity listActivity;
    //AlertDialog Libraries for notes_popup fro within PetNotesActivity
    private AlertDialog alertDialog;
    private AlertDialog.Builder alerteDialogBuilder;
    //declare image variables for petNotes Popup
    private Uri croppedURI;
    private byte[] createdByteImage = null;
    private byte[] petImageBytePlaceholder;
    private Uri petImageUriPlaceholder;

    @Override
    //Create the custom menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //inflate custom menu for PetNotesActivity
        menuInflater.inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    //When custom toolbar menu item is seleted
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_notes_menuItem:
                saveNotes(petCreatedFromDatabase);
                break;
            case R.id.add_pet_record_menuItem:
                Log.d("PetNoteAct", "hello");
                Intent intent = new Intent(PetNotesActivity.this, AddPetRecordActivity.class);
                intent.putExtra("petID", petCreatedFromDatabase.getId());
                //ListAct(RecycleViewAdapter = 1, PetNotesActivity = 2,
                intent.putExtra("where", 2);
                startActivity(intent);
                finish();
                break;
            case R.id.go_back_notes_activity_menuItem:
                finish();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    //When starting PetNotesActivity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get paired layout xml file ( pet_notes_activity.xml)
        setContentView(R.layout.pet_notes_activity);

        //Set the toolbar to custom color toolbar1
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        //initialize Access to Pet Database Functions
        petRecordsDatabase = new DatabaseHandler(this);

        //Get Intent bundle items
        Bundle recycleAdapterActivityBundle = getIntent().getExtras();
        int petIDFromRecyclerAdapter = recycleAdapterActivityBundle.getInt("petID");

        //Create Pet instance from PetRecordsDatabase using petID
        petCreatedFromDatabase = initializeUseablePetObjectFromDatabase(petIDFromRecyclerAdapter);

        //link interactive objects from pet_notes_activity.xml to PetNotesActivity.this
        petNotesEditText = findViewById(R.id.pet_notes_edit_text_in_notes_editText);
        petNameTextView = findViewById(R.id.pet_name_in_records_TextView);
        petAgeTextView = findViewById(R.id.pet_age_in_notes_textView);
        petWeightTextView = findViewById(R.id.pet_weight_in_notes_textView);
        petHumanAgeTextView = findViewById(R.id.pet_age_human_years_in_notes_textView);
        petImageButton = findViewById(R.id.pet_image_in_notes_imagebutton);
        petNotes_titleRow = findViewById(R.id.petNotes_clickableTitleRow);

        //Set TextView values for PetNotesActivity
        setTextViewFieldsInPetNotesActivity(petCreatedFromDatabase);

        //when petImageButton is clicked
        petNotes_titleRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes_Popup(petCreatedFromDatabase);
            }
        });
        petImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes_Popup(petCreatedFromDatabase);
            }
        });


    } //END OF ON CREATE

    //****************************************************************************
    // notes_popup                                                              //
    // Creates a popup window to edit the pet info and add weight to pet        //
    //****************************************************************************
    public void notes_Popup(final Pet pet) {

        //Start Alert Dialog Builder and inflate the notes_popup view
        alerteDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.notes_popup, null);

        alerteDialogBuilder.setView(view);
        alertDialog = alerteDialogBuilder.create();
        alertDialog.show();


        //Link interactive elements of layout with PetNOtesActivity
        //when inside a function use VIEW.findViewByID
        petName_NotesPopup_EditText = view.findViewById(R.id.petName_in_notes_popup);
        petBirthday_NotesPopup_EditText = view.findViewById(R.id.petBirthday_in_notes_popup);
        petWeight_NotesPopup_EditText = view.findViewById(R.id.pet_weight_in_notes_popup);
        petImage_NotesPopup_ImageButton = view.findViewById(R.id.pet_Image_in_notes_popup_ImageButton);
        saveButton_NotesPopup = view.findViewById(R.id.saveButton_in_notes_popup);

        //Set fields with values
        setEditTextFieldsInNotesPopup();


        //When SaveButton is pressed
        saveButton_NotesPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditPetFieldsIntoPetCreatedFromDatabase(petCreatedFromDatabase);
                if (noDataValidationErrors()) {
                    setTextViewFieldsInPetNotesActivity(petCreatedFromDatabase);
                    savePet_inPopup(petCreatedFromDatabase);
                }
            }
        });

        //When ImageButton is pressed
        petImage_NotesPopup_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(PetNotesActivity.this);

            }
        });


    } // END notes_Popup


    //****************************************************************************
    // openGallery Function Description                                         //
    // OpenGallery, pick image, crop image, and set to imagebutton              //
    //****************************************************************************
    public void openGallery(Activity activity) {
        CropImage.activity()
                //    .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(200, 200)
                .start(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if cancel is pressed before cropping dismiss then Notes_popup dialog
        if (resultCode == 0) {
            alertDialog.dismiss();

            //if not canceled during picture choosing, start Crop image activity
        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                croppedURI = result.getUri();

                //if image successfully cropped,
                if (resultCode == RESULT_OK) {
                    //set Image button to newly croppedImage
                    petImage_NotesPopup_ImageButton.setImageURI(croppedURI);

                    //get a file path from newly cropped image
                    File myFilePath = new File(croppedURI.getPath());
                    myFilePath.getAbsolutePath();
                    imagePathFromCropResult = myFilePath.getAbsolutePath();

                    //set that image result is true
                    imageResult = true;

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();

                }
            }
        }
    }


    //****************************************************************************
    // saveNotes Function Description                                           //
    // Save pet notes into database by passing in petCreatedFromDatabase then   //
    // passing these values to the appropriate database object                  //
    //****************************************************************************
    public void saveNotes(Pet pet) {
        //set the notes field of petCreatedFromDatabase to what is in the edit text field
        pet.setNotes(petNotesEditText.getText().toString());

        //update the notes column in the database
        petRecordsDatabase.updatePetNotes(pet);
        petRecordsDatabase.updatePetWeight_in_Database(pet);


        Log.d("saveNotes", String.valueOf(pet.getId()));

        com.kismet.petsapp.Activities.ListActivity.getInstance().recreate();

        //close the PetNotesActvity and return to listActivity
        finish();
    }

    //***********************************************************************
    //  setEditPetFieldsIntoPetCreatedFromDatabase                         //
    //  sets the editable values in the notes popup to the values of       //
    //  the current pet selected. If a new photo was selected, it          //
    //  saves the new image into the pet passed into it                    //
    //***********************************************************************
    public void setEditPetFieldsIntoPetCreatedFromDatabase(Pet pet) {
        pet.setName(petName_NotesPopup_EditText.getText().toString().trim());
        pet.setBirthdayString(petBirthday_NotesPopup_EditText.getText().toString().trim());
        pet.setWeight(petWeight_NotesPopup_EditText.getText().toString().trim());

        if (imageResult) {
            pet.setImageURI(croppedURI.toString());
            createdByteImage = UtilMethods.byteImageFromPath(imagePathFromCropResult);
            pet.setImageBYTE(createdByteImage);
            imageResult = false;
        }
    }


    //***********************************************************************
    //  setEditPetFieldsIntoPetCreatedFromDatabase                         //
    //  sets the editable values in the notes popup to the values of       //
    //  the current pet selected. If a new photo was selected, it          //
    //  saves the new image into the pet passed into it                    //
    //***********************************************************************
    public void setEditTextFieldsInNotesPopup() {
        petName_NotesPopup_EditText.setText(petCreatedFromDatabase.getName());
        petBirthday_NotesPopup_EditText.setText(petCreatedFromDatabase.getBirthdayString());
        checkForImageAndPlace(petImage_NotesPopup_ImageButton, petCreatedFromDatabase);
        checkForWeightAndPlace(petWeight_NotesPopup_EditText, petCreatedFromDatabase);
    }

    //************************************************************************
    // setTextViewFieldsInPetNotesActivity                                  //
    // sets the textViews in PetNotesActivity to the petName and petAge     //
    // of the pet passed to it. It calls the checkImageAndPlace function    //
    // as well as the checkForWeightAndPlace function and sets those        //
    // interactive views as well                                            //
    //************************************************************************
    public void setTextViewFieldsInPetNotesActivity(Pet pet) {
        petNameTextView.setText(pet.getName());
        petAgeTextView.setText(UtilMethods.birthdayStringToPrettyAge(pet.getBirthdayString()));
        checkForImageAndPlace(petImageButton, pet);
        checkForWeightAndPlace(petWeightTextView, pet);
        checkForNotesAndPlace();
    }

    //************************************************************************
    // savePet_inPopup Function Description                                 //
    // Save pet info into database by passing in petCreatedFromDatabase     //
    // then passing these values to the appropriate database object         //
    //************************************************************************
    public void savePet_inPopup(Pet pet) {
        pet.setWeight(petWeight_NotesPopup_EditText.getText().toString().trim());
        petRecordsDatabase.updatePet(pet);
        petRecordsDatabase.updatePetWeight_in_Database(pet);


        com.kismet.petsapp.Activities.ListActivity.getInstance().recreate();

        setTextViewFieldsInPetNotesActivity(petCreatedFromDatabase);

        if (petWeight_NotesPopup_EditText.getText().toString().isEmpty()) {
            petHumanAgeTextView.setVisibility(View.INVISIBLE);
            petWeightTextView.setVisibility(View.INVISIBLE);
        } else {
            petHumanAgeTextView.setVisibility(View.VISIBLE);
            petWeightTextView.setVisibility(View.VISIBLE);
        }


        //close the PetNotesActvity and return to listActivity
        alertDialog.dismiss();
    }
    //************************************************************************
    // savePet_inPopup Function Description                                 //
    // Save pet info into database by passing in petCreatedFromDatabase     //
    // then passing these values to the appropriate database object         //
    //***********************************************************************

    public boolean noDataValidationErrors() {
        Log.d("PetNotes_validation", String.valueOf(UtilMethods.dateValidation(petBirthday_NotesPopup_EditText.toString().trim())));
        Log.d("PetNotes_validation", petBirthday_NotesPopup_EditText.toString());

        //  weight input contains something other than letters
        if (!validateWeight()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid weight", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return false;
        }

        // Return 0 = the date was not a valid date (could not be turned into a date)
        if (UtilMethods.dateValidation(petBirthday_NotesPopup_EditText.getText().toString().trim()) == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid date", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();

            return false;

            // 2 = birthday is not before today (not born yet)
        } else if (UtilMethods.dateValidation(petBirthday_NotesPopup_EditText.getText().toString().trim()) == 2) {
            Toast toast = Toast.makeText(getApplicationContext(), "Birthday must be before today", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }

    //***********************************************************************
    // checkForImageAndPlace                                               //
    // checks if the pet object passed to it has a byte array image        //
    // attached If so this function will convert the array into a bitmap   //
    // and set the ImageView passed to it to the pets image byte array.    //
    //***********************************************************************
    public void checkForImageAndPlace(ImageView petImageButton, Pet pet) {
        if (pet.getImageBYTE() != null) {
            Bitmap reconstructedPetImageFromRecyclerAdapter = UtilMethods.bitmapFromByte(petCreatedFromDatabase.getImageBYTE());
            petImageButton.setImageBitmap(reconstructedPetImageFromRecyclerAdapter);
            //  petImageButton.setImageURI(Uri.parse(pet.getImageURI()));
        } else {
            petImageButton.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.paw3));
            //petImageButton.setImageBitmap(null);
        }
    }

    //********************************************************************
    // checkForWeightAndPlace **Overloaded Function**                   //
    // checks if the pet in the database has a weight attached. If so   //
    // this function will set the weight value from the database into   //
    // either a TextView or an EditText as needed.                      //
    // **will use the weight and set the the human age in TextView      //
    //********************************************************************
    public void checkForWeightAndPlace(EditText petWeightEditText, Pet pet) {
        if (pet.getWeight() != null) {

            petWeightEditText.setText(pet.getWeight());
        }
    }

    public void checkForWeightAndPlace(TextView petWeightTextView, Pet pet) {
        if (pet.getWeight() != null) {
            String weightString = pet.getWeight() + " lbs";
            petWeightTextView.setText(weightString);

            if (pet.getWeight() != null)
                setHumanAge(weightString, pet);
        }
    }

    //************************************************************************
    // checkForNotesAndPlace                                                //
    // checks if the pet created in the activity has a weight               //
    // attached from the activities Edit Text input. If so this function    //
    // will set the weight value into the database                          //
    //************************************************************************
    public void checkForNotesAndPlace() {
        if (petCreatedFromDatabase.getNotes() != null) {
            petNotesEditText.setText(petCreatedFromDatabase.getNotes());
        }
    }

    //************************************************************************
    // setHumanAge                                                          //
    // uses the weight value and determines the pets age in human years     //
    // based on the age and the weight                                      //
    //************************************************************************
    public void setHumanAge(String weightString, Pet pet) {
        Log.d("PetNotesActivity", String.valueOf(weightString.equalsIgnoreCase(" lbs")));
        if (!weightString.isEmpty() && !weightString.equalsIgnoreCase(" lbs")) {
            String[] weightBreakdown = weightString.split(" ");
            int weight = Integer.parseInt(weightBreakdown[0]);
            int years = UtilMethods.ageInYears(pet.getBirthdayLong());
            int months = UtilMethods.ageInMonths(pet.getBirthdayLong());

            if (years <= 5) {

                if (years < 1)
                    petHumanAgeTextView.setText((months * 1.25) + " Human Years ");
                if (years >= 1)
                    petHumanAgeTextView.setText("15 Human Years");
                if (years == 2)
                    petHumanAgeTextView.setText("24 Human Years");
                if (years == 3)
                    petHumanAgeTextView.setText("28 Human Years");
                if (years == 4)
                    petHumanAgeTextView.setText("32 Human Years");
                if (years == 5)
                    petHumanAgeTextView.setText("36 Human Years");
            }
            if (years == 6) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("40 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("42 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("45 Human Years");
            }
            if (years == 7) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("44 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("47 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("50 Human Years");
            }
            if (years == 8) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("48 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("51 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("55 Human Years");
            }
            if (years == 9) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("52 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("66 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("61 Human Years");
            }
            if (years == 10) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("56 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("60 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("66 Human Years");
            }
            if (years == 11) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("60 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("65 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("72 Human Years");
            }
            if (years == 12) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("64 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("69 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("77 Human Years");
            }
            if (years == 13) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("68 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("74 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("82 Human Years");
            }
            if (years == 14) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("72 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("78 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("88 Human Years");
            }
            if (years == 15) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("76 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("83 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("93 Human Years");
            }
            if (years == 16) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("80 Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("87 Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("120 Human Years");
            }

            if (years > 16) {
                if (weight <= 20)
                    petHumanAgeTextView.setText("80+ Human Years");
                else if (weight <= 50)
                    petHumanAgeTextView.setText("87+ Human Years");
                else if (weight > 50)
                    petHumanAgeTextView.setText("120+ Human Years");
            }

        }
    }


    //************************************************************************
    // validateWeight                                                       //
    // checks if the value in the editText of the notes popup is            //
    // only numbers. If only numbers it returns true, if it has anything    //
    //  else it returns false.                                               //
    //************************************************************************
    public Boolean validateWeight() {
        Log.d("petNotes", petWeight_NotesPopup_EditText.getText().toString());

        //int integer = Integer.parseInt(petWeight_NotesPopup_EditText.getText().toString().trim());


        if (petWeight_NotesPopup_EditText.getText().toString().isEmpty())
            return true;

        else if ((petWeight_NotesPopup_EditText.getText().toString().matches("^[^a-zA-Z]*") && Integer.parseInt(petWeight_NotesPopup_EditText.getText().toString().trim()) > 0)) {
            petCreatedFromDatabase.setWeight(petWeight_NotesPopup_EditText.getText().toString().trim());
            return true;
        }

        return false;
    }

    //************************************************************************
    // createUseablePetObjectFromDatabase                                   //
    //  weight and notes were added in an update and start in this activity //
    //  because of this the pet object needs to be initialized in seperate  //
    //  steps.                                                              //
    //************************************************************************
    public Pet initializeUseablePetObjectFromDatabase(int petID) {
        Pet pet = new Pet();
        pet = petRecordsDatabase.getPet(petID);

        // Log.d("petURI", pet.getImageURI());


        if (petRecordsDatabase.getPetWeight(petID) != null) {
            pet.setWeight(petRecordsDatabase.getPetWeight(petID));
            pet.setNotes(petRecordsDatabase.getNotesFromDatabase(petID));

        }


        return pet;

    }

} //END OF PetNotesActivity.java