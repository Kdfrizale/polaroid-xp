package frizzell.flores.polaroidxp.asynctask;

import android.os.AsyncTask;

import org.beyka.tiffbitmapfactory.TiffConverter;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import frizzell.flores.polaroidxp.entity.TiffImage;
import frizzell.flores.polaroidxp.utils.ImageHelper;

public class ConvertTiffToJpegTask extends AsyncTask<ConvertTiffToJpegTask.ConvertTiffTaskParam, Void, Boolean> {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected Boolean doInBackground(ConvertTiffTaskParam... params){
        for (int i =0; i < params.length; i++) {
            boolean result = TiffConverter.convertTiffJpg(params[i].tiffImageFile.getTiffFile().getAbsolutePath(), params[i].jpegImageFile.getAbsolutePath(), null, null);

            if(result){
                String [] properties = params[i].tiffImageFile.getLayerDescription(TiffImage.TIFF_BASE_LAYER).split(TiffImage.ImageDescription.delimiter);
                ImageHelper.setImageOrientation(params[i].jpegImageFile.getAbsolutePath(),Integer.parseInt(properties[TiffImage.ImageDescription.ORIENTATION]));
            }
            if(params[i].countDown != null){
                params[i].countDown.countDown();
            }
            return result;
        }
        return false;
    }

    public static class ConvertTiffTaskParam {
        public TiffImage tiffImageFile;
        public File jpegImageFile;
        private CountDownLatch countDown;

        public ConvertTiffTaskParam(TiffImage tiffImageFile, File jpegImageFile){
            this.tiffImageFile = tiffImageFile;
            this.jpegImageFile = jpegImageFile;
        }
    }
}
