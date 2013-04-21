package idrabenia.worktime.test.domain.date;

import idrabenia.worktime.domain.date.Time;
import junit.framework.TestCase;

public class TimeTest extends TestCase {
	
	public void testCreateTime() {
		assertEquals(3660000, new Time(1, 1).toMillis());
	}
	
	public void testCreateTimeFromMillis() {
		Time resultTime = new Time(3660000L);
		
		assertEquals(1L, resultTime.hour);
		assertEquals(1L, resultTime.minute);
	}

}
