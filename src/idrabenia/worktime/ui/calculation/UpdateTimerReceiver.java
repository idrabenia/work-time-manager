package idrabenia.worktime.ui.calculation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import idrabenia.worktime.domain.calculation.TimerService;

import java.util.concurrent.TimeUnit;

/**
 * @author Ilya Drabenia
 * @since 24.04.13
 */
public class UpdateTimerReceiver extends BroadcastReceiver {
    public static final long REPEAT_INTERVAL = TimeUnit.MINUTES.toMillis(1L);

    @Override
    public void onReceive(Context context, Intent intent) {
        TimerService.getInstance(context).recalculate();
    }

    public static void start(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, UpdateTimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REPEAT_INTERVAL, pendingIntent);
    }

}
