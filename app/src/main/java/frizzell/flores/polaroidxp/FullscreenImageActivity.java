package frizzell.flores.polaroidxp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

public class FullscreenImageActivity extends AppCompatActivity {

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Fullscreen", "Creating activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        mImageView = (ImageView) findViewById(R.id.fullScreenImg);

        File passedImage = (File) getIntent().getExtras().get("ImageFile");
        Log.e("fullscreen","File Name Passed: " + passedImage.getAbsolutePath());
        if(passedImage.exists()){
            Log.e("Fullscreen","Image received");
            Matrix matrix = getOrientationMatrix(passedImage.getAbsolutePath());
            Log.e("fullscreen","Trying to make bitmap to display");
            //TODO switch this to load the .tif file instead
            Bitmap bitmapSelectedImage = BitmapFactory.decodeFile(passedImage.getAbsolutePath());
            bitmapSelectedImage = Bitmap.createBitmap(bitmapSelectedImage,0,0,bitmapSelectedImage.getWidth(),bitmapSelectedImage.getHeight(),matrix,true);
            mImageView.setImageBitmap(bitmapSelectedImage);
        }
        else{
            Log.e("Fullscreen","File did not exist");
        }
    }

    private Matrix getOrientationMatrix(String filePath){
        try{
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            Log.d("EXIF initial", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            return matrix;
        }catch (Exception e){
            Log.e("Fullscreen Exif", "Error, returning default orientation matrix");
            e.printStackTrace();
            return new Matrix();
        }
    }
}
