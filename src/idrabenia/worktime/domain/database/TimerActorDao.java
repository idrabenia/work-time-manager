package idrabenia.worktime.domain.database;

import idrabenia.worktime.domain.calculation.Timer;
import idrabenia.worktime.domain.notification.NotificationPanel;
import android.content.Context;

public interface TimerActorDao {

	void saveTimerActor(Timer calculator, NotificationPanel panel);
	
	NotificationPanel loadOrCreateNotificationPanel(Context context);
	
	Timer loadOrCreateTimer();
	
	void close();
	
}
