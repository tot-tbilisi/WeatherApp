package ge.tot.weatherapp.app.ui.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ge.tot.weatherapp.R;
import ge.tot.weatherapp.model.Forecast;


/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class CollageFragment extends Fragment {

    private class MakeCollageTask extends AsyncTask<Void, Void, Pair<String, Bitmap>> {

        @Override
        protected Pair<String, Bitmap> doInBackground(Void... params) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String cityName = settings.getString("city", "?");
            String unit = settings.getString("unit", "metric");
            String unitSign = unit.equals("imperial") ? "F" : "C";
            Bitmap picBitmap = BitmapFactory.decodeFile(picImagePath);
            Bitmap collageBitmap = drawForecastCollageOnBitmap(picBitmap, cityName, forecast, "°" + unitSign);
            File collageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "collage" + System.currentTimeMillis() + ".jpg");
            String collagePath = saveBitmapToFile(collageBitmap, collageFile, Bitmap.CompressFormat.JPEG);
            return new Pair<String, Bitmap>(collagePath, collageBitmap);
        }

        @Override
        protected void onPostExecute(Pair<String, Bitmap> result) {
            super.onPostExecute(result);
            if (getActivity() != null) {
                collageImagePath = result.first;
                collageImage.setImageBitmap(result.second);
            }
        }
    }

    @InjectView(R.id.collage_photo) ImageView collageImage;

    private String picImagePath;
    private String collageImagePath;
    private Forecast forecast;

    public static class Builder {
        private Bundle arguments;

        public Builder() {
            this.arguments = new Bundle();
        }

        public Builder picImagePath(String picImagePath) {
            arguments.putString("picImagePath", picImagePath);
            return this;
        }

        public Builder forecast(Forecast forecast) {
            arguments.putSerializable("forecast", forecast);
            return this;
        }

        public CollageFragment build() {
            CollageFragment collageFragment = new CollageFragment();
            collageFragment.setArguments(arguments);
            return collageFragment;
        }
    }

    public CollageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFromArguments(savedInstanceState == null ? getArguments() : savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        if (collageImagePath == null) {
            new MakeCollageTask().execute();
        } else {
            Bitmap collageBitmap = BitmapFactory.decodeFile(collageImagePath);
            collageImage.setImageBitmap(collageBitmap);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Weather Collage");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("picImagePath", picImagePath);
        outState.putString("collageImagePath", collageImagePath);
        outState.putSerializable("forecast", forecast);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @SuppressWarnings("UnusedDeclaration") // Used by injector
    @OnClick(R.id.collage_share)
    /*injected*/ void onShareClick() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(collageImagePath)));
        shareIntent.setType("image/jpeg");
        startActivity(shareIntent);
    }

    private void initFromArguments(Bundle bundle) {
        picImagePath = bundle.getString("picImagePath");
        collageImagePath = bundle.getString("collageImagePath");
        forecast = (Forecast) bundle.getSerializable("forecast");
    }

    private static Bitmap drawForecastCollageOnBitmap(Bitmap bitmap, String cityName,
                                                      Forecast forecast, String tempUnitSuffix) {

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTextSize(400.0f);
        paint.setStrokeWidth(2.0f);
        paint.setShadowLayer(50.0f, 10.0f, 10.0f, Color.BLACK);

        String date = new SimpleDateFormat("MMM d").format(forecast.getDate());
        String dayTemp = (int) forecast.getDayTemp() + tempUnitSuffix;

        Rect cityNameBound = new Rect();
        paint.getTextBounds(cityName, 0, dayTemp.length(), cityNameBound);
        c.drawText(cityName, 100, cityNameBound.height() + 100, paint);

        c.drawText(date, 100, bitmap.getHeight() - 150, paint);

        Rect dayTempBounds = new Rect();
        paint.getTextBounds(dayTemp, 0, dayTemp.length(), dayTempBounds);
        c.drawText(dayTemp, bitmap.getWidth() - dayTempBounds.width() - 100, bitmap.getHeight() - 150, paint);

        return bitmap;
    }

    private static String saveBitmapToFile(Bitmap bitmap, File file, Bitmap.CompressFormat compressFormat) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(compressFormat, 100, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
