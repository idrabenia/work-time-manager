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
	private static final String QUERY_WEEK_STATISTICS 
			= "select day, work_time from work_time where day >= ? and day <= ? order by day asc";
	private static final String QUERY_LAST_WEEK_TOTAL_HOURS 
			= "select sum(work_time) from work_time where day >= ? and day <= ?";
	private static final String QUERY_OLDEST_RECORD_DAY = "select min(day) from work_time";

    private final WorkTimeDbHelper dbHelper;

    public WorkStatisticsDaoImpl(Context context) {
        dbHelper = new WorkTimeDbHelper(context);
    }

    @Override
    public List<DayStatistics> loadWeekStatisticsFor(int relativeWeekNumber) {
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery(QUERY_WEEK_STATISTICS, asArray(
        		getFirstDayOfWeek(relativeWeekNumber), 
        		getLastDayOfWeek(relativeWeekNumber)));

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
	public Time calculateTotalWorkedTimeFor(int relativeWeekNumber) {
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(QUERY_LAST_WEEK_TOTAL_HOURS, asArray(getFirstDayOfWeek(relativeWeekNumber), 
    			getLastDayOfWeek(relativeWeekNumber)));
    	
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
    
    @Override
	public boolean isStatisticsAvailableFor(int relativeWeekNumber) {
    	if (relativeWeekNumber > 0) {
    		return false;
    	}
    	
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor result = db.rawQuery(QUERY_OLDEST_RECORD_DAY, null);
    	try {
    		if (result.getCount() == 1) {
    			result.moveToFirst();
    			long oldestRecordDay = result.getLong(0);
    			return oldestRecordDay <= getLastDayOfWeek(relativeWeekNumber);
    		} else {
    			return false;
    		}
    	} finally {
    		result.close();
    	}
	}

	private String[] asArray(Object... args) {
    	String[] stringValues = new String[args.length];
    	
    	for (int i = 0; i < args.length; i++) {
    		stringValues[i] = args[i].toString();
    	}
    	
    	return stringValues;
    }
	
	private int getCurrentWeekNumber() {
		GregorianCalendar curDate = new GregorianCalendar();
        curDate.setTime(new Date());
        return curDate.get(Calendar.WEEK_OF_YEAR);
	}
	
	private int getCurrentYear() {
		GregorianCalendar curDate = new GregorianCalendar();
        curDate.setTime(new Date());
        return curDate.get(Calendar.YEAR);
	}

	private Calendar getFirstDayOfWeekCalendar(int relativeWeekNumber) {    	
        GregorianCalendar firstDayOfWeek = new GregorianCalendar();
        firstDayOfWeek.clear();

        firstDayOfWeek.set(Calendar.WEEK_OF_YEAR, getCurrentWeekNumber() + relativeWeekNumber);
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, firstDayOfWeek.getFirstDayOfWeek());
        firstDayOfWeek.set(Calendar.YEAR, getCurrentYear());
        
        return firstDayOfWeek;
	}

	private Long getFirstDayOfWeek(int relativeWeekNumber) {    	        
        return getFirstDayOfWeekCalendar(relativeWeekNumber).getTimeInMillis();
	}
	
	private Long getLastDayOfWeek(int relativeWeekNumber) {
		Calendar lastDayOfWeek = getFirstDayOfWeekCalendar(relativeWeekNumber);
		
        lastDayOfWeek.add(Calendar.DAY_OF_MONTH, 6);
        
        return lastDayOfWeek.getTimeInMillis();
	}

    public void close() {
        dbHelper.close();
    }

}
