import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EchoServer {
    public static void main(String[] args) throws IOException {

        Map<String, Packet> tab= new HashMap<String, Packet>();
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
                    new Packet(inputLine);
                }




            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port 8080 "
                        + " or listening for a connection");
                System.out.println(e.getMessage());
            }

        }
    }
}