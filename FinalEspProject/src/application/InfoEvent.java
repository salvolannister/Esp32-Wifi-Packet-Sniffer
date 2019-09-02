package application;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class InfoEvent implements EventHandler<MouseEvent> {
    private String btn;
    private String dataF;
    private String dataI;
    private Long num;
    private TextArea AreaInfo;

    public InfoEvent(Long cont, String name, TextArea area, String Idata, String Fdata) {
        this.btn=name;
        this.num=cont;
        this.AreaInfo=area;
        this.dataF=Fdata;
        this.dataI=Idata;

    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        AreaInfo.setText("");
        AreaInfo.appendText("Il MAC "+ btn+ "\n è stato rilevato "+num+ " volte\n\n\n");
        AreaInfo.appendText("Nell'intervallo seguente:\n");
        AreaInfo.appendText("TS inizio: "+dataI + "\n" + "TS fine: " +dataF);
    }
}
