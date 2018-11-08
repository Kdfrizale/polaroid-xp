package frizzell.flores.polaroidxp.asynctask;

import android.os.AsyncTask;

import java.io.File;

import frizzell.flores.polaroidxp.utils.TiffHelper;

public class SaveTiffTask extends AsyncTask<SaveTiffTask.SaveTiffTaskParam, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(SaveTiffTaskParam... params) {
        for (int i =0; i < params.length; i++){
            TiffHelper.createFilteredTiff(params[i].parentDirectory,params[i].baseImageFile,params[i].filterImageFile);
        }
        return null;
    }

    public static class SaveTiffTaskParam {
        String parentDirectory;
        File baseImageFile;
        File filterImageFile;

        public SaveTiffTaskParam(String parentDirectory, File baseImageFile, File filterImageFile){
            this.parentDirectory = parentDirectory;
            this.baseImageFile = baseImageFile;
            this.filterImageFile = filterImageFile;
        }
    }
}
