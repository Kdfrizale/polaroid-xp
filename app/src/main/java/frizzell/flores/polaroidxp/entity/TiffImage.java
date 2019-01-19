package frizzell.flores.polaroidxp.entity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.File;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.application.App;
import frizzell.flores.polaroidxp.asynctask.ConvertTiffToJpegTask;
import frizzell.flores.polaroidxp.asynctask.SaveTiffTask;
import frizzell.flores.polaroidxp.singleton.ActiveWorkRepo;
import frizzell.flores.polaroidxp.singleton.TiffFileFactory;
import frizzell.flores.polaroidxp.utils.StorageHelper;

public class TiffImage extends AbstractImage {
    private static final String TAG = TiffImage.class.getSimpleName();
    public final static int TIFF_BASE_LAYER = 0;
    public final static int TIFF_FILTER_LAYER = 1;

    private TiffImageDescription m_tiffImageDescription;

    public TiffImage(File file){
        m_file = file;
        //TODO create new instance of TIffImage Description here
    }

    @Override
    public int getImageOrientation(){
        return m_tiffImageDescription.getmOrientation();
    }

    public Bitmap getLayerBitmap(int layer){
        layer = this.validateLayer(layer);
        String layerDescription = this.getLayerDescription(layer);

        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        Log.e(TAG,"image description: " + layerDescription);
//        String [] imageProperties = layerDescription.split(TiffImageDescription.delimiter);
//        Matrix matrix = this.getOrientationMatrix(Integer.parseInt(imageProperties[TiffImageDescription.ORIENTATION]));
        Matrix matrix = this.getOrientationMatrix();//TODO check that this calls the TiffImage.getImageOrientation and not base
        options.inJustDecodeBounds = false;
        options.inDirectoryNumber = layer;
        Bitmap temp = TiffBitmapFactory.decodeFile(m_file,options);
        return Bitmap.createBitmap(temp,0,0,temp.getWidth(),temp.getHeight(),matrix,true);
    }

    public String getLayerDescription(int layer){
        layer = this.validateLayer(layer);
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();

        options.inJustDecodeBounds = true;
        options.inDirectoryNumber = layer;
        TiffBitmapFactory.decodeFile(m_file, options);
        return options.outImageDescription;
    }

    public int getNumberOfLayers(){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        TiffBitmapFactory.decodeFile(m_file, options);
        return options.outDirectoryCount;
    }
    private int validateLayer(int layer){
        return (layer >= this.getNumberOfLayers() || layer < 0) ? 0 : layer;
    }

    public File getJpegToShowForTiff(){//TODO why do I need this, shouldn't this return a bitmap?
        if(this.m_tiffImageDescription.getmIsUnfiltered()){
            //return baseimage
            return getRelatedJpeg();
        }
        else{
            //return filter image if TIFF has a filter
            return (getNumberOfLayers() > 1)? getFilterJpeg(): getRelatedJpeg();
        }
    }

    public File getFilterJpeg(){
        TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDirectoryNumber = validateLayer(TIFF_FILTER_LAYER);

        TiffBitmapFactory.decodeFile(m_file, options);

        Log.e(TAG, "Reading Tiff File options: " + options.outImageDescription);
        TiffImageDescription imageDescrip = TiffImageDescription.decodeImageDescription(options.outImageDescription);
        if(imageDescrip != null){
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),App.getContext().getString(R.string.filterImagesFolder));
            return new File(storageDir, imageDescrip.mFilterFileName);
        }
        return null;
    }

    public boolean doesRelatedJpegExist(){
        File jpegFile = getRelatedJpeg();
        return jpegFile.exists();
    }

    public void createRelatedJpeg(){
        ConvertTiffToJpegTask.ConvertTiffTaskParam aParam = new ConvertTiffToJpegTask.ConvertTiffTaskParam(m_file, getRelatedJpeg());//TODO switch to use thread or a different executor pool so that asynctasks can remain free for UI changes
        ConvertTiffToJpegTask task = new ConvertTiffToJpegTask();
        task.execute(aParam);
    }



    public File getRelatedJpeg(){
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),App.getContext().getString(R.string.jpegImagesFolder));
        return new File(storageDir, StorageHelper.removeFileSuffix(m_file.getName() +".jpg"));
    }

    public void setUnfilterStatus(boolean isUnfiltered){
        if(!ActiveWorkRepo.getInstance().isWorkClaimed(m_file.getAbsolutePath())){
            ActiveWorkRepo.getInstance().addActiveWork(m_file.getAbsolutePath());

            File filter = getFilterJpeg();
            File jpegBase = getRelatedJpeg();

            TiffFileFactory.Options aParam = new TiffFileFactory.Options(jpegBase,filter, isUnfiltered);
            SaveTiffTask createImageTask = new SaveTiffTask();
            createImageTask.execute(aParam);
        }
    }

    public boolean isUnfiltered(){
        return m_tiffImageDescription.getmIsUnfiltered();
    }

}
