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
	private String text;
	private float w,h,opacity=255;
	
	public TextParticle(int x, int y,float _w,float _h,int _vx,int _vy,String _text) {
		super(x, y);
		vx=_vx;
		vy=_vy;
		w=_w;
		h=_h;
		text=_text;
	}

	public void display(Graphics2D g2) {
		g2.setFont(Constants.font);
		g2.setColor(new Color(255,255,255,(int)opacity));
		g2.drawString(text, (int)(x-w*0.5), (int)(y+ h * 0.25));
	}
	public void update(){
		x+=vx;
		y+=vy;
		vx*=0.85;
		vy*=0.94;
		opacity*=0.95;
		if(opacity<10)kill();
	}
	public void kill(){
		dead=true;
	}
}
