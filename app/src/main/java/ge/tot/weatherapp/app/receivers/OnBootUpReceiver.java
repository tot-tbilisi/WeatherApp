package ge.tot.weatherapp.app.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ge.tot.weatherapp.app.services.WeatherUpdateService;

/**
 * Created by maui on 28.09.14.
 */
public class OnBootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent runWeatherServiceIntent = new Intent(context, WeatherUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, runWeatherServiceIntent, 0);


        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_HALF_HOUR,
                AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
    }
}
