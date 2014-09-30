package ge.tot.weatherapp.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.ui.MyActivity;

public class GCMHandlerService extends IntentService {

    private static final String TAG = GCMHandlerService.class.getSimpleName();

    public GCMHandlerService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            onHandleIntentImpl(intent);
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void onHandleIntentImpl(Intent intent) {
        String messageBody = intent.getStringExtra("custom_message");
        Log.d(TAG, "Received message: " + messageBody);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent activityIntent = new Intent(this, MyActivity.class);
        Notification notification = builder
                .setContentTitle("New message")
                .setContentText(messageBody)
                .setTicker(messageBody)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setLights(0x00ff0000, 500, 500)
                .setContentIntent(PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
