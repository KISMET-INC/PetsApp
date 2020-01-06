package com.kismet.petsapp.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kismet.petsapp.Activities.PhotoView;
import com.kismet.petsapp.Data.DatabaseHandler;
import com.kismet.petsapp.Model.Pet;
import com.kismet.petsapp.R;

import java.util.ArrayList;

public class MY_RecycleViewAdapter2 extends RecyclerView.Adapter<MY_RecycleViewAdapter2.MY_ViewHolder> {

    public Pet pet;
    private ArrayList<String> entryPositionList;
    private LayoutInflater layoutInflater;
    private MY_ItemClickListener clickListener;

    private int position1;
    private String record;
    private DatabaseHandler db;


    public MY_RecycleViewAdapter2(Context context, ArrayList<String> entryPositionList, Pet pet) {
        this.layoutInflater = LayoutInflater.from(context);
        this.entryPositionList = entryPositionList;
        this.pet = pet;
        db = new DatabaseHandler(context);

    }


    @Override
    public MY_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_row_for_records, null);
        return new MY_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MY_ViewHolder holder, int position) {
        record = entryPositionList.get(position);
        Log.d("MyRecycle", record);
        holder.myTextView.setText(record);
        position1 = position;

    }

    @Override
    public int getItemCount() {
        return entryPositionList.size();
    }

    // allows clicks events to be caught
    void setClickListener(MY_ItemClickListener MYItemClickListener) {
        this.clickListener = MYItemClickListener;
    }

/*    // convenience method for getting data at click position
    PetRecord getRecord(int id) {

        return entryPositionList.get(id);
    }*/

    // parent activity will implement this method to respond to click events
    public interface MY_ItemClickListener {
        void onItemClick(View view, int position);


    }

    public class MY_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        Button deleteRecordButton;
        TableRow recordName;
        //  TextView recordDate;

        MY_ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.record_recyclerRow_TextView);
            deleteRecordButton = itemView.findViewById(R.id.records_activity_delete_Button);
            recordName = itemView.findViewById(R.id.records_activity_TextView_TableRow);
            //     recordDate = itemView.findViewById(R.id.recordDate_recyclerRow_TextView);


            // itemView.setOnClickListener(this);
            deleteRecordButton.setOnClickListener(this);
            recordName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int string = getAdapterPosition();
            String name = entryPositionList.get(getAdapterPosition());

            switch (view.getId()) {
                case R.id.records_activity_TextView_TableRow:

                    // if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());

                    Intent intent = new Intent(view.getContext(), PhotoView.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("recordID", getAdapterPosition());
                    bundle.putInt("petID", pet.getId());
                    Log.d("recordIDposition", Integer.toString(getAdapterPosition()));

                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);

                    ((Activity) view.getContext()).finish();


                    // Log.d("RecycleAdapter", getAdapterPosition());
                    break;

                case R.id.records_activity_delete_Button:

                    db.deletPetRecord(name, pet.getId());
                    entryPositionList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    Log.d("deletePet_Recycler", name);
                    break;

            }
        }
    }
}