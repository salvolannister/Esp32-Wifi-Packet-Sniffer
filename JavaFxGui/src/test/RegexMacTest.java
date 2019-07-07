package test;

import java.util.regex.Pattern;

public class RegexMacTest {
	
	private static String regex ="^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
	private String MAC ="3D:F2:C9:A6:B3:4F";
	
	public static void main(String[] args) {
		String MAC ="3D:F2:C9:A6:B3:4F";
	if(	Pattern.matches(regex, MAC))
		System.out.println("beleza funcionou!!!");
	}
}
