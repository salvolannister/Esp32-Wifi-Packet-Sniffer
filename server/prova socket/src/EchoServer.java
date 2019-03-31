import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EchoServer {
    private static final Integer TOT_ESP = 1;

    public static void main(String[] args) throws IOException {

        Map<String, PacketRec> tab= new HashMap<String, PacketRec>();
/*
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);*/

        //byte[] buffer=new byte[1024];
        //File fos=new File("out.txt");

        while(true) {

            try (
                    ServerSocket serverSocket =
                            new ServerSocket(8080);
                    Socket clientSocket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));

                    /*FileWriter fw = new FileWriter(fos);
                    BufferedWriter out = new BufferedWriter(fw)*/
                    /*PrintWriter out =
                            new PrintWriter(fos);

                /*InputStream in = clientSocket.getInputStream();
                BufferedOutputStream out =
                        new BufferedOutputStream(fos)*/


            ) {
           /* int byteRead=in.read(buffer, 0, buffer.length);
            out.write(buffer, 0, byteRead);*/
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    //out.println(inputLine);
                    Packet p=new Packet(inputLine);
                    if(checkInsert(p, tab)==false){
                        System.out.println("pacchetto già ricevuto");
                    }
                	//System.out.println(inputLine);

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
            System.out.println(tab.toString());
            return true;
        }
        return false;
    }
}