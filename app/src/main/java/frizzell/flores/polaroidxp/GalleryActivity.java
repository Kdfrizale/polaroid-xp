package frizzell.flores.polaroidxp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.beyka.tiffbitmapfactory.TiffConverter;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        File file[] = storageDir.listFiles();
        file = convertTiffsToJpeg(file);

        MyAdapter adapter = new MyAdapter(getApplicationContext(), file);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_gallery_sort:
                //TODO create options to sort by date or something? maybe get rid of this method
                return true;
            case R.id.action_gallery_select_all:
                //TODO create action to select all images,
                return true;
            case R.id.action_gallery_decode_selected:
                //TODO implement decode function
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private File[] convertTiffsToJpeg(File[] files){
        for(File file : files){
            File tempFile = new File(file.toString() + ".jpg");
            if(!tempFile.exists() && (file.toString().endsWith(".tif") || file.toString().endsWith(".TIF"))) {
                Log.e("FIle CONVERTED", file.toString());
                TiffConverter.convertTiffJpg(file.toString(), file.toString() + ".jpg", null, null);
            }
        }
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        File file[] = storageDir.listFiles();
        return file;
    }
}


