package idrabenia.worktime.domain.calculation.actor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import idrabenia.worktime.R;
import idrabenia.worktime.domain.calculation.actor.message.GetTimerValueMessage;
import idrabenia.worktime.domain.calculation.actor.message.GetWorkLocationMessage;
import idrabenia.worktime.domain.calculation.actor.message.Message;
import idrabenia.worktime.domain.calculation.actor.message.StartTimeCalculationMessage;
import idrabenia.worktime.domain.location.LocationListener;
import idrabenia.worktime.domain.location.LocationService;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class TimeCalculationActor extends Thread {
    private final Context context;
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
    private Timer timer = new Timer("Time Calculation Timer");
    private long timeCounter = 0;
    private Long previousDate;
    private Integer workingDay;
    private int notificationsCounter = 0;
    private boolean isUserNotified = false;
    private boolean isActive = false;
    private Double workPlaceLatitude;
    private Double workPlaceLongitude;

    public TimeCalculationActor(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = messageQueue.take();

                if ("start".equals(message.command)) {
                    if (!isActive) {
                        startTimeCalculation((StartTimeCalculationMessage) message);
                        isActive = true;
                    }
                } else if ("reset".equals(message.command)) {
                    reset();
                } else if ("calculate".equals(message.command)) {
                    //calculateTime();
                    calculateTimeByWifi();
                } else if ("getTimerValue".equals(message.command)) {
                    getTimerValue((GetTimerValueMessage) message);
                } else if ("getWorkLocation".equals(message.command)) {
                    getWorkLocation((GetWorkLocationMessage) message);
                }
            } catch (InterruptedException ex) {
                Log.e("WorkTime", "Interrupted exception", ex);
            }
        }
        onDestroy();
    }

    private void getWorkLocation(GetWorkLocationMessage message) {
        if (message.resultListener != null) {
            message.resultListener.onLocationReceived(workPlaceLatitude, workPlaceLongitude);
        }
    }

    private void getTimerValue(GetTimerValueMessage message) {
        calculateTime();
        if (message.resultListener != null) {
            message.resultListener.onValueReceived(timeCounter);
        }
    }

    private void reset() {
        resetCounter();
        workPlaceLatitude = null;
        workPlaceLongitude = null;
        isActive = false;
    }

    private void resetCounter() {
        timeCounter = 0;
        workingDay = getCurrentDay();
        isUserNotified = false;
        previousDate = null;
    }

    private void startTimeCalculation(final StartTimeCalculationMessage message) {
        workPlaceLatitude = message.latitude;
        workPlaceLongitude = message.longitude;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageQueue.offer(new Message("calculate"));
            }
        }, 1000 * 10 /* 10 minutes */);
    }

    private void calculateTime() {
        if (!isActive) {
            return;
        }

        new LocationService(context).getCurrentLocationAsync(new LocationListener() {
            @Override
            public void onLocationReceived(Location curLocation) {
                float[] distance = new float[1];
                Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(),
                        workPlaceLatitude, workPlaceLongitude, distance);
                if (distance[0] < 50) {
                    if (previousDate != null) {
                        timeCounter += new Date().getTime() - previousDate;
                        previousDate = new Date().getTime();
                    } else {
                        previousDate = new Date().getTime();
                        timeCounter = 0;
                    }
                } else {
                    previousDate = new Date().getTime();
                }

                if (workingDay == null || !workingDay.equals(getCurrentDay())) {
                    resetCounter();
                }

                if (timeCounter > 8 * 60 * 60 * 1000 && !isUserNotified) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                            Service.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationsCounter++, new Notification.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Overtime Detected")
                            .setVibrate(new long[] { 200, 200, 200, 200 })
                            .setLights(0x00551100, 200, 600)
                            .setDefaults(Notification.FLAG_SHOW_LIGHTS)
                            .setContentText("Please go home! :)")
                            .getNotification());
                    isUserNotified = true;
                }
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageQueue.offer(new Message("calculate"));
            }
        }, 10 * 1000);
    }

    private void calculateTimeByWifi() {
        if (isNetworkPresent("ViadenEmployee")) {
            if (previousDate != null) {
                timeCounter += new Date().getTime() - previousDate;
                previousDate = new Date().getTime();
            } else {
                previousDate = new Date().getTime();
                timeCounter = 0;
            }
        } else {
            previousDate = new Date().getTime();
        }

        if (workingDay == null || !workingDay.equals(getCurrentDay())) {
            resetCounter();
        }

        if (timeCounter > 8 * 60 * 60 * 1000 && !isUserNotified) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Service.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationsCounter++, new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Overtime Detected")
                    .setVibrate(new long[] { 200, 200, 200, 200 })
                    .setLights(0x00551100, 200, 600)
                    .setDefaults(Notification.FLAG_SHOW_LIGHTS)
                    .setContentText("Please go home! :)")
                    .getNotification());
            isUserNotified = true;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageQueue.offer(new Message("calculate"));
            }
        }, 60 * 1000);
    }
    
    private List<String> getAvailableNetworks() {
        List<ScanResult> networks = ((WifiManager) context.getSystemService(Service.WIFI_SERVICE)).getScanResults();
        List<String> networkNames = new ArrayList<String>(networks.size());

        for (ScanResult curNetwork : networks) {
            networkNames.add(curNetwork.SSID);
        }

        return networkNames;
    }

    private boolean isNetworkPresent(String name) {
        List<String> networks = getAvailableNetworks();

        for (String curNetwork : networks) {
            if (name.equals(curNetwork)) {
                return true;
            }
        }

        return false;
    }

    public void onDestroy() {
        timer.cancel();
    }

    public BlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

    private Integer getCurrentDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

}
