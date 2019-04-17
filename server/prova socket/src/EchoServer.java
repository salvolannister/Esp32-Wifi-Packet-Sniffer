import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EchoServer {
    private static final Integer TOT_ESP = 1;

    public static void main(String[] args) throws IOException {
    	/*
    	 * prova sinc GIT!!	
    	 */
        Map<String, PacketRec> tab= new HashMap<String, PacketRec>();
        int waitSec = 10;
        String MacESPDavide = "24:0a:c4:9b:4f:ac";

        while(true) {

            try (
                    ServerSocket serverSocket =
                            new ServerSocket(8080);
                    Socket clientSocket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
            		
            		//PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            		//prova!!!!!!!!!!!!!!!!!!!!!
            		DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

                
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                	String str = trunc(inputLine, 5);
                	if(str.compareTo("Hello")==0) {
                		     	
                		System.out.println(inputLine);
     		
                		// create a calendar
                        Calendar cal = Calendar.getInstance();
                        // get time in millis from Epoch
                        Long TimeLong = cal.getTimeInMillis();
                        // add to current time waitSec -> ESP start sniffing at now+waitSec
                        TimeLong = TimeLong + waitSec*1000;
                        // convert long to string in order to truncate at 10 number
                        String StartTime = Long.toString(TimeLong);
                        
                        //VECCHIO
                        /*out.println(StartTime.substring(0, Math.min(StartTime.length(), 10)));
                        out.close();
                        */
                        
                        //PROVA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        for (int i = 0; i < 10; i++) {
                        	System.out.print(StartTime.charAt(i));
							dOut.write(StartTime.charAt(i));
						}
                        dOut.flush(); // Send off the data
                        dOut.close();
                    	System.out.println();

                        //System.out.println("Time send to ESP: "+ StartTime.substring(0, Math.min(StartTime.length(), 10)));
                        
                        //check if MAC has changed!!!!!
                        String[] mac = inputLine.split("My Mac is: ");
                        if(mac[1].compareTo(MacESPDavide)!=0)
                        	System.out.println("---------- MAC HAS CHANGED!!!!!!!!!");
                        break;
                	}
                	else
                	{	
                		if(inputLine.compareTo("STOP")!=0) {
                			Packet p=new Packet(inputLine);
                            if(checkInsert(p, tab)==false)
                                System.out.println("pacchetto già ricevuto");
                		}
                		else
                		{
                			System.out.println("Stop message received: " + inputLine);
                		}
                			
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
            return true;
        }
        return false;
    }
    
    private static String trunc(String value, int length)
    {
    String val = "";
      if (value != null && value.length() > length)
        val = value.substring(0, length);
      return val;
    }
}