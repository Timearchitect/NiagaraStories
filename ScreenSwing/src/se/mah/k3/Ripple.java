package se.mah.k3;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;


public class Ripple extends Particle{
	float w,h,increment,opacity=255;
	BasicStroke circleStroke;
	public Ripple(int _x, int _y,int _increment){
		super((int)_x,(int)_y);
		x=_x;
		y=_y;
		//w=50;
		//h=50;
		//vx=0;
		//vy=0;
		//ax=0;
		//ay=0;
		increment=_increment;
	}

	public Ripple(int _x, int _y) {
		super((int)_x,(int)_y);
		x=_x;
		y=_y;
		//w=50;
		//h=50;
		//vx=0;
		//vy=0;
		//ax=0;
		//ay=0;
		increment=50;
		}
	public void update(){
		x+=Math.round(vx);
		y+=Math.round(vy);
		vx*=0.93;
		vy*=0.93;
		vx+=ax;
		vy+=ay;
		w+=increment;
		h+=increment;
		circleStroke = new BasicStroke(increment*2);
		increment*=0.90;
		opacity*=0.90;
		if(increment<1)kill();
	}
	
	public void display(Graphics2D g2) {
		g2.setColor(new Color(0,100,255,(int)opacity));
		g2.setStroke(circleStroke);
		g2.drawOval((int)(x-w*0.5), (int)(y-h*0.5), (int)w,(int) h);
		//g2.drawLine((int)x,(int) y, (int)x, (int)(y-vy*2));
	}

}

