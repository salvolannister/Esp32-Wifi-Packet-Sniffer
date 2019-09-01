package DB;

import DTO.Posizione;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryPosition {

    private Connection conn;
    public QueryPosition(Connection conn) {
        this.conn=conn;
    }


    public boolean aggiungiTupla(String hash,String mac, long time,  int room, Float posX, Float posY) throws SQLException {

        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);

            String s=new String("INSERT INTO Position"+ "(MAC, Timestamp, Room, X, Y, Hash)"+" VALUES (?, ?, ?, ?, ?, ?)");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(6, hash );
                pstmt.setString(1, mac );
                pstmt.setLong(2, time);
                pstmt.setInt(3, room);
                pstmt.setFloat(4, posX);
                pstmt.setFloat(5, posY);
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;
        }

    }


    public Map<String, Long> showMac(String timeI, String timeF) throws SQLException {

        PreparedStatement pstmt;
        Map<String, Long> risultato= new HashMap<>();

        try {
            conn.setAutoCommit(false);

            String s=new String("SELECT MAC, count(*) AS val FROM Position WHERE Timestamp >= ? AND Timestamp <= ? GROUP BY MAC ORDER BY count(*) DESC");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                pstmt.setString(2, timeF);
                ResultSet res=pstmt.executeQuery();

                while (res.next()){
                        risultato.put(res.getString("MAC"), res.getLong("val"));
                        //System.out.println(res.getString("MAC")+"  "+res.getLong("val"));
                    }


                System.out.println(risultato);

                if(risultato.isEmpty()==false){
                    conn.commit();
                    return risultato;
                }
                return null;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }



    public Map<String, Long> showMacPerRoom(String timeI, String timeF, String room) throws SQLException {

        PreparedStatement pstmt;
        Map<String, Long> risultato= new HashMap<>();

        try {
            conn.setAutoCommit(false);

            String s=new String("SELECT MAC, count(*) AS val FROM Position WHERE Timestamp >= ? AND Timestamp <= ? AND Room = ? GROUP BY MAC ORDER BY count(*) DESC");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                pstmt.setString(2, timeF);
                pstmt.setString(3, room);
                ResultSet res=pstmt.executeQuery();

                while (res.next()){
                    risultato.put(res.getString("MAC"), res.getLong("val"));
                    //System.out.println(res.getString("MAC")+"  "+res.getLong("val"));
                }


                System.out.println(risultato);

                if(risultato.isEmpty()==false){
                    conn.commit();
                    return risultato;
                }
                return null;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }






    /*query creata da Umberto
    considerando anche un tempo di fine

     */
    public Map<String, Posizione> showPosition(String timeI, String timeF) throws SQLException {

        PreparedStatement pstmt;
        Map<String, Posizione> risultato= new HashMap<>();

        try {
            conn.setAutoCommit(false);
            /* DESC sta per descendant */
            String s=new String("SELECT MAC, AVG(X) AS posX, AVG(Y) AS posY FROM Position WHERE Timestamp >= ? AND Timestamp <= ? GROUP BY MAC ORDER BY count(*) DESC");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                pstmt.setString(2, timeF);
                ResultSet res=pstmt.executeQuery();

                while (res.next()){
                    risultato.put(res.getString("MAC"),new Posizione(res.getFloat("posX"),res.getFloat("posY")));
                    //System.out.println(res.getString("MAC")+"  "+res.getLong("val"));
                }


                System.out.println(risultato);

                if(risultato.isEmpty()==false){
                    conn.commit();
                    return risultato;
                }
                return null;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }




    /*query che in un determinato minuto, det stanza
    e configurazione
    restituisce i dati del dispositivo
     */
    public Map<String, Posizione> showPosition(String timeI, String room, String conf, ArrayList<Float> roomDim) throws SQLException {

        PreparedStatement pstmt;
        Map<String, Posizione> risultato= new HashMap<>();

        try {
            conn.setAutoCommit(false);
            /* DESC sta per descendant */
            String s=new String("SELECT MAC, Position.X, Position.Y FROM Position, Room WHERE Timestamp >= ? AND Timestamp <= ? AND Room = ? AND Configuration = ? AND Room.Name=Position.Room AND Position.X>=0 AND Position.Y>=0 AND Position.X<=Room.X AND Position.Y<=Room.Y");

            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                Long timeF = Long.parseLong(timeI);
                Long minuto =(long) 60000;
                /* aggiungo un minuto come tempo finale
                perché vengono contati anche i microsecondi quindi
                non ci sara' mai un pacchetto nell'esatto minuto
                 */
                timeF = timeF + minuto;

                pstmt.setLong(2,  timeF);
                pstmt.setString(3, room);
                pstmt.setString(4, conf);
                ResultSet res=pstmt.executeQuery();

                while (res.next()){
                    risultato.put(res.getString("MAC"),new Posizione(res.getFloat("X"),res.getFloat("Y")));
                    //System.out.println(res.getString("MAC")+"  "+res.getLong("val"));
                }


                System.out.println(risultato);

                if(risultato.isEmpty()==false){
                    conn.commit();
                    return risultato;
                }
                System.out.println("No device was found form this time: "+ timeI+ "to this "+timeF);
                return null;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }

    /* stamap il contenuto di Position a scopi di debugging
    il codice che la richiama e' presente in TimeDatabaseSet
    * */
    public void printTable() throws SQLException {
        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);
            /* DESC sta per descendant */
            String s=new String("SELECT * FROM Position ");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {

                ResultSet res=pstmt.executeQuery();

                while (res.next()){
                    long timeLong = res.getLong("Timestamp");
                    Timestamp tS = new Timestamp(timeLong);

                    String t = tS.toString();
                    System.out.println(res.getString("MAC")+
                            " Timestamp "+t+" X: "+
                            res.getInt("X")+" Y: "+
                                    res.getInt("Y")+" Room: "+res.getString("Room")+
                                    " Configuration: "+ res.getString("Configuration"));
                }



            }catch (Exception ex){
                ex.printStackTrace();

            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");

        }
    }


    /*Data una stanza. un tempo di inzio, seleziona i MAC e il numero di volte che compaiono
     dal tempo immesso dall'utente + 5 min a partire da esso*/

    public Map<String, Long> showNumberMacPerRoom(String timeI, String timeF, String room) throws SQLException {

        PreparedStatement pstmt;
        Map<String, Long> risultato= new HashMap<>();

        try {
            conn.setAutoCommit(false);
            /* DESC sta per descendant */
            String s= new String("SELECT MAC, count(*) AS val FROM Position WHERE Timestamp >= ? AND Timestamp <= ? AND Room = ? GROUP BY MAC ORDER BY count(*) DESC");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                pstmt.setString(2,  timeF);
                pstmt.setString(3, room);
                ResultSet res=pstmt.executeQuery();


                while (res.next()){
                    risultato.put(res.getString("MAC"), res.getLong("val"));
                    System.out.println(res.getString("MAC")+"  "+res.getLong("val"));
                }

                if(risultato.isEmpty()==false){
                    conn.commit();
                    return risultato;
                }
                return null;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }


    public Map<String, Long> getGlobalError(String timeI, String timeF, String room) throws SQLException {

        PreparedStatement pstmt;
        Map<String, Long> risultato= new HashMap<>();

        try {
            conn.setAutoCommit(false);
            /* DESC sta per descendant */
            String s= new String("SELECT MAC, count(*) AS val, SUM(Err) AS Errore, SUM(MergedNumb) AS merged FROM Position WHERE Timestamp >= ? AND Timestamp <= ? AND Room = ? AND Err>0 GROUP BY MAC HAVING COUNT(*) >4 ORDER BY count(*) DESC");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                pstmt.setString(2,  timeF);
                pstmt.setString(3, room);
                ResultSet res=pstmt.executeQuery();


                long N=0;
                long Err=0;
                long merge=0;
                while (res.next()){
                    N+=res.getLong("val");
                    Err+=res.getLong("Errore");
                    merge+=res.getLong("merged");
                }
                risultato.put("N", N);
                risultato.put("Err", Err);
                risultato.put("merge", merge);
                System.out.println("N "+ N + "ERR "+ Err+ "merged "+ merge);

                if(risultato.isEmpty()==false){
                    conn.commit();
                    return risultato;
                }
                return null;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }



    /*aggiungi di test nel database database.db*/
    public boolean addtupla(String mac, long time, Float posX, Float posY,  String room, long hash, String conf) throws SQLException {

        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);

            String s=new String("INSERT INTO Position"+ "(MAC, Timestamp, X, Y, Room, Hash, Configuration)"+" VALUES (?, ?, ?, ?, ?, ?, ?) ");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1, mac);
                pstmt.setLong(2, time);
                pstmt.setFloat(3, posX);
                pstmt.setFloat(4, posY);
                pstmt.setString(5, room);
                pstmt.setLong(6, hash);
                pstmt.setString(7, conf);
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;
        }

    }


    /** da capire
     *
     */

    public boolean dropDB() throws SQLException {
        try{
            conn.setAutoCommit(false);
            String s=new String("DROP TABLE \"Position\"");
            try (PreparedStatement pstmt = conn.prepareStatement(s)) {
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;

        }
    }

    public boolean createDB() throws SQLException {
        try{
            conn.setAutoCommit(false);

            String s=new String("CREATE TABLE IF NOT EXISTS \"Position\" ( \"MAC\" TEXT, \"Timestamp\" INTEGER, \"X\" REAL, \"Y\" REAL, \"Room\" INTEGER, \"Hash\" REAL, \"Err\" REAL, \"MergedNumb\" INTEGER, PRIMARY KEY(\"Hash\"))" );
            try (PreparedStatement pstmt = conn.prepareStatement(s)) {
                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;

        }
    }


    public boolean aggiungiTuplaN(String hash, String mac, long time, String room, Float posX, Float posY, Float err, int mergnumb, String configurazione) throws SQLException {

        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);

            String s=new String("INSERT INTO Position"+ "(MAC, Timestamp, Room, X, Y, Err, MergedNumb, Hash, Configuration)"+" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(8, hash );
                pstmt.setString(1, mac );
                pstmt.setLong(2, time);
                pstmt.setString(3, room);
                pstmt.setFloat(4, posX);
                pstmt.setFloat(5, posY);
                pstmt.setFloat(6, err);
                pstmt.setInt(7, mergnumb);
                pstmt.setString(9, configurazione );

                pstmt.executeUpdate();
                conn.commit();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
                return false;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return false;
        }

    }

    public Long getGlobalMAC(String timeI, String timeF, String room) throws SQLException {
        PreparedStatement pstmt;

        try {
            conn.setAutoCommit(false);
            /* DESC sta per descendant */
            String s= new String("SELECT  MAC, count(*) AS val FROM Position WHERE Timestamp >= ? AND Timestamp <= ? AND Room = ? AND Err=0 GROUP BY MAC HAVING COUNT(*) >4 ORDER BY count(*) DESC");
            try (PreparedStatement preparedStatement = pstmt = conn.prepareStatement(s)) {
                pstmt.setString(1,  timeI);
                pstmt.setString(2,  timeF);
                pstmt.setString(3, room);
                ResultSet res=pstmt.executeQuery();


                long N=0;
                while (res.next()) {
                    N++;
                }




                    conn.commit();
                    return N;


            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.out.println("errore");
            return null;
        }

    }
}