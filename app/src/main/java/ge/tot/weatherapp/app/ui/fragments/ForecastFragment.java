package ge.tot.weatherapp.app.ui.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ge.tot.weatherapp.R;
import ge.tot.weatherapp.app.events.IncrementAchievementEvent;
import ge.tot.weatherapp.app.events.ShowWeatherCollageEvent;
import ge.tot.weatherapp.app.events.UnlockAchievementEvent;
import ge.tot.weatherapp.di.ServiceProvider;
import ge.tot.weatherapp.model.Forecast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ForecastFragment extends Fragment {

    @InjectView(R.id.forecast_icon) ImageView iconImage;
    @InjectView(R.id.forecast_day_temp) TextView dayTempText;
    @InjectView(R.id.forecast_night_temp) TextView nightTempText;
    @InjectView(R.id.forecast_description) TextView descriptionText;

    private Forecast forecast;
    private String cameraPicturePath;

    public static ForecastFragment newInstance(Forecast forecast) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable("forecast", forecast);
        fragment.setArguments(args);
        return fragment;
    }

    public ForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initFromBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_capture_photo) {
            openCamera();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit = settings.getString("unit", "metric");
        String unitSign = unit.equals("imperial") ? "F" : "C";
        dayTempText.setText((int) forecast.getDayTemp() + "°" + unitSign);
        nightTempText.setText((int) forecast.getNightTemp() + "°" + unitSign);
        descriptionText.setText(WordUtils.capitalize(forecast.getDescription()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // workaround because onActivityResult is called before onStart and event posting is not working
            // TODO: implement better solution
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ServiceProvider.getInstance().provideBus().post(new ShowWeatherCollageEvent(cameraPicturePath, forecast));
                }
            }, 500);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (forecast.getDescription().toLowerCase().contains("rain")) {
            ServiceProvider.getInstance().provideBus().post(new UnlockAchievementEvent(getString(R.string.achievement_id_rainy_day)));
            ServiceProvider.getInstance().provideBus().post(new IncrementAchievementEvent(getString(R.string.achievement_id_5_rainy_days)));
        } else if (forecast.getDescription().toLowerCase().contains("sky is clear")) {
            ServiceProvider.getInstance().provideBus().post(new UnlockAchievementEvent(getString(R.string.achievement_id_sunny_day)));
            ServiceProvider.getInstance().provideBus().post(new IncrementAchievementEvent(getString(R.string.achievement_id_5_sunny_days)));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("forecast", forecast);
        outState.putString("cameraPicturePath", cameraPicturePath);
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

    public void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),  "pic" + System.currentTimeMillis() + ".jpg");
        cameraPicturePath = photo.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(intent, 0);
    }

    private void initFromBundle(Bundle bundle) {
        forecast = (Forecast) bundle.getSerializable("forecast");
        cameraPicturePath = bundle.getString("cameraPicturePath");
    }

    private static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
