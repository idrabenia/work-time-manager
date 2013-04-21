package idrabenia.worktime.test.domain.calculation;

import idrabenia.worktime.domain.calculation.TimeCalculator;


public class TimeCalculatorSpy extends TimeCalculator {
	public long curTime;

	@Override
	public long getCurTime() {
		return curTime;
	}
}
