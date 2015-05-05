package se.mah.k3;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

public class Particle {
		float x,y,vx,vy,ax,ay;
		boolean kill;
		Color particleColor= Color.BLUE;
	
	
	public Particle(int x, int y){
		this.x=x;
		this.y=y;
		vy=5;
		ay=(float) 0.1;
	}
	
	public void update(){
		x+=vx;
		y+=vy;
		vx+=ax;
		vy+=ay;
	}

	public void display(Graphics g) {
		Graphics2D g2= (Graphics2D) g;
		g2.setColor(particleColor);
		g2.setStroke(new BasicStroke(40, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		Random r= new Random();
	//	g2.drawString(Character.(r.nextInt()*300));
		g2.drawLine((int)x,(int) y, (int)x, (int)(y-vy));
		//g2.fillOval((int)x,(int) y, 20, 20);
	}
}
