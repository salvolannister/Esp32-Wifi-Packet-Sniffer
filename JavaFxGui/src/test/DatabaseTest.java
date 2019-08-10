package test;

import DB.DBUtil;
import DB.QueryRoom;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTest {
    private static List<String> risultato;


    public static void main(String[] args) throws SQLException {
        DBUtil db = new DBUtil();
        if(!db.openConnection("database.db")){
            System.err.println("Errore di Connessione al DB. Impossibile Continuare");
            System.exit(-1);
        }

        QueryRoom p=new QueryRoom(db.getConn());

        risultato = p.getRoomName();

        for (String s: risultato
             ) {
            System.out.print(" NAME "+s);
            ArrayList<Float> XY = new ArrayList<>();
            XY = p.getRoomDim(s);
            System.out.println("X "+XY.get(0)+ "Y "+ XY.get(1));
        }
    }



}
