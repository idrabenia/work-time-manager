package idrabenia.worktime.domain.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import idrabenia.worktime.domain.calculation.Timer;
import idrabenia.worktime.domain.notification.NotificationPanel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Ilya Drabenia
 * @since 27.04.13
 */
public class TimerActorDao {
    private final String TIMER_ACTOR_TABLE = "work_time";
    private final String DAY_COLUMN = "day";
    private final String WORK_TIME_COLUMN = "work_time";
    private final String LAST_NOTIFICATION_TIME_COLUMN = "last_notification_time";
    private final String QUERY_TIMER = "select work_time from work_time where day = ?";
    private final String QUERY_NOTIFICATION_PANEL = "select last_notification_time from work_time where day = ?";

    private final WorkTimeDbHelper dbHelper;

    public TimerActorDao(Context context) {
        dbHelper = new WorkTimeDbHelper(context);
    }

    public void saveTimerActor(Timer calculator, NotificationPanel panel) {
        ContentValues row = new ContentValues();
        row.put(DAY_COLUMN, getCurrentDateWithoutTime());
        row.put(WORK_TIME_COLUMN, calculator.getTimeValue());
        row.put(LAST_NOTIFICATION_TIME_COLUMN, panel.getLastNotificationTime());

        dbHelper.getWritableDatabase().replace(TIMER_ACTOR_TABLE, null, row);
    }

    public NotificationPanel loadOrCreateNotificationPanel(Context context) {
        Date curDate = new Date();
        Cursor result = dbHelper.getReadableDatabase().rawQuery(QUERY_NOTIFICATION_PANEL, new String[] {
                getCurrentDateWithoutTime(curDate).toString() });

        try {
            if (result.getCount() == 1) {
                result.moveToFirst();
                return new NotificationPanel(context, new Date(result.getLong(0)));
            } else {
                return new NotificationPanel(context);
            }
        } finally {
            result.close();
        }
    }

    public Timer loadOrCreateTimer() {
        Date curDate = new Date();
        Cursor result = dbHelper.getReadableDatabase().rawQuery(QUERY_TIMER, new String[] { Long.toString(
                getCurrentDateWithoutTime(curDate)) });

        try {
            if (result.getCount() == 1) {
                result.moveToFirst();
                return new Timer(result.getLong(0), curDate.getTime());
            } else {
                return new Timer();
            }
        } finally {
            result.close();
        }
    }

    public void close() {
        dbHelper.close();
    }

    private Long getCurrentDateWithoutTime() {
        Date curDate = new Date();
        return getCurrentDateWithoutTime(curDate);
    }

    private Long getCurrentDateWithoutTime(Date curDate) {
        Calendar calendar = new GregorianCalendar(curDate.getYear(), curDate.getMonth(), curDate.getDay());
        return calendar.getTimeInMillis();
    }
}
