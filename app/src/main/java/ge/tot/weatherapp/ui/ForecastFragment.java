package ge.tot.weatherapp.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
        dayTempText.setText((int) forecast.getDayTemp() + "°C");
        nightTempText.setText((int) forecast.getNightTemp() + "°C");
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

    @SuppressWarnings("UnusedDeclaration") // Used by injector
    @OnClick(R.id.forecast_icon)
    /*injected*/ void onIconClick() {
        final float dp48 = dipToPixels(getActivity(), 48);
        final ObjectAnimator shiftAnimator = ObjectAnimator.ofFloat(iconImage, "translationX", 0, +dp48);
        shiftAnimator.setDuration(750);
        shiftAnimator.setInterpolator(new LinearInterpolator());
        shiftAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                final ObjectAnimator walkAnimator = ObjectAnimator.ofFloat(iconImage, "translationX", dp48, -dp48);
                walkAnimator.setDuration(1500);
                walkAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                walkAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                walkAnimator.setInterpolator(new LinearInterpolator());
                walkAnimator.start();
            }
        });
        shiftAnimator.start();
    }

    private static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
