package idrabenia.worktime.ui.statistics;

import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.date.TimeFormatter;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class TimeFormatAdapter extends NumberFormat {
	private static final long serialVersionUID = 1L;
	
	private static final TimeFormatter timeFormatter = new TimeFormatter();

	@Override
	public Number parse(String string, ParsePosition position) {
		return null;
	}
	
	@Override
	public StringBuffer format(long value, StringBuffer buffer,
			FieldPosition field) {
		return null;
	}
	
	@Override
	public StringBuffer format(double value, StringBuffer buffer,
			FieldPosition field) {
		return new StringBuffer(timeFormatter.format(new Time((long)(value * 3600 * 1000))));
	}

}
