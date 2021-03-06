package ge.tot.weatherapp.app.ui.fragments;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationFragment extends Fragment
        implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private LocationClient locationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(getActivity(), this, this);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
        locationRequest.setInterval(5000);
        return locationRequest;
    }

    @Override
    public void onStart() {
        super.onStart();
        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (statusCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(statusCode, getActivity(), 0).show();
        } else {
            locationClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationClient.requestLocationUpdates(getLocationRequest(), this);
        Location lastLocation = locationClient.getLastLocation();
        if (lastLocation != null) {
            onLocationChanged(lastLocation);
        } else {
            Toast.makeText(getActivity(), "Last location is not available - waiting",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getActivity(), location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }
}
