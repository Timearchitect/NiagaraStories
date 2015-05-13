package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

public class TextParticle extends Particle {
String text;
float w,h,opacity ;
boolean kill;
	public TextParticle(int x, int y,float _w,float _h,int _vx,int _vy,String _text) {
		super(x, y);
		vx=_vx;
		vy=_vy;
		w=_w;
		h=_h;
		text=_text;
		opacity=200;
		
	}

	public void display(Graphics g) {
		Graphics2D g2= (Graphics2D) g;
		
		 Font font = new Font("Roboto-Regular", Font.BOLD, 20);
			
		
		try{
		    font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Roboto-Regular.ttf")).deriveFont(24f);
		    
			} catch (IOException e) {
			    e.printStackTrace();
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		g2.setFont(font);
		
		/*g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);*/
		
		g2.setColor(new Color(255,255,255,(int)opacity));
		g2.drawString(text, (int)(x-w*0.5), (int)(y+ h * 0.25));
	}
	public void update(){
		x+=vx;
		y+=vy;
		vx*=0.85;
		vy*=0.94;
		opacity*=0.90;
		if(opacity<10)kill();
	}
	public void kill(){
		this.kill=true;
	}
}
