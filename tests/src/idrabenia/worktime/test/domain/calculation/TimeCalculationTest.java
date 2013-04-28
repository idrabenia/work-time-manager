package idrabenia.worktime.test.domain.calculation;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class TimeCalculationTest extends TestCase {
	public static final long MILLIS_PER_MINUTE = 60000L;
	public static final long MILLIS_PER_DAY = TimeUnit.DAYS.toMillis(1L);
	
	private TimerSpy timer = new TimerSpy();
	private long testDate = new GregorianCalendar(2009, 10, 10, 9, 30, 30).getTimeInMillis();

	
	public void testIncreaseTimeCalculation() {		
		timer.curTime = testDate;
		timer.skip();
		
		timer.curTime = testDate + MILLIS_PER_MINUTE;
		timer.increase();
		
		assertEquals(MILLIS_PER_MINUTE, timer.getTimeValue());
	}
	
	public void testSkipTimeCalculation() {
		timer.curTime = testDate;
		timer.skip();
		
		timer.curTime = testDate + MILLIS_PER_MINUTE;
		timer.skip();
		
		assertEquals(0, timer.getTimeValue());
	}
	
	public void testResetCounterOnDayChanged() {
		timer.curTime = testDate;
		timer.increase();
		
		timer.curTime = testDate + MILLIS_PER_MINUTE;
		timer.increase();
		
		timer.curTime = testDate + MILLIS_PER_DAY;
		timer.increase();
		
		assertEquals(0, timer.getTimeValue());
	}

}
