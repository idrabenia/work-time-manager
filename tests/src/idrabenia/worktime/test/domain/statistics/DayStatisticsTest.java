package idrabenia.worktime.test.domain.statistics;

import java.util.GregorianCalendar;

import idrabenia.worktime.domain.statistics.DayStatistics;
import junit.framework.TestCase;

public class DayStatisticsTest extends TestCase {
	
	public void testGetWorkedHours() {
		final double WORKED_HOURS = 8.5; 
		DayStatistics statistics = new DayStatistics(new GregorianCalendar(2009, 10, 1).getTimeInMillis(), 
				(long)(WORKED_HOURS * 3600 * 1000));
		
		assertEquals(WORKED_HOURS, statistics.getWorkedHours(), 0.001);
	}

}
