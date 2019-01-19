package frizzell.flores.polaroidxp.asynctask;

import android.os.AsyncTask;

import org.beyka.tiffbitmapfactory.TiffConverter;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import frizzell.flores.polaroidxp.entity.TiffImage;
import frizzell.flores.polaroidxp.entity.TiffImageDescription;
import frizzell.flores.polaroidxp.utils.ImageHelper;
import frizzell.flores.polaroidxp.utils.TiffHelper;

public class ConvertTiffToJpegTask extends AsyncTask<TiffImage, Void, Boolean> {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected Boolean doInBackground(TiffImage... params){
        for (int i =0; i < params.length; i++) {
            boolean result = TiffConverter.convertTiffJpg(params[i].tiffImageFile.getAbsolutePath(), params[i].jpegImageFile.getAbsolutePath(), null, null);

            if(result){
                String [] properties = params[i].getLayerDescription(TiffHelper.TIFF_BASE_LAYER).split(TiffImageDescription.delimiter);
                ImageHelper.setImageOrientation(params[i].jpegImageFile.getAbsolutePath(),Integer.parseInt(properties[TiffImageDescription.ORIENTATION]));
            }
            if(params[i].countDown != null){
                params[i].countDown.countDown();
            }
            return result;
        }
        return false;
    }

}
