package idrabenia.worktime.domain.calculation.actor;

import android.content.Context;
import android.util.Log;
import idrabenia.worktime.domain.calculation.Timer;
import idrabenia.worktime.domain.calculation.actor.message.CalculateMessage;
import idrabenia.worktime.domain.calculation.actor.message.GetTimerValueMessage;
import idrabenia.worktime.domain.calculation.actor.message.Message;
import idrabenia.worktime.domain.database.TimerActorDao;
import idrabenia.worktime.domain.log.TextLog;
import idrabenia.worktime.domain.notification.NotificationPanel;
import idrabenia.worktime.domain.preferences.Preferences;
import idrabenia.worktime.domain.wifi.WifiNetworkAdapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class TimerActor extends Thread {
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

    private final TextLog log = new TextLog("time-calculation-actor");

    protected NotificationPanel notificationPanel;
    protected WifiNetworkAdapter wifiNetworkAdapter;
    protected Timer timer;
    protected Preferences preferences;
    private TimerActorDao timerActorDao;

    public TimerActor(Context context) {
        wifiNetworkAdapter = new WifiNetworkAdapter(context);
        preferences = new Preferences(context);

        timerActorDao = new TimerActorDao(context);
        timer = timerActorDao.loadOrCreateTimer();
        notificationPanel = timerActorDao.loadOrCreateNotificationPanel(context);
    }
    
    protected TimerActor() {

    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = messageQueue.take();

                if ("reset".equals(message.command)) {
                    reset();
                } else if ("calculate".equals(message.command)) {
                    calculateTimeByWifi((CalculateMessage) message);
                } else if ("getTimerValue".equals(message.command)) {
                    getTimerValue((GetTimerValueMessage) message);
                }
            } catch (InterruptedException ex) {
                Log.e("WorkTime", "Interrupted exception", ex);
            }
        }
        onDestroy();
    }

    private void getTimerValue(GetTimerValueMessage message) {
        if (message.resultListener != null) {
            message.resultListener.onValueReceived(timer.getTimeValue());
        }
        log.log("Get timer value processed. Value is " + timer.getTimeValue());
    }

    private void reset() {
        log.log("Actor#reset");
        notificationPanel.reset();

        timer.reset();
        timerActorDao.saveTimerActor(timer, notificationPanel);
    }

    protected void calculateTimeByWifi(CalculateMessage message) {
        if (wifiNetworkAdapter.isNetworkPresent(preferences.getWorkingNetworkSsid())) {
            timer.increase();
            log.log("Timer increased, current value is " + timer.getTimeValue());
        } else {
            timer.skip();
            log.log("Timer not increased, current value is " + timer.getTimeValue());
        }

        if (timer.getTimeValue() > preferences.getWorkDayDuration().toMillis()
        		&& !notificationPanel.isNotifiedToday()) {
            notificationPanel.notifyAboutOvertime();
            log.log("Notification sent");
        }

        timerActorDao.saveTimerActor(timer, notificationPanel);

        if (message.listener != null) {
            message.listener.onCalculationFinished();
        }
    }

    public void onDestroy() {
        log.log("Service destroyed");
        log.close();
        timerActorDao.close();
    }

    public BlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

}
