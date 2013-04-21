package idrabenia.worktime.domain.calculation.actor;

import android.content.Context;
import android.util.Log;
import idrabenia.worktime.domain.calculation.TimeCalculator;
import idrabenia.worktime.domain.calculation.actor.message.GetTimerValueMessage;
import idrabenia.worktime.domain.calculation.actor.message.Message;
import idrabenia.worktime.domain.notification.NotificationPanel;
import idrabenia.worktime.domain.preferences.Preferences;
import idrabenia.worktime.domain.wifi.WifiNetworkAdapter;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class TimeCalculationActor extends Thread {
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

    private Timer timer = new Timer("Time Calculation Timer");

    private final NotificationPanel notificationPanel;
    private final WifiNetworkAdapter wifiNetworkAdapter;
    private final TimeCalculator timeCalculator = new TimeCalculator();
    private final Preferences preferences;

    public TimeCalculationActor(Context context) {
        notificationPanel = new NotificationPanel(context);
        wifiNetworkAdapter = new WifiNetworkAdapter(context);
        preferences = new Preferences(context);
    }

    @Override
    public void run() {
        scheduleNextCalculateTask();
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
    }

    private void reset() {
        timeCalculator.reset();
    }

    private void calculateTimeByWifi() {
        if (wifiNetworkAdapter.isNetworkPresent(preferences.getWorkingNetworkSsid())) {
            timeCalculator.increase();
        } else {
            timeCalculator.skip();
        }

        if (timeCalculator.getTimeValue() > 8 * 60 * 60 * 1000 && !notificationPanel.isNotifiedToday()) {
            notificationPanel.notifyAboutOvertime();
        }

        scheduleNextCalculateTask();
    }

    private void scheduleNextCalculateTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageQueue.offer(new Message("calculate"));
            }
        }, 60 * 1000);
    }

    public void onDestroy() {
        timer.cancel();
    }

    public BlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

}
