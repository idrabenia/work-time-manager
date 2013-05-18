package idrabenia.worktime.domain.database;

import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.statistics.DayStatistics;

import java.util.List;

public interface WorkStatisticsDao {

	List<DayStatistics> loadWeekStatisticsFor(int relativeWeekNumber);
	
	Time calculateLastWeekTotalWorkedTime();
	
	void close();
	
}
