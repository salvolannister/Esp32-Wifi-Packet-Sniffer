package application;

import java.util.regex.Pattern;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class EspInfo {
	
	private TextField x;
	private TextField y;
	private TextField mac;
	private String MAC;
	private float X;
	private float Y;
	private static String regex ="^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
	
	public EspInfo(TextField x, TextField y, TextField mac) {
		this.x = x;
		this.y = y;
		this.mac = mac;
	}
	
	public boolean textFieldCheck(){
	
		
		if(checkFieldXY(x,X)) {
			if(checkFieldXY(y,Y)){
				/*check mac address*/
				if(mac.getText() == null || mac.getText().trim().isEmpty()){
					 Alert fail= new Alert(AlertType.INFORMATION);
				        fail.setHeaderText("failure");
				        fail.setContentText("You must fullfill the mac fields");
				        fail.showAndWait();
				        return false;
				}else {
					this.MAC = mac.getText();
					if(!Pattern.matches(regex, MAC)) {
						 Alert fail= new Alert(AlertType.INFORMATION);
					        fail.setHeaderText("failure");
					        fail.setContentText("Mac should be ex. 3D:F2:C9:A6:B3:4F or 3D-F2-C9-A6-B3-4F");
					        fail.showAndWait();
					        return false;
					}else {
						return true;
					}
				}
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	private static boolean checkFieldXY( TextField o, float N) {
		
		if(o.getText() == null || o.getText().trim().isEmpty()) {
			 Alert fail= new Alert(AlertType.INFORMATION);
		        fail.setHeaderText("failure");
		        fail.setContentText("You must fullfill the X and Y fields");
		        fail.showAndWait();
			 return false;
		}else {
		
			try {
			N = Float.parseFloat(o.getText());
			
			}catch ( NumberFormatException e) {
				o.setText("Must be a float or Integer");
				return false;
			}
		}
		return true;
		
	}
}
