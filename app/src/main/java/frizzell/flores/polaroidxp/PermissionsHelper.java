package frizzell.flores.polaroidxp;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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
