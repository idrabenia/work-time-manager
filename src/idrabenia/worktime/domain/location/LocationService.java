package idrabenia.worktime.domain.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.*;

import java.util.Date;
import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 21.03.13
 */
public class LocationService {
    public static final long TEN_SECONDS = 10 * 1000;

    private final Context context;

    private static class  LooperThread extends Thread {
        public volatile Handler handler;

        public void run() {
            Looper.prepare();

            handler = new Handler();

            Looper.loop();
        }
    }

    private static final LooperThread LOOPER_THREAD = new LooperThread();

    static {
        LOOPER_THREAD.start();
    }

    public LocationService(Context context) {
        this.context = context;
    }

    private LocationManager getLocationManager() {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private LocationProvider getNetworkProvider() {
        return (LocationProvider) getLocationManager().getProvider(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isNetworkProviderAvailable() {
        List<String> enabledProviders = getLocationManager().getProviders(true);

        for (String curProvider : enabledProviders) {
            if (curProvider.equals(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
        }

        return false;
    }

    private Location getPassiveLocation() {
        return getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private void requestLocation(final LocationListener listener) {
        LOOPER_THREAD.handler.post(new Runnable() {
            @Override
            public void run() {
                getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0,
                        new android.location.LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                listener.onLocationReceived(location);

                                final android.location.LocationListener self = this;
                                getLocationManager().removeUpdates(this);
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                // NOOP
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                                // NOOP
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                                // NOOP
                            }
                        });
            }
        });
    }

    private void notifyListenerAsync(final LocationListener listener, final Location lastKnownLocation) {
        LOOPER_THREAD.handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onLocationReceived(lastKnownLocation);
            }
        });
    }

    public void getCurrentLocationAsync(final LocationListener listener) {
        if (!isNetworkProviderAvailable()) {
            return;
        }

        final Location lastKnownLocation = getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation != null && lastKnownLocation.getTime() > new Date().getTime() - TEN_SECONDS) {
            notifyListenerAsync(listener, lastKnownLocation);
        } else {
            requestLocation(listener);
        }
    }

    public boolean isLocationAvailable() {
        return isNetworkProviderAvailable();
    }

}
