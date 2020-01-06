package com.kismet.petsapp.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kismet.petsapp.Activities.AddPetRecordActivity;
import com.kismet.petsapp.Activities.ListActivity;
import com.kismet.petsapp.Activities.PetNotesActivity;
import com.kismet.petsapp.Data.DatabaseHandler;
import com.kismet.petsapp.Model.Pet;
import com.kismet.petsapp.R;
import com.kismet.petsapp.Util.UtilMethods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    public List<Pet> petListForPosition;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private UtilMethods util;

    private DatabaseHandler db;

    private byte[] imageByte;
    private String ageInYears;

    ListActivity listActivity = new ListActivity();



    public RecyclerViewAdapter(Context context, List<Pet> petListForPosition) {
        this.context = context;
        this.petListForPosition = petListForPosition;
        db = new DatabaseHandler(context);


    }

    public static void makeOptionalFitsSystemWindows(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                // We need to use getMethod() for makeOptionalFitsSystemWindows since both View
                // and ViewGroup implement the method
                Method method = view.getClass().getMethod("makeOptionalFitsSystemWindows");
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                method.invoke(view);
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "Could not find method makeOptionalFitsSystemWindows. Oh well...");
            } catch (InvocationTargetException e) {
                Log.d(TAG, "Could not invoke makeOptionalFitsSystemWindows", e);
            } catch (IllegalAccessException e) {
                Log.d(TAG, "Could not invoke makeOptionalFitsSystemWindows", e);
            }
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        makeOptionalFitsSystemWindows(view);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {


        Uri uri = Uri.parse("android.resource://com.segf4ult.test/" + R.drawable.pencil);

        Pet pet = petListForPosition.get(position);


        long years = UtilMethods.ageInYears(pet.getBirthdayLong());
        long months = UtilMethods.ageInMonths(pet.getBirthdayLong());
        long days = UtilMethods.ageInDays(pet.getBirthdayLong());

        Log.d("birthday", pet.getName());


        imageByte = pet.getImageBYTE();


        if (imageByte != null) {
            byte[] imageBYTE = pet.getImageBYTE();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBYTE, 0, imageBYTE.length);
            holder.petImageIcon.setImageBitmap(bitmap);

            Log.d("PetName", pet.getName());
        } else {

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.paw);
            holder.petImageIcon.setImageBitmap(icon);
        }


        String prettyDate = UtilMethods.prettyDate(pet.getBirthdayString());
        ageInYears = UtilMethods.prettyAgeString(years, months);


        holder.petNameTextView.setText(pet.getName());
        holder.petBirthdayTextView.setText(prettyDate);
        holder.age.setText(ageInYears);


    }

    @Override
    public int getItemCount() {
        return petListForPosition.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView petNameTextView;
        public TextView petBirthdayTextView;
        public Button editButton;
        public Button deleteButton;
        public TextView age;
        public ImageButton imageButton;
        public ImageButton petImageIcon;
        public RelativeLayout cardView;
        public Button recordsButton;



        public int id;

        public ViewHolder(View view, Context cxt) {
            super(view);

            context = cxt;

            petImageIcon = view.findViewById(R.id.petImage);
            imageButton = view.findViewById(R.id.imageButton);
            petNameTextView = view.findViewById(R.id.name);
            petBirthdayTextView = view.findViewById(R.id.birthdayString);
            deleteButton = view.findViewById(R.id.deleteButton);
            recordsButton = view.findViewById(R.id.records_button_cardView);

            age = view.findViewById(R.id.age);
            cardView = view.findViewById(R.id.cardView_layout);



            deleteButton.setOnClickListener(this);
            recordsButton.setOnClickListener(this);
            petImageIcon.setOnClickListener(this);
            cardView.setOnClickListener(this);




        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.records_button_cardView:
                    int position = getAdapterPosition();
                    Pet pet = petListForPosition.get(position);

                    Intent intent = new Intent(v.getContext(), AddPetRecordActivity.class);
                    Bundle bundle = new Bundle();
                    //  bundle.putInt("recordID", getAdapterPosition());
                    bundle.putInt("petID", pet.getId());
                    //ListAct(RecycleViewAdapter = 1, PetNotesActivity = 2,
                    bundle.putInt("where", 1);

                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                    break;

                case R.id.deleteButton:
                    position = getAdapterPosition();
                    pet = petListForPosition.get(position);
                    deleteItem(pet.getId());


                        Log.d("Item 3id :", String.valueOf(pet.getId()));

                    break;

                case R.id.petImage:
                case R.id.cardView_layout:
                    position = getAdapterPosition();
                    pet = petListForPosition.get(position);

                    intent = new Intent(v.getContext(), PetNotesActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("petID", pet.getId());
                  /*  extras.putString("petName", pet.getName());
                    extras.putString("petAge", ageInYears);
                    //Log.d("RecycleViewAdapter", pet.getNotes());

                    if (pet.getImageBYTE() != null) {
                        extras.putByteArray("petImageByteArray", pet.getImageBYTE());
                    }

                    if (pet.getNotes() != null) {
                        extras.putString("petNotes", pet.getNotes());

                    }
*/
                    intent.putExtras(extras);
                    v.getContext().startActivity(intent);

                    Log.d("Item 3id :", String.valueOf(pet.getId()));

                    break;

            }

        }


        public void deleteItem(final int id) {
            //create alert dialog
            alertDialogBuilder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_dialog, null);

            Button noButton = view.findViewById(R.id.noButton);
            Button yesButton = view.findViewById(R.id.yesButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();

            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //DatabaseHandler db = new DatabaseHandler(context);
                   Log.d("Item 1:", String.valueOf(db.getCount()));

                    Log.d("position:", String.valueOf(getAdapterPosition()));

                    db.deletePetEverythig(id);
                    petListForPosition.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    db.close();

                    dialog.dismiss();

                    Log.d("Item 2 :", String.valueOf(db.getCount()));
                    Log.d("Item id :", String.valueOf(id));


                }
            });

        }

        public void editPet(final Pet pet) {

            listActivity = ListActivity.getInstance();

            Intent intent = listActivity.getIntent();
            intent.putExtra("petName", pet.getName());
            intent.putExtra("petBirthday", pet.getBirthdayString());
            intent.putExtra("petImageString", pet.getImageURI());
            intent.putExtra("petImageByte", pet.getImageBYTE());
            intent.putExtra("petID",pet.getId());
            intent.putExtra("petIndex",getAdapterPosition());


            Log.d("adapterID", String.valueOf(pet.getImageURI()));

            context.startActivity(intent);
            listActivity.addPetPopupDialog();

            intent.removeExtra("petName");
            intent.removeExtra("petBirthday");
            intent.removeExtra("petImageString");
            intent.removeExtra("petImageByte");
            intent.removeExtra("petID");
            intent.removeExtra("petIndex");
        }
    }
}


//TODO: Search Only for jPGS