package frizzell.flores.polaroidxp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.Orientation;
import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import frizzell.flores.polaroidxp.FileConverterThread;
import frizzell.flores.polaroidxp.R;

public class StorageHelper {
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
}