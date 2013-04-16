package idrabenia.worktime.domain.calculation.actor.message;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class StartTimeCalculationMessage extends Message {
    public final double latitude;
    public final double longitude;

    public StartTimeCalculationMessage(double latitude, double longitude) {
        super("start");
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
