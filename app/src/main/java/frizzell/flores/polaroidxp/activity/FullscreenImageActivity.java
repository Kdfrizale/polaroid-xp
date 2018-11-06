package frizzell.flores.polaroidxp.activity;

import android.annotation.SuppressLint;
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
                changeImage(mTiffImage,TiffHelper.TIFF_BASE_LAYER);
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

        changeImage(tempFile,TiffHelper.TIFF_FILTER_LAYER);
        //Async End

    }

    private boolean changeImage(File tiffImage, int tiffLayer){
        if(tiffImage.exists()){
            mTiffImage = tiffImage;
            mImageBitmap = TiffHelper.getLayerOfTiff(mTiffImage,tiffLayer);
            setmImageView(mImageBitmap);
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
