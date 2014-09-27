package ge.tot.weatherapp;



import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WeatherListFragment extends ListFragment {


    public WeatherListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Forecast> forecasts = Forecast.makeRandom(7);

        ArrayAdapter<Forecast> adapter = new ArrayAdapter<Forecast>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                forecasts);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Forecast forecast = (Forecast) getListAdapter().getItem(position);
        BusProvider.getBus().post(new WeatherItemClickedEvent(forecast));
    }
}
