package frizzell.flores.polaroidxp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.application.App;
import frizzell.flores.polaroidxp.asynctask.ConvertTiffToJpegTask;
import frizzell.flores.polaroidxp.asynctask.SaveTiffTask;
import frizzell.flores.polaroidxp.singleton.ActiveWorkRepo;
import frizzell.flores.polaroidxp.singleton.TiffFileFactory;

public class TiffHelper {
    public final static int TIFF_BASE_LAYER = 0;
    public final static int TIFF_FILTER_LAYER = 1;

    public static Bitmap getLayerOfTiff(File tiffImage, int layer){
        layer = validateLayer(tiffImage,layer);
        String layerDescription = getLayerDescription(tiffImage, layer);

        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        Log.e("Tiff desc","image description: " + layerDescription);
        String [] imageProperties = layerDescription.split(ImageDescription.delimiter);
        Matrix matrix = ImageHelper.getOrientationMatrix(Integer.parseInt(imageProperties[ImageDescription.ORIENTATION]));

        options.inJustDecodeBounds = false;
        options.inDirectoryNumber = layer;
        Bitmap temp = TiffBitmapFactory.decodeFile(tiffImage,options);
        return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
    }

    public static String getLayerDescription(File tiffFile, int layer){
        layer = validateLayer(tiffFile,layer);
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();

        options.inJustDecodeBounds = true;
        options.inDirectoryNumber = layer;
        TiffBitmapFactory.decodeFile(tiffFile, options);
        return options.outImageDescription;
    }

    public static int getNumberOfLayers(File tiffFile){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        TiffBitmapFactory.decodeFile(tiffFile, options);
        return options.outDirectoryCount;
    }

    private static int validateLayer(File tiffImage, int layer){
        return (layer >= getNumberOfLayers(tiffImage) || layer < 0) ? 0 : layer;
    }

    public static boolean isUnfiltered(File tiffImage){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        TiffBitmapFactory.decodeFile(tiffImage, options);

        Log.e("Reading Tiff", "File options: " + options.outImageDescription);
        ImageDescription imageDescrip = ImageDescription.decodeImageDescription(options.outImageDescription);
        if(imageDescrip != null){
            return  imageDescrip.mIsUnfiltered;
        }
        return false;
    }

