package idrabenia.worktime.domain.calculation.actor.message;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class GetTimerValueMessage extends Message {
    public final GetTimerValueListener resultListener;

    public GetTimerValueMessage(GetTimerValueListener resultListener) {
        super("getTimerValue");
        this.resultListener = resultListener;
    }

}
