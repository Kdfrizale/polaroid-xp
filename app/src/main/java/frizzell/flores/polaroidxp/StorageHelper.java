package frizzell.flores.polaroidxp;

import android.os.Environment;
import android.util.Log;

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

}
