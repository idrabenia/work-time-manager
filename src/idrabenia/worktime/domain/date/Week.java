package idrabenia.worktime.domain.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Week {
	private final int weekOffset;
	private final Date dayOfBaseWeek;

	/**
	 * Construct object that represent week using current week as base week and offset for current week
	 * @param weekOffset is number that represent target week offset relative to base week, 
	 * for example last week number is -1, next week number is 1
	 */
	public Week(int weekOffset) {
		this(new Date(), weekOffset);
	}
	
	public Week(Date dayOfBaseWeek, int weekOffset) {
		this.dayOfBaseWeek = dayOfBaseWeek;
		this.weekOffset = weekOffset;
	}
	
	private int getNumberOfBaseWeek() {
		GregorianCalendar curDate = new GregorianCalendar();
        curDate.setTime(dayOfBaseWeek);
        return curDate.get(Calendar.WEEK_OF_YEAR);
	}
	
	private int getYearOfBaseWeek() {
		GregorianCalendar curDate = new GregorianCalendar();
        curDate.setTime(dayOfBaseWeek);
        return curDate.get(Calendar.YEAR);
	}

	private Calendar getFirstDayOfWeekAsCalendar(int relativeWeekNumber) {
        GregorianCalendar firstDayOfWeek = new GregorianCalendar();
        firstDayOfWeek.clear();

        firstDayOfWeek.set(Calendar.WEEK_OF_YEAR, getNumberOfBaseWeek() + relativeWeekNumber);
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, firstDayOfWeek.getFirstDayOfWeek());
        firstDayOfWeek.set(Calendar.YEAR, getYearOfBaseWeek());
        
        return firstDayOfWeek;
	}

	public Date getFirstDayOfWeek() {
		return getFirstDayOfWeekAsCalendar(weekOffset).getTime();
	}
	
	public Long getFirstDayOfWeekInMillis() {
        return getFirstDayOfWeekAsCalendar(weekOffset).getTimeInMillis();
	}
	
	private Calendar getLastDayOfWeekAsCalendar() {
		Calendar lastDayOfWeek = getFirstDayOfWeekAsCalendar(weekOffset);
		
        lastDayOfWeek.add(Calendar.DAY_OF_MONTH, 6);
        
        return lastDayOfWeek;
	}
	
	public Long getLastDayOfWeekInMillis() {
        return getLastDayOfWeekAsCalendar().getTimeInMillis();
	}
	
	public Date getLastDayOfWeek() {
        return getLastDayOfWeekAsCalendar().getTime();
	}
	
}
