package idrabenia.worktime.domain.calculation;

import idrabenia.worktime.domain.date.DateWithoutTimeComparator;

import java.util.Date;

/**
 * @author Ilya Drabenia
 * @since 20.04.13
 */
public class TimeCalculator {
    private long timeCounter = 0;
    private Long previousTime;
    private final DateWithoutTimeComparator dateWithoutTimeComparator = new DateWithoutTimeComparator();

    public void increase() {
        resetIfNewDayStarts();
    	
    	long curTime = getCurTime();
    	
        if (previousTime != null) {
            timeCounter += curTime - previousTime;
            previousTime = curTime;
        } else {
            previousTime = curTime;
        }
    }

    private boolean isNewDayStarts() {
    	if (previousTime != null) {
    		return dateWithoutTimeComparator.compare(new Date(previousTime), new Date(getCurTime())) != 0;
    	} else {
    		return false;
    	}
    }

    public void skip() {
    	resetIfNewDayStarts();
    	
        previousTime = getCurTime();
    }

	private void resetIfNewDayStarts() {
		if (isNewDayStarts()) {
            reset();
        }
	}

    public void reset() {
        timeCounter = 0;
        previousTime = null;
    }

    public long getTimeValue() {
        return timeCounter;
    }
    
    public long getCurTime() {
    	return new Date().getTime();
    }

}
