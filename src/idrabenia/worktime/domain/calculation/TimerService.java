package idrabenia.worktime.domain.calculation;

import idrabenia.worktime.domain.calculation.actor.TimerActor;
import idrabenia.worktime.domain.calculation.actor.message.CalculateListener;
import idrabenia.worktime.domain.calculation.actor.message.CalculateMessage;
import idrabenia.worktime.domain.calculation.actor.message.GetTimerValueListener;
import idrabenia.worktime.domain.calculation.actor.message.GetTimerValueMessage;
import idrabenia.worktime.domain.calculation.actor.message.Message;
import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.ui.calculation.UpdateTimerReceiver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class TimerService {
    private volatile TimerActor timerActor;

    private static volatile TimerService instance;

    public static TimerService getInstance(Context context) {
        if (instance == null) {
            synchronized (TimerService.class) {
                if (instance == null) {
                    instance = new TimerService(context);
                }
            }
        }

        return instance;
    }

    public TimerService(Context context) {
        UpdateTimerReceiver.start(context);
        timerActor = new TimerActor(context.getApplicationContext());
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

    public void recalculateAsync() {
        timerActor.getMessageQueue().offer(new CalculateMessage());
    }

    public void recalculate() {
        final CountDownLatch latch = new CountDownLatch(1);
        timerActor.getMessageQueue().offer(new CalculateMessage(new CalculateListener() {
            @Override
            public void onCalculationFinished() {
                latch.countDown();
            }
        }));

        latch.countDown();
    }
}
