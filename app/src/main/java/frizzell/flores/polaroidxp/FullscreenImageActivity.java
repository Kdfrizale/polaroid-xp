package frizzell.flores.polaroidxp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.beyka.tiffbitmapfactory.Orientation;
import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.File;

public class FullscreenImageActivity extends AppCompatActivity {

    ImageView mImageView;
    File mTiffImage;
    File mPassedJpegImage;
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
                Bitmap bitmapSelectedImage = getLayerOfTiff(mTiffImage,0);
                Matrix matrix = StorageHelper.getOrientationMatrix(mPassedJpegImage.getAbsolutePath());
                mImageBitmap = Bitmap.createBitmap(bitmapSelectedImage,0,0,bitmapSelectedImage.getWidth(),bitmapSelectedImage.getHeight(),matrix,true);
                setmImageView(mImageBitmap);
            }

        });

        //Async Task can go here|| Start
        File passedImage = (File) getIntent().getExtras().get("ImageFile");
        mPassedJpegImage = passedImage;//TODO maybe switch all these passedImages to the member variable one
        Log.e("fullscreen","File Name Passed: " + passedImage.getAbsolutePath());
        if(passedImage.exists()){
            Log.e("Fullscreen","Image received");
            Matrix matrix = StorageHelper.getOrientationMatrix(passedImage.getAbsolutePath());
            Log.e("fullscreen","Trying to make bitmap to display");
            //TODO switch this to load the .tif file instead
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),getString(R.string.tiffImagesFolder));
            mTiffImage = new File(storageDir, passedImage.getName() + ".tif");


            Bitmap bitmapSelectedImage = getLayerOfTiff(mTiffImage,1);//TODO add a constant int for Filter and base image

            //Bitmap bitmapSelectedImage = BitmapFactory.decodeFile(passedImage.getAbsolutePath());
            mImageBitmap = Bitmap.createBitmap(bitmapSelectedImage,0,0,bitmapSelectedImage.getWidth(),bitmapSelectedImage.getHeight(),matrix,true);
            setmImageView(mImageBitmap);
        }
        else{
            Log.e("Fullscreen","File did not exist");
        }
    }

    //TODO change to javadoc
    //layer =0 for base image, layer =1 for filter
    private Bitmap getLayerOfTiff(File tiffImage, int layer){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        TiffBitmapFactory.decodeFile(tiffImage, options);
        int dirCount = options.outDirectoryCount;
        Log.e("Tiff desc","iamge description: " + options.outImageDescription);
        Log.e("Tiff desc","iamge software: " + options.outSoftware);
        Log.e("Tiff desc","iamge copyright: " + options.outCopyright);
        if(dirCount - 1 <= layer){
            options.inDirectoryNumber = layer;//0 is base image, 1 is filter
            return TiffBitmapFactory.decodeFile(tiffImage,options);
        }
        else{
            return TiffBitmapFactory.decodeFile(tiffImage);
        }
    }

    private void setmImageView(Bitmap bitmap){
        mImageView.setImageBitmap(bitmap);
    }


}
