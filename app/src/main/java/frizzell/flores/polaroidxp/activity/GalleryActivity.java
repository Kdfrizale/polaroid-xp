package frizzell.flores.polaroidxp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.entity.TiffImage;
import frizzell.flores.polaroidxp.utils.ImageHelper;

import java.util.Vector;

public class GalleryActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    static int mCurrentVisiblePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        mRecyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);//TODO const here
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //TiffHelper.checkAllTiffsHaveRelatedJpegs();

        Vector<TiffImage> tiffFiles = ImageHelper.getTiffImagesInFolder(getString(R.string.tiffImagesFolder));
//        File files[] = new File[tiffFiles.length];
//        for(int i=0; i < tiffFiles.length;i++){
//            files[i]= TiffHelper.getRelatedJpegFromTiff(tiffFiles[i].getAbsolutePath());
//        }

        //File files[] = ImageHelper.getImagesInFolder(getString(R.string.jpegImagesFolder));

        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), tiffFiles);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        ((GridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(mCurrentVisiblePosition);
        mCurrentVisiblePosition = 0;
    }

    @Override
    protected void onPause(){
        super.onPause();
        mCurrentVisiblePosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


