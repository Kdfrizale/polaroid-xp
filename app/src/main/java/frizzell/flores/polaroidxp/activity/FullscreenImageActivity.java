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
    Bitmap mImageBitmap;
    private LruCache<String, Bitmap> mMemoryCache;
    private AlphaAnimation mFadeOut;
    private long lastUpdate;
    private SensorManager sensorManager;

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

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 2;
        Log.e("SIZE OF CACHE", "SIZE OF CACHE IS: "  +Integer.toString(cacheSize));

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };


        //TODO show image with AsynctaskLoad
        //changeImage(tempFile,TiffHelper.TIFF_FILTER_LAYER);
        changeImage(mTiffImage, (filteredStatus) ? TiffHelper.TIFF_BASE_LAYER : TiffHelper.TIFF_FILTER_LAYER);

        //Start loading base if filter is shown
        if(filteredStatus){

        }

        //TESTING LOAD BOTH TO CACHE
        loadBitmapIntoCache(mTiffImage, TiffHelper.TIFF_BASE_LAYER);
        loadBitmapIntoCache(mTiffImage, TiffHelper.TIFF_FILTER_LAYER);

        //TODO all functions here need to be redesigned to accomodate cacheing images before displaying them


        ///
        //Anitmate playground
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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

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
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
//            Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
//                    .show();
            mImageView.startAnimation(mFadeOut);
            unFilterImage(mTiffImage);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }





    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void loadBitmapIntoCache(File tiffFile, int layerOfTiff) {
        final String imageKey = tiffFile.getAbsolutePath() + Integer.toString(layerOfTiff);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            //mImageView.setImageBitmap(bitmap);
        } else {

            BitmapWorkerTask task = new BitmapWorkerTask();
            LoadTiffImageTask.LoadTiffTaskParam aParam = new LoadTiffImageTask.LoadTiffTaskParam(tiffFile,layerOfTiff);
            task.execute(aParam);
        }
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
            Bitmap bitmap = getBitmapFromMemCache(tiffImage.getAbsolutePath()+Integer.toString(tiffLayer));
            if (bitmap != null){
                mImageView.setImageBitmap(bitmap);
            }
            else{
                LoadTiffImageTask.LoadTiffTaskParam aParam = new LoadTiffImageTask.LoadTiffTaskParam(tiffImage, tiffLayer);
                LoadTiffImageTask loadTiffTask = new LoadTiffImageTask(mImageView);
                loadTiffTask.execute(aParam);
            }



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

    class BitmapWorkerTask extends AsyncTask<LoadTiffImageTask.LoadTiffTaskParam, Void, Bitmap> {

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(LoadTiffImageTask.LoadTiffTaskParam... params) {
            final Bitmap bitmap = TiffHelper.getLayerOfTiff(params[0].tiffImageFile, params[0].selectedLayer);
            addBitmapToMemoryCache(params[0].tiffImageFile.getAbsoluteFile() + Integer.toString(params[0].selectedLayer), bitmap);
            return bitmap;
        }

    }

}
