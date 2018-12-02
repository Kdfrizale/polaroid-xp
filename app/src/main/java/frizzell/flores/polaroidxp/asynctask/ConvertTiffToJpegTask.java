package frizzell.flores.polaroidxp.asynctask;

import android.media.ExifInterface;
import android.os.AsyncTask;

import org.beyka.tiffbitmapfactory.TiffConverter;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import frizzell.flores.polaroidxp.utils.ImageHelper;
import frizzell.flores.polaroidxp.utils.TiffHelper;

public class ConvertTiffToJpegTask extends AsyncTask<ConvertTiffToJpegTask.ConvertTiffTaskParam, Void, Boolean> {

    @Override
    protected Boolean doInBackground(ConvertTiffTaskParam... params){
        for (int i =0; i < params.length; i++) {
            boolean result = TiffConverter.convertTiffJpg(params[i].tiffImageFile.getAbsolutePath(), params[i].jpegImageFile.getAbsolutePath(), null, null);

            if(result){
                String [] properties = TiffHelper.getLayerDescription(params[i].tiffImageFile, TiffHelper.TIFF_BASE_LAYER).split(TiffHelper.ImageDescription.delimiter);
                ImageHelper.setImageOrientation(params[i].jpegImageFile.getAbsolutePath(),Integer.parseInt(properties[TiffHelper.ImageDescription.ORIENTATION]));
            }
            if(params[i].countDown != null){
                params[i].countDown.countDown();
            }
            return result;
        }
        return false;
    }

    public static class ConvertTiffTaskParam {
        public File tiffImageFile;
        public File jpegImageFile;
        private CountDownLatch countDown;

        public ConvertTiffTaskParam(File tiffImageFile, File jpegImageFile){
            this.tiffImageFile = tiffImageFile;
            this.jpegImageFile = jpegImageFile;
        }
        public ConvertTiffTaskParam(File tiffImageFile, File jpegImageFile, CountDownLatch countDown){
            this.tiffImageFile = tiffImageFile;
            this.jpegImageFile = jpegImageFile;
            this.countDown = countDown;
        }
    }
}
