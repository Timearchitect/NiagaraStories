package se.mah.k3;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

 public class Constants {
	public static final int WaterStrokeWidth = 30;
	public static Font font,lightFont,boldFont ;
	public static int screenNbr=145;
	
	public static String wordBg = "#009688";
	public static String wordSt = "#8d0096";
	public static String waterC = "#90caf9";
	public static Color wordBackground = (hexToRgb(wordBg));
	public static Color wordStroke = (hexToRgb(wordSt));
	public static Color waterColor = (hexToRgb(waterC));
	public static int screenWidth = 1920, screenHeight = 1080;
	public static BasicStroke squareStroke = new BasicStroke(35, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	public static BasicStroke userStroke = new BasicStroke(15, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	public static BasicStroke wordOutline = new BasicStroke(5, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);

	public static Font userFont=boldFont;

	public static Color hexToRgb(String colorString) {
		return new Color(Integer.valueOf(colorString.substring(1, 3), 16),
				Integer.valueOf(colorString.substring(3, 5), 16),
				Integer.valueOf(colorString.substring(5, 7), 16));
	}
}
