package frizzell.flores.polaroidxp.asynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;

import frizzell.flores.polaroidxp.utils.TiffHelper;


public class SaveBitmapToCacheTask extends AsyncTask<LoadTiffImageTask.LoadTiffTaskParam, Void, Bitmap> {
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

    public static void addBitmapToMemoryCache(LruCache<String, Bitmap>  memoryCache, String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(memoryCache, key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(LruCache<String, Bitmap>  memoryCache, String key) {
        return memoryCache.get(key);
    }



}