package se.mah.k3;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

public class Particle {
		float x,y,vx,vy,ax,ay;
		int StrokeWidth=40;
		boolean kill;
		Color particleColor= Color.BLUE;
		Random r= new Random();
	
	public Particle(int x, int y){
		this.x=x;
		this.y=y;
		vy=5;
		ay=(float) 0.5;
	}
	
	public void update(){
		x+=Math.round(vx);
		y+=(int)vy;
		vx*=0.93;
		vx+=ax;
		vy+=ay;
	}

	public void display(Graphics g) {
		Graphics2D g2= (Graphics2D) g;
		g2.setColor(particleColor);
		g2.setStroke(new BasicStroke(StrokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		g2.drawLine((int)x,(int) y, (int)x, (int)(y-vy*2));
	}
	
	public void collisionCircle(int _x,int _y, int _radius){
		float a,b,dist;
		a=_x-x;
		b= _y-y;
		dist=(float) Math.sqrt((a*a)+(b*b));
		if(dist-_radius<StrokeWidth){
		vy*=(-1)*0.5;
		vx=r.nextInt(10)-5;
		}
		
		
	}
}


