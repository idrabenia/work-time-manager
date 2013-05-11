package idrabenia.worktime.domain.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import idrabenia.worktime.domain.calculation.Timer;
import idrabenia.worktime.domain.notification.NotificationPanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * @author Ilya Drabenia
 * @since 27.04.13
 */
public class TimerActorDaoImpl implements TimerActorDao {
    private final String TIMER_ACTOR_TABLE = "work_time";
    private final String DAY_COLUMN = "day";
    private final String WORK_TIME_COLUMN = "work_time";
    private final String LAST_NOTIFICATION_TIME_COLUMN = "last_notification_time";
    private final String QUERY_TIMER = "select work_time from work_time where day = ?";
    private final String QUERY_NOTIFICATION_PANEL = "select last_notification_time from work_time where day = ?";

    private final WorkTimeDbHelper dbHelper;

    public TimerActorDaoImpl(Context context) {
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

    public class DayInfo {
        public int index;
        public double timeSpentAtWork;

        public DayInfo(int index, double timeSpentAtWork) {
            this.index = index;
            this.timeSpentAtWork = timeSpentAtWork;
        }
    }

    public List<DayInfo> loadAllStatistics() {
        Cursor result = dbHelper.getReadableDatabase().rawQuery("select day, work_time from work_time order by day asc",
                null);

        try {
            List<DayInfo> statistics = new ArrayList<DayInfo>(result.getCount());

            int i = 0;
            while (result.moveToNext()) {
                statistics.add(new DayInfo(i++, result.getLong(1) / (3600 * 1000.0)));
            }

            return statistics;
        } finally {
            result.close();
        }
    }

    public List<DayInfo> loadLastWeekStatistics() {
        Cursor result = dbHelper.getReadableDatabase().rawQuery("select day, work_time from work_time order by day asc",
                null);

        try {
            List<DayInfo> statistics = new ArrayList<DayInfo>(result.getCount());

            while (result.moveToNext()) {
                statistics.add(new DayInfo(0, result.getLong(1) / (3600 * 1000.0)));
            }

            return statistics;
        } finally {
            result.close();
        }
    }

    private void copyDb(Context context) throws Exception {
        FileChannel inChannel = new FileInputStream(new File("sdcard/worktime.db")).getChannel();
        FileChannel outChannel = new FileOutputStream(context.getDatabasePath("worktime.db")).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
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
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
