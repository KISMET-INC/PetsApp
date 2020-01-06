package com.kismet.petsapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kismet.petsapp.Data.DatabaseHandler;
import com.kismet.petsapp.Model.Pet;
import com.kismet.petsapp.R;
import com.kismet.petsapp.UI.RecyclerViewAdapter;
import com.kismet.petsapp.Util.UtilMethods;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends AppCompatActivity {

    //Create access to this class ListActivity.class ( for an instance later)
    private static ListActivity listActivty;
    //pet index within the arraylist. Used when editing pet.
    int petIndex;
    String imagePathFromCropResult;
    //Declare on Create Elements
    private Toolbar toolbar;
    private ImageView addFirstPet;   // Image to show users where to start
    private Bundle bundle = new Bundle();
    private Context context;
    private RecyclerViewAdapter recyclerViewAdapter;
    //Access Database Handler
    private DatabaseHandler databaseHandler;
    private List<Pet> petListForRecycleViewer;
    //Declare elements for Recycle View and Recycle View Adapter
    private RecyclerView recyclerView;
    //Declare Array Lists for use with Recycle View Adapter
    private List<Pet> petsListFromDB;
    //Declare Elements of Add Pet Popup Dialog
    private AlertDialog.Builder addPet_DialogBuilder;
    private EditText petBirthdayInput;
    private Button saveButton;
    private AlertDialog addPet_DialogBox;
    //Declare interactive elements of AddPetPopupDialog
    private EditText petNameInput;
    private EditText petWeightInput;
    private ImageView imageButton;
    //Delcare items for uses with OpenGallery for image selection
    private int petID_forEditingPet;
    private String petImageString_bundle_fromRecycleAdapter;
    private byte[] petImageByte_bundle_fromRecycleAdapter;
    private Uri croppedURI;
    private byte[] createdByteImage = null;
    private boolean imageResult = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    //****************************************************************************
    // getInstance()                                                            //
    // Return an instance of this activity, ListActivity. Static so it wont be  //
    // recreated, will return THIS EXACT instance                               //
    //****************************************************************************
    public static ListActivity getInstance() {
        return listActivty;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        addPetPopupDialog();

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list1);
        listActivty = this;
        context = this;

        //Setup Custom Toolbar
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        //INITIALIZE STARTING ELEMENTS

        //link interactive elements
        addFirstPet = findViewById(R.id.add_first_pet);
        recyclerView = findViewById(R.id.recycle);


        //Database Access
        databaseHandler = new DatabaseHandler(this);
        determineVisibility_ofAddFirstPetImage(); //Check if there are any pets already in the database

        //INITIALIZE EMPTY ARRAY LISTS :

        // Holds all pets received from DB
        petsListFromDB = new ArrayList<>();

        // Captures all pets from petsListFromDB to be viewed by Recycler Viewer
        petListForRecycleViewer = new ArrayList<>();


        //GET ALL PETS FROM DATABASE
        //collect pets from database into a local arraylist of pet Objects
        petsListFromDB = databaseHandler.getAllPets();

        for (Pet c : petsListFromDB) {
            Pet pet = new Pet();
            pet.setName(c.getName());
            pet.setBirthdayString(c.getBirthdayString());
            pet.setBirthdayLong(c.getBirthdayLong());
            pet.setId(c.getId());
            pet.setImageURI(c.getImageURI());
            pet.setImageBYTE(c.getImageBYTE());
            pet.setNotes(c.getNotes());
            pet.setWeight(c.getWeight());

            //add each pet to an array list of pets for the recycle viewer
            petListForRecycleViewer.add(pet);
        }

        //CREATE THE RECYCLE VIEWER
        recyclerView.setHasFixedSize(true);
        //set the layout of the recycle viewer
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //the adapter object is needed as a middle man to help link the local array list to the recycle viewer
        recyclerViewAdapter = new RecyclerViewAdapter(this, petListForRecycleViewer);
        //pass the middle man into the recycle viewer so it will present the local arraylist of pets
        recyclerView.setAdapter(recyclerViewAdapter);

    } //END OF ON CREATE - LIST ACTIVITY

    //****************************************************************************
    // addPetPopupDialog()                                                      //
    // Creates an alert dialog (popup) to allow user to enter pet information   //
    // into program and database.                                               //
    //****************************************************************************
    public void addPetPopupDialog() {

        //Get Bundle information (if available) passed to this activity
        bundle = getIntent().getExtras();

        //Inflate the layout of the popup
        View view = getLayoutInflater().inflate(R.layout.notes_popup, null);
        //Link interactive elements from notes_popup.xml use VIEW because it is a popup dialog
        petNameInput = view.findViewById(R.id.petName_in_notes_popup);
        petBirthdayInput = view.findViewById(R.id.petBirthday_in_notes_popup);
        saveButton = view.findViewById(R.id.saveButton_in_notes_popup);
        imageButton = view.findViewById(R.id.pet_Image_in_notes_popup_ImageButton);
        petWeightInput = view.findViewById(R.id.pet_weight_in_notes_popup);


        //BUILD THE DIALOG BOX
        //Create a new Builder/Adapter to help link the View to the Dialog Box Creator
        addPet_DialogBuilder = new AlertDialog.Builder(this);
        //Set the View into the Adapter/Builder
        addPet_DialogBuilder.setView(view);
        //Tell the dialog box to create what the adapter has presented
        addPet_DialogBox = addPet_DialogBuilder.create();

        //****************************************************************************
        // TODO: DEFAULT VALUES FOR TESTING                                         //
        //****************************************************************************
        //petNameInput.setText("trevor");
        //petBirthdayInput.setText("12122002");


        //When Save Button is pressed in Add Pet Popup:
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DATA VALIDATION

                //set boolean to determine what sort of input user has typed. Helps to determine two ways birthday can be input.
                boolean petBirthday_Is_NumbersOnly = UtilMethods.Validate_is_NumbersOnly(petBirthdayInput.getText().toString().trim());
                boolean petNameInput_NOT_EMPTY = !petNameInput.getText().toString().isEmpty();
                boolean petBirthdayInput_NOT_EMPTY = !petBirthdayInput.getText().toString().trim().isEmpty();

                //If birthday input is not empty, and birthday input consists of numbers only:
                if (petNameInput_NOT_EMPTY && petBirthdayInput_NOT_EMPTY && petBirthday_Is_NumbersOnly) {
                    //Convert birthday input into Date object and check if it is a date that can be verified.
                    int isValidDate = UtilMethods.dateValidation((petBirthdayInput.getText().toString().trim()));

                    //DATE IS VALID AND PET NAME IS NOT EMPTY - Save Pet to Database and dismissDialog
                    if (isValidDate == 1) {
                        savePetToDB(v);
                        addPet_DialogBox.dismiss();

                        //Date is not in the valid format
                    } if (isValidDate ==0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid date MMDDYYYY", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();

                        // Date is not before today (i.e Pet is not born yet)
                    } if (isValidDate ==2) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Birthday must be before today.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, -10);
                        toast.show();
                    }
                } // END BIRTHDAY VALIDATION - Name and Birthday Not Empty

                // if birthday or name is empty
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "You must enter a name and birthday.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, -10);
                    toast.show();
                }
            }

        });  // END ON CLICK FOR SAVE BUTTON


        //When image button in AddPetPopup is selected Open the Gallery
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(ListActivity.this);
            }
        });

        // if there is a non null bundled attached to the intent, User is editing the pet info
        if (bundle != null) {

            //the pets index location in the arraylist.
            petIndex = bundle.getInt("petIndex");
            petID_forEditingPet = bundle.getInt("petID");

            //Create a local pet object from fields in the database for the petID that
            //was passed to this activity via a bundle;
            Pet pet = databaseHandler.getPet(petID_forEditingPet);

            if (databaseHandler.getPetWeight(petID_forEditingPet) != null) {
                pet.setWeight(databaseHandler.getPetWeight(petID_forEditingPet));
            }


            //Set the interactive values in the add pet popup to the values from
            //the local pet object which was pulled from Database
            petNameInput.setText(pet.getName());
            petBirthdayInput.setText(pet.getBirthdayString());

            //If a pets weight was previously entered, set the weight input to this value
            if (pet.getWeight() != null) {
                petWeightInput.setText(pet.getWeight());
            }


            //Image values still pulled from datbase
            petImageString_bundle_fromRecycleAdapter = bundle.getString("petImageString");
            petImageByte_bundle_fromRecycleAdapter = bundle.getByteArray("petImageByte");


            if (bundle.getByteArray("petImageByte") != null) {
                imageButton.setImageBitmap(UtilMethods.bitmapFromByte(pet.getImageBYTE()));
            }
        }

        //Show the dialogBox
        addPet_DialogBox.show();

    } // END ADD PET POPUP


    //***************************************************************************
    // savePetToDB()                                                           //
    // Save the data from the EditText inputs into a database pet file.        //
    //***************************************************************************
    private void savePetToDB(View v) {
        //Create a new pet to input into database
        Pet pet = new Pet();

        //trim and capitalize inputs and refactor
        String newPetData = UtilMethods.capitalizeFirstLetter(petNameInput.getText().toString().trim());
        String newBirthdayData = petBirthdayInput.getText().toString().trim();

        //set mandatory pet Information from EditText Input fields.
        pet.setName(newPetData);
        pet.setBirthdayString(newBirthdayData);

        //if user has entered a weight for the pet, set the weight of the pet we are about to
        // save to the database.
        if (petWeightInput != null) {
            pet.setWeight(petWeightInput.getText().toString().trim());
            Log.d("PetWeight", pet.getWeight());
        }

        //WHEN USER WAS EDITING THE PET
        //TODO: Decide if i actually need this
        //If an ImageURI and a Byte Array were passed to this activity via a bundle. (editing pet)
        //Set the pet we are about to save to the DBs byte array and image URI fields
        //to what was passed in.
        if (petImageString_bundle_fromRecycleAdapter != null) {
            pet.setImageURI(petImageString_bundle_fromRecycleAdapter);
            pet.setImageBYTE(petImageByte_bundle_fromRecycleAdapter);
        }

        //TODO make into a function for list activity and notes activity
        //If an image was chosen to go with this pet profile, link the byteArray and the ImageURI
        // of the image chosen into the pet we are about to save to the database.
        //  This works for editing an existing photo as well as when creating a brand new pet profile
        //  and choosing a photo for it.

        if (imageResult) {
            //get the URI of the image chosen
            pet.setImageURI(croppedURI.toString());

            //get the imagePath from the image chosen (onActivityResult) and convert it to a byteArray
            createdByteImage = UtilMethods.byteImageFromPath(imagePathFromCropResult);

            //Use the byte array created above and link it to the pet object we are about to save
            //to the database
            pet.setImageBYTE(createdByteImage);

            //set imageResult to false since this imageResult has been used.
            imageResult = false;
        }

        //FINAL STEP FOR CREATING A NEW PET - using pet created from steps above
        //If there was no bundle, this pet is not being edited, it is a brand new pet.
        //ENTER BRAND NEW PET INTO DATABASE
        if (bundle == null) {

            //add the brand new pet to the database
            databaseHandler.addPet(pet);

            //TODO: is this in the wrong spot?
            //notify the recycleView adapter which links the arraylist to the viewr
            //that the data has changed.
            recyclerViewAdapter.notifyDataSetChanged();

            //add this brand new pet into the list linked to the recycleViewer
            petListForRecycleViewer.add(pet);

            //get the petID randomly created by the database helper.
            pet.setId(databaseHandler.getLastId());

            //close the databaseHandler
            databaseHandler.close();
        } //END ADD BRAND NEW PET TO DATABASE


        //A BUNDLE WAS FOUND - UPDATE EXISTING PET.
        else {

            //Name is set(changed or not), Birthday is Set(changed or not)
            // Set the pet id of local pet object to locate the pet in the database for editing
            pet.setId(petID_forEditingPet);

            //Officially update the pet in the database using the accumulated local pet object
            //to this point
            databaseHandler.updatePet(pet);

            if (pet.getWeight() != null) {
                databaseHandler.updatePetWeight_in_Database(pet);
            }


            //let the recycleView adapter which links the arraylist to the recycleViewer know
            //that an element of the list has changed.
            recyclerViewAdapter.notifyDataSetChanged();

            //close the database
            databaseHandler.close();

            //update the pet in the arraylist for the recycleViewer using the index number obtained
            //from the recycleAdapter (bundle),
            petListForRecycleViewer.set(petIndex, pet);

        }
        //determineVisibility_ofAddFirstPetImage();

    } //END SAVE/UPDATE PET TO DATABASE FUNCTION

    //***************************************************************************
    // openGallery() & OnActivityResult()                                      //
    // open the gallery. Make croppedURI from the result. Set the local image  //
    // button to the selected cropped image. get the file path of the cropped  //
    // image and name it imagePathFromCrop result. Dismiss local dialog if     //
    // back button is pressed.                                                 //
    //***************************************************************************
    public void openGallery(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(200, 200)
                .start(activity);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == 0) {
            addPet_DialogBox.dismiss();
            Log.d("resultcode", String.valueOf(resultCode));

        } else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                croppedURI = result.getUri();
                Log.d("result", String.valueOf(resultCode));


                if (resultCode == RESULT_OK) {

                    Log.d("result", String.valueOf(resultCode));


                    imageButton.setImageURI(croppedURI);

                    //  File myFilePath = new File(croppedURI.getPath());
                    //myFilePath.getAbsolutePath();

                    //imagePathFromCropResult = myFilePath.getAbsolutePath();

                    imagePathFromCropResult = UtilMethods.getCropResultAbsolutePathString(croppedURI);
                    imageResult = true;

                    Log.d("croppedURI1", imagePathFromCropResult);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d("result", "error");

                }

            }
        }
    }

    //***************************************************************************
    //  determineVisibility_ofAddFirstPetImage()                               //
    // check the number of rows (pet profiles) in the database. if there are   //
    // none, the addFirstPet image to be visible                               //
    //***************************************************************************
    public void determineVisibility_ofAddFirstPetImage() {
        if (databaseHandler.getCount() == 0) {
            addFirstPet.setVisibility(View.VISIBLE);
            Log.d("zero", "databaseHandler count is zero");
        } else {
            addFirstPet.setVisibility(View.INVISIBLE);
        }
    }//END DETERMINE VISABILITY


} //END (THIS)- LIST ACTIVITY













