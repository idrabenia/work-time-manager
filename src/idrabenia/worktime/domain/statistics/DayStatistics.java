package idrabenia.worktime.domain.statistics;

import idrabenia.worktime.domain.date.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Ilya Drabenia
 * @since 08.05.13
 */
public class DayStatistics {
    public final Date day;
    public final Time workedTime;

    public DayStatistics(long day, long workedTime) {
        this.day = new Date(day);
        this.workedTime = new Time(workedTime);
    }

    public double getWorkedHours() {
        return workedTime.hour + workedTime.minute / 60.0;
    }

    public int getDayOfWeek() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}
