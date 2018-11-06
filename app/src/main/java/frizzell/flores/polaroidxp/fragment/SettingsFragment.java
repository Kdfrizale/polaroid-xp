package frizzell.flores.polaroidxp.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import frizzell.flores.polaroidxp.R;

public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_page);
    }
}
