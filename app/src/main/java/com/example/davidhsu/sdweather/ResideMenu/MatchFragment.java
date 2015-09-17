package com.example.davidhsu.sdweather.ResideMenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidhsu.sdweather.GPSTracker;
import com.example.davidhsu.sdweather.R;
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


        return matchParentView;
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
        mTextView.setText("現在的溫度是 : " + mCurrentWeather.getTemperature() +" 度，比較適合穿....");

        
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
