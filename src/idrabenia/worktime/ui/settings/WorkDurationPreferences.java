package idrabenia.worktime.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import idrabenia.worktime.domain.date.Time;
import idrabenia.worktime.domain.preferences.Preferences;

/**
 * @author Ilya Drabenia
 * @since 21.04.13
 */
public class WorkDurationPreferences extends DialogPreference {
    private TimePicker timePicker;
    private Preferences preferences;

    public WorkDurationPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public WorkDurationPreferences(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        preferences = new Preferences(context);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected View onCreateDialogView() {
        timePicker = new TimePicker(getContext());
        timePicker.setIs24HourView(true);
        return timePicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        Time curDayDuration = preferences.getWorkDayDuration();
        timePicker.setCurrentHour(curDayDuration.hour);
        timePicker.setCurrentMinute(curDayDuration.minute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            Time resultDuration = new Time(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            preferences.setWorkDayDuration(resultDuration);
        }
    }

}
