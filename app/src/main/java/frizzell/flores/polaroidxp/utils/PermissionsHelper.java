package frizzell.flores.polaroidxp.utils;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

//TODO This class was replaced by an open source library, kept here only for reference, eventually remove
public class PermissionsHelper {
    public static boolean isPermissionAllowed(android.app.Activity aActivity, String permissionToCheck){
        return ContextCompat.checkSelfPermission(aActivity,permissionToCheck) == PackageManager.PERMISSION_GRANTED;
    }

    //TODO Rethink approach, in current state this does not trigger onRequestPermissionResult
    public static void askForPermission(android.app.Activity aActivity, String permissionToAskFor, int requestCode) {
        if (!isPermissionAllowed(aActivity, permissionToAskFor)) {
            ActivityCompat.requestPermissions(aActivity, new String[]{permissionToAskFor}, requestCode);
        }
    }
}
