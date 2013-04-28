package idrabenia.worktime.ui.calculation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Ilya Drabenia
 * @since 27.04.13
 */
public class BackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        UpdateTimerReceiver.start(getApplicationContext());
    }

    public static void start(Context context) {
        context.startService(new Intent(context, BackgroundService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
