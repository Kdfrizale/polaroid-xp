package frizzell.flores.polaroidxp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;



public class Polaroid_XP extends AppCompatActivity {
    //Processes values
    private static final int REQUEST_IMAGE_CAPTURE =1;
    private static final int CAMERA_RESULT = 2;

    //values saved/changed in Bundle
    private boolean picture_border_state;
    private String picture_custom_message;

    //value names saved in Bundle
    private static final String saved_bundle_picture_border_state = "picture_border_state";
    private static final String saved_bundle_picture_custom_message = "picture_custom_message";

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polaroid__xp);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
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

        //Guarantee options that are necessary here are always saved and retrieved per user. NO MATTER WHAT!
        //also to save the value of the photo path just in case.
        if(savedInstanceState != null){
            picture_border_state = savedInstanceState.getBoolean(saved_bundle_picture_border_state, true);
            picture_custom_message = savedInstanceState.getString(saved_bundle_picture_custom_message);
        }
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

    //Saves the value in the bundle for next time app is accessed
    @Override
    protected void onSaveInstanceState(Bundle savingInstanceState){
        super.onSaveInstanceState(savingInstanceState);

        savingInstanceState.putBoolean(saved_bundle_picture_border_state, picture_border_state);
        savingInstanceState.putString(saved_bundle_picture_custom_message, picture_custom_message);
    }

    //Restores the value in the bundle for the next time app is accessed. Just in case app is force closed.
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            picture_border_state = savedInstanceState.getBoolean(saved_bundle_picture_border_state, true);
            picture_custom_message = savedInstanceState.getString(saved_bundle_picture_custom_message);
        }
        //add image view here if it is to be included
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap) extras.get("data");

            mImageView.setImageBitmap(bmp);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
