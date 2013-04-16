package idrabenia.worktime.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import idrabenia.worktime.R;
import idrabenia.worktime.domain.calculation.TimeCalculationService;
import idrabenia.worktime.ui.calculation.CalculationServiceBinder;
import idrabenia.worktime.ui.calculation.TimeCalculationAndroidService;

import java.util.Timer;
import java.util.TimerTask;

public class WorkTimeManager extends Activity {
    public static final int REFRESH_PERIOD = 30 * 1000; // in milliseconds

    private TimeCalculationService timeCalculationService;
    private final Timer refreshTimeValueTimer = new Timer("Refresh Timer Value Timer");
    private GoogleMap map;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timeCalculationService = ((CalculationServiceBinder) service).getService();

            showCurrentWorkPlaceMarker();
            scheduleRefreshTimeTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timeCalculationService = null;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                startTimeCalculation(latLng);
            }
        });

        Intent timerServiceIntent = new Intent(this, TimeCalculationAndroidService.class);
        startService(timerServiceIntent);
        bindService(timerServiceIntent, connection, BIND_AUTO_CREATE);
    }

    private void showCurrentWorkPlaceMarker() {
        double[] location = timeCalculationService.getWorkLocation();
        if (location != null) {
            LatLng curWorkLocation = new LatLng(location[0], location[1]);
            addWorkPlaceMarker(curWorkLocation);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curWorkLocation, 15));
        }
    }

    private void scheduleRefreshTimeTask() {
        refreshTimeValueTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                WorkTimeManager.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshTimeValue();
                    }
                });
            }
        }, 0, REFRESH_PERIOD);
    }

    private void refreshTimeValue() {
        Long value = timeCalculationService.getTimerValue();
        if (value != null) {
            ((TextView) findViewById(R.id.timer_value)).setText(value / (60 * 60 * 1000) + ":"
                    + value % (60 * 60 * 1000) / (60 * 1000));
        } else {
            ((TextView) findViewById(R.id.timer_value)).setText(getString(R.string.zero_time_value));
        }
    }

    private void startTimeCalculation(LatLng latLng) {
        if (timeCalculationService.getWorkLocation() == null) {
            addWorkPlaceMarker(latLng);
            timeCalculationService.start(latLng.latitude, latLng.longitude);
        }
    }

    private void addWorkPlaceMarker(LatLng location) {
        map.addMarker(new MarkerOptions().position(location).title(getString(R.string.work_place)));
    }

    public void resetTimeCalculator(View source) {
        timeCalculationService.reset();
        map.clear();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        refreshTimeValueTimer.cancel();
        super.onDestroy();
    }
}