    public static File getRelatedTiffFromJpeg(String jpegFileName){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), App.getContext().getString(R.string.tiffImagesFolder));
        return new File(storageDir, StorageHelper.removeFileSuffix(jpegFileName) + ".tif");
    }

    public static File getRelatedJpegFromTiff(String tiffFileName){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),App.getContext().getString(R.string.jpegImagesFolder));
        return new File(storageDir, StorageHelper.removeFileSuffix(tiffFileName) +".jpg");
    }

    public static File getFilterJpegFromTiff(File tiffFile){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDirectoryNumber = validateLayer(tiffFile,TIFF_FILTER_LAYER);

        TiffBitmapFactory.decodeFile(tiffFile, options);

        Log.e("Reading Tiff", "File options: " + options.outImageDescription);
        ImageDescription imageDescrip = ImageDescription.decodeImageDescription(options.outImageDescription);
        if(imageDescrip != null){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),App.getContext().getString(R.string.filterImagesFolder));
            return new File(storageDir, imageDescrip.mFilterFileName);
        }
        return null;
    }
    public static File getJpegToShowForTiff(File tiffFile){
        if(isUnfiltered(tiffFile)){
            //return baseimage
            return getRelatedJpegFromTiff(tiffFile.getName());
        }
        else{
            //return filter image
            return getFilterJpegFromTiff(tiffFile);
        }
    }

    public static void checkAllTiffsHaveRelatedJpegs(){
        File tiffFiles[] = ImageHelper.getImagesInFolder(App.getContext().getString(R.string.tiffImagesFolder));
        CountDownLatch doneSignal = new CountDownLatch(tiffFiles.length);
        for(int i=0; i < tiffFiles.length; i++){
            File relatedJpegFile = getRelatedJpegFromTiff(tiffFiles[i].getName());
            if(!relatedJpegFile.exists()){
                ConvertTiffToJpegTask.ConvertTiffTaskParam aParam = new ConvertTiffToJpegTask.ConvertTiffTaskParam(tiffFiles[i],relatedJpegFile, doneSignal);//TODO switch to use thread or a different executor pool so that asynctasks can remain free for UI changes
                ConvertTiffToJpegTask task = new ConvertTiffToJpegTask();
                task.execute(aParam);
            }
            else{
                doneSignal.countDown();
            }
        }
        try{
            doneSignal.await();
        }catch (InterruptedException ex){
            ex.printStackTrace();

        }
    }

    public static void checkIfJpegBaseExistsFromTiff(File tiffFile, CountDownLatch doneSignal){
        File jpegFile = getRelatedJpegFromTiff(tiffFile.getName());
        if(!jpegFile.exists()){
            ConvertTiffToJpegTask.ConvertTiffTaskParam aParam = new ConvertTiffToJpegTask.ConvertTiffTaskParam(tiffFile,jpegFile, doneSignal);//TODO switch to use thread or a different executor pool so that asynctasks can remain free for UI changes
            ConvertTiffToJpegTask task = new ConvertTiffToJpegTask();
            task.execute(aParam);
        }
    }



    public static void setUnfilterStatus(File tiffFile, boolean isUnfiltered){
        if(!isWorkClaimed(tiffFile.getAbsolutePath())){
            addWorkToLedger(tiffFile.getAbsolutePath());

            File filter = getFilterJpegFromTiff(tiffFile);
            File jpegBase = getRelatedJpegFromTiff(tiffFile.getName());

            TiffFileFactory.Options aParam = new TiffFileFactory.Options(jpegBase,filter, isUnfiltered);
            SaveTiffTask createImageTask = new SaveTiffTask();
            createImageTask.execute(aParam);
        }
    }

    private static boolean isWorkClaimed(String aWorkItemKey){
        return ActiveWorkRepo.getInstance().getActiveWork().contains(aWorkItemKey);
    }

    private static void addWorkToLedger(String aWorkItemKey){
        ActiveWorkRepo.getInstance().addActiveWork(aWorkItemKey);
    }


    public static class ImageDescription {
        public static final int DESCRIPTION =0;
        public static final int FILTER_FILE_NAME=1;
        public static final int FILTERED =2;
        public static final int ORIENTATION =3;

        public static final String delimiter = ",";

        String mDescription = "";
        String mFilterFileName="";
        Boolean mIsUnfiltered = false;
        int mOrientation = 1;

        public ImageDescription(Boolean isUnfiltered, int orientation, String description, String filterFileName){
            this.mIsUnfiltered = isUnfiltered;
            this.mOrientation = orientation;
            this.mDescription = description;
            this.mFilterFileName = filterFileName;
        }

        public String encodeToString(){
            return mDescription + delimiter + mFilterFileName+ delimiter + String.valueOf(mIsUnfiltered) + delimiter + String.valueOf(mOrientation);
        }

        public static ImageDescription decodeImageDescription(String description){
            String[] properties = description.split(ImageDescription.delimiter);
            if(properties.length == 4){
                Boolean filterStatus = Boolean.valueOf(properties[FILTERED]);
                int orientationStaus = Integer.valueOf(properties[ORIENTATION]);
                return new ImageDescription(filterStatus,orientationStaus,properties[DESCRIPTION], properties[FILTER_FILE_NAME]);
            }
            return null;
        }
        public String getmDescription() {
            return mDescription;
        }

        public void setmDescription(String mDescription) {
            this.mDescription = mDescription;
        }

        public String getmFilterFileName() {
            return mFilterFileName;
        }

        public void setmFilterFileName(String mFilterFileName) {
            this.mFilterFileName = mFilterFileName;
        }

        public Boolean getmIsUnfiltered() {
            return mIsUnfiltered;
        }

        public void setmIsUnfiltered(Boolean mIsUnfiltered) {
            this.mIsUnfiltered = mIsUnfiltered;
        }

        public int getmOrientation() {
            return mOrientation;
        }

        public void setmOrientation(int mOrientation) {
            this.mOrientation = mOrientation;
        }
    }
}
