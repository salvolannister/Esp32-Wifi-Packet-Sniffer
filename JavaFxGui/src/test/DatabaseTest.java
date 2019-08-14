package test;

import DB.DBUtil;
import DB.QueryFake;
import DB.QueryRoom;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

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

        /*        QueryFake o=new QueryFake(db.getConn());
        Timestamp now1 = Timestamp.valueOf("2018-03-12 12:11:32.0");

        float posX = 243443423;
        float posY = 345545344;

        o.addtupla("00:00:00:GG:GG:GG", now1.getTime(), posX, posY, "aulaI", 58, "Margaret");*/
    }



}
