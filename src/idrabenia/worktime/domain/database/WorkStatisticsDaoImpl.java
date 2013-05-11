package idrabenia.worktime.domain.database;

import idrabenia.worktime.domain.statistics.DayStatistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

public class WorkStatisticsDaoImpl implements WorkStatisticsDao {

    private final WorkTimeDbHelper dbHelper;

    public WorkStatisticsDaoImpl(Context context) {
        dbHelper = new WorkTimeDbHelper(context);
    }

//    public List<DayStatistics> loadAllStatistics() {
//        Cursor result = dbHelper.getReadableDatabase().rawQuery("select day, work_time from work_time order by day asc",
//                null);
//
//        try {
//            List<DayStatistics> statistics = new ArrayList<DayStatistics>(result.getCount());
//
//            while (result.moveToNext()) {
//                statistics.add(new DayStatistics(result.getLong(0), result.getLong(1)));
//            }
//
//            return statistics;
//        } finally {
//            result.close();
//        }
//    }

    public List<DayStatistics> loadLastWeekStatistics() {
        Cursor result = dbHelper.getReadableDatabase().rawQuery(
        		"select day, work_time from work_time where day >= ? order by day asc",
                new String[] { getFirstDayOfCurrentWeek().toString() });

        try {
            List<DayStatistics> statistics = new ArrayList<DayStatistics>(result.getCount());

            while (result.moveToNext()) {
            	statistics.add(new DayStatistics(result.getLong(0), result.getLong(1)));
            }

            return statistics;
        } finally {
            result.close();
        }
    }
    
    private Long getFirstDayOfCurrentWeek() {
        GregorianCalendar curDate = new GregorianCalendar();
        curDate.setTime(new Date());
        int curWeekNumber = curDate.get(Calendar.WEEK_OF_YEAR);
    	
        GregorianCalendar firstDayOfWeek = new GregorianCalendar();
        firstDayOfWeek.clear();

        firstDayOfWeek.set(Calendar.WEEK_OF_YEAR, curWeekNumber);
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, firstDayOfWeek.getFirstDayOfWeek());
        firstDayOfWeek.set(Calendar.YEAR, curDate.get(Calendar.YEAR));
        
        return firstDayOfWeek.getTimeInMillis();
    }

    public void close() {
        dbHelper.close();
    }

}
