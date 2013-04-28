package idrabenia.worktime.domain.calculation.actor.message;

/**
 * @author Ilya Drabenia
 * @since 25.04.13
 */
public class CalculateMessage extends Message {
    public final CalculateListener listener;

    public CalculateMessage() {
        this(null);
    }

    public CalculateMessage(CalculateListener listener) {
        super("calculate");
        this.listener = listener;
    }
}
