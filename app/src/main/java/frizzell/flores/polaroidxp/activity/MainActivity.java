package frizzell.flores.polaroidxp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import frizzell.flores.polaroidxp.asynctask.SaveTiffTask;
import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.singleton.TiffFileFactory;
import frizzell.flores.polaroidxp.utils.ImageHelper;
import frizzell.flores.polaroidxp.utils.StorageHelper;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;

    ImageView mImageView;
    File mWorkingImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polaroid__xp);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        PreferenceManager.setDefaultValues(this, R.xml.settings_page, false);
        mImageView = (ImageView) findViewById(R.id.returnedImageView);

        Fabric.with(this, new Crashlytics());

        setUpButtons();

        checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        StorageHelper.createDirectoryTrees(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO switch to SharedPreferenceListen so we only have to update when user edits fields
        TextView captionTextView = (TextView) findViewById(R.id.captionTextView);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String captionPref = sharedPref.getString(getResources().getString(R.string.Signed_Photo_Key), getResources().getString(R.string.default_caption));
        captionTextView.setText(captionPref);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsPageActivity.class));
                return true;
            case R.id.action_gallery:
                startActivity(new Intent(this, GalleryActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    if(mWorkingImageFile.exists()){
                        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),getString(R.string.filterImagesFolder));
                        File filter = new File(storageDir,"1.jpg");//TODO change this to function getChosenFilter()
                        TiffFileFactory.Options aParam = new TiffFileFactory.Options(mWorkingImageFile,filter);
                        //SaveTiffTask.SaveTiffTaskParam aParam = new SaveTiffTask.SaveTiffTaskParam(getString(R.string.tiffImagesFolder),mWorkingImageFile,filter);
                        SaveTiffTask createImageTask = new SaveTiffTask();
                        createImageTask.execute(aParam);

                    }
                }
        }
    }

    private void checkPermission(Context context, String aPermission){
        Permissions.check(context, aPermission, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.d("PolaroidXP", "PERMISSIONS GRANTED");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Log.e("PolaroidXP", "PERMISSION DENIED, exiting app");
                finish();
                System.exit(0);
            }
        });
    }

    private void setUpButtons(){
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
            public void onClick(View view) { startActivity(new Intent(MainActivity.this, GalleryActivity.class));
            }});
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                mWorkingImageFile = ImageHelper.createImageFile(getString(R.string.jpegImagesFolder),".jpg");
            } catch (IOException ex) {
                Log.e("PolaroidXP", "IO exception", ex);
                checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
            if (mWorkingImageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "frizzell.flores.polaroidxp", mWorkingImageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE);
            }
        }
        else{
            Snackbar.make(findViewById(R.id.polaroid_coorLayout), "ERROR: No Camera App found", Snackbar.LENGTH_LONG).show();
        }
    }


}