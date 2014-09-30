package ge.tot.weatherapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GCMRegistrationService extends IntentService {

    private static final String TAG = GCMRegistrationService.class.getSimpleName();

    private static final String SENDER_ID = "245519464677";
    public static final String HAVE_REGISTRATION_ID = "have_registration_id";

    public GCMRegistrationService() {
        super(GCMRegistrationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (shouldGoForRegistrationId()) {
            GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
            try {
                String registrationId = googleCloudMessaging.register(SENDER_ID);
                Log.d(TAG, "Received registration id: " + registrationId);
                storeSuccessfulRegistrationId();
            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
            }
        } else {
            Log.d(TAG, "Already have a registration_id. Skipping.");
        }
    }

    private boolean shouldGoForRegistrationId() {
        return !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(HAVE_REGISTRATION_ID, false);
    }

    private void storeSuccessfulRegistrationId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putBoolean(HAVE_REGISTRATION_ID, true)
                .commit();
    }
}
