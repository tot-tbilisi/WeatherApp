package ge.tot.weatherapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import ge.tot.weatherapp.service.GCMHandlerService;

public class GCMReceiver extends BroadcastReceiver {

    private static final String TAG = GCMReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GCMHandlerService.class);
        serviceIntent.putExtras(intent);
        WakefulBroadcastReceiver.startWakefulService(context, serviceIntent);
        Log.d(TAG, "Received some message!");
    }
}
