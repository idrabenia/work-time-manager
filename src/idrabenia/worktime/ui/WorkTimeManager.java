package idrabenia.worktime.ui;

import idrabenia.worktime.R;
import idrabenia.worktime.domain.calculation.TimerService;
import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.date.TimeFormatter;
import idrabenia.worktime.domain.wifi.WifiNetworkAdapter;
import idrabenia.worktime.ui.calculation.BackgroundService;
import idrabenia.worktime.ui.settings.SettingsActivity;
import idrabenia.worktime.ui.statistics.StatisticsActivity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class WorkTimeManager extends Activity {
    public static final int REFRESH_PERIOD = 30 * 1000; // in milliseconds

    private TimeFormatter timeFormatter = new TimeFormatter();
    private TimerService timerService;
    private WifiNetworkAdapter wifiAdapter;
    private Timer refreshTimer;
    private AlertDialog resetDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        BackgroundService.start(this);
        timerService = TimerService.getInstance(this);
        wifiAdapter = new WifiNetworkAdapter(this);
        
        validateWifiState();
    }
    
    private void validateWifiState() {
    	if (!wifiAdapter.isWifiEnabled()) {
    		new AlertDialog.Builder(this)
    			.setTitle(R.string.wifi_not_enabled)
    			.setMessage(R.string.need_to_enable_wifi)
    			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
    			.show();
    	}
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduleRefreshTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (refreshTimer != null) {
            refreshTimer.cancel();
        }

        if (resetDialog != null && resetDialog.isShowing()) {
            resetDialog.cancel();
        }
    }

    private void scheduleRefreshTimer() {
        refreshTimer = new Timer("Refresh Timer Value Timer");
        refreshTimer.schedule(new TimerTask() {
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
        if (timerService == null) {
            return;
        }

        Time value = timerService.getTimerValue();
        String textValue = timeFormatter.format(value);

        ((TextView) findViewById(R.id.timer_value)).setText(textValue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.time_manager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean resetTimeCalculator(MenuItem item) {
        resetDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.timer_reset)
                .setMessage(R.string.approve_reset_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timerService.reset();
                        refreshTimeValue();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create();
        resetDialog.show();

        return true;
    }

    public boolean showSettingsActivity(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    public boolean showStatisticsActivity(MenuItem item) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
        return true;
    }

}

