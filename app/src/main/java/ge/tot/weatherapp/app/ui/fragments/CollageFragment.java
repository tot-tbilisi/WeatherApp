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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    @InjectView(R.id.collage_photo) ImageView collageImage;

    private String picImagePath;
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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit = settings.getString("unit", "metric");
        String unitSign = unit.equals("imperial") ? "F" : "C";
        Bitmap picBitmap = BitmapFactory.decodeFile(picImagePath);
        Bitmap collageBitmap = drawTextToBitmap(picBitmap,
                Integer.toString((int)forecast.getDayTemp()) + "°" + unitSign);
        collageImage.setImageBitmap(collageBitmap);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("picImagePath", picImagePath);
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
        Uri bmpUri = Uri.parse(picImagePath);
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/png");
        startActivity(shareIntent);
    }

    private void initFromArguments(Bundle bundle) {
        picImagePath = bundle.getString("picImagePath");
        forecast = (Forecast) bundle.getSerializable("forecast");
    }

    public static Bitmap drawTextToBitmap(Bitmap bitmap, String text) {

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(400);
        paint.setStrokeWidth(10);
        paint.setColor(Color.parseColor("#ffffff"));

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - (bounds.width() + 150));
        int y = (bitmap.getHeight() - bounds.height());

        c.drawText(text, x, y, paint);

        return bitmap;
    }
}
