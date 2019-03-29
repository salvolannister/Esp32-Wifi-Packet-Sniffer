
public class Packet {

	int RSSI;
	String MacSource;
	String SSID;
	
	/**
	 * 
	 */
	public Packet(String packetChar) {
		GetMac(packetChar);
		GetRssi(packetChar);
		GetSSID(packetChar);
		System.out.println("");
		//System.out.println(packetChar);
	}
	
	private void GetRssi(String packetChar) {
		String[] begin = packetChar.split("RSSI=");
		String[] rssi = begin[1].split("/"); //split string after RSSI= by "/"
		this.RSSI=Integer.parseInt(rssi[0]); //take cahracters from the beginning to /
		System.out.print("RSSI: " + this.RSSI + "; ");
	}
	
	private void GetMac(String packetChar) {
		String[] begin = packetChar.split("MAC_SRC=");
		String[] mac = begin[1].split("/");
		this.MacSource=mac[0];
		System.out.print("Mac Source: " + this.MacSource + "; ");
	}
	
	private void GetSSID(String packetChar) {
		String[] begin = packetChar.split("SSID_length=");
		String[] ssid = begin[1].split("/");
		if(ssid[0]!="0")
			this.SSID = ssid[0];
		else
			this.SSID = null;
		System.out.print("SSID: " + this.SSID);
	}

}
