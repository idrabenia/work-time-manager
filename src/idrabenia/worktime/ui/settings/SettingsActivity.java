package idrabenia.worktime.ui.settings;

import idrabenia.worktime.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

/**
 * @author Ilya Drabenia
 * @since 20.04.13
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }
}
