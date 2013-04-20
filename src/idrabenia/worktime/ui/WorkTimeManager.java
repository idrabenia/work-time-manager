package idrabenia.worktime.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import idrabenia.worktime.R;
import idrabenia.worktime.domain.calculation.TimeCalculationService;
import idrabenia.worktime.ui.calculation.CalculationServiceBinder;
import idrabenia.worktime.ui.calculation.TimeCalculationAndroidService;

import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class WorkTimeManager extends Activity {
    public static final int REFRESH_PERIOD = 30 * 1000; // in milliseconds
    public static final int MINUTES_PER_HOUR = 60;
    public static final String ELAPSED_TIME_FORMAT = "{0,number,00}:{1,number,00}";

    private TimeCalculationService timeCalculationService;
    private final Timer refreshTimeValueTimer = new Timer("Refresh Timer Value Timer");

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timeCalculationService = ((CalculationServiceBinder) service).getService();

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

        Intent timerServiceIntent = new Intent(this, TimeCalculationAndroidService.class);
        startService(timerServiceIntent);
        bindService(timerServiceIntent, connection, BIND_AUTO_CREATE);
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

        String textValue;
        if (value != null) {
            textValue = MessageFormat.format(ELAPSED_TIME_FORMAT, MILLISECONDS.toHours(value),
                    MILLISECONDS.toMinutes(value) % MINUTES_PER_HOUR);
        } else {
            textValue = getString(R.string.zero_time_value);
        }

        ((TextView) findViewById(R.id.timer_value)).setText(textValue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.time_manager_menu, menu);

        menu.findItem(R.id.menu_reset).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                resetTimeCalculator();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void resetTimeCalculator() {
        timeCalculationService.reset();
        refreshTimeValue();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        refreshTimeValueTimer.cancel();
        super.onDestroy();
    }
}

