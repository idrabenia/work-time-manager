package idrabenia.worktime.test.domain.date;

import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.date.TimeFormatter;
import junit.framework.TestCase;

public class TimeFormatterTest extends TestCase {

	public void testFormatTime() {
		Time time = new Time(2, 3);
		
		String result = new TimeFormatter().format(time);
		
		assertEquals("02:03", result);
	}
	
	public void testFormatTimeForNullInput() {
		String result = new TimeFormatter().format(null);
		
		assertEquals("00:00", result);
	}
	
}
