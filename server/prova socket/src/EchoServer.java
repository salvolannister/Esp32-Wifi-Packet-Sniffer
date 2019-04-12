import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EchoServer {
    private static final Integer TOT_ESP = 1;

    public static void main(String[] args) throws IOException {

        Map<String, PacketRec> tab= new HashMap<String, PacketRec>();

        while(true) {

            try (
                    ServerSocket serverSocket =
                            new ServerSocket(8080);
                    Socket clientSocket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
            		
            		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                /*InputStream in = clientSocket.getInputStream();
                BufferedOutputStream out =
                        new BufferedOutputStream(fos)*/

            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                	if(inputLine.compareTo("Hello from ESP32")==0)
                	{
                		System.out.println(inputLine);
                		out.println("SERVER OK!");
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