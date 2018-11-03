package frizzell.flores.polaroidxp;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

public class FullscreenImageActivity extends AppCompatActivity {

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        mImageView = (ImageView) findViewById(R.id.fullScreenImg);

        File passedImage = (File) getIntent().getExtras().get("ImageFile");
        if(passedImage.exists()){
            Log.e("Fullscreen","Image received");
            mImageView.setImageBitmap(BitmapFactory.decodeFile(passedImage.getAbsolutePath()));
        }
        else{
            Log.e("Fullscreen","File did not exist");
        }
    }
}
