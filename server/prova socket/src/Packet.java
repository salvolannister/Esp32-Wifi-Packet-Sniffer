
public class Packet {

	int RSSI;
	String MacSource;
	String SSID;
	String Digest;
	
	/**
	 * 
	 */
	public Packet(String packetChar) {
		ExtractMac(packetChar);
		ExtractRssi(packetChar);
		ExtractSSID(packetChar);
		ExtractDigest(packetChar);
		System.out.println("");
	}
	
	private void ExtractRssi(String packetChar) {
		String[] begin = packetChar.split("RSSI=");
		String[] rssi = begin[1].split("/"); //split string after RSSI= by "/"
		this.RSSI=Integer.parseInt(rssi[0]); //take cahracters from the beginning to /
		System.out.print("RSSI: " + this.RSSI + "; ");
	}
	
	private void ExtractMac(String packetChar) {
		String[] begin = packetChar.split("MAC_SRC=");
		String[] mac = begin[1].split("/");
		this.MacSource=mac[0];
		System.out.print("Mac Source: " + this.MacSource + "; ");
	}
	
	private void ExtractSSID(String packetChar) {
		String[] begin = packetChar.split("SSID_length=");
		String[] ssid = begin[1].split("/");
		if(ssid[0]!="0")
			this.SSID = ssid[0];
		else
			this.SSID = null;
		System.out.print("SSID: " + this.SSID + "; ");
	}
	
	private void ExtractDigest(String packetChar) {
		String[] begin = packetChar.split("Digest=");
		String[] hash = begin[1].split("/");
		this.Digest = hash[0];
		System.out.print("Digest: " + this.Digest);
	}
	
	

}
