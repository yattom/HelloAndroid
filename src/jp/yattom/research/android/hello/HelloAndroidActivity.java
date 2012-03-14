package jp.yattom.research.android.hello;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelloAndroidActivity extends Activity {
	private WifiManager wifiManager;
	private WifiScanReceiver receiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		Button readLocatoinButton = (Button) findViewById(R.id.read_location_button);
		readLocatoinButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				addScanResult(null);
			}
		});
		
		Button addStationButton = (Button) findViewById(R.id.add_metro_station_button);
		addStationButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String line = ((TextView)findViewById(R.id.metro_line_text)).getText().toString();
				String station = ((TextView)findViewById(R.id.metro_station_text)).getText().toString();

				List<ScanResult> results = wifiManager.getScanResults();
				if(results == null) {
					return;
				}
				String[] bssids = new String[results.size()];
				for (int i = 0; i < bssids.length; i++) {
					bssids[i] = results.get(i).BSSID;
				}
				
				Whereabout metroStationHome = new MetroStationHomeWhereabout(line, station, bssids);
				Interpreter.getInstance().recordNewWhereabout(HelloAndroidActivity.this, metroStationHome);
			}
		});

		Button showStationsButton = (Button) findViewById(R.id.show_metro_stations_button);
		showStationsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TextView text = (TextView) findViewById(R.id.log_text);
				String out = "";
				out += "Dumped stored Whereabouts at " + new Date() + "\n";
				SharedPreferences prefs = getSharedPreferences("Whereabouts", MODE_PRIVATE);
				Map<String, ?> all = prefs.getAll();
				for(String key : all.keySet()) {
					out += key + " = " + all.get(key).toString() + "\n";
				}
				out += "----------\n";
				text.append(out);
			}
		});

		receiver = new WifiScanReceiver();
		registerReceiver(receiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	public void onSaveHomeBssid(View view) {
		TextView text = (TextView) findViewById(R.id.home_bssid_text);
		Interpreter.getInstance().setHomeBssid(text.getText());
		Interpreter.getInstance().setHomeStatusReceiver(this);
	}

	public void addScanResult(String caller) {
		receiver.forceUpdate(this);
		TextView text = (TextView) findViewById(R.id.log_text);
		String out = "";
		out += "Recorded at " + new Date() + "\n";
		if(caller != null) {
			out += "  requested from " + caller + "\n";
		}
		out += "Wifi: \n";
		out += "enabled: " + wifiManager.isWifiEnabled() + "\n";
		out += "scan results:\n";
		List<ScanResult> results = wifiManager.getScanResults();
		if(results == null) {
			out += "  no results\n";
		} else {
			for (ScanResult result : results) {
				out += "  BSSID: " + result.BSSID + "\n";
				out += "  SSID: " + result.SSID + "\n";
				out += "  capabilities: " + result.capabilities + "\n";
				out += "  frequency: " + result.frequency + "\n";
				out += "  level: " + result.level + "\n";
				out += "  -----\n";
			}
		}
		out += "----------\n";
		text.append(out);
	}

	public void notifyBackToHome() {
		TextView text = (TextView) findViewById(R.id.home_status_text);
		text.setText("Here");
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}

	public void notifyHomeIsAway() {
		TextView text = (TextView) findViewById(R.id.home_status_text);
		text.setText("Away");
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}
}