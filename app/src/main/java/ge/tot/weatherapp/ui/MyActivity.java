package ge.tot.weatherapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.otto.BusProvider;
import ge.tot.weatherapp.otto.WeatherItemClickedEvent;
import ge.tot.weatherapp.service.GCMRegistrationService;

public class MyActivity extends Activity {

    Bitmap photoBitmap;
    String dayTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            startService(new Intent(this, GCMRegistrationService.class));
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new WeatherListFragment(), "weather_list")
                    .add(new LocationFragment(), "location")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_capture_photo) {
            openCamera();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            if (bp != null) {
                photoBitmap = drawTextToBitmap(bp, dayTemp);
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new PhotoFragment(), "photo_fragment")
                        .addToBackStack("forecast")
                        .commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String city = prefs.getString("city", "Tbilisi");
        setTitle("Weather in " + city);
    }

    public void setDayTemp(String dayTemp) {
        this.dayTemp = dayTemp + "°C";
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getBus().register(this);
    }

    @Subscribe
    public void onWeatherItemClicked(WeatherItemClickedEvent event) {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        ForecastFragment.newInstance(event.getForecast()),
                        "forecast")
                .addToBackStack("forecast")
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getBus().unregister(this);
    }

    public Bitmap drawTextToBitmap(Bitmap bitmap, String text) {

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.rgb(0, 0, 0));

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - (bounds.width() + 10));
        int y = (bitmap.getHeight() - bounds.height());

        c.drawText(text, x, y, paint);

        return bitmap;
    }
}
