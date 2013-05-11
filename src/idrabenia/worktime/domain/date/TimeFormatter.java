package idrabenia.worktime.domain.date;

import java.text.MessageFormat;

public class TimeFormatter {
	public static final String DEFAULT_VALUE = "00:00";
    public static final String ELAPSED_TIME_FORMAT = "{0,number,00}:{1,number,00}";
    
    public String format(Time time) {
    	if (time != null) {
    		return MessageFormat.format(ELAPSED_TIME_FORMAT, time.hour, time.minute);
		} else {
			return DEFAULT_VALUE;
		}
    }
}
