package frizzell.flores.polaroidxp;

import android.util.Log;

import org.beyka.tiffbitmapfactory.TiffConverter;

import java.io.File;

public class FileConverterThread extends Thread{
    File fileToConvert;

    public FileConverterThread(File fileToConvert){
        this.fileToConvert = fileToConvert;
    }

    public void run(){
                Log.e("FIle CONVERTED", fileToConvert.toString());
                TiffConverter.convertTiffJpg(fileToConvert.toString(), fileToConvert.toString() + ".jpg", null, null);

    }
}
