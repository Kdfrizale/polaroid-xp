package frizzell.flores.polaroidxp.asynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;

import frizzell.flores.polaroidxp.utils.TiffHelper;

public class LoadTiffImageTask extends AsyncTask<LoadTiffImageTask.LoadTiffTaskParam, Void, Bitmap> {
    private ImageView mImageView;//TODO look into how best to approach this
    private LruCache<String, Bitmap> mLruCache;
    private String mKey;
    public LoadTiffImageTask(ImageView imageView){
        mImageView = imageView;
    }
    public LoadTiffImageTask(ImageView imageView, LruCache<String, Bitmap> lruCache){
        mImageView = imageView;
        mLruCache = lruCache;
    }

    @Override
    protected Bitmap doInBackground(LoadTiffTaskParam... params){
        for (int i =0; i < params.length; i++){
            mKey = params[i].tiffImageFile.getAbsolutePath() + Integer.toString(params[i].selectedLayer);
            if( mLruCache != null){
                Bitmap result = mLruCache.get(mKey);
                if(result != null){
                    return result;
                }
            }
            return TiffHelper.getLayerOfTiff(params[i].tiffImageFile,params[i].selectedLayer);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        mImageView.setImageBitmap(bitmap);
        if (mLruCache != null){
            if(mLruCache.get(mKey) == null){
                mLruCache.put(mKey, bitmap);
            }
        }
    }

    public static class LoadTiffTaskParam {
        public File tiffImageFile;
        public int selectedLayer;

        public LoadTiffTaskParam(File tiffImageFile, int selectedLayer){
            this.tiffImageFile = tiffImageFile;
            this.selectedLayer = selectedLayer;
        }
    }
}
