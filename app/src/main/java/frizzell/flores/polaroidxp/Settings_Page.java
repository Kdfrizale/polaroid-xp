package frizzell.flores.polaroidxp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class Settings_Page extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
//        setSupportActionBar(toolbar);
        setupActionBar();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Settings_Fragment())
                .commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
