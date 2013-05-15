package idrabenia.worktime.domain.wifi;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

/**
 * @author Ilya Drabenia
 * @since 20.04.13
 */
public class WifiNetworkAdapter {
    private final Context context;

    public WifiNetworkAdapter(Context curContext) {
        this.context = curContext;
    }

    private WifiManager getWifiManager() {
    	return (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
    }
    
    private List<String> getAvailableNetworks() {
        List<ScanResult> networks = getWifiManager().getScanResults();
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
    
    public boolean isWifiEnabled() {
    	return getWifiManager().isWifiEnabled();
    }
}
