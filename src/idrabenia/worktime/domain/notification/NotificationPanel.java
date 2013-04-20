package idrabenia.worktime.domain.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import idrabenia.worktime.R;
import idrabenia.worktime.domain.date.DateWithoutTimeComparator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Ilya Drabenia
 * @since 19.04.13
 */
public class NotificationPanel {
    private int notificationsCounter = 0;
    private Date lastNotificationTime;
    private Context context;
    private final DateWithoutTimeComparator dateComparator = new DateWithoutTimeComparator();

    public NotificationPanel(Context curContext) {
        context = curContext;
    }

    public void notifyAboutOvertime() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Service.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationsCounter++, new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.overtime_notification))
                .setVibrate(new long[]{200, 200, 200, 200})
                .setLights(0x00551100, 200, 600)
                .setDefaults(Notification.FLAG_SHOW_LIGHTS)
                .setContentText(context.getString(R.string.you_work_too_more))
                .getNotification());

        lastNotificationTime = new Date();
    }

    public boolean isNotifiedToday() {
        if (lastNotificationTime == null) {
            return false;
        }

        return dateComparator.compare(lastNotificationTime, new Date()) == 0;
    }

}
