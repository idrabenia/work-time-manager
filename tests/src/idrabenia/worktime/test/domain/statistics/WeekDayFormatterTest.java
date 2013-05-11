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
	
	public void testGetDayCoordinateForMondayOnWeekStartsFromMonday() {
		assertEquals(0, makeMondayStartsWeekFormatter().getDayCoordinate(Calendar.MONDAY));
	}
	
	public void testGetDayCoordinateForSundayOnWeekStartsFromMonday() {
		assertEquals(6, makeMondayStartsWeekFormatter().getDayCoordinate(Calendar.SUNDAY));
	}
	
	public void testGetDayCoordinateForSundayOnWeekStartsFromSunday() {
		assertEquals(0, makeSundayStartsWeekFormatter().getDayCoordinate(Calendar.SUNDAY));
	}
	
	public void testGetDayCoordinateForSaturdayOnWeekStartsFromSunday() {
		assertEquals(6, makeSundayStartsWeekFormatter().getDayCoordinate(Calendar.SATURDAY));
	}
	
	public void testGetDayCoordinateForIncorrectWeekDay() {
		try {
			makeMondayStartsWeekFormatter().getDayCoordinate(0);
			fail();
		} catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}
	}
	
	public void testGetWeekDayTitle() {
		assertEquals("Sun", makeMondayStartsWeekFormatter().getWeekDayTitle(Calendar.SUNDAY));
	}

}
