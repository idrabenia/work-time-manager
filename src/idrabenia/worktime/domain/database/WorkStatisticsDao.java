package idrabenia.worktime.domain.database;

import idrabenia.worktime.domain.statistics.DayStatistics;

import java.util.List;

public interface WorkStatisticsDao {

	List<DayStatistics> loadLastWeekStatistics();
	
	void close();
	
}
