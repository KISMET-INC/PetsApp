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

import com.kismet.petsapp.Data.DatabaseHandler;
import com.kismet.petsapp.R;


public class MainActivity extends Activity {
    private DatabaseHandler db;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    public static final int REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);
        db = new DatabaseHandler(this);
       verifyPermissions();


        if(permissionsVerified()){
           loader();
       } else {
           verifyPermissions();
       }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

