package frizzell.flores.polaroidxp;

import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.util.Vector;

public class GalleryActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    static int mCurrentVisiblePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.gallery_layout);

        mRecyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //TODO add storage permission check here
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        File files[] = storageDir.listFiles();
        files = convertTiffsToJpeg(files);

        MyAdapter adapter = new MyAdapter(getApplicationContext(), files);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e("Restoring visition", "Position is: " + mCurrentVisiblePosition);
        ((GridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(mCurrentVisiblePosition);
        mCurrentVisiblePosition = 0;
    }

    @Override
    protected void onPause(){
        super.onPause();
        mCurrentVisiblePosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        Log.e("Save visible position", "Position is: " + mCurrentVisiblePosition);
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
                //TODO note this is just a tempory crash me button to test our debugging and crash reporting services
                throw new RuntimeException("This was a test crash");
                //return true;
            case R.id.action_gallery_decode_selected:
                //TODO implement decode function
                //TODO note this is just a tempory crash me button to test our debugging and crash reporting services
                Crashlytics.getInstance().crash();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO analyze why thread.start() join() causes a deprecated warning?? for now ignore
    @SuppressWarnings("deprecation")
    private File[] convertTiffsToJpeg(File[] files){
        Vector<FileConverterThread> convertorThreads = new Vector<FileConverterThread>();
        for(File file : files){
            if((file.toString().endsWith(".tif") || file.toString().endsWith(".TIF"))) {
                File tempFile = new File(file.toString() + ".jpg");
                if(!tempFile.exists() ){
                    convertorThreads.add(new FileConverterThread(file));
                    //Log.e("FIle CONVERTED", file.toString());
                    //TiffConverter.convertTiffJpg(file.toString(), file.toString() + ".jpg", null, null);
                }
            }
        }
        //TODO add a popup to tell the user that the images are loading (50 conversions take 14.55 seconds) but in normal use there is almost no delay
        for(FileConverterThread thread : convertorThreads){
            thread.start();
        }
        for(FileConverterThread thread : convertorThreads){
            try{
                thread.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        File file[] = storageDir.listFiles();
        return file;
    }

}


