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
        if (previousTime != null) {
            timeCounter += new Date().getTime() - previousTime;
            previousTime = new Date().getTime();
        } else {
            previousTime = new Date().getTime();
        }

        if (isNewDayStarts()) {
            timeCounter = 0;
        }
    }

    private boolean isNewDayStarts() {
        return dateWithoutTimeComparator.compare(new Date(previousTime), new Date()) != 0;
    }

    public void skip() {
        previousTime = new Date().getTime();
    }

    public void reset() {
        timeCounter = 0;
        previousTime = null;
    }

    public long getTimeValue() {
        return timeCounter;
    }

}
