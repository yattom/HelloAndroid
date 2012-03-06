package jp.yattom.research.android.hello;

public class MetroStationHomeWhereabout implements Whereabout {
	private String line;
	private String station;
	private String[] bssids;
	
	public MetroStationHomeWhereabout(String line, String station, String[] bssids) {
		this.line = line;
		this.station = station;
		this.bssids = bssids;
	}

	public String name() {
		return line + "/" + station;
	}

	public String id() {
		return name();
	}

	public Type type() {
		return Type.METRO_STATION_HOME;
	}
	
	public String[] bssids() {
		return bssids;
	}

	public String toString() {
		return name() + "=" + bssids;
	}
}
