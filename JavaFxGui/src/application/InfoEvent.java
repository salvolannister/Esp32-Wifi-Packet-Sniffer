package application;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class InfoEvent implements EventHandler<MouseEvent> {
    private String btn;
    private Long num;
    private TextArea AreaInfo;

    public InfoEvent(Long cont, String name, TextArea area) {
        this.btn=name;
        this.num=cont;
        this.AreaInfo=area;

    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        AreaInfo.setText("");
        AreaInfo.appendText("il mac "+ btn+ "\n è stato rilevato "+num+ " volte");
    }
}
