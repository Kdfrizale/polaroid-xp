package frizzell.flores.polaroidxp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.CompressionScheme;
import org.beyka.tiffbitmapfactory.TiffBitmapFactory;
import org.beyka.tiffbitmapfactory.TiffConverter;
import org.beyka.tiffbitmapfactory.TiffSaver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class StorageHelper {

    final static int TIFF_BASE_LAYER = 0;
    final static int TIFF_FILTER_LAYER =1;

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean createDirectoryTrees(Context context) {
        if (createDirectory(context.getString(R.string.mainImagesFolder))) {
            if (createDirectory(context.getString(R.string.tiffImagesFolder))) {
                if(createDirectory(context.getString(R.string.jpegImagesFolder))){
                    return createDirectory(context.getString(R.string.filterImagesFolder));
                }
            }
        }
        return false;
    }

    public static boolean createDirectory(String directoryName){
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);
        if (!folder.exists()) {
            return folder.mkdirs();
        }
        return false;
    }

    public static File createImageFile(String parentDirectory, String fileSuffix) throws IOException {
        if(isExternalStorageWritable()){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "PolaroidXP_" + timeStamp + "_";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
            //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            storageDir.mkdirs();

            return File.createTempFile(imageFileName,fileSuffix,storageDir);
        }
        return null;
    }

    public static boolean createFilteredTiff(String parentDirectory, File jpegFile, String jpegFilterFilePath){
        File tempTiff = createTiffFromJpeg(parentDirectory, jpegFile);
        return appendFilterToTiff(tempTiff.getAbsolutePath(),jpegFilterFilePath);
    }

    public static File createTiffFromJpeg(String parentDirectory, File jpegFile){
        if(isExternalStorageWritable()){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
            File tempTiff = new File(storageDir, jpegFile.getName() + ".tif");
            TiffConverter.ConverterOptions options = new TiffConverter.ConverterOptions();
            //options.compressionScheme = CompressionScheme.LZW;
            options.compressionScheme = CompressionScheme.JPEG;
            options.imageDescription = Integer.toString(getImageOrientation(jpegFile.getAbsolutePath()));
            if(TiffConverter.convertJpgTiff(jpegFile.toString(), tempTiff.toString(), options, null)){
                return tempTiff;
            }
        }
        return null;
    }

    public static boolean appendFilterToTiff(String tiffFilePath, String jpegFilterFilePath){
        Bitmap filter = BitmapFactory.decodeFile(jpegFilterFilePath);
        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
        //options.compressionScheme = CompressionScheme.LZW;
        options.compressionScheme = CompressionScheme.JPEG;
        options.imageDescription = Integer.toString(getImageOrientation(jpegFilterFilePath));
        return TiffSaver.appendBitmap(tiffFilePath, filter, options);

    }

    private static int getImageOrientation(String filePath){
        try{
            ExifInterface exif = new ExifInterface(filePath);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
        }catch (Exception e){
            Log.e("Fullscreen Exif", "Error, returning default orientation matrix");
            return 1;
        }
    }

    public static Matrix getOrientationMatrix(String filePath){
        int orientation = getImageOrientation(filePath);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 3) {
            matrix.postRotate(180);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            Log.d("EXIF", "Exif: " + orientation);
        }
        return matrix;
    }

    public static Matrix getOrientationMatrix(int orientation){
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 3) {
            matrix.postRotate(180);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            Log.d("EXIF", "Exif: " + orientation);
        }
        return matrix;
    }

    //TODO change to javadoc
    //Q: Is this just meant for filtered version/ unfiltered version, or does it correspond to specific layers (Means we are gonna need multiple layers)
    //A: it will respond if the the layer give is too high(doesn't exist) in which case it gives base Image
    //layer = 0 for base image, layer = 1 for filter
    public static Bitmap getLayerOfTiff(File tiffImage, int layer){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        TiffBitmapFactory.decodeFile(tiffImage, options);
        int dirCount = options.outDirectoryCount;
        Log.e("Tiff desc","image description: " + options.outImageDescription);
        Matrix matrix = StorageHelper.getOrientationMatrix(Integer.parseInt(options.outImageDescription));
        if(dirCount - 1 <= layer){
            options.inDirectoryNumber = layer;//0 is base image, 1 is filter
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage,options);
            return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
        }
        else{
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage);
            return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
        }
    }

    public static File[] getImagesInFolder(String parentDirectory){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
        return storageDir.listFiles();
    }

    //TODO analyze why thread.start() join() causes a deprecated warning?? for now ignore
    @SuppressWarnings("deprecation")
    private File[] convertTiffsToJpeg(File[] files){
        Vector<FileConverterThread> convertorThreads = new Vector<FileConverterThread>();
        for(File file : files){
            if((file.toString().endsWith(".tif") || file.toString().endsWith(".TIF"))) {
                File tempFile = new File(file.toString() + ".jpg");
                if(!tempFile.exists() ){
                    convertorThreads.add(new FileConverterThread(file));
                    //Log.e("FIle CONVERTED", file.toString());
                    //TiffConverter.convertTiffJpg(file.toString(), file.toString() + ".jpg", null, null);
                }
            }
        }
        //TODO add a popup to tell the user that the images are loading (50 conversions take 14.55 seconds) but in normal use there is almost no delay
        //TODO implement with the use of multi-Async tasks.
        for(FileConverterThread thread : convertorThreads){
            thread.start();
        }
        for(FileConverterThread thread : convertorThreads){
            try{
                thread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        File file[] = storageDir.listFiles();
        return file;
    }

}