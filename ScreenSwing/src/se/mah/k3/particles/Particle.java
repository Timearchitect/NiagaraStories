package se.mah.k3.particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import se.mah.k3.Constants;

public abstract class Particle {
		public float x,y,vx,vy,ax,ay;
		public boolean dead;
		protected Color particleColor= Constants.waterColor;
		protected Random r= new Random();
	public Particle(int x2, int y2){
		x=x2;
		y=y2;
	}
	
	public void update(){
		x+=Math.round(vx);
		y+=Math.round(vy);
		vx+=ax;
		vy+=ay;
	}

	public void display(Graphics2D g2) {
		g2.setColor(particleColor);
		g2.setStroke(Constants.squareStroke);
		g2.drawLine((int)x,(int) y,(int) x, (int)y);
	}
	
	public void collisionCircle(int _x,int _y, int _radius){
		float xDiff,yDiff,dist;
		xDiff=_x-x;
		yDiff= _y-y;
		dist=(float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
		//dist=(float) Math.sqrt(Math.pow(xDiff,2)+Math.pow(yDiff,2));
		if(dist-_radius< Constants.WaterStrokeWidth){
		vy*=(-1)*0.5;
		vx=r.nextInt(10)-5;
		}
	}
	public void collisionRect(int _x,int _y, int _w,int _h){

		if(_x+_w*0.5 > x &&  _x-_w*0.5 < x){
			if(_y+_h*0.5 > y &&  _y-_h*0.5 < y){
				vy*=(-1)*0.5;
				vx=r.nextInt(10)-5;
			}
		}
	}
	public void kill(){
		dead=true;
	}

	public void collisionVSParticle(Particle p) {
	
		
	}
}
