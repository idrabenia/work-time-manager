package idrabenia.worktime.domain.calculation.actor.message;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class GetWorkLocationMessage extends Message {
    public final GetWorkLocationListener resultListener;

    public GetWorkLocationMessage(GetWorkLocationListener resultListener) {
        super("getWorkLocation");
        this.resultListener = resultListener;
    }
}
