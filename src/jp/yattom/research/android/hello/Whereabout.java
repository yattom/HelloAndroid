package jp.yattom.research.android.hello;

public interface Whereabout {
	enum Type {
		HOME,
		METRO_STATION_HOME,
	}
	
	String name();
	String id();
	Type type();
	String[] bssids();
}
