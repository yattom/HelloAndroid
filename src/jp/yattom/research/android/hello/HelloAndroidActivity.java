package jp.yattom.research.android.hello;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelloAndroidActivity extends Activity {
	private WifiManager wifiManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        
        Button readLocatoinButton = (Button) findViewById(R.id.read_location_button);
        readLocatoinButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				TextView text = (TextView) findViewById(R.id.log_text);
				String out = "";
				out += "Recorded at " + new Date() + "\n";
				out += "Wifi: \n";
				out += "enabled: " + wifiManager.isWifiEnabled() + "\n";
				out += "scan results:\n";
				List<ScanResult> results = wifiManager.getScanResults();
				for(ScanResult result : results) {
					out += "  BSSID: " + result.BSSID + "\n";
					out += "  SSID: " + result.SSID + "\n";
					out += "  capabilities: " + result.capabilities + "\n";
					out += "  frequency: " + result.frequency + "\n";
					out += "  level: " + result.level + "\n";
					out += "  -----\n";
				}
				out += "----------\n";
				text.append(out);
			}
		});

    }
}