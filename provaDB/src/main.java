import DB.DBUtil;
import DB.QueryFake;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

public class main {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        //apro una nuova connessione con il DB
        DBUtil db=new DBUtil();
        if(!db.openConnection("fake_db.db")){
            System.err.println("Errore di Connessione al DB. Impossibile Continuare");
            System.exit(-1);
        }


        int c=0;
        //input di prova
        int i, j;
        long time;
        Random r=new Random();
        int min=0, max=10;//suppongo stanza di 10*10
        float x, y;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        time=timestamp.getTime();

        String[] ListMAC=new String[5];

        for(j=0; j<5; j++) {
            char m= (char) ('A'+j);
            ListMAC[j]="00:00:00:"+m+m+":"+m+m+":"+m+m;

        }

        for(j=0; j<5; j++) {//scorro di un giorno
            time+=24*60*60*1000;
            for (i = 0; i < 5; i++) {//scorro di 5 minuti nell'arco dello stesso giorno
                time+=5*60*1000;
/*
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(time);

                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(ListMAC[i].getBytes());
                md.update(buffer.array());
                //byte[] digest = md.digest();
                String myHash = md.toString();

                System.out.println(myHash);*/
                 x = min + r.nextFloat() * (max - min);
                 y = min + r.nextFloat() * (max - min);

                try {
                    QueryFake q = new QueryFake(db.getConn());

                    if (!q.aggiungiTupla(String.valueOf(c++),ListMAC[i], time, 1, x, y)) {
                        System.err.println("Errore nell'inserimento");
                        System.exit(-1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        db.closeConnection();

    }

}