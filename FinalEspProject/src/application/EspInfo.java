package application;

import java.util.regex.Pattern;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class EspInfo {
	/* Classe usata per memorizzare dati relativi ai dispositivi
	Esp
	 */

	private TextField x;
	private TextField y;
	private TextField mac;



	public String getMAC() {
		return MAC;
	}

	public float getX() {
		return X;
	}

	public float getY() {
		return Y;
	}

	private String MAC;
	private float X;
	private float Y;
	private static String regex ="^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
	
	public EspInfo(TextField x, TextField y, TextField mac) {
		this.x = x;
		this.y = y;
		this.mac = mac;
	}

	public EspInfo(Float X, Float Y, String MAC){
		this.X = X;
		this.Y = Y;
		this.MAC = MAC;
	}

	public boolean textFieldCheck(){
	
		
		if(checkFieldXY(x) ){
			X =  Float.parseFloat(x.getText());
			if(checkFieldXY(y)){
				Y = Float.parseFloat(y.getText());
				/*check mac address*/
				if(mac.getText() == null || mac.getText().trim().isEmpty()){
					 Alert fail= new Alert(AlertType.INFORMATION);
					fail.getDialogPane().setExpanded(true);
				        fail.setHeaderText("failure");
				        fail.setContentText("You must fullfill the mac fields");
				        fail.showAndWait();
				        return false;
				}else {
					this.MAC = mac.getText();
					if(!Pattern.matches(regex, MAC)) {
						 Alert fail= new Alert(AlertType.INFORMATION);
							fail.getDialogPane().setExpanded(true);
							fail.getDialogPane().autosize();
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
	
	private static boolean checkFieldXY( TextField o) {
		
		if(o.getText() == null || o.getText().trim().isEmpty()) {
			 Alert fail= new Alert(AlertType.INFORMATION);
		        fail.setHeaderText("failure");
		        fail.setContentText("You must fullfill the X and Y fields");
		        fail.showAndWait();
			 return false;
		}else {
		
			try {
			
			}catch ( NumberFormatException e) {
				o.setText("Must be a float or Integer");
				return false;
			}
		}
		return true;
		
	}
}
