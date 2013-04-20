package idrabenia.worktime.domain.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Ilya Drabenia
 * @since 20.04.13
 */
public class DateWithoutTimeComparator {

    public int compare(Date firstDate, Date secondDate) {
        Calendar firstCalendar = new GregorianCalendar(firstDate.getYear(), firstDate.getMonth(), firstDate.getDay());
        Calendar secondCalendar = new GregorianCalendar(secondDate.getYear(), secondDate.getMonth(),
                secondDate.getDay());

        return firstDate.compareTo(secondDate);
    }

}
