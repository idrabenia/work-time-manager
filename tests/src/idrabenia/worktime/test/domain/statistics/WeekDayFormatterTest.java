package idrabenia.worktime.test.domain.statistics;

import java.util.Calendar;
import java.util.Locale;

import idrabenia.worktime.ui.statistics.WeekDayFormatter;
import junit.framework.TestCase;

public class WeekDayFormatterTest extends TestCase {
	
	private WeekDayFormatter makeMondayStartsWeekFormatter() {
		return new WeekDayFormatter(Locale.UK);
	}
	
	private WeekDayFormatter makeSundayStartsWeekFormatter() {
		return new WeekDayFormatter(Locale.US);
	}
	
	public void testGetWeekDayIndexForMondayOnWeekStartsFromMonday() {
		assertEquals(0, makeMondayStartsWeekFormatter().getWeekDayIndex(Calendar.MONDAY));
	}
	
	public void testGetWeekDayIndexForSundayOnWeekStartsFromMonday() {
		assertEquals(6, makeMondayStartsWeekFormatter().getWeekDayIndex(Calendar.SUNDAY));
	}
	
	public void testGetWeekDayIndexForSundayOnWeekStartsFromSunday() {
		assertEquals(0, makeSundayStartsWeekFormatter().getWeekDayIndex(Calendar.SUNDAY));
	}
	
	public void testGetWeekDayIndexForSaturdayOnWeekStartsFromSunday() {
		assertEquals(6, makeSundayStartsWeekFormatter().getWeekDayIndex(Calendar.SATURDAY));
	}
	
	public void testGetWeekDayIndexForIncorrectWeekDay() {
		try {
			makeMondayStartsWeekFormatter().getWeekDayIndex(0);
			fail();
		} catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}
	}
	
	public void testGetWeekDayTitle() {
		assertEquals("Sun", makeMondayStartsWeekFormatter().getWeekDayTitle(Calendar.SUNDAY));
	}

}
