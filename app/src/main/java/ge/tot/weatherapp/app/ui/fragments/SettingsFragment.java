package ge.tot.weatherapp.app.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import ge.tot.weatherapp.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.my_settings);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initializeSettingsView();
    }

    private void initializeSettingsView() {
        int index = 0;
        String currentValue = mPrefs.getString("unit", "metric");
        ListPreference unitPreference = (ListPreference) findPreference("unit");
        for (CharSequence unit : unitPreference.getEntryValues()) {
            if (unit.equals(currentValue)) {
                break;
            }
            index++;
        }
        unitPreference.setSummary(unitPreference.getEntries()[index]);
        String currentCity = mPrefs.getString("city", "Tbilisi");
        findPreference("city").setSummary(currentCity);
        getActivity().setTitle("Weather in " + currentCity);
    }


    @Override
    public void onResume() {
        super.onResume();
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initializeSettingsView();
    }
}
