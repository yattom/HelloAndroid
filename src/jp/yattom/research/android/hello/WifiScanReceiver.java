package jp.yattom.research.android.hello;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiScanReceiver extends BroadcastReceiver {
	private List<String> activeBssids;

	public WifiScanReceiver() {
		activeBssids = new ArrayList<String>();
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		List<ScanResult> results = ((WifiManager) c
				.getSystemService(Context.WIFI_SERVICE)).getScanResults();
		if(results == null) {
			results = new ArrayList<ScanResult>();
		}
		List<String> scannedBssids = new ArrayList<String>();
		for (ScanResult result : results) {
			scannedBssids.add(result.BSSID);
		}
		Collections.sort(scannedBssids);

		if (activeBssids.equals(scannedBssids)) {
			return;
		}

		if(!scannedBssids.containsAll(activeBssids)) {
			List<String> noLongerActive = new ArrayList<String>(activeBssids);
			noLongerActive.removeAll(scannedBssids);
			for (String bssid : noLongerActive) {
				Interpreter.getInstance().notifyBssidLost(bssid);
			}
		}

		if(!activeBssids.containsAll(scannedBssids)) {
			List<String> newlyActive = new ArrayList<String>(scannedBssids);
			newlyActive.removeAll(activeBssids);
			for (String bssid : newlyActive) {
				Interpreter.getInstance().notifyBssidFound(bssid);
			}
		}
		activeBssids = scannedBssids;
	}

	public void forceUpdate(Context c) {
		onReceive(c, null);
	}

}