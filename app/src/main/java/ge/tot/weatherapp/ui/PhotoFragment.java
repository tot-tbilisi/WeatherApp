package ge.tot.weatherapp.ui;


import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ge.tot.weatherapp.R;
import ge.tot.weatherapp.model.Forecast;
import ge.tot.weatherapp.otto.BusProvider;
import ge.tot.weatherapp.otto.WeatherItemClickedEvent;
import ge.tot.weatherapp.protocol.Response;


/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class PhotoFragment extends Fragment {

    ImageView photo;
    Button sharePhoto;

    public PhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);

        photo = (ImageView) rootView.findViewById(R.id.photo);
        sharePhoto = (Button) rootView.findViewById(R.id.button_photo_share);

        final Bitmap photoBitmap = ((MyActivity)getActivity()).getPhotoBitmap();

        photo.setImageBitmap(photoBitmap);

        sharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePhoto(photoBitmap);
            }
        });

        return rootView;
    }

    public void sharePhoto(Bitmap bitmap) {
        String pathofBmp = MediaStore.Images.Media.insertImage(
                getActivity().getContentResolver(), bitmap, "title", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/png");
        startActivity(shareIntent);
    }
}
