import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EchoServer {
    private static final Integer TOT_ESP = 1;

    public static void main(String[] args) throws IOException {
    	
    	//variable used to verify if the esp is sniffing or in configuration phase
    	Boolean isSetted = false;
    	
        Map<String, PacketRec> tab= new HashMap<String, PacketRec>();

        while(true) {

            try (
                    ServerSocket serverSocket =
                            new ServerSocket(8080);
                    Socket clientSocket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
            		
            		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                	if(!isSetted)
                	{
                		System.out.println(inputLine);
     		
                		// create a calendar
                        Calendar cal = Calendar.getInstance();
                        // get time in millis from Epoch
                        Long TimeLong = cal.getTimeInMillis();
                        System.out.println(TimeLong);
                        // add to current time 20seconds -> ESP start sniffing at now+20
                        TimeLong = TimeLong + 20000; //20sec
                        System.out.println(TimeLong);
                        // convert long to string in order to truncate at 10 number
                        String StartTime = Long.toString(TimeLong);
                        out.println(StartTime.substring(0, Math.min(StartTime.length(), 10)));
                		isSetted = true;
                	}
                	else
                	{
                		Packet p=new Packet(inputLine);
                        if(checkInsert(p, tab)==false)
                            System.out.println("pacchetto già ricevuto");
                	}
           
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port 8080 "
                        + " or listening for a connection");
                System.out.println(e.getMessage());
            }

        }
    }

    private static boolean checkInsert(Packet p, Map<String,PacketRec> tab) {
        if(tab.containsKey(p.getDigest())==true){
            if(tab.get(p.getDigest()).getN_ESP()<TOT_ESP){
                tab.get(p.getDigest()).newSignal(p.getRSSI());
                return true;
            }
        }
        else{
            tab.put(p.getDigest(), new PacketRec(p));
            //System.out.println(tab.toString());
            return true;
        }
        return false;
    }
}