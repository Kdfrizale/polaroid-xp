package frizzell.flores.polaroidxp.utils;

import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.Orientation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHelper {
    public static File createImageFile(String parentDirectory, String fileSuffix) throws IOException {
        if(StorageHelper.isExternalStorageWritable()){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "PolaroidXP_" + timeStamp + "_";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
            //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            storageDir.mkdirs();

            return File.createTempFile(imageFileName,fileSuffix,storageDir);
        }
        return null;
    }

    public static File[] getImagesInFolder(String parentDirectory){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),parentDirectory);
        return storageDir.listFiles();
    }

    public static Orientation getOrientationEnum(int ordinal){
        switch(ordinal){
            case 1:
                return Orientation.TOP_LEFT;
            case 2:
                return Orientation.TOP_RIGHT;
            case 3:
                return Orientation.BOT_RIGHT;
            case 4:
                return Orientation.BOT_LEFT;
            case 5:
                return Orientation.LEFT_TOP;
            case 6:
                return Orientation.RIGHT_TOP;
            case 7:
                return Orientation.RIGHT_BOT;
            case 8:
                return Orientation.LEFT_BOT;
            case 0:
                return Orientation.UNAVAILABLE;
            default:
                return Orientation.UNAVAILABLE;
        }

    }

    public static int getImageOrientation(String filePath){
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
}
