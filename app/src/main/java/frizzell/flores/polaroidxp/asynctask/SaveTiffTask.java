package frizzell.flores.polaroidxp.asynctask;

import android.os.AsyncTask;

import java.io.File;

import frizzell.flores.polaroidxp.utils.TiffHelper;

public class SaveTiffTask extends AsyncTask<SaveTiffTask.SaveTiffTaskParam, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(SaveTiffTaskParam... params) {
        for (int i =0; i < params.length; i++){
            if(params[i].overwriteFile){
                //Change an existing tiff file so that is base is shown
                //get the current files TIFF jpeg, filter jpeg, and create options with filter set to true
                TiffHelper.createFilteredTiff(params[i].parentDirectory,params[i].baseImageJpegFile,params[i].filterImageJpegFile, true);
                //TODO change this so filterstatus just pulls overwritefile boolean
            }
            else{
                //create a new tiff image that has filter shown
                TiffHelper.createFilteredTiff(params[i].parentDirectory,params[i].baseImageJpegFile,params[i].filterImageJpegFile, false);
            }

        }
        return null;
    }

    public static class SaveTiffTaskParam {
        String parentDirectory;
        File baseImageJpegFile;
        File filterImageJpegFile;
        boolean overwriteFile = false;

        public SaveTiffTaskParam(String parentDirectory, File baseImageFile, File filterImageFile){
            this.parentDirectory = parentDirectory;
            this.baseImageJpegFile = baseImageFile;
            this.filterImageJpegFile = filterImageFile;
        }

        public SaveTiffTaskParam(String parentDirectory, File baseImageFile, File filterImageFile, boolean overwriteFile){
            this.parentDirectory = parentDirectory;
            this.baseImageJpegFile = baseImageFile;
            this.filterImageJpegFile = filterImageFile;
            this.overwriteFile = overwriteFile;
        }
    }
}
