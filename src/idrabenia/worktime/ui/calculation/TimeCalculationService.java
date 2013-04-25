package idrabenia.worktime.ui.calculation;

import android.content.Context;
import idrabenia.worktime.domain.calculation.actor.message.*;
import idrabenia.worktime.domain.calculation.actor.TimeCalculationActor;
import idrabenia.worktime.domain.date.Time;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class TimeCalculationService {
    private volatile TimeCalculationActor timerActor;

    private static volatile TimeCalculationService instance;

    public static TimeCalculationService getInstance(Context context) {
        if (instance == null) {
            synchronized (TimeCalculationService.class) {
                if (instance == null) {
                    instance = new TimeCalculationService(context);
                }
            }
        }

        return instance;
    }

    public TimeCalculationService(Context context) {
        AlarmReceiver.setAlarm(context);
        timerActor = new TimeCalculationActor(context.getApplicationContext());
        timerActor.start();
    }

    public Time getTimerValue() {
        final AtomicReference<Time> result = new AtomicReference<Time>();
        final CountDownLatch latch = new CountDownLatch(1);
        GetTimerValueMessage message = new GetTimerValueMessage(new GetTimerValueListener() {
            @Override
            public void onValueReceived(Long value) {
                result.set(new Time(value));
                latch.countDown();
            }
        });
        timerActor.getMessageQueue().offer(message);

        try {
            latch.await();
            return result.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void reset() {
        timerActor.getMessageQueue().offer(new Message("reset"));
    }

    public void recalculate() {
        timerActor.getMessageQueue().offer(new Message("calculate"));
    }
}
