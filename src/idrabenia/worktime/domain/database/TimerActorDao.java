package idrabenia.worktime.domain.database;

import android.content.Context;
import idrabenia.worktime.domain.calculation.Timer;
import idrabenia.worktime.domain.notification.NotificationPanel;

public interface TimerActorDao {

	void saveTimerActor(Timer calculator, NotificationPanel panel);
	
	NotificationPanel loadOrCreateNotificationPanel(Context context);
	
	Timer loadOrCreateTimer();
	
	void close();
	
}
