package idrabenia.worktime.domain.preferences;

import idrabenia.worktime.R;
import idrabenia.worktime.domain.date.Time;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Ilya Drabenia
 * @since 21.04.13
 */
public class Preferences {
    private static final long DEFAULT_WORK_DAY_DURATION =  TimeUnit.HOURS.toMillis(8);

    private String WORK_DAY_DURATION_KEY;
    private Context context;

    public Preferences(Context context) {
        this.context = context;
        WORK_DAY_DURATION_KEY = context.getString(R.string.work_day_duration_key);
    }
    
    protected Preferences() {
    	
    }

    public String getWorkingNetworkSsid() {
        return getPreferences().getString(context.getString(R.string.network_ssid_key), "");
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String getWorkDayDurationKey() {
        return context.getString(R.string.work_day_duration_key);
    }

    public Time getWorkDayDuration() {
        long value = getPreferences().getLong(WORK_DAY_DURATION_KEY, DEFAULT_WORK_DAY_DURATION);
        return new Time(value);
    }

    public void setWorkDayDuration(Time value) {
        if (value == null) {
            throw new IllegalArgumentException("workDayDuration is null");
        }

        getPreferences().edit()
                .putLong(WORK_DAY_DURATION_KEY, value.toMillis())
                .commit();
    }
}
