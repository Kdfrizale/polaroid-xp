package frizzell.flores.polaroidxp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    //private ArrayList<GalleryItemModel> galleryList;
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
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i){
        //viewHolder.title.setText(galleryList.get(i).getImage_title());
        //viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Log.e("TAG:", "Helloooo");
        File image = galleryList[i];
        Glide.with(this.myView).load(image).into(viewHolder.img);
//        Bitmap bmp = BitmapFactory.decodeFile(galleryList.get(i));
//        int origWidth = bmp.getWidth();
//        int origHeight = bmp.getHeight();
//        bmp = Bitmap.createScaledBitmap(bmp, origWidth / 10, origHeight / 10, false);
        //viewHolder.img.setImageBitmap(galleryList.get(i).getImg());
        //viewHolder.img.setImageResource((galleryList.get(i).getImage_ID()));
//        viewHolder.img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Image",Toast.LENGTH_SHORT).show();
//            }
//        });
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
