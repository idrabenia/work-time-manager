package idrabenia.worktime.domain.database;

import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.statistics.DayStatistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WorkStatisticsDaoImpl implements WorkStatisticsDao {
	private static final String QUERY_LAST_WEEK_STATISTICS 
			= "select day, work_time from work_time where day >= ? order by day asc";
	private static final String QUERY_LAST_WEEK_TOTAL_HOURS 
			= "select sum(work_time) from work_time where day >= ?"; 

    private final WorkTimeDbHelper dbHelper;

    public WorkStatisticsDaoImpl(Context context) {
        dbHelper = new WorkTimeDbHelper(context);
    }

    @Override
    public List<DayStatistics> loadLastWeekStatistics() {
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery(QUERY_LAST_WEEK_STATISTICS, asArray(getFirstDayOfCurrentWeek()));

        try {
            List<DayStatistics> statistics = new ArrayList<DayStatistics>(result.getCount());

            while (result.moveToNext()) {
            	statistics.add(new DayStatistics(result.getLong(0), result.getLong(1)));
            }

            return statistics;
        } finally {
            result.close();
            db.close();
        }
    }
    
    @Override
	public Time calculateLastWeekTotalWorkedTime() {
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(QUERY_LAST_WEEK_TOTAL_HOURS, asArray(getFirstDayOfCurrentWeek()));
    	
    	try {
    		long totalTime = 0L;
    		
    		if (cursor.getCount() == 1) {
    			cursor.moveToFirst();
    			totalTime = cursor.getLong(0);
    		} 
    		
			return new Time(totalTime);
    	} finally {
    		cursor.close();
    	}
	}
    
    private String[] asArray(Object... args) {
    	String[] stringValues = new String[args.length];
    	
    	for (int i = 0; i < args.length; i++) {
    		stringValues[i] = args[i].toString();
    	}
    	
    	return stringValues;
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
