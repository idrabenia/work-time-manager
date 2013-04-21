package idrabenia.worktime.test.domain.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import idrabenia.worktime.domain.date.DateWithoutTimeComparator;
import junit.framework.TestCase;

public class DateWithoutTimeComparatorTest extends TestCase {
	private DateWithoutTimeComparator comparator = new DateWithoutTimeComparator();
	
	public void testCompareDatesAtSimilarDay() {
		Calendar date1 = new GregorianCalendar();
		date1.set(2009, 10, 10, 10, 30);
		
		Calendar date2 = new GregorianCalendar();
		date2.set(2009, 10, 10, 9, 10);
		
		int result = comparator.compare(date1.getTime(), date2.getTime());
		
		assertEquals(0, result);
	}
	
	public void testCompareDateAtDifferentDays() {
		Calendar date1 = new GregorianCalendar();
		date1.set(2009, 10, 10, 10, 30);
		
		Calendar date2 = new GregorianCalendar();
		date2.set(2009, 10, 9, 9, 10);
		
		int result = comparator.compare(date1.getTime(), date2.getTime());
		
		assertTrue(result != 0);
	}

}
