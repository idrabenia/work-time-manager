package idrabenia.worktime.domain.calculation.actor.message;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public interface GetTimerValueListener {

    void onValueReceived(Long value);

}
