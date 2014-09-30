package ge.tot.weatherapp.app.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import ge.tot.weatherapp.app.ui.fragments.SettingsFragment;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }
}
