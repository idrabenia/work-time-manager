package idrabenia.worktime.test.domain.calculation;

import idrabenia.worktime.domain.calculation.Timer;


public class TimerSpy extends Timer {
	public long curTime;

	@Override
	public long getCurTime() {
		return curTime;
	}
}
