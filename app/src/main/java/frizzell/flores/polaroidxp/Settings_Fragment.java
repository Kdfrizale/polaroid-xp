package frizzell.flores.polaroidxp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Settings_Fragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_page);
    }
}
