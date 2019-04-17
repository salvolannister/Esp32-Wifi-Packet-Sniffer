import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Map;

public class Receiver implements Runnable {

    Socket csocket;
    private  static Integer n_ESP;
    private Integer id;

    public Receiver(Socket csocket, Integer n, Integer id) {
        this.csocket = csocket;
        this.n_ESP=n;
        this.id=id;
    }

    @Override
    public void run() {
        synchronized (EchoServer.tab) {


            try (

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(csocket.getInputStream()));

                    PrintWriter out = new PrintWriter(csocket.getOutputStream(), true);

            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    //if(!isSetted)
                    //{
                    String str = trunc(inputLine, 5);
                    if(str.compareTo("Hello")==0) {

                        System.out.println(inputLine);

                        // create a calendar
                        Calendar cal = Calendar.getInstance();
                        // get time in millis from Epoch
                        Long TimeLong = cal.getTimeInMillis();
                        //System.out.println(TimeLong);
                        // add to current time 20seconds -> ESP start sniffing at now+20
                        TimeLong = TimeLong + 6000; //20sec
                        // convert long to string in order to truncate at 10 number
                        String StartTime = Long.toString(TimeLong);
                        out.println(StartTime.substring(0, Math.min(StartTime.length(), 10)));
                        System.out.println("Time sendend to ESP: "+ StartTime.substring(0, Math.min(StartTime.length(), 10)));
                        //isSetted = true;
                    }
                    else
                    {
                        if(inputLine.compareTo("STOP")!=0) {
                            Packet p=new Packet(inputLine);
                            if(checkInsert(p, EchoServer.tab)==false)
                                System.out.println("pacchetto già ricevuto");
                        }
                        else
                        {
                            System.out.println("Stop message received: " + inputLine);
                            //isSetted=false;
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
            if(tab.get(p.getDigest()).getN_ESP()<n_ESP){
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

    private static String trunc(String value, int length)
    {
        String val = "";
        if (value != null && value.length() > length)
            val = value.substring(0, length);
        return val;
    }

}