package frizzell.flores.polaroidxp.asynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

import frizzell.flores.polaroidxp.utils.TiffHelper;

public class LoadTiffImageTask extends AsyncTask<LoadTiffImageTask.LoadTiffTaskParam, Void, Bitmap> {
    private ImageView mImageView;
    public LoadTiffImageTask(ImageView imageView){
        mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(LoadTiffTaskParam... params){
        for (int i =0; i < params.length; i++){
            return TiffHelper.getLayerOfTiff(params[i].tiffImageFile,params[i].selectedLayer);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        mImageView.setImageBitmap(bitmap);
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
