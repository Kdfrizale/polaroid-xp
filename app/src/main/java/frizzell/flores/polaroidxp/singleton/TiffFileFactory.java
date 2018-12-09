package frizzell.flores.polaroidxp.singleton;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.CompressionScheme;
import org.beyka.tiffbitmapfactory.TiffConverter;
import org.beyka.tiffbitmapfactory.TiffSaver;

import java.io.File;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.application.App;
import frizzell.flores.polaroidxp.utils.ImageHelper;
import frizzell.flores.polaroidxp.utils.StorageHelper;
import frizzell.flores.polaroidxp.utils.TiffHelper;

public class TiffFileFactory {
    private static final String TAG = TiffFileFactory.class.getSimpleName();

    private TiffFileFactory(){}

    public static File createTiffFile(final Options options){
        File tempTiff = createTiffFromJpeg(options);
        if(tempTiff != null){
            appendFilterToTiff(tempTiff.getAbsolutePath(),options);
            return tempTiff;
        }
        return null;
    }

    private static File createTiffFromJpeg(final Options options){
        if(StorageHelper.isExternalStorageWritable()){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), App.getContext().getString(R.string.tiffImagesFolder));
            File tempTiff = new File(storageDir, StorageHelper.removeFileSuffix(options.jpegBaseFile.getName()) + ".tif");
            TiffConverter.ConverterOptions tiffOptions = new TiffConverter.ConverterOptions();
            tiffOptions.compressionScheme = options.compressionScheme;
            tiffOptions.imageDescription = options.baseImageDescription.encodeToString();
            if(TiffConverter.convertJpgTiff(options.jpegBaseFile.getAbsolutePath(), tempTiff.getAbsolutePath(), tiffOptions, null)){
                Log.i(TAG,"Successful Tiff Conversion");
                return tempTiff;
            }
        }
        Log.e(TAG,"Failed Tiff Conversion");
        return null;
    }

    private static  boolean appendFilterToTiff(String tiffFilePath,final Options options){
        Bitmap filter = BitmapFactory.decodeFile(options.jpegFilterFile.getAbsolutePath());
        if(filter != null){
            TiffSaver.SaveOptions tiffOptions = new TiffSaver.SaveOptions();
            tiffOptions.compressionScheme = options.compressionScheme;
            tiffOptions.imageDescription = options.filterImageDescription.encodeToString();
            return TiffSaver.appendBitmap(tiffFilePath, filter, tiffOptions);
        }
        return false;
    }


    public static class Options{
        public File jpegBaseFile;
        public File jpegFilterFile;
        public CompressionScheme compressionScheme;
        public boolean isUnfiltered;
        public TiffHelper.ImageDescription baseImageDescription;
        public TiffHelper.ImageDescription filterImageDescription;

        public Options(File jpegBaseFile, File jpegFilterFile){
            this.jpegBaseFile = jpegBaseFile;
            this.jpegFilterFile = jpegFilterFile;
            this.compressionScheme = CompressionScheme.JPEG;
            this.isUnfiltered = false;
            this.baseImageDescription = new TiffHelper.ImageDescription(isUnfiltered,ImageHelper.getImageOrientation(jpegBaseFile.getAbsolutePath()),"description",jpegFilterFile.getName());
            this.filterImageDescription = new TiffHelper.ImageDescription(isUnfiltered,ImageHelper.getImageOrientation(jpegFilterFile.getAbsolutePath()),"description",jpegFilterFile.getName());
        }
        public Options(File jpegBaseFile, File jpegFilterFile, boolean isUnfiltered){
            this.jpegBaseFile = jpegBaseFile;
            this.jpegFilterFile = jpegFilterFile;
            this.compressionScheme = CompressionScheme.JPEG;
            this.isUnfiltered = isUnfiltered;
            this.baseImageDescription = new TiffHelper.ImageDescription(isUnfiltered,ImageHelper.getImageOrientation(jpegBaseFile.getAbsolutePath()),"description",jpegFilterFile.getName());
            this.filterImageDescription = new TiffHelper.ImageDescription(isUnfiltered,ImageHelper.getImageOrientation(jpegFilterFile.getAbsolutePath()),"description",jpegFilterFile.getName());
        }
    }
}
