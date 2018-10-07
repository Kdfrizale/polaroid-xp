package frizzell.flores.polaroidxp;

import android.app.Activity;
import android.os.Bundle;

public class Settings_Page extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Settings_Fragment())
                .commit();
    }
}
