package idrabenia.worktime.ui.calculation;

import android.os.Binder;
import idrabenia.worktime.domain.calculation.TimeCalculationService;

/**
 * @author Ilya Drabenia
 * @since 13.04.13
 */
public class CalculationServiceBinder extends Binder {
    private final TimeCalculationService service;

    public CalculationServiceBinder(TimeCalculationService service) {
        this.service = service;
    }

    public TimeCalculationService getService() {
        return service;
    }
}
