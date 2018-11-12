package frizzell.flores.polaroidxp.utils;

import android.content.Context;
import android.content.res.Resources;
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
import java.util.Vector;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.asynctask.SaveTiffTask;

public class TiffHelper {
    public final static int TIFF_BASE_LAYER = 0;
    public final static int TIFF_FILTER_LAYER = 1;

    ///TODO concurrent Possibilities
    //Creating TIFF, Bitmap filter generation can be concurrent, results in a 10% improvement (192 out of 2159milliseconds)
    //Thread Creating tiff/appending filter after taking a picture, so the user can either take another picture quickly or navigate the menu
    //Potentially create function to start caching the Tiff's bitmaps the user is most likely to open (e.g. the filter and base at the same time, the lataest photo they have taken, etc)

    public static boolean createFilteredTiff(String parentDirectory, File jpegFile, File jpegFilterFile, boolean filterStatus){
        //LogHelper.Stopwatch stopwatch = new LogHelper.Stopwatch("CreateFilteredTiff");
        File tempTiff = createTiffFromJpeg(parentDirectory, jpegFile, filterStatus);
        //stopwatch.logStopwatch("Created Based");
        Boolean result = appendFilterToTiff(tempTiff.getAbsolutePath(),jpegFilterFile, filterStatus);
        //stopwatch.logStopwatch("Created Appenned Filter");
        return result;
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
                Log.e("Failed Tiff Conversion","Returning file");
                return tempTiff;
            }
        }
        Log.e("Failed Tiff Conversion","Returning null file");
        return null;
    }

    public static boolean appendFilterToTiff(String tiffFilePath, File jpegFilterFile, boolean filterStatus){
        //LogHelper.Stopwatch stopwatch = new LogHelper.Stopwatch("Bitmap creation");
        Bitmap filter = BitmapFactory.decodeFile(jpegFilterFile.getAbsolutePath());//TODO it reads a null file here sometimes; happens when user unfilters the same image in quick succession
        //stopwatch.logStopwatch("Finished Bitmap");
        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
        options.orientation = ImageHelper.getOrientationEnum(ImageHelper.getImageOrientation(jpegFilterFile.getAbsolutePath()));
        options.compressionScheme = CompressionScheme.JPEG;
        ImageDescription tempDescrip =  new ImageDescription(filterStatus, ImageHelper.getImageOrientation(jpegFilterFile.getAbsolutePath()),"description",jpegFilterFile.getName());
        options.imageDescription = tempDescrip.encodeToString();
        boolean result = TiffSaver.appendBitmap(tiffFilePath, filter, options);
        return result;

    }

    //TODO change to javadoc
    //Q: Is this just meant for filtered version/ unfiltered version, or does it correspond to specific layers (Means we are gonna need multiple layers)
    //A: it will respond if the the layer give is too high(doesn't exist) in which case it gives base Image
    //layer = 0 for base image, layer = 1 for filter
    public static Bitmap getLayerOfTiff(File tiffImage, int layer){
        //Log.e("screen width", "Width in static!: " + Resources.getSystem().getDisplayMetrics().widthPixels);
        //Log.e("screen width", "Width in static!: " + Resources.getSystem().getDisplayMetrics().heightPixels);
        //LogHelper.Stopwatch stopwatch = new LogHelper.Stopwatch("getLayerOfTiff");
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        TiffBitmapFactory.decodeFile(tiffImage, options);
        //stopwatch.logStopwatch("get file metadata");
        int numberOfLayers = options.outDirectoryCount;
        options.inJustDecodeBounds = false;
        Log.e("Tiff desc","image description: " + options.outImageDescription);
        String [] imageProperties = options.outImageDescription.split(ImageDescription.delimiter);
        //Log.e("Tiff desc","image orientation: " + options.outImageOrientation);
        Matrix matrix = ImageHelper.getOrientationMatrix(Integer.parseInt(imageProperties[ImageDescription.ORIENTATION]));
        if(layer <= numberOfLayers - 1){
            options.inDirectoryNumber = layer;//1 is base image, 0 is filter
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage,options);
            Log.e("IMPORTNAT SIZE BITMAP", "the size of bitmap is : " + temp.getByteCount());
            //stopwatch.logStopwatch("tiff bitmap returned");
            return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
        }
        else{
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage);
            //stopwatch.logStopwatch();
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
            //File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),context.getString(R.string.filterImagesFolder));
            //return new File(storageDir,"1.jpg");//TODO change this to function getChosenFilter()

        }
    }

    public static void setFilterStatus(Context context, File tiffFile, boolean filterStatus){
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

        public String getmDescription() {
            return mDescription;
        }

        public void setmDescription(String mDescription) {
            this.mDescription = mDescription;
        }

        public Boolean getmFiltered() {
            return mFiltered;
        }

        public void setmFiltered(Boolean mFiltered) {
            this.mFiltered = mFiltered;
        }

        public int getmOrientation() {
            return mOrientation;
        }

        public void setmOrientation(int mOrientation) {
            this.mOrientation = mOrientation;
        }
    }





//    //TODO analyze why thread.start() join() causes a deprecated warning?? for now ignore
//    @SuppressWarnings("deprecation")
//    private File[] convertTiffsToJpeg(File[] files){
//        Vector<FileConverterThread> convertorThreads = new Vector<FileConverterThread>();
//        for(File file : files){
//            if((file.toString().endsWith(".tif") || file.toString().endsWith(".TIF"))) {
//                File tempFile = new File(file.toString() + ".jpg");
//                if(!tempFile.exists() ){
//                    convertorThreads.add(new FileConverterThread(file));
//                    //Log.e("FIle CONVERTED", file.toString());
//                    //TiffConverter.convertTiffJpg(file.toString(), file.toString() + ".jpg", null, null);
//                }
//            }
//        }
//        //TODO add a popup to tell the user that the images are loading (50 conversions take 14.55 seconds) but in normal use there is almost no delay
//        //TODO implement with the use of multi-Async tasks.
//        for(FileConverterThread thread : convertorThreads){
//            thread.start();
//        }
//        for(FileConverterThread thread : convertorThreads){
//            try{
//                thread.join();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
//        File file[] = storageDir.listFiles();
//        return file;
//    }
}
