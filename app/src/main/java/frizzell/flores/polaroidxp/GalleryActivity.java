package frizzell.flores.polaroidxp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        ArrayList<GalleryItemModel> galleryImages = prepareData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), galleryImages);
        recyclerView.setAdapter(adapter);


    }

    private ArrayList<GalleryItemModel> prepareData(){

        ArrayList<GalleryItemModel> images = new ArrayList<>();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        String path = storageDir.toString();
        File f = new File(path);
        File file[] = f.listFiles();
        for (int i=0; i <file.length;i++){
            GalleryItemModel itemModel = new GalleryItemModel();
            itemModel.setImg(BitmapFactory.decodeFile(file[i].getAbsolutePath()));

            images.add(itemModel);
            //GalleryItemModel createList = new GalleryItemModel();
            //createList.setImage_Path(file[i].getAbsolutePath());
            //createList.setImage_title(file[i].getName());
            //theimage.add(createList);
        }


//        for(int i = 0; i< image_titles.length; i++){
//            GalleryItemModel createList = new GalleryItemModel();
//            createList.setImage_title(image_titles[i]);
//            createList.setImage_path(image_ids[i]);
//            theimage.add(createList);
//        }
        return images;
    }

}


