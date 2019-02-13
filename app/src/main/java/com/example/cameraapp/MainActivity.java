package com.example.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button captureBtn; // the button that will capture a photo
    private ImageView imgV; // an image that will appear on the device once a photo is taken
    private String pathToFile; // path of the image in directory
    private DrawerLayout drawer; // for the drawer menu


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); // preventing screenshots
        setContentView(R.layout.activity_main);

        // ----------------------- Navigation Drawer Implementations ---------------------------------------------------------------
        // The tool bar or navigation to add friend implementations
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layiout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // This line of code stops all other activities, a goal must be met inorder to proceed
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // This code below loads the add friend fragment

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        //--------------------------- End of Navigation Drawer Implementations ----------------------------------------------------

// -------------------------------------------- CAMERA IMPLEMENTATIONS -----------------------------------
/*
        captureBtn = findViewById(R.id.captureBtn);
        imgV = findViewById(R.id.image);

        // for the camera
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPictureTakerAction();
            }
        });
        */
    }

// ----------------------- END of CAMERA IMPLEMENTATIONS ---------------------------------------------------------------

    // ---------------------------------- Navigation Drawer Methods ----------------------------------------


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_add:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                        new AddFragment()).commit();
                break;
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                        new HomeFragment()).commit();
                break;
        }

        drawer.closeDrawer((GravityCompat.START));
        // false means no items will be selected
        return true;
    }

    @Override
    public void onBackPressed() {
        // after pressing the back key, instead of leaving the menu, we just close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    // ------------------------------- End of Navigation Drawer Methods ---------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                imgV.setImageBitmap(bitmap);
            }
        }
    }

    private void dispatchPictureTakerAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath(); // path to the photo file
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.provider.cartoonprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }

        }
    }

    // Creating the file of the photo
    private File createPhotoFile() {
        String pattern = "yyyy-MM-dd";
        String name = new SimpleDateFormat(pattern).format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("myLog", "Excep: " + e.toString());
        }

        return image;

    }
}
