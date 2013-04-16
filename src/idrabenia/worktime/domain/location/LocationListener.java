package idrabenia.worktime.domain.location;

import android.location.Location;

/**
 * @author Ilya Drabenia
 * @since 22.03.13
 */
public interface LocationListener {

    void onLocationReceived(Location location);

}
