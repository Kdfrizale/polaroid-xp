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
    static final int REQUEST_IMAGE_CAPTURE =1;
    static final int CAMERA_RESULT = 2;
    String mCurrentPhotoPath;
    private Uri imageUri;
//    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polaroid__xp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //mImageView = (ImageView) findViewById(R.id.photo_imageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        FloatingActionButton fab_camera = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
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
                    startActivity(it);
                }
//                Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
//                it.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(photo));
//                imageUri = Uri.fromFile(photo);
//                startActivityForResult(it, CAMERA_RESULT);
            }
        });
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
            File photoFile = null;
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
