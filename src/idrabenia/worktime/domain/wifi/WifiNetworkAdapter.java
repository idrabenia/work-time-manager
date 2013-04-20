package idrabenia.worktime.domain.wifi;

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 20.04.13
 */
public class WifiNetworkAdapter {
    private final Context context;

    public WifiNetworkAdapter(Context curContext) {
        this.context = curContext;
    }

    private List<String> getAvailableNetworks() {
        List<ScanResult> networks = ((WifiManager) context.getSystemService(Service.WIFI_SERVICE)).getScanResults();
        List<String> networkNames = new ArrayList<String>(networks.size());

        for (ScanResult curNetwork : networks) {
            networkNames.add(curNetwork.SSID);
        }

        return networkNames;
    }

    public boolean isNetworkPresent(String name) {
        List<String> networks = getAvailableNetworks();

        for (String curNetwork : networks) {
            if (name.equals(curNetwork)) {
                return true;
            }
        }

        return false;
    }
}
