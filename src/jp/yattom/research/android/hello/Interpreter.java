package jp.yattom.research.android.hello;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Interpreter {
	static private String TAG = "Interpreter";
	static private Interpreter instance = new Interpreter();
	private String homeBssid;
	private HelloAndroidActivity homeStatusReceiver;

	static public Interpreter getInstance() {
		return instance;
	}

	private Interpreter() {

	}

	public void recordNewWhereabout(Context context, Whereabout target) {
		SharedPreferences prefs = context.getSharedPreferences("Whereabouts", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		for(String bssid : target.bssids()) {
			editor.putString(bssid, target.name());
		}
		editor.commit();
	}

	public void notifyBssidLost(String bssid) {
		Log.i(TAG, "notifyBssidLosg(" + bssid + ")");
		if (bssid.toUpperCase().equals(homeBssid)) {
			Log.i(TAG, "Away from home");
			homeStatusReceiver.notifyHomeIsAway();
		}
	}

	public void notifyBssidFound(String bssid) {
		Log.i(TAG, "notifyBssidFound(" + bssid + ")");
		if (bssid.toUpperCase().equals(homeBssid)) {
			Log.i(TAG, "Back to home");
			homeStatusReceiver.notifyBackToHome();
		}
	}

	public void setHomeBssid(CharSequence text) {
		homeBssid = text.toString().toUpperCase();
	}

	public void setHomeStatusReceiver(HelloAndroidActivity receiver) {
		homeStatusReceiver = receiver;
	}
}
