package frizzell.flores.polaroidxp.entity;

import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;

public abstract class AbstractImage {
    private static final String TAG = AbstractImage.class.getSimpleName();
    protected File m_file;

    public int getImageOrientation(){
        try{
            ExifInterface exif = new ExifInterface(m_file.getAbsolutePath());
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
        }catch (Exception e){
            Log.e(TAG, "Error, returning default orientation matrix");
            return 1;
        }
    }

    public Matrix getOrientationMatrix(){
        int orientation = this.getImageOrientation();
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        return matrix;
    }

    public void setImageOrientation( int orientation){
        if(orientation > 8 || orientation < 0){
            orientation = 0;
        }
        try{
            ExifInterface exif = new ExifInterface(m_file.getAbsolutePath());
            exif.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(orientation));
            exif.saveAttributes();
        }catch (Exception e){
            Log.e(TAG, "Error reading image orientation information");
        }
    }

}
