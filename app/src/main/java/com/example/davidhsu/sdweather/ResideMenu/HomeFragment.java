package com.example.davidhsu.sdweather.ResideMenu;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.davidhsu.sdweather.GPSTracker;
import com.example.davidhsu.sdweather.MainActivity;
import com.example.davidhsu.sdweather.R;
import com.example.davidhsu.sdweather.weather.AlertDialogFragment;
import com.example.davidhsu.sdweather.weather.CurrentWeather;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午1:33
 * Mail: specialcyci@gmail.com
 */
public class HomeFragment extends Fragment {

    private View parentView;
    private ResideMenu resideMenu;

    public static final String TAG = HomeFragment.class.getSimpleName();

    private CurrentWeather mCurrentWeather;

    //用ButterKnife 套件可以用 @bind 取代繁雜的 TextView mTextView = (TextView)findViewById(R.id.mTextView);
   /* @Bind(R.id.timeLabel) TextView mTimeLabel;
    @Bind(R.id.temperatureLabel) TextView mTemperatureLabel;
    @Bind(R.id.humidityValue) TextView mHumidityValue;
    @Bind(R.id.precipValue) TextView mPrecipValue;
    @Bind(R.id.summaryLabel) TextView mSummaryLabel;
    @Bind(R.id.iconImageView) ImageView mIconImageView;
    @Bind(R.id.refreshImageView)ImageView mRefreshImageView;
    @Bind(R.id.locationLabel) TextView mLocationLabel;
*/

    private TextView mTimeLabel;
    private TextView mTemperatureLabel;
    private TextView mHumidityValue;
    private TextView mPrecipValue;
    private TextView mSummaryLabel;
    private TextView mLocationLabel;
    private ImageView mIconImageView;
    private ImageView mRefreshImageView;


    private double latitude;
    private double longitude;
    private double doubleLongitude;
    private double doubleLatitude;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);  //parentView = ..... //R.layout.home
//        setUpViews();
        //ButterKnife.bind(getActivity());

        //ButterKnife不行就用這個code.
        mTimeLabel = (TextView) parentView.findViewById(R.id.timeLabel);
        mTemperatureLabel = (TextView)parentView.findViewById(R.id.temperatureLabel);
        mHumidityValue = (TextView)parentView.findViewById(R.id.humidityValue);
        mPrecipValue = (TextView)parentView.findViewById(R.id.precipValue);
        mSummaryLabel = (TextView)parentView.findViewById(R.id.summaryLabel);
        mIconImageView = (ImageView)parentView.findViewById(R.id.iconImageView);
        mRefreshImageView = (ImageView)parentView.findViewById(R.id.refreshImageView);
        mLocationLabel = (TextView)parentView.findViewById(R.id.locationLabel);

         //final double latitude = 25.0329694;
         //final double longitude = 121.5654177;

        //TrackGPS
        GPSTracker gpsTracker = new GPSTracker(getActivity().getApplicationContext());
        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            doubleLatitude = gpsTracker.getLatitude();
            //latitude = doubleLatitude;
            doubleLongitude = gpsTracker.getLongitude();
            //longitude = doubleLongitude;
        }

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getForecast(doubleLatitude, doubleLongitude);  //latitude, longitude
            }
        });

        getForecast(doubleLatitude, doubleLongitude); //latitude, longitude


        Log.d(TAG, "Main UI is running!!");



        return parentView;  //return parentView;


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
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() +"");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + "it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + " %");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mLocationLabel.setText(mCurrentWeather.getTimeZone());

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
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


//因為要把Homefragment原本的Ignore和button刪掉所以註腳調下面的程式碼
//    private void setUpViews() {
//       MainActivity parentActivity = (MainActivity) getActivity();
//        resideMenu = parentActivity.getResideMenu();
//
//        parentView.findViewById(R.id.btn_open_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
//            }
//        });
//
//        // add gesture operation's ignored views
//        FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
//        resideMenu.addIgnoredView(ignored_view);
//    }


