package frizzell.flores.polaroidxp;


import android.graphics.Bitmap;

public class GalleryItemModel {

    private String image_title;
    private String image_path;
    private Bitmap img;

    public String getImage_title() {
        return image_title;
    }

    public void setImage_title(String android_version_name) {
        this.image_title = android_version_name;
    }

    public String getImage_Path() {
        return image_path;
    }

    public void setImage_Path(String filePath) {
        this.image_path = filePath;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        int origWidth = img.getWidth();
        int origHeight = img.getHeight();
        this.img = Bitmap.createScaledBitmap(img, origWidth / 10, origHeight / 10, false);
        //this.img = img;
    }
}