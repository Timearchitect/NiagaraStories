package se.mah.k3.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import se.mah.k3.DrawPanel;

public class WaterParticle extends Particle {
	private Random r =  new Random();

	public WaterParticle(int x2, int y2){
		super(x2,y2);
		x=x2;
		y=y2;
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
	public void kill(){
		if((int)r.nextInt(20)==0)DrawPanel.overParticles.add(new SplashParticle((int)x,(int)y)); //splash
		dead=true;
		
	}
}
