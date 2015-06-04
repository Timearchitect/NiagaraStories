package se.mah.k3;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;

 public class Constants {
	public static String theme="";
	public static Calendar cal= Calendar.getInstance();
	public static boolean debug, noCollision=true,noTimer=false,noUser=true,simple,spawnParticle;
	public static int spaceOnScreen=300, screenNbr=145,screenWidth = 1920, screenHeight = 1080;
    public static final int PARTICLE_LIMIT=3000,HEAVY_PARTICLE_LIMIT=400,PROJECTILE_LIMIT=100;
    public static final int clearInterval = 5*60; // time for next clearWave
	public static String wordBg = "#009688";
	public static String wordSt = "#8d0096";
	public static String waterC = "#90caf9";
	public static Color waterColorTrans = (new Color(144, 202, 249, 150));
	public static Color waterEffect = (new Color(255, 255, 255, 50));
	public static Color wordBackground = (hexToRgb(wordBg));
	public static Color waterColor = (hexToRgb(waterC));
	public static Color wordStroke = (hexToRgb(wordSt));
	public static float fontSize=58;
	public static Font font,lightFont,boldFont,boldFontScreen ,userFont=boldFont;
	public static final int WaterStrokeWidth = 35;
	protected static final int StartWordAmount = 5;

	public static BasicStroke waterStroke = new BasicStroke(WaterStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	public static BasicStroke squareStroke = new BasicStroke(20, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	public static BasicStroke userStroke = new BasicStroke(15, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	public static BasicStroke wordOutline = new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	public static BasicStroke wordEffectLine = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);

	public static long timeLeft;
	public static long startTime;


	public static Color hexToRgb(String colorString) {
		return new Color(Integer.valueOf(colorString.substring(1, 3), 16),
				Integer.valueOf(colorString.substring(3, 5), 16),
				Integer.valueOf(colorString.substring(5, 7), 16));
	}
}
