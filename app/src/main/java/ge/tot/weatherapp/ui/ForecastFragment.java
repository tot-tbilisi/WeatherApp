package ge.tot.weatherapp.ui;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ge.tot.weatherapp.model.Forecast;
import ge.tot.weatherapp.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ForecastFragment extends Fragment {
    private static final String ARG_FORECAST = "forecast";

    private Forecast forecast;


    public static ForecastFragment newInstance(Forecast forecast) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FORECAST, forecast);
        fragment.setArguments(args);
        return fragment;
    }
    public ForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFromBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView text = (TextView) view.findViewById(R.id.forecast_text);
        text.setText(forecast.toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_FORECAST, forecast);
    }

    private void initFromBundle(Bundle bundle) {
        forecast = (Forecast) bundle.getSerializable(ARG_FORECAST);
    }
}
