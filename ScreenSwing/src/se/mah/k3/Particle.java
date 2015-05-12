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
		x+=(int)vx;
		y+=(int)vy;
		vx+=ax;
		vy+=ay;
	}

	public void display(Graphics g) {
		Graphics2D g2= (Graphics2D) g;
		g2.setColor(particleColor);
		g2.setStroke(new BasicStroke(StrokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
	
	//	g2.drawString(Character.(r.nextInt()*300));
		g2.drawLine((int)x,(int) y, (int)x, (int)(y-vy));
		//g2.fillOval((int)x,(int) y, 20, 20);
	}
	
	public void collisionCircle(int _x,int _y){
		float a,b,c;
		a=_x-x;
		b= _y-y;
		c=(float) Math.sqrt((a*a)+(b*b));
		if(c<StrokeWidth){
			System.out.println("collision!!!!");
		vy*=(-1)*0.5;
		vx=r.nextInt(10)-5;
		}
		
		
		
	}
}
