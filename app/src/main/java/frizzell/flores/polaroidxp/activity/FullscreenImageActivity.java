package frizzell.flores.polaroidxp.activity;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.io.File;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.application.App;
import frizzell.flores.polaroidxp.asynctask.LoadTiffImageTask;
import frizzell.flores.polaroidxp.asynctask.SaveBitmapToCacheTask;
import frizzell.flores.polaroidxp.utils.StorageHelper;
import frizzell.flores.polaroidxp.utils.TiffHelper;

public class FullscreenImageActivity extends AppCompatActivity implements SensorEventListener{

    private ImageView mImageView;
    private File mTiffImage;
    private LruCache<String, Bitmap> mMemoryCache;
    private AlphaAnimation mFadeOut;
    private long mLastUpdate;
    private SensorManager mSensorManager;
    private int SENSOR_TIME_INTERVAL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        mImageView = (ImageView) findViewById(R.id.fullScreenImg);

        File passedImageName = (File) getIntent().getExtras().get("ImageFileName");
        mTiffImage = passedImageName;
        //mTiffImage = TiffHelper.getRelatedTiffFromJpeg(passedImageName);
        if(!mTiffImage.exists()){
            //TODO notify user that the image could not be found
            //Log error
            finish();
        }

        setUpCache();

        loadStartingImage(mTiffImage, (TiffHelper.isUnfiltered(mTiffImage)) ? TiffHelper.TIFF_BASE_LAYER : TiffHelper.TIFF_FILTER_LAYER);

        setUpAnimation();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLastUpdate = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long actualTime = event.timestamp;
            if(getAccelerometer(event) >= 1.7 && actualTime - mLastUpdate > SENSOR_TIME_INTERVAL){
                mLastUpdate = actualTime;
                unFilterImage(mTiffImage);
            }
        }
    }

    private void setUpCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 2;
        Log.e("SIZE OF CACHE", "SIZE OF CACHE IS: "  +Integer.toString(cacheSize));

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) { return bitmap.getByteCount() / 1024; }};
    }

    private void setUpAnimation(){
        mFadeOut = new AlphaAnimation(0,1);
        mFadeOut.setDuration(1700);
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        mImageView.setAnimation(mFadeOut);
        mFadeOut.cancel();
    }

    private void loadStartingImage(File tiffImage, int selectedLayer){
        if(selectedLayer == TiffHelper.TIFF_FILTER_LAYER){
            startLoadTiffTask(tiffImage,selectedLayer);
            startBitmapToCacheTask(tiffImage,TiffHelper.TIFF_BASE_LAYER);
        }
        else{
            startLoadTiffTask(tiffImage,selectedLayer);
        }
    }

    private void startLoadTiffTask(File tiffImage, int selectedLayer){
        LoadTiffImageTask.AsyncResponse asyncResponse = new LoadTiffImageTask.AsyncResponse() {
            @Override
            public void processFinish(Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
            }
        };
        LoadTiffImageTask.LoadTiffTaskParam aParam = new LoadTiffImageTask.LoadTiffTaskParam(tiffImage, selectedLayer);
        LoadTiffImageTask loadTiffTask = new LoadTiffImageTask(asyncResponse, mMemoryCache);
        loadTiffTask.execute(aParam);
    }

    private void startBitmapToCacheTask(File tiffImage, int selectedLayer){
        LoadTiffImageTask.LoadTiffTaskParam aParam = new LoadTiffImageTask.LoadTiffTaskParam(tiffImage, selectedLayer);
        SaveBitmapToCacheTask task = new SaveBitmapToCacheTask(mMemoryCache);
        task.execute(aParam);

    }

    private void unFilterImage(File tiffImage){
        startLoadTiffTask(tiffImage, TiffHelper.TIFF_BASE_LAYER);
        mImageView.startAnimation(mFadeOut);

        if(!TiffHelper.isUnfiltered(tiffImage)){
            TiffHelper.setUnfilterStatus(tiffImage, true);
        }
    }

    private float getAccelerometer(SensorEvent event) {

        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        return accelationSquareRoot;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
