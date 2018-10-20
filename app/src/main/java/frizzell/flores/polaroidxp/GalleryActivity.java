package frizzell.flores.polaroidxp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

        //ArrayList<GalleryItemModel> galleryImages = prepareData();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        String path = storageDir.toString();
        File f = new File(path);
        File file[] = f.listFiles();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), file);
        recyclerView.setAdapter(adapter);


    }

    private ArrayList<GalleryItemModel> prepareData(){

        ArrayList<GalleryItemModel> images = new ArrayList<>();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"polaroidXP");
        String path = storageDir.toString();
        File f = new File(path);
        File file[] = f.listFiles();
        for (int i=0; i <file.length;i++){
            long startTime = System.currentTimeMillis();
            Log.e("Prepare Data", "loading a file...");
            //TODO improve performance here
            GalleryItemModel itemModel = new GalleryItemModel();
            long endTime = System.currentTimeMillis();
            Log.e("TAG crete OBject","That took " + (endTime - startTime) + " milliseconds");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file[i].getAbsolutePath(),options);
            Log.d("Height:", Integer.toString(options.outHeight));
            Log.d("width:", Integer.toString(options.outWidth));
            options.inJustDecodeBounds = false;

            options.inSampleSize = calculateInSampleSize(options, 400, 200);


            endTime = System.currentTimeMillis();
            Log.e("TAG heightwidth","That took " + (endTime - startTime) + " milliseconds");
            itemModel.setImg(BitmapFactory.decodeFile(file[i].getAbsolutePath(),options));

            endTime = System.currentTimeMillis();
            Log.e("TAG After Bitmapfactory","That took " + (endTime - startTime) + " milliseconds");

            images.add(itemModel);
            endTime = System.currentTimeMillis();
            Log.e("TAG Total Time","That took " + (endTime - startTime) + " milliseconds");
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}


