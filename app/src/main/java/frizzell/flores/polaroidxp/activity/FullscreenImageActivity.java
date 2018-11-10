package frizzell.flores.polaroidxp.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
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
import frizzell.flores.polaroidxp.asynctask.LoadTiffImageTask;
import frizzell.flores.polaroidxp.utils.TiffHelper;

public class FullscreenImageActivity extends AppCompatActivity implements SensorEventListener{

    ImageView mImageView;
    File mTiffImage;
    private LruCache<String, Bitmap> mMemoryCache;
    private AlphaAnimation mFadeOut;
    private long mLastUpdate;
    private SensorManager mSensorManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Fullscreen", "Creating activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        mImageView = (ImageView) findViewById(R.id.fullScreenImg);
//        mImageView.setOnTouchListener(new OnGestureTouchListener(this) {
//            @Override
//            public void onLongClick(){
//                Log.e("TOUCH","LONG TOUCH");
//                Snackbar.make(mImageView, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//            @Override
//            public void onDoubleClick() {
//                //mFadeOut.startNow();
//                mImageView.startAnimation(mFadeOut);
//                unFilterImage(mTiffImage);
//            }
//            @Override
//            public void onSwipeRight() {
//                //#Useful
//            }
//        });

        String passedImageName = (String) getIntent().getExtras().get("ImageFileName");
        mTiffImage = TiffHelper.getRelatedTiffFromJpeg(this, passedImageName);
        if(!mTiffImage.exists()){
            //TODO notify user that the image could not be found
            //Log error
            finish();
        }

        setUpCache();

        loadStartingImage(mTiffImage, (TiffHelper.isFiltered(mTiffImage)) ? TiffHelper.TIFF_BASE_LAYER : TiffHelper.TIFF_FILTER_LAYER);

        setUpAnimation();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLastUpdate = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
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
            public void onAnimationEnd(Animation animation) {
                //Trigger your action to change screens here.
            }

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

        //TODO Re-save the tiff image with the isFilter property changed after AsyncTask has completed
        TiffHelper.changeFilterStatus(tiffImage);//TODO implement this function
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 1.7) // sensitivity
        {
            if (actualTime - mLastUpdate < 200) {
                return;
            }
            mLastUpdate = actualTime;
//            Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
//                    .show();

            unFilterImage(mTiffImage);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void addBitmapToMemoryCache(LruCache<String, Bitmap>  memoryCache, String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(memoryCache, key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(LruCache<String, Bitmap>  memoryCache, String key) {
        return memoryCache.get(key);
    }
    static class SaveBitmapToCacheTask extends AsyncTask<LoadTiffImageTask.LoadTiffTaskParam, Void, Bitmap> {
        private LruCache<String,Bitmap> memoryCache;

        public SaveBitmapToCacheTask(LruCache<String,Bitmap> memoryCache){
            this.memoryCache = memoryCache;
        }

        @Override
        protected Bitmap doInBackground(LoadTiffImageTask.LoadTiffTaskParam... params) {
            final Bitmap bitmap = TiffHelper.getLayerOfTiff(params[0].tiffImageFile, params[0].selectedLayer);
            addBitmapToMemoryCache(this.memoryCache,params[0].tiffImageFile.getAbsoluteFile() + Integer.toString(params[0].selectedLayer), bitmap);
            return bitmap;
        }



    }

}
