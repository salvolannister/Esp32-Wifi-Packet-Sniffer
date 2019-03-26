
import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) throws IOException {
/*
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
*/
        InetAddress hostName = InetAddress.getLocalHost();
        //int portNumber = Integer.parseInt(args[1]);

        //File myFile= new File("prova.txt");

       // byte[] buffer=new byte[(int) myFile.length()];

        try (
                Socket echoSocket = new Socket(hostName, 8080);
               /* BufferedInputStream FileIn =
                        new BufferedInputStream(
                                new FileInputStream(myFile));*/

               BufferedReader reader=new BufferedReader(new FileReader("/home/umb/Scrivania/APPLICAZIONI INTERNET/lab/lab1/client/src/prova.txt"));

                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                /*OutputStream out =
                        echoSocket.getOutputStream();
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()))*/

        ) {


            //String userInput;
            //FileIn.read(buffer, 0, buffer.length);
            String line;
            while((line= reader.readLine())!=null){
                out.println(line);
                System.out.println("file letto");
            }


        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}