package frizzell.flores.polaroidxp;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Polaroid_XP extends AppCompatActivity {
    //Processes values
    private static final int REQUEST_IMAGE_CAPTURE =1;
    private static final int CAMERA_RESULT = 2;

    //values saved/changed in Bundle
    private String mCurrentPhotoPath;
    private boolean picture_border_state;
    private String picture_custom_message;

    //value names saved in Bundle
    private static final String saved_bundle_mCurrentPhotoPath = "mCurrentPhotoPath";
    private static final String saved_bundle_picture_border_state = "picture_border_state";
    private static final String saved_bundle_picture_custom_message = "picture_custom_message";

    private Uri imageUri;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polaroid__xp);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //mImageView = (ImageView) findViewById(R.id.photo_imageView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        FloatingActionButton fab_camera = findViewById(R.id.floatingActionButton2);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(it,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe){
                    //Intent intenttest = new Intent(Polaroid_XP.this, TestSettingsActivity.class);
                    //Intent chooser = Intent.createChooser(it, "select camera");
                    //Intent intent = new Intent(Intent.ACTION_SEND);
                    startActivityForResult(it,REQUEST_IMAGE_CAPTURE);
                }
//                Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
//                it.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(photo));
//                imageUri = Uri.fromFile(photo);
//                startActivityForResult(it, CAMERA_RESULT);
            }
        });

        //Guarantee options that are necessary here are always saved and retrieved per user. NO MATTER WHAT!
        //also to save the value of the photo path just in case.
        if(savedInstanceState != null){
            mCurrentPhotoPath = savedInstanceState.getString(saved_bundle_mCurrentPhotoPath);
            picture_border_state = savedInstanceState.getBoolean(saved_bundle_picture_border_state, true);
            picture_custom_message = savedInstanceState.getString(saved_bundle_picture_custom_message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_polaroid__x, menu);
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
                //User chose Settings in the menu
                Intent intent = new Intent(this, Settings_Page.class);
                startActivity(intent);
                return true;
            case R.id.action_testsettings:
                //User chose Settings in the menu
                Intent intenttest = new Intent(this, TestSettingsActivity.class);
                startActivity(intenttest);
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Must be changed from a temp file as it will cause headaches and too many problem if we want to access the same picture again.
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void sendTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//if statement avoids crash if no app can handle request
            File photoFile;
            try{
                photoFile = createImageFile();
            } catch(IOException ex){
                return;
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this, "frizzell.flores.polaroidxp",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Saves the value in the bundle for next time app is accessed
    @Override
    protected void onSaveInstanceState(Bundle savingInstanceState){
        super.onSaveInstanceState(savingInstanceState);

        savingInstanceState.putString(saved_bundle_mCurrentPhotoPath, mCurrentPhotoPath);
        savingInstanceState.putBoolean(saved_bundle_picture_border_state, picture_border_state);
        savingInstanceState.putString(saved_bundle_picture_custom_message, picture_custom_message);
    }

    //Restores the value in the bundle for the next time app is accessed. Just in case app is force closed.
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            mCurrentPhotoPath = savedInstanceState.getString(saved_bundle_mCurrentPhotoPath);
            picture_border_state = savedInstanceState.getBoolean(saved_bundle_picture_border_state, true);
            picture_custom_message = savedInstanceState.getString(saved_bundle_picture_custom_message);
        }
        //add image view here if it is to be included
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//        if (resultCode == RESULT_OK){
////            Bundle extras = data.getExtras();
////            Bitmap bmp = (Bitmap) extras.get("data");
////
////            mImageView = (ImageView) findViewById(R.id.ReturnedImageView);
////            mImageView.setImageBitmap(bmp);
//        }
//    }
}
