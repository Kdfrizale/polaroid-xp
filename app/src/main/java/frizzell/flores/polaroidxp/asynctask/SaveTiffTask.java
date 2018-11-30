package frizzell.flores.polaroidxp.asynctask;

import android.os.AsyncTask;

import java.io.File;

import frizzell.flores.polaroidxp.singleton.ActiveWorkRepo;
import frizzell.flores.polaroidxp.singleton.TiffFileFactory;
import frizzell.flores.polaroidxp.utils.StorageHelper;

public class SaveTiffTask extends AsyncTask<TiffFileFactory.Options, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(TiffFileFactory.Options... params) {
        for (int i =0; i < params.length; i++){
            File tempTiff = TiffFileFactory.createTiffFile(params[i]);
            if(tempTiff != null){
                removeWorkFromLedger(tempTiff.getAbsolutePath());
            }
        }
        return null;
    }

    private void removeWorkFromLedger(String aWorkItemKey){
        ActiveWorkRepo.getInstance().removeActiveWork(aWorkItemKey);
    }
}
