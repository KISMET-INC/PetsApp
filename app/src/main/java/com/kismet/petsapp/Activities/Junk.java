package com.kismet.petsapp.Activities;

public class Junk {

   /* public void createPopUpDialog() {

   *//*     Bundle b = new Bundle();
        b = getIntent().getExtras();*//*

        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        petNameInput = (EditText) view.findViewById(R.id.petName);
        petBirthdayInput = (EditText) view.findViewById(R.id.petBirthday);
        saveButton = (Button) view.findViewById(R.id.savePet);
        imageButton = (ImageView) view.findViewById(R.id.imageButton);


    *//*    if (b != null) {
            String petName = b.getString("petName");
            String petBirthday = b.getString("petBirthday");
            String petImageString = b.getString("petImageString");
            byte [] petImageByte = b.getByteArray("petImageByte");

            Uri petImageUri = Uri.parse(petImageString);
            Log.d("image uri",petImageUri.toString());

            petNameInput.setText(petName);
            petBirthdayInput.setText(petBirthday);

            imageByte = petImageByte;
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            imageButton.setImageBitmap(bitmap);

        }
*//*

        dialog = dialogBuilder.create();
        dialog.show();


        *//*    //hard code for test purposes
        //TODO Remove test code
        petNameInput.setText("rhapsody");
        petBirthdayInput.setText("12/12/2009");
*//*

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

    }*/


     /* public void createPopUpDialog() {

         dialogBuilder = new AlertDialog.Builder(this);
         View view = getLayoutInflater().inflate(R.layout.popup, null);
         petNameInput = (EditText) view.findViewById(R.id.petName);
         petBirthdayInput = (EditText) view.findViewById(R.id.petBirthday);
         saveButton = (Button) view.findViewById(R.id.savePet);
         imageButton = (ImageView) view.findViewById(R.id.imageButton);

         dialogBuilder.setView(view);
         dialog = dialogBuilder.create();
         dialog.show();


         *//*    //hard code for test purposes
        //TODO Remove test code
        petNameInput.setText("rhapsody");
        petBirthdayInput.setText("12/12/2009");
*//*

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

    }

    private void savePetToDB(View v) {
        Pet pet = new Pet();

        String newPetData = util.capitalizeFirstLetter(petNameInput.getText().toString().trim());
        String newBirthdayData = petBirthdayInput.getText().toString().trim();
        String imageUriFromGallery = pickedImageUri.toString();

        byte[] createdByteImage = null;

        try {

            //get address of createdByteImage chosen from gallery
            FileInputStream fis = new FileInputStream(imagePathFromCropResult);

            //create image from fis Uri info
            createdByteImage = new byte[fis.available()];
            fis.read(createdByteImage);

            ContentValues values = new ContentValues();
            values.put(Constants.KEY_IMAGE_BYTE, createdByteImage);

            fis.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        //create a JAVA pet object from input and gallery Data
        pet.setName(newPetData);
        pet.setBirthdayString(newBirthdayData);
        pet.setImageURI(imageUriFromGallery);
        pet.setImageBYTE(createdByteImage);

        Log.d("PETBYTE ", pet.getImageBYTE().toString()) ;

        db.addpet(pet);
        db.close();


        Snackbar.make(v, "Pet Added!", Snackbar.LENGTH_LONG).show();
        dialog.dismiss();
        //start a new activity
        startActivity(new Intent(MainActivity.this, ListActivity.class));

    }



    private void openGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,SELECT_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {

            pickedImageUri = data.getData();
            CropImage.activity(pickedImageUri)
                    .setAspectRatio(200,200)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri croppedURI = result.getUri();

            if (resultCode == RESULT_OK) {

                imageButton.setImageURI(result.getUri());
                File myFilePath = new File(croppedURI.getPath());
                myFilePath.getAbsolutePath();
                Log.d("file path", myFilePath.getAbsolutePath().toString());

                imagePathFromCropResult = myFilePath.getAbsolutePath().toString();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
*/
}
