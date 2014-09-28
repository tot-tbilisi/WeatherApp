package ge.tot.weatherapp.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ge.tot.weatherapp.R;
import ge.tot.weatherapp.model.Forecast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ForecastFragment extends Fragment {
    private static final String ARG_FORECAST = "forecast";

    @InjectView(R.id.forecast_icon) ImageView iconImage;
    @InjectView(R.id.forecast_day_temp) TextView dayTempText;
    @InjectView(R.id.forecast_night_temp) TextView nightTempText;
    @InjectView(R.id.forecast_description) TextView descriptionText;

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
        ButterKnife.inject(this, view);
        Picasso.with(getActivity()).load(forecast.getIconUrl()).into(iconImage);
        dayTempText.setText(forecast.getDayTemp() + "°C");
        nightTempText.setText(forecast.getNightTemp() + "°C");
        descriptionText.setText(forecast.getDescription());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_FORECAST, forecast);
    }

    private void initFromBundle(Bundle bundle) {
        forecast = (Forecast) bundle.getSerializable(ARG_FORECAST);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
