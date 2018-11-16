package frizzell.flores.polaroidxp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.CompressionScheme;
import org.beyka.tiffbitmapfactory.TiffBitmapFactory;
import org.beyka.tiffbitmapfactory.TiffConverter;
import org.beyka.tiffbitmapfactory.TiffSaver;

import java.io.File;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.asynctask.SaveTiffTask;
import frizzell.flores.polaroidxp.singleton.ActiveWorkLedger;

public class TiffHelper {
    public final static int TIFF_BASE_LAYER = 0;
    public final static int TIFF_FILTER_LAYER = 1;

    public static File createFilteredTiff(String parentDirectory, File jpegFile, File jpegFilterFile, boolean filterStatus){

        File tempTiff = createTiffFromJpeg(parentDirectory, jpegFile, filterStatus);
        appendFilterToTiff(tempTiff.getAbsolutePath(),jpegFilterFile, filterStatus);
        return tempTiff;
    }

    public static File createTiffFromJpeg(String parentDirectory, File jpegFile, boolean filterStatus){
        if(StorageHelper.isExternalStorageWritable()){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
            File tempTiff = new File(storageDir, jpegFile.getName() + ".tif");
            TiffConverter.ConverterOptions options = new TiffConverter.ConverterOptions();
            options.compressionScheme = CompressionScheme.JPEG;
            ImageDescription tempDescrip =  new ImageDescription(filterStatus, ImageHelper.getImageOrientation(jpegFile.getAbsolutePath()));
            options.imageDescription = tempDescrip.encodeToString();
            if(TiffConverter.convertJpgTiff(jpegFile.getAbsolutePath(), tempTiff.getAbsolutePath(), options, null)){
                Log.e("Success Tiff Conversion","Returning file");
                return tempTiff;
            }
        }
        Log.e("Failed Tiff Conversion","Returning null file");
        return null;
    }

    public static boolean appendFilterToTiff(String tiffFilePath, File jpegFilterFile, boolean filterStatus){
        Bitmap filter = BitmapFactory.decodeFile(jpegFilterFile.getAbsolutePath());
        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
        options.orientation = ImageHelper.getOrientationEnum(ImageHelper.getImageOrientation(jpegFilterFile.getAbsolutePath()));
        options.compressionScheme = CompressionScheme.JPEG;
        ImageDescription tempDescrip =  new ImageDescription(filterStatus, ImageHelper.getImageOrientation(jpegFilterFile.getAbsolutePath()),"description",jpegFilterFile.getName());
        options.imageDescription = tempDescrip.encodeToString();
        return TiffSaver.appendBitmap(tiffFilePath, filter, options);
    }

    public static Bitmap getLayerOfTiff(File tiffImage, int layer){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        TiffBitmapFactory.decodeFile(tiffImage, options);
        int numberOfLayers = options.outDirectoryCount;
        options.inJustDecodeBounds = false;
        Log.e("Tiff desc","image description: " + options.outImageDescription);
        String [] imageProperties = options.outImageDescription.split(ImageDescription.delimiter);
        Matrix matrix = ImageHelper.getOrientationMatrix(Integer.parseInt(imageProperties[ImageDescription.ORIENTATION]));
        if(layer <= numberOfLayers - 1){
            options.inDirectoryNumber = layer;
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage,options);
            return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
        }
        else{
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage);
            return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
        }
    }

    public static boolean isFiltered(File tiffImage){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        TiffBitmapFactory.decodeFile(tiffImage, options);

        Log.e("Reading Tiff", "File options: " + options.outImageDescription);
        ImageDescription imageDescrip = ImageDescription.decodeImageDescription(options.outImageDescription);
        if(imageDescrip != null){
            return  imageDescrip.mFiltered;
        }
        return false;
    }

    public static File getRelatedTiffFromJpeg(Context context, String jpegFileName){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),context.getString(R.string.tiffImagesFolder));
        return new File(storageDir, jpegFileName + ".tif");
    }

    public static File getRelatedJpegFromTiff(Context context, String tiffFileName){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),context.getString(R.string.jpegImagesFolder));
        return new File(storageDir, tiffFileName.substring(0, tiffFileName.length() - 4));
    }

    public static File getFilterJpegFromTiff(Context context, File tiffFile){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDirectoryNumber = TIFF_FILTER_LAYER;

        TiffBitmapFactory.decodeFile(tiffFile, options);

        Log.e("Reading Tiff", "File options: " + options.outImageDescription);
        ImageDescription imageDescrip = ImageDescription.decodeImageDescription(options.outImageDescription);
        if(imageDescrip != null){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),context.getString(R.string.filterImagesFolder));
            return new File(storageDir, imageDescrip.mFilterFileName);
        }
        return null;
    }
    public static File getJpegToShowForTiff(Context context, File tiffFile){
        if(isFiltered(tiffFile)){
            //return baseimage
            return getRelatedJpegFromTiff(context, tiffFile.getName());
        }
        else{
            //return filter image
            return getFilterJpegFromTiff(context,tiffFile);
        }
    }

    public static void setFilterStatus(Context context, File tiffFile, boolean filterStatus){
        if(!isWorkClaimed(tiffFile.getAbsolutePath())){
            addWorkToLedger(tiffFile.getAbsolutePath());
            File filter = getFilterJpegFromTiff(context,tiffFile);

            TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
            options.inJustDecodeBounds = true;
            TiffBitmapFactory.decodeFile(tiffFile, options);

            Log.e("Reading Tiff", "File options: " + options.outImageDescription);

            File jpegBase = getRelatedJpegFromTiff(context, tiffFile.getName());

            SaveTiffTask.SaveTiffTaskParam aParam = new SaveTiffTask.SaveTiffTaskParam("polaroidXP/TiffImages",jpegBase,filter, filterStatus);
            SaveTiffTask createImageTask = new SaveTiffTask();
            createImageTask.execute(aParam);
        }
    }

    private static boolean isWorkClaimed(String aWorkItemKey){
        return ActiveWorkLedger.getInstance().getActiveWork().contains(aWorkItemKey);
    }

    private static void addWorkToLedger(String aWorkItemKey){
        ActiveWorkLedger.getInstance().addActiveWork(aWorkItemKey);
    }


    public static class ImageDescription {
        public static final int DESCRIPTION =0;
        public static final int FILTER_FILE_NAME=1;
        public static final int FILTERED =2;
        public static final int ORIENTATION =3;

        private static final String delimiter = ",";

        String mDescription = "";
        String mFilterFileName="";
        Boolean mFiltered = false;
        int mOrientation = 1;
        public ImageDescription(Boolean filtered, int orientation){
            this.mFiltered = filtered;
            this.mOrientation = orientation;
        }

        public ImageDescription(Boolean filtered, int orientation, String description, String filterFileName){
            this.mFiltered = filtered;
            this.mOrientation = orientation;
            this.mDescription = description;
            this.mFilterFileName = filterFileName;
        }

        public String encodeToString(){
            return mDescription + delimiter + mFilterFileName+ delimiter + String.valueOf(mFiltered) + delimiter + String.valueOf(mOrientation);
        }

        public static ImageDescription decodeImageDescription(String description){
            String[] properties = description.split(ImageDescription.delimiter);
            if(properties.length == 4){
                Boolean filterStatus = Boolean.valueOf(properties[FILTERED]);
                int orientationStaus = Integer.valueOf(properties[ORIENTATION]);
                return new ImageDescription(filterStatus,orientationStaus,properties[DESCRIPTION], properties[FILTER_FILE_NAME]);
            }
            return null;
        }
    }
}
