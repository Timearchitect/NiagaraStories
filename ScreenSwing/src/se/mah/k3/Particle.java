package se.mah.k3;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

public class Particle {
		public float x,y,vx,vy,ax,ay;
		public boolean dead;
		private int StrokeWidth=40;
		private Color particleColor= Color.BLUE;
		private Random r= new Random();
		private BasicStroke squareStroke = new BasicStroke(StrokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
	public Particle(int _x, int _y){
		x=_x;
		y=_y;
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

	public void display(Graphics2D g2) {
		g2.setColor(particleColor);
		g2.setStroke(squareStroke);
		g2.drawLine((int)x,(int) y, (int)x, (int)(y-vy*2));
	}
	
	public void collisionCircle(int _x,int _y, int _radius){
		float xDiff,yDiff,dist;
		xDiff=_x-x;
		yDiff= _y-y;
		dist=(float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
		//dist=(float) Math.sqrt(Math.pow(xDiff,2)+Math.pow(yDiff,2));
		if(dist-_radius<StrokeWidth){
		vy*=(-1)*0.5;
		vx=r.nextInt(10)-5;
		}
		
	}
}


