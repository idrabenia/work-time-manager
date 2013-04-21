package idrabenia.worktime.ui.calculation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import idrabenia.worktime.domain.calculation.actor.message.*;
import idrabenia.worktime.domain.calculation.actor.TimeCalculationActor;
import idrabenia.worktime.domain.calculation.TimeCalculationService;
import idrabenia.worktime.domain.date.Time;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class TimeCalculationAndroidService extends Service implements TimeCalculationService {
    private volatile TimeCalculationActor timerActor;

    @Override
    public void onCreate() {
        timerActor = new TimeCalculationActor(getApplicationContext());
        timerActor.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CalculationServiceBinder(this);
    }

    @Override
    public void onDestroy() {
        timerActor.interrupt();
        Log.i("worktime", "TimeCalculationAndroidService#destroy()");
    }

    @Override
    public void start(double latitude, double longitude) {
        StartTimeCalculationMessage message = new StartTimeCalculationMessage(latitude, longitude);
        timerActor.getMessageQueue().offer(message);
    }

    @Override
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

    @Override
    public void reset() {
        timerActor.getMessageQueue().offer(new Message("reset"));
    }

}
