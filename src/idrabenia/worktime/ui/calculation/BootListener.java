package idrabenia.worktime.ui.calculation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Ilya Drabenia
 * @since 27.04.13
 */
public class BootListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BackgroundService.start(context);
    }

}
