package idrabenia.worktime.domain.calculation;

import idrabenia.worktime.domain.date.Time;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public interface TimeCalculationService {

    void start(double latitude, double longitude);

    Time getTimerValue();

    void reset();

}
