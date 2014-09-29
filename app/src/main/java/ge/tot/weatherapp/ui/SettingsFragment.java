package ge.tot.weatherapp.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import ge.tot.weatherapp.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.my_settings);
    }
}
