package idrabenia.worktime.test.domain.calculation.actor;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import idrabenia.worktime.domain.calculation.actor.TimeCalculationActor;
import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.notification.NotificationPanel;
import idrabenia.worktime.domain.preferences.Preferences;
import idrabenia.worktime.domain.wifi.WifiNetworkAdapter;
import idrabenia.worktime.test.domain.calculation.TimeCalculatorSpy;
import junit.framework.TestCase;

public class TimeCalculationActorTest extends TestCase {
	private static final long TEST_DATE = new GregorianCalendar(2009, 10, 10, 10, 30, 15).getTimeInMillis();
	
	private class NotificationPanelSpy extends NotificationPanel {
		private int notificationsCount = 0;

		public NotificationPanelSpy(Context curContext) {
			super(curContext);
		}

		@Override
		public void notifyAboutOvertime() {
			notificationsCount++;
		}
		
	}
	
	private class WifiAdapterSpy extends WifiNetworkAdapter {
		private boolean isNetworkPresent;
		
		public WifiAdapterSpy(Context curContext) {
			super(curContext);
		}

		@Override
		public boolean isNetworkPresent(String name) {
			return isNetworkPresent;
		}
	}
	
	private class PreferencesSpy extends Preferences {
		private String networkSsid;
		private long dayDuration;
		
		public PreferencesSpy() {
			super();
		}

		@Override
		public String getWorkingNetworkSsid() {
			return networkSsid;
		}

		@Override
		public Time getWorkDayDuration() {
			return new Time(dayDuration);
		}
	}
	
	private class TimeActorSpy extends TimeCalculationActor {
		public TimeActorSpy(Context context) {
			this.notificationPanel = notificationPanelSpy;
			this.wifiNetworkAdapter = wifiAdapterSpy;
			this.preferences = preferencesSpy;
			this.timeCalculator = calculatorSpy;
		}

		@Override
		public void calculateTimeByWifi() {
			super.calculateTimeByWifi();
		}
	}

	private NotificationPanelSpy notificationPanelSpy = new NotificationPanelSpy(null);
	private PreferencesSpy preferencesSpy = new PreferencesSpy();
	private WifiAdapterSpy wifiAdapterSpy = new WifiAdapterSpy(null);
	private TimeCalculatorSpy calculatorSpy = new TimeCalculatorSpy();
	private TimeActorSpy actor = new TimeActorSpy(null);
	
	public void testOvertimeNotification() {
		final long DAY_DURATION = TimeUnit.HOURS.toMillis(4);
		
		preferencesSpy.dayDuration = DAY_DURATION;
		
		preferencesSpy.networkSsid = "NetworkSSID";
		wifiAdapterSpy.isNetworkPresent = true;
		
		calculatorSpy.curTime = TEST_DATE;
		actor.calculateTimeByWifi();
		
		calculatorSpy.curTime = TEST_DATE + DAY_DURATION + 1L;
		actor.calculateTimeByWifi();
		
		assertNotificationWasSent();
	}
	
	private void assertNotificationWasSent() {
		assertEquals(1, notificationPanelSpy.notificationsCount);
	}

}
