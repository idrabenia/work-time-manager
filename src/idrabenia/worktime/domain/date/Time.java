package idrabenia.worktime.domain.date;

import java.util.concurrent.TimeUnit;

/**
 * @author Ilya Drabenia
 * @since 21.04.13
 */
public class Time {
    private static final int MINUTES_PER_HOUR = 60;

    public final int hour;
    public final int minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(long milliseconds) {
        hour = (int) TimeUnit.MILLISECONDS.toHours(milliseconds);
        minute = (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds) % MINUTES_PER_HOUR;
    }

    public long toMillis() {
        return TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
    }
    
}
