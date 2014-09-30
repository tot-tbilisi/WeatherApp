package ge.tot.weatherapp.app.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import ge.tot.weatherapp.R;
import ge.tot.weatherapp.model.Forecast;

/**
 * WeatherListAdapter
 */
public class WeatherListAdapter extends ArrayAdapter<Forecast> {

    public WeatherListAdapter(Context context, List<Forecast> objects) {
        super(context, R.layout.list_item_weather, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Forecast forecast = getItem(position);
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_weather, parent, false);

        ImageView iconImage = ButterKnife.findById(itemView, R.id.weather_list_item_icon);
        Picasso.with(getContext()).load(forecast.getIconUrl()).into(iconImage);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");

        TextView infoText = ButterKnife.findById(itemView, R.id.weather_list_item_info);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String unit = prefs.getString("unit", "metric");
        String mark;
        if ("metric".equals(unit)) {
            mark = "C";
        } else {
            mark = "F";
        }
        String description = String.format("%s (%d°" + mark + ")",
                dateFormat.format(forecast.getDate()),
                (int) forecast.getDayTemp());
        infoText.setText(description);

        if (position % 2 != 0) {
            itemView.setBackgroundColor(Color.parseColor("#e0e0e0"));
        }
        return itemView;
    }
}
