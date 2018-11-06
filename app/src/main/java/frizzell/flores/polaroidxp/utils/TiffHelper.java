package frizzell.flores.polaroidxp.utils;

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

public class TiffHelper {
    public final static int TIFF_BASE_LAYER = 0;
    public final static int TIFF_FILTER_LAYER =1;

    public static boolean createFilteredTiff(String parentDirectory, File jpegFile, String jpegFilterFilePath){
        File tempTiff = createTiffFromJpeg(parentDirectory, jpegFile);
        return appendFilterToTiff(tempTiff.getAbsolutePath(),jpegFilterFilePath);
    }

    public static File createTiffFromJpeg(String parentDirectory, File jpegFile){
        if(StorageHelper.isExternalStorageWritable()){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
            File tempTiff = new File(storageDir, jpegFile.getName() + ".tif");
            TiffConverter.ConverterOptions options = new TiffConverter.ConverterOptions();
            options.compressionScheme = CompressionScheme.JPEG;
            options.imageDescription = Integer.toString(ImageHelper.getImageOrientation(jpegFile.getAbsolutePath()));
            if(TiffConverter.convertJpgTiff(jpegFile.toString(), tempTiff.toString(), options, null)){
                return tempTiff;
            }
        }
        return null;
    }

    public static boolean appendFilterToTiff(String tiffFilePath, String jpegFilterFilePath){
        Bitmap filter = BitmapFactory.decodeFile(jpegFilterFilePath);
        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
        options.compressionScheme = CompressionScheme.JPEG;
        options.imageDescription = Integer.toString(ImageHelper.getImageOrientation(jpegFilterFilePath));
        return TiffSaver.appendBitmap(tiffFilePath, filter, options);

    }

    //TODO change to javadoc
    //Q: Is this just meant for filtered version/ unfiltered version, or does it correspond to specific layers (Means we are gonna need multiple layers)
    //A: it will respond if the the layer give is too high(doesn't exist) in which case it gives base Image
    //layer = 0 for base image, layer = 1 for filter
    public static Bitmap getLayerOfTiff(File tiffImage, int layer){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        TiffBitmapFactory.decodeFile(tiffImage, options);
        int numberOfLayers = options.outDirectoryCount;
        Log.e("Tiff desc","image description: " + options.outImageDescription);
        //Log.e("Tiff desc","image orientation: " + options.outImageOrientation);
        Matrix matrix = ImageHelper.getOrientationMatrix(Integer.parseInt(options.outImageDescription));
        if(layer <= numberOfLayers - 1){
            options.inDirectoryNumber = layer;//0 is base image, 1 is filter
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage,options);
            return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
        }
        else{
            Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage);
            return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
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
