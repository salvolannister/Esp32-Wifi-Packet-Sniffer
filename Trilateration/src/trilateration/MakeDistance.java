package trilateration;

public class MakeDistance {
    
	/* Potenza del segnale ricevuto alla distanza di riferimento di un metro */
	/* Settato a questo valore dopo varie misurazioni fatte */
	final double Px = 62;
	
	/* Converte l'RSSI in distanza */
	public double RSSIToDistance (double RSSI, double n) {
		
		double x = (Px - RSSI)/(10 * n);		
		double distance = Math.pow(10,x);		
		return distance;		
	}
	
	
}
