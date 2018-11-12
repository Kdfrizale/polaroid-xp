package frizzell.flores.polaroidxp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import frizzell.flores.polaroidxp.OnGestureTouchListener;
import frizzell.flores.polaroidxp.R;
import frizzell.flores.polaroidxp.utils.TiffHelper;

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private File[] mGalleryList;
    private Context context;
    private View myView;

    public GalleryAdapter(Context context, File[] galleryList){
        this.mGalleryList = galleryList;
        this.context = context;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout,  viewGroup,false);
        this.myView = view;
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final GalleryAdapter.ViewHolder viewHolder, int i){
        final File imageOriginal = mGalleryList[i];
        final File tiffImage = TiffHelper.getRelatedTiffFromJpeg(this.context,imageOriginal.getName());//TODO this  can be improved by changing functions around
        final File image = TiffHelper.getJpegToShowForTiff(this.context, tiffImage);


        setUpImageView(viewHolder.img, imageOriginal);

        Log.e("Glide","image name: " + image.getAbsolutePath());
        Glide.with(this.myView).load(image).into(viewHolder.img);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpImageView(final ImageView imageView, final File imageOriginal){
       imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
       imageView.setOnTouchListener(new OnGestureTouchListener(this.context) {
            @Override
            public void onLongClick(){
                Snackbar.make(imageView, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onDoubleClick() {
                Intent fullscreenImageIntent = new Intent(context, FullscreenImageActivity.class);
                fullscreenImageIntent.putExtra("ImageFileName", imageOriginal.getName());
                fullscreenImageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fullscreenImageIntent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mGalleryList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.image_cell);

        }
    }
}
