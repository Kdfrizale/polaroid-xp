package frizzell.flores.polaroidxp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageHelper {
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File createImageFile() throws IOException {
        if(isExternalStorageWritable()){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
            //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            storageDir.mkdirs();

            File image = File.createTempFile(imageFileName,".jpg",storageDir);
            return image;
        }
       return null;
    }
}
