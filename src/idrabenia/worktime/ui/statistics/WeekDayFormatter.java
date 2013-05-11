package idrabenia.worktime.ui.statistics;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class WeekDayFormatter {
	public static final int DAYS_COUNT_PER_WEEK = 7;
	public final int FIRST_DAY_OF_WEEK;
	public final Locale locale;
	
	public WeekDayFormatter() {
		FIRST_DAY_OF_WEEK = Calendar.getInstance().getFirstDayOfWeek();
		locale = Locale.getDefault();
	}
	
	public WeekDayFormatter(Locale locale) {
		FIRST_DAY_OF_WEEK = Calendar.getInstance(locale).getFirstDayOfWeek();
		this.locale = locale;
	}
	
	/**
	 * Method returns chart coordinate for specified week day 
	 * @param calendarWeekDayIndex
	 * @return
	 */
	public int getDayCoordinate(int calendarWeekDayIndex) {
		checkCalendarDayRange(calendarWeekDayIndex);
		
		int index = calendarWeekDayIndex - FIRST_DAY_OF_WEEK;
		if (index < 0) {
			index += DAYS_COUNT_PER_WEEK;
		}
		
		return index;
	}
		
	/**
	 * Method returns short title of specified day of week
	 * @param calendarWeekDayIndex
	 * @return
	 */
	public String getWeekDayTitle(int calendarWeekDayIndex) {
		checkCalendarDayRange(calendarWeekDayIndex);
		
		return new DateFormatSymbols(locale).getShortWeekdays()[calendarWeekDayIndex];
	}

	private void checkCalendarDayRange(int calendarDateIndex) {
		if (calendarDateIndex < 1 || calendarDateIndex > 7) {
			throw new IllegalArgumentException("calendarDateRange < 1 || calendarDateRange > 7");
		}
	}
}
