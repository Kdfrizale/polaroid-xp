package frizzell.flores.polaroidxp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    //Processes values
    private static final int REQUEST_CODE_IMAGE_CAPTURE =1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;

//    //values saved/changed in Bundle
//    private boolean picture_border_state;
//    private String picture_custom_message;
//
//    //value names saved in Bundle
//    private static final String saved_bundle_picture_border_state = "picture_border_state";
//    private static final String saved_bundle_picture_custom_message = "picture_custom_message";

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polaroid__xp);
        setSupportActionBar((Toolbar)findViewById(R.id.main_toolbar));
        PreferenceManager.setDefaultValues(this, R.xml.settings_page, false);
        mImageView = (ImageView) findViewById(R.id.returnedImageView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab_camera = findViewById(R.id.floatingActionButtonCamera);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        FloatingActionButton fab_gallery = findViewById(R.id.floatingActionButtonGallery);
        fab_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                PermissionsHelper.askForPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE,REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        });

//        //Guarantee options that are necessary here are always saved and retrieved per user. NO MATTER WHAT!
//        //also to save the value of the photo path just in case.
//        if(savedInstanceState != null){
//            picture_border_state = savedInstanceState.getBoolean(saved_bundle_picture_border_state, true);
//            picture_custom_message = savedInstanceState.getString(saved_bundle_picture_custom_message);
//        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        //TODO switch to SharedPreferenceListen so we only have to update when user edits fields
        TextView captionTextView = (TextView) findViewById(R.id.captionTextView);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String captionPref = sharedPref.getString(getResources().getString(R.string.Signed_Photo_Key),getResources().getString(R.string.default_message));
        captionTextView.setText(captionPref);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar willgit
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsPageActivity.class));
                return true;
            case R.id.action_testsettings:
                startActivity(new Intent(this, TestSettingsActivity.class));
                return true;
            case R.id.action_gallery:
                startActivity(new Intent(this, GalleryActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    //Saves the value in the bundle for next time app is accessed
//    @Override
//    protected void onSaveInstanceState(Bundle savingInstanceState){
//        super.onSaveInstanceState(savingInstanceState);
//
//        savingInstanceState.putBoolean(saved_bundle_picture_border_state, picture_border_state);
//        savingInstanceState.putString(saved_bundle_picture_custom_message, picture_custom_message);
//    }

//    //Restores the value in the bundle for the next time app is accessed. Just in case app is force closed.
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState){
//        super.onRestoreInstanceState(savedInstanceState);
//        if(savedInstanceState != null){
//            picture_border_state = savedInstanceState.getBoolean(saved_bundle_picture_border_state, true);
//            picture_custom_message = savedInstanceState.getString(saved_bundle_picture_custom_message);
//        }
//    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = StorageHelper.createImageFile();
            } catch (IOException ex) {
                Log.e("PolaroidXP", "IO exception", ex);
                PermissionsHelper.askForPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE,REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "frizzell.flores.polaroidxp", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE);
            }
        }
        Snackbar.make(findViewById(R.id.polaroid_coorLayout),"ERROR: No Camera App found",Snackbar.LENGTH_LONG).show();
    }

//    private boolean isPermissionAllowed(String permissionToCheck){
//        return ContextCompat.checkSelfPermission(this,permissionToCheck) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void askForPermission(String permissionToAskFor, int requestCode) {
//        if (!isPermissionAllowed(permissionToAskFor)) {
//            ActivityCompat.requestPermissions(this, new String[]{permissionToAskFor}, requestCode);
//        }
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.e("PolaroidXP", "onRequestPermission received");
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("PolaroidXP", "PErmission received");
                } else {
                    Log.e("PolaroidXP", "PErmission not received");
                    // TODO Critical permission denied
                    // TODO Add popup dialog to use explaining this app needs the ability to save images and present a link
                    // TODO to the settings page of the app's permissions
                }
                return;
            }
        }
    }
}
