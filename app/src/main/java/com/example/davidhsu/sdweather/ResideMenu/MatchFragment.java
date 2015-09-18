package com.example.davidhsu.sdweather.ResideMenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidhsu.sdweather.GPSTracker;
import com.example.davidhsu.sdweather.R;
import com.example.davidhsu.sdweather.sqlDatabase.MySQLiteOpenHelper;
import com.example.davidhsu.sdweather.sqlDatabase.Spot;
import com.example.davidhsu.sdweather.weather.AlertDialogFragment;
import com.example.davidhsu.sdweather.weather.CurrentWeather;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午3:28
 * Mail: specialcyci@gmail.com
 */
public class MatchFragment extends Fragment {

    private View matchParentView;
    private double doubleLongitude;
    private double doubleLatitude;
    private TextView mTextView;
    private ImageView ivSpot;
    private TextView tvId;
    private MySQLiteOpenHelper helper;
    private byte[] image;
    private final int tem = 21;
    private List<Spot> spotList1;
    private List<Spot> spotList2;
    private int index;

    public static final String TAG = MatchFragment.class.getSimpleName();

    private CurrentWeather mCurrentWeather;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        matchParentView = inflater.inflate(R.layout.match_layout, container, false);

        mTextView = (TextView)matchParentView.findViewById(R.id.tvTemp);



        //TrackGPS
        GPSTracker gpsTracker = new GPSTracker(getActivity().getApplicationContext());
        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            doubleLatitude = gpsTracker.getLatitude();
            //latitude = doubleLatitude;
            doubleLongitude = gpsTracker.getLongitude();
            //longitude = doubleLongitude;
        }



        getForecast(doubleLatitude, doubleLongitude); //latitude, longitude


        Log.d(TAG, "Main UI is running!!");


        if (helper == null) {
            helper = new MySQLiteOpenHelper(getActivity());
        }
        spotList1 =helper.getShortSpots();
        spotList2 =helper.getLongSpots();
        showSpotsMatch(0);

        Button btnMatchNext = (Button)matchParentView.findViewById(R.id.btnMatchNext);
        btnMatchNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if (index >= spotList1.size()) {
                    index = 0;
                }
                showSpotsMatch(index);
            }
        });

        Button btnMatchBack = (Button)matchParentView.findViewById(R.id.btnMatchBack);
        btnMatchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                if (index < 0) {
                    index = spotList1.size() - 1;
                }
                showSpotsMatch(index);
            }
        });

        return matchParentView;
    }


    private void showSpotsMatch(int index) {
        ivSpot = (ImageView) matchParentView.findViewById(R.id.imageView);
        //tvId = (TextView) matchParentView.findViewById(R.id.tvId);



        if (helper == null) {
            helper = new MySQLiteOpenHelper(getActivity());
        }



        if (tem<=24){
            Spot spot = spotList1.get(index);
            image = spot.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,
                    spot.getImage().length);
            ivSpot.setImageBitmap(bitmap);
            //tvId.setText(Integer.toString(spot.getId()));
        }
        else if (tem>=24) {
            Spot spot = spotList2.get(index);
            image = spot.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,
                    spot.getImage().length);
            ivSpot.setImageBitmap(bitmap);
            //tvId.setText(Integer.toString(spot.getId()));
        }

    }

    public void onNextClickMatch(View view) {
        index++;
        if (index >= spotList1.size()) {
            index = 0;
        }
        showSpotsMatch(index);
    }

    public void onBackClickMatch(View view) {
        index--;
        if (index < 0) {
            index = spotList1.size() - 1;
        }
        showSpotsMatch(index);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.close();
        }
    }






    private void getForecast(double latitude, double longitude) {
        String apiKey = "d405c6cfa70d6a1489f2a63e7d484bf1";

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/"+ latitude + "," + longitude;



        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {

                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);

                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e)
                    {
                        Log.e(TAG, "Exception caught: ", e);
                    }

                }
            });

        }else{
            Toast.makeText(getActivity(), R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        mTextView.setText("現在的溫度是 : " + mCurrentWeather.getTemperature() + " 度，比較適合穿....");


    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG,"From JSON : " + timezone);

        //從JSON資料中的currently 找資料
        JSONObject currently = forecast.getJSONObject("currently");

        //已經進入currently了  現在要從currently中獲取currently類別的資料
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));  //"" 中打的資料名稱必須和 原本JSON資料來源中的一樣
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getActivity().getFragmentManager(), "error_dialog");
    }



}