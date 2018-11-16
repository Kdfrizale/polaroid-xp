package frizzell.flores.polaroidxp.asynctask;

import android.os.AsyncTask;

import java.io.File;

import frizzell.flores.polaroidxp.singleton.ActiveWorkLedger;
import frizzell.flores.polaroidxp.utils.TiffHelper;

public class SaveTiffTask extends AsyncTask<SaveTiffTask.SaveTiffTaskParam, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(SaveTiffTaskParam... params) {
        for (int i =0; i < params.length; i++){
            File tempTiff = TiffHelper.createFilteredTiff(params[i].parentDirectory,params[i].baseImageJpegFile,params[i].filterImageJpegFile, params[i].filterStatus);
            removeWorkFromLedger(tempTiff.getAbsolutePath());
        }
        return null;
    }

    private void removeWorkFromLedger(String aWorkItemKey){
        ActiveWorkLedger.getInstance().removeActiveWork(aWorkItemKey);
    }

    public static class SaveTiffTaskParam {
        String parentDirectory;
        File baseImageJpegFile;
        File filterImageJpegFile;
        boolean filterStatus = false;

        public SaveTiffTaskParam(String parentDirectory, File baseImageFile, File filterImageFile){
            this.parentDirectory = parentDirectory;
            this.baseImageJpegFile = baseImageFile;
            this.filterImageJpegFile = filterImageFile;
        }

        public SaveTiffTaskParam(String parentDirectory, File baseImageFile, File filterImageFile, boolean filterStatus){
            this.parentDirectory = parentDirectory;
            this.baseImageJpegFile = baseImageFile;
            this.filterImageJpegFile = filterImageFile;
            this.filterStatus = filterStatus;
        }
    }
}
