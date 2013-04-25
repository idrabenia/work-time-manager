package idrabenia.worktime.domain.calculation.actor;

import android.content.Context;
import android.util.Log;
import idrabenia.worktime.domain.calculation.TimeCalculator;
import idrabenia.worktime.domain.calculation.actor.message.GetTimerValueMessage;
import idrabenia.worktime.domain.calculation.actor.message.Message;
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
public class TimeCalculationActor extends Thread {
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

    private final TextLog log = new TextLog("time-calculation-actor");

    protected NotificationPanel notificationPanel;
    protected WifiNetworkAdapter wifiNetworkAdapter;
    protected TimeCalculator timeCalculator = new TimeCalculator();
    protected Preferences preferences;

    public TimeCalculationActor(Context context) {
        notificationPanel = new NotificationPanel(context);
        wifiNetworkAdapter = new WifiNetworkAdapter(context);
        preferences = new Preferences(context);
    }
    
    protected TimeCalculationActor() {
    	
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = messageQueue.take();

                if ("reset".equals(message.command)) {
                    reset();
                } else if ("calculate".equals(message.command)) {
                    calculateTimeByWifi();
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
            message.resultListener.onValueReceived(timeCalculator.getTimeValue());
        }
        log.log("Get timer value processed. Value is " + timeCalculator.getTimeValue());
    }

    private void reset() {
        log.log("Actor#reset");
        notificationPanel.reset();
        timeCalculator.reset();
    }

    protected void calculateTimeByWifi() {
        if (wifiNetworkAdapter.isNetworkPresent(preferences.getWorkingNetworkSsid())) {
            timeCalculator.increase();
            log.log("Timer increased, current value is " + timeCalculator.getTimeValue());
        } else {
            timeCalculator.skip();
            log.log("Timer not increased, current value is " + timeCalculator.getTimeValue());
        }

        if (timeCalculator.getTimeValue() > preferences.getWorkDayDuration().toMillis() 
        		&& !notificationPanel.isNotifiedToday()) {
            notificationPanel.notifyAboutOvertime();
            log.log("Notification sent");
        }
    }

    public void onDestroy() {
        log.log("Service destroyed");
        log.close();
    }

    public BlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

}
