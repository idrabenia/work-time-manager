package idrabenia.worktime.domain.calculation;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public interface TimeCalculationService {

    void start(double latitude, double longitude);

    Long getTimerValue();

    void reset();

    double[] getWorkLocation();

}
