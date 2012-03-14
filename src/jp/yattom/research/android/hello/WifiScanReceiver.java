package jp.yattom.research.android.hello;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class WifiScanReceiver extends BroadcastReceiver {
	static final public String TAG = "WifiScanReceiver";
	private List<String> activeBssids;

	public WifiScanReceiver() {
		activeBssids = new ArrayList<String>();
	}

	@Override
	public void onReceive(Context c, Intent intent) {
		dumpScanResult(c);
		List<ScanResult> results = ((WifiManager) c
				.getSystemService(Context.WIFI_SERVICE)).getScanResults();
		if (results == null) {
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

		if (!scannedBssids.containsAll(activeBssids)) {
			List<String> noLongerActive = new ArrayList<String>(activeBssids);
			noLongerActive.removeAll(scannedBssids);
			for (String bssid : noLongerActive) {
				Interpreter.getInstance().notifyBssidLost(bssid);
			}
		}

		if (!activeBssids.containsAll(scannedBssids)) {
			List<String> newlyActive = new ArrayList<String>(scannedBssids);
			newlyActive.removeAll(activeBssids);
			for (String bssid : newlyActive) {
				Interpreter.getInstance().notifyBssidFound(bssid);
			}
		}
		activeBssids = scannedBssids;
	}

	private void dumpScanResult(Context c) {
		WifiManager wifiManager = (WifiManager) c
				.getSystemService(Context.WIFI_SERVICE);
		try {
			String path = c.getExternalFilesDir(null).getPath();
			File dir = new File(path);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, "scan.txt");
			FileOutputStream os = new FileOutputStream(file, true);
			PrintStream writer = new PrintStream(os, true, "utf-8");
			Log.i(TAG, "dump scan to " + file.getAbsolutePath());

			writer.printf("Recorded at %s\n", new Date());
			writer.printf("  requested from WifiScanReceiver#onReceive()\n");
			writer.printf("Wifi: \n");
			writer.printf("enabled: %b\n", wifiManager.isWifiEnabled());
			writer.printf("scan results:\n");
			List<ScanResult> results = wifiManager.getScanResults();
			if (results == null) {
				writer.printf("  no results\n");
			} else {
				for (ScanResult result : results) {
					writer.printf("  BSSID: %s\n", result.BSSID);
					writer.printf("  SSID: %s\n", result.SSID);
					writer.printf("  capabilities: %s", result.capabilities);
					writer.printf("  frequency: %d\n", result.frequency);
					writer.printf("  level: %d\n", result.level);
					writer.printf("  -----\n");
				}
			}
			writer.printf("----------\n");

			writer.close();
			os.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}

	public void forceUpdate(Context c) {
		// onReceive(c, null);
	}

}