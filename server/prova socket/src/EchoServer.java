import DB.DBUtil;

import java.net.*;
import java.io.*;
import java.util.*;

public class EchoServer {
    private static final Integer TOT_ESP = 1;
    public  static Map<String, PacketRec> tab= new HashMap<String, PacketRec>();
    public  static List<Sum_PacketRec> sum_tab= new ArrayList<Sum_PacketRec>();
    public static void main(String[] args) throws IOException {
    	/*
    	 * prova sinc GIT!!	
    	 */

        int waitSec = 10;
        String MacESPDavide = "24:0a:c4:9b:4f:ac";

        DBUtil db=new DBUtil();
        if(!db.openConnection("fake_db.db")){
            System.err.println("Errore di Connessione al DB. Impossibile Continuare");
            System.exit(-1);
        }

        while(true) {

            try (
                    ServerSocket serverSocket =
                            new ServerSocket(8080);
                    //Socket clientSocket = serverSocket.accept();

                  /*  BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));

            		//PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            		//prova!!!!!!!!!!!!!!!!!!!!!
            		DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
*/
                
            ) {

                while (true) {
                    //count++;


                    new Receiver(serverSocket.accept(),TOT_ESP,db).start();
                    System.out.println("io");
                    synchronized (tab){
                        writeFile(tab, "prova.txt");
                    }
                    synchronized (sum_tab){
                        writeFile2(sum_tab, "out.txt");
                    }
                }
                /*String inputLine;
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
                        //out.println(StartTime.substring(0, Math.min(StartTime.length(), 10)));
                        //out.close();
                        //
                        
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
           */
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port 8080 "
                        + " or listening for a connection");
                System.out.println(e.getMessage());
            }finally {
                db.closeConnection();
            }

        }
    }


    /***
     *
     * @param tab
     *
     * funzione di debug, scrive il contenuto della mappa tab su di un file
     */
    public static void writeFile(Map<String, PacketRec> tab, String path) {



        File f=new File(".");
        f.getAbsolutePath();
        String url =f.getAbsolutePath()+"//"+path;
        try {
            File file = new File(url);
            FileWriter fw = new FileWriter(file);
            fw.write(tab.toString());
            fw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile2(List<Sum_PacketRec> tab, String path) {



        File f=new File(".");
        f.getAbsolutePath();
        String url =f.getAbsolutePath()+"//"+path;
        try {
            File file = new File(url);
            FileWriter fw = new FileWriter(file);
            fw.write(tab.toString());
            fw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}