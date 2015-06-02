package se.mah.k3.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import se.mah.k3.Constants;
import se.mah.k3.DrawPanel;
import se.mah.k3.Word;

public class FrameParticle extends Particle {
	Word owner;
	float w,h,margin,increment,opacity=255;
	BasicStroke rectStroke;

	public FrameParticle(int _x, int _y,Word _w,int _increment){
		super((int)_x,(int)_y);
		owner=_w;
		x=_x;
		y=_y;
		margin=_w.margin;
		w=_w.width;
		h=_w.height;
		increment=_increment;
	}

	public FrameParticle(int _x, int _y,Word _w) {
		super((int)_x,(int)_y);
		owner=_w;
		x=_x;
		y=_y;
		margin=_w.margin;
		w=_w.width;
		h=_w.height;
		increment=2;
	}

	
	public void update(){
		x+=Math.round(vx);
		y+=Math.round(vy);
		//vx*=0.93;
		//vy*=0.93;
		//vx+=ax;
		//vy+=ay;
		w+=increment;
		h+=increment;
		rectStroke = new BasicStroke(increment);
		increment*=0.8;
		opacity*=0.95;
		if(opacity<5)kill();
	}
	
	public void display(Graphics2D g2) {
		g2.setColor(new Color(255,255,255,(int)opacity));
		g2.setStroke(rectStroke);
		if (owner==null){

			g2.drawRect((int)(x-w*0.5-margin), (int)(y-h*0.5-margin*0.5), (int)(w+margin*2),(int) (h+margin));
		}else{
			AffineTransform oldTransform = DrawPanel.g2.getTransform();
				DrawPanel.g2.translate((int) (owner.xPos +owner.offsetX),(int) (owner.yPos +owner.offsetY));
				if(!Constants.simple)DrawPanel.g2.rotate(Math.toRadians(owner.angle));
				g2.drawRect((int)(-w*0.5-margin), (int)(-h*0.5-margin*0.5), (int)(w+margin*2),(int) (h+margin-8));
			DrawPanel.g2.setTransform(oldTransform);
		}
	}

}
