package frizzell.flores.polaroidxp.activity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import frizzell.flores.polaroidxp.OnGestureTouchListener;
import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.asynctask.LoadTiffImageTask;
import frizzell.flores.polaroidxp.utils.TiffHelper;

public class FullscreenImageActivity extends AppCompatActivity {

    ImageView mImageView;
    File mTiffImage;
    Bitmap mImageBitmap;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Fullscreen", "Creating activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        mImageView = (ImageView) findViewById(R.id.fullScreenImg);
        mImageView.setOnTouchListener(new OnGestureTouchListener(this) {
            @Override
            public void onLongClick(){
                Log.e("TOUCH","LONG TOUCH");
                Snackbar.make(mImageView, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            @Override
            public void onDoubleClick() {
                unFilterImage(mTiffImage);
            }
            @Override
            public void onSwipeRight() {
                //#Useful
            }
        });

        //Async Task can go here|| Async Start
        String passedImageName = (String) getIntent().getExtras().get("ImageFileName");
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),getString(R.string.tiffImagesFolder));
        File tempFile = new File(storageDir, passedImageName + ".tif");
        if(!tempFile.exists()){
            //TODO notify user that the image could not be found
            //Log error
            finish();
        }
        mTiffImage = tempFile;
        //TODO read filtered status of picture
        //Show filtered or unfilterd layer--asynctask Load
        boolean filteredStatus = TiffHelper.isFiltered(mTiffImage);

        //TODO show image with AsynctaskLoad
        //changeImage(tempFile,TiffHelper.TIFF_FILTER_LAYER);
        changeImage(mTiffImage, (filteredStatus) ? TiffHelper.TIFF_BASE_LAYER : TiffHelper.TIFF_FILTER_LAYER);
        //Async End

    }

    private boolean unFilterImage(File tiffImage){
        //re-save the tiff image with the isFiltered property set to ---(true for now)
        //show fancy transition
        //display bitmap of base image
        return changeImage(tiffImage, TiffHelper.TIFF_BASE_LAYER);
    }

    private boolean changeImage(File tiffImage, int tiffLayer){
        if(tiffImage.exists()){
            mTiffImage = tiffImage;
            LoadTiffImageTask.LoadTiffTaskParam aParam = new LoadTiffImageTask.LoadTiffTaskParam(tiffImage, tiffLayer);
            LoadTiffImageTask loadTiffTask = new LoadTiffImageTask(mImageView);
            loadTiffTask.execute(aParam);


            //mImageBitmap = TiffHelper.getLayerOfTiff(mTiffImage,tiffLayer);
            //setmImageView(mImageBitmap);
            return true;
        }
        else{
            Log.e("Fullscreen","File did not exist");
            return false;
        }
    }

    private void setmImageView(Bitmap bitmap){
        mImageView.setImageBitmap(bitmap);
    }

}
