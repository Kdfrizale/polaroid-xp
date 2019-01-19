package frizzell.flores.polaroidxp.singleton;

import android.os.Environment;

import java.io.File;
import java.util.Vector;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.application.App;
import frizzell.flores.polaroidxp.utils.StorageHelper;

public class TiffImageRepo {
    private final String TAG = getClass().getSimpleName();
    private static TiffImageRepo instance = null;
    public Vector<String> listOfTiffs = new Vector<String>();
    private TiffImageRepo(){}

    public static synchronized TiffImageRepo getInstance(){
        if(instance == null){
            instance = new TiffImageRepo();
        }
        return instance;
    }

    public Vector<String> getTiffs(){
        return this.listOfTiffs;
    }

    public void addActiveWork(String activeWorkKey){
        synchronized (listOfTiffs){
            listOfTiffs.add(activeWorkKey);
        }
    }

    public void removeActiveWork(String activeWorkKey){
        synchronized (listOfTiffs){
            listOfTiffs.remove(activeWorkKey);
        }
    }

    public static File getRelatedTiffFromJpeg(String jpegFileName){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), App.getContext().getString(R.string.tiffImagesFolder));
        return new File(storageDir, StorageHelper.removeFileSuffix(jpegFileName) + ".tif");
    }
}

