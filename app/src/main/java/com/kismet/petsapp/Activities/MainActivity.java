package com.kismet.petsapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kismet.petsapp.R;

import static com.kismet.petsapp.Util.Constants.REQUEST_CODE;


public class MainActivity extends Activity {

    //TODO : Find out why certain images dont work
    //TODO: Limit images to be selected in Gallery
    //TODO: Allow reordering of Recycle View items
    //TODO: Figure out how to make AutoBackup work
    //TODO: Make it so image cropper doesnt go too far back when back button is pressed.

    //Declare Progress bar elements
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        //Run verify permissios function
        verifyPermissions();

        //if permissions verified, continue loading
        if(permissionsVerified()){
           loader();

       } else {
            //if permissions not verified, restart function. Permissions must be granted
            //to move forward in program
           verifyPermissions();
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void verifyPermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

    }

    private boolean permissionsVerified() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        return ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permissions[2]) == PackageManager.PERMISSION_GRANTED;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        if (permissionsVerified()) {
            loader();
        }
    }


   public void loader() {
       progressBar = findViewById(R.id.progressBar3);

       new Thread(new Runnable() {
           @Override
           public void run() {
               while (progressStatus < 100) {
                   progressStatus++;
                   android.os.SystemClock.sleep(40);
                   handler.post(new Runnable() {
                       @Override
                       public void run() {
                           progressBar.setProgress(progressStatus);
                       }
                   });

               }
               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       startActivity(new Intent(MainActivity.this, ListActivity.class));
                       MainActivity.this.finish();

                   }
               });
           }
       }).start();

   }

}

