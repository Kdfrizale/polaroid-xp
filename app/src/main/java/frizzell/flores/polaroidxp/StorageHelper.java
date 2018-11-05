package frizzell.flores.polaroidxp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;
import org.beyka.tiffbitmapfactory.TiffConverter;
import org.beyka.tiffbitmapfactory.TiffSaver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageHelper {
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
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
            if(TiffConverter.convertJpgTiff(jpegFile.toString(), tempTiff.toString(), null, null)){
                return tempTiff;
            }
        }
        return null;
    }

    public static boolean appendFilterToTiff(String tiffFilePath, String jpegFilterFilePath){
        Bitmap filter = BitmapFactory.decodeFile(jpegFilterFilePath);
        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
        //options.compressionScheme = CompressionScheme.COMPRESSION_LZW;
        return TiffSaver.appendBitmap(tiffFilePath, filter, options);

    }

    //TODO example of reading multi page tiff
//    public static boolean createTiffFromJpeg(String parentDirectory, File jpegFile){
//        if(isExternalStorageWritable()){
//            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
//            File tempTiff = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "multipage_tif_example.tif");
//            TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
//            TiffBitmapFactory.decodeFile(tempTiff, options);
//            int dirCount = options.outDirectoryCount;
//            //Read and process all images in file
//            for (int i = 0; i < dirCount; i++) {
//                options.inDirectoryNumber = i;
//                TiffBitmapFactory.decodeFile(tempTiff, options);
//                int curDir = options.outCurDirectoryNumber;
//            }
//
//
//
//
//            return TiffConverter.convertJpgTiff(jpegFile.toString(), tempTiff.toString(), null, null);
//        }
//        return false;
//    }

    public static File[] getImagesInFolder(String parentDirectory){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
        return storageDir.listFiles();
    }

}