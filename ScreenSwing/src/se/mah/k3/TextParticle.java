package se.mah.k3;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class TextParticle extends Particle {
String text;
float w,h,opacity;
	public TextParticle(int x, int y,float _w,float _h,int _vx,int _vy,String _text) {
		super(x, y);
		vx=_vx;
		vy=_vy;
		w=_w;
		h=_h;
		text=_text;
		 opacity=255;
	}

	public void display(Graphics g) {
		Graphics2D g2= (Graphics2D) g;
		g2.setFont( new Font("Verdana", Font.BOLD, 20));
		g2.setColor(new Color(255,255,255,(int)opacity));
		g2.drawString(text, (int)(x-w*0.5), (int)(y+ h * 0.25));
	}
	public void update(){
		x+=vx;
		y+=vy;
		vx*=0.94;
		vy*=0.94;
		opacity*=0.99;
	}
}
