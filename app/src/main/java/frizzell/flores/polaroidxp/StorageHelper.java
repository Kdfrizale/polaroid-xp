package frizzell.flores.polaroidxp;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;
import org.beyka.tiffbitmapfactory.TiffConverter;

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

    public static boolean createTiffFromJpeg(String parentDirectory, File jpegFile){
        if(isExternalStorageWritable()){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
            File tempTiff = new File(storageDir, jpegFile.getName() + ".tif");
            return TiffConverter.convertJpgTiff(jpegFile.toString(), tempTiff.toString(), null, null);
        }
        return false;
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