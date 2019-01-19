package frizzell.flores.polaroidxp.entity;

public class TiffImageDescription {
    public static final int DESCRIPTION =0;
    public static final int FILTER_FILE_NAME=1;
    public static final int FILTERED =2;
    public static final int ORIENTATION =3;

    public static final String delimiter = ",";

    String mDescription = "";
    String mFilterFileName="";
    Boolean mIsUnfiltered = false;
    int mOrientation = 1;

    public TiffImageDescription(Boolean isUnfiltered, int orientation, String description, String filterFileName){
        this.mIsUnfiltered = isUnfiltered;
        this.mOrientation = orientation;
        this.mDescription = description;
        this.mFilterFileName = filterFileName;
    }

    public String encodeToString(){
        return mDescription + delimiter + mFilterFileName+ delimiter + String.valueOf(mIsUnfiltered) + delimiter + String.valueOf(mOrientation);
    }

    public static TiffImageDescription decodeImageDescription(String description){
        String[] properties = description.split(TiffImageDescription.delimiter);
        if(properties.length == 4){
            Boolean filterStatus = Boolean.valueOf(properties[FILTERED]);
            int orientationStaus = Integer.valueOf(properties[ORIENTATION]);
            return new TiffImageDescription(filterStatus,orientationStaus,properties[DESCRIPTION], properties[FILTER_FILE_NAME]);
        }
        return null;
    }
    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmFilterFileName() {
        return mFilterFileName;
    }

    public void setmFilterFileName(String mFilterFileName) {
        this.mFilterFileName = mFilterFileName;
    }

    public Boolean getmIsUnfiltered() {
        return mIsUnfiltered;
    }

    public void setmIsUnfiltered(Boolean mIsUnfiltered) {
        this.mIsUnfiltered = mIsUnfiltered;
    }

    public int getmOrientation() {
        return mOrientation;
    }

    public void setmOrientation(int mOrientation) {
        this.mOrientation = mOrientation;
    }
}
