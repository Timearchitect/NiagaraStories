package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;



 public class Constants {

	public static Font font,lightFont,boldFont ;
	public static int screenNbr=145;
	public static String wordBg = "#009688";
	public static Color wordBackground = (hexToRgb(Constants.wordBg));

	

	public static Color hexToRgb(String colorString) {
		return new Color(Integer.valueOf(colorString.substring(1, 3), 16),
				Integer.valueOf(colorString.substring(3, 5), 16),
				Integer.valueOf(colorString.substring(5, 7), 16));
	}
}
