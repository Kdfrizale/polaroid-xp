package frizzell.flores.polaroidxp.asynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;

import frizzell.flores.polaroidxp.entity.TiffImage;

public class LoadTiffImageTask extends AsyncTask<LoadTiffImageTask.LoadTiffTaskParam, Void, Bitmap> {
    private final String TAG = getClass().getSimpleName();
    private LruCache<String, Bitmap> mLruCache;
    private String mKey;
    public AsyncResponse response = null;
    public LoadTiffImageTask(AsyncResponse response){
        this.response = response;
    }
    public LoadTiffImageTask(AsyncResponse response, LruCache<String, Bitmap> lruCache){
        mLruCache = lruCache;
        this.response = response;
    }

    public interface AsyncResponse {
        void processFinish(Bitmap bitmap);
    }

    @Override
    protected Bitmap doInBackground(LoadTiffTaskParam... params){
        for (int i =0; i < params.length; i++){
            if( mLruCache != null){
                mKey = params[i].tiffImageFile.getTiffFile().getAbsolutePath() + Integer.toString(params[i].selectedLayer);
                Bitmap result = mLruCache.get(mKey);
                if(result != null){
                    return result;
                }
            }
            return params[i].tiffImageFile.getLayerOfTiff(params[i].selectedLayer);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        response.processFinish(bitmap);
        if (mLruCache != null){
            if(mLruCache.get(mKey) == null){
                mLruCache.put(mKey, bitmap);
            }
        }
    }

    public static class LoadTiffTaskParam {
        public TiffImage tiffImageFile;
        public int selectedLayer;

        public LoadTiffTaskParam(TiffImage tiffImageFile, int selectedLayer){
            this.tiffImageFile = tiffImageFile;
            this.selectedLayer = selectedLayer;
        }
    }
}
