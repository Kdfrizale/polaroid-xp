package frizzell.flores.polaroidxp.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import frizzell.flores.polaroidxp.R;

public class StorageHelper {
    private static final String TAG =StorageHelper.class.getSimpleName();
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean createDirectoryTrees(Context context) {
        boolean result  = true;
        result = createDirectory(context.getString(R.string.mainImagesFolder)) && result;
        result = createDirectory(context.getString(R.string.tiffImagesFolder)) && result;
        result = createDirectory(context.getString(R.string.jpegImagesFolder)) && result;
        result = createDirectory(context.getString(R.string.filterImagesFolder)) && result;

        return result;
    }

    public static boolean createDirectory(String directoryName){
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);
        if (!folder.exists()) {
            Log.i(TAG, "Creating folder: " + folder.getAbsolutePath());
            return folder.mkdirs();
        }
        return true;
    }

    public static String removeFileSuffix(String filename){
        return filename.substring(0,filename.lastIndexOf('.'));
    }
}