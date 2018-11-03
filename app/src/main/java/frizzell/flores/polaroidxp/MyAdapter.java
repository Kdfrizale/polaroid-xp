package frizzell.flores.polaroidxp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private File[] galleryList;
    private Context context;
    private View myView;

    public MyAdapter(Context context, File[] galleryList){
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout,  viewGroup,false);
        this.myView = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder viewHolder, int i){
        File image = galleryList[i];
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        viewHolder.img.setOnTouchListener(new OnGestureTouchListener(this.context) {
            @Override
            public void onLongClick(){
                Log.e("TOUCH","LONG TOUCH");
                //TODO the myView goes out of scope and results in a crash
                Snackbar.make(viewHolder.img, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });

        if(image.toString().endsWith(".tif") || image.toString().endsWith(".TIF")){
            //Ignore tiff images in directory
            //TODO fix this, either reorder here, or only add jpeg files to the File [] gallerylist
        }
        else{
            Glide.with(this.myView).load(image).into(viewHolder.img);
        }

    }

    @Override
    public int getItemCount(){
        return galleryList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.img);

        }
    }





}
