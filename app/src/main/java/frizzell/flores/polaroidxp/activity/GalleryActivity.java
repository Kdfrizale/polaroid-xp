package frizzell.flores.polaroidxp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import frizzell.flores.polaroidxp.activity.MyAdapter;
import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.utils.ImageHelper;
import io.fabric.sdk.android.Fabric;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    static int mCurrentVisiblePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());//TODO move this to the main Activity
        setContentView(R.layout.gallery_layout);

        mRecyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        File files[] = ImageHelper.getImagesInFolder(getString(R.string.jpegImagesFolder));

        MyAdapter adapter = new MyAdapter(getApplicationContext(), files);
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
            case R.id.action_gallery_select_all:
                //TODO create action to select all images,
                //TODO note this is just a temporary crash me button to test our debugging and crash reporting services
                throw new RuntimeException("This was a test crash");
                //return true;
            case R.id.action_gallery_decode_selected:
                //TODO implement decode function
                //TODO note this is just a temporary crash me button to test our debugging and crash reporting services
                //TODO when implemented, multi-Async tasks will be absolutely necessary for mass picture conversions
                Crashlytics.getInstance().crash();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


