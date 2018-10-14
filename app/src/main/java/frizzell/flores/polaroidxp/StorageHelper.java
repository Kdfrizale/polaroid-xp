package frizzell.flores.polaroidxp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageHelper {
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("polarioidXP", "is is writeable");
            return true;
        }
        Log.e("polarioidXP", "is is not writeable");
        return false;
    }

    public static File createImageFile() throws IOException {
        isExternalStorageWritable();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //File storageDir = new File("/images");
        if (!storageDir.mkdirs()) {
            Log.e("PolariodXP", "Directory not created");
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
}
